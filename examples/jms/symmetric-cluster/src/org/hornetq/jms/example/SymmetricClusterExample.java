/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.hornetq.jms.example;

import org.hornetq.common.example.HornetQExample;
import org.hornetq.jms.HornetQQueue;
import org.hornetq.jms.HornetQTopic;
import org.hornetq.jms.client.HornetQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * This example demonstrates a cluster of three nodes set up in a symmetric topology - i.e. each node
 * is connected to every other node in the cluster. Also each node, has it's own backup node.
 * 
 * This is probably the most obvious clustering topology and the one most people will be familiar with from
 * using clustering in an app server, where every node has pretty much identical configuration to every other node.
 * 
 * By clustering nodes symmetrically, JBoss Messaging can give the impression of clustered queues, topics and
 * durable subscriptions.
 * 
 * In this example we send some messages to a distributed queue and topic and kill all the live servers at different
 * times, and verify that they transparently fail over onto their backup servers.
 * 
 * Please see the readme.html file for more information.
 *
 * @author <a href="tim.fox@jboss.com>Tim Fox</a>
 */
public class SymmetricClusterExample extends HornetQExample
{
   public static void main(String[] args)
   {
      String[] serverArgs = new String[] { "-Xms50M",
                                           "-Xmx100M",
                                           "-XX:+UseParallelGC",
                                           "-XX:+AggressiveOpts",
                                           "-XX:+UseFastAccessorMethods" };
      
      new SymmetricClusterExample().run(args);
   }

   public boolean runExample() throws Exception
   {
      Connection connection0 = null;

      Connection connection1 = null;
      
      Connection connection2 = null;
      
      try
      {
         // Step 1 - We instantiate a connection factory directly, specifying the UDP address and port for discovering
         // the list of servers in the cluster.
         // We could use JNDI to look-up a connection factory, but we'd need to know the JNDI server host and port for the
         // specific server to do that, and that server might not be available at the time. By creating the
         // connection factory directly we avoid having to worry about a JNDI look-up.
         // In an app server environment you could use HA-JNDI to lookup from the clustered JNDI servers without
         // having to know about a specific one.
         ConnectionFactory cf = new HornetQConnectionFactory("231.7.7.7", 9876); 
         
         // We give a little while for each server to broadcast its whereabouts to the client
         Thread.sleep(2000);
         
         // Step 2. Directly instantiate JMS Queue and Topic objects
         Queue queue = new HornetQQueue("exampleQueue");
         
         Topic topic = new HornetQTopic("exampleTopic");

         // Step 3. We create three connections, they should be to different nodes of the cluster in a round-robin fashion
         // and start them
         connection0 = cf.createConnection();
         
         connection1 = cf.createConnection();
         
         connection2 = cf.createConnection();
         
         connection0.start();
         
         connection1.start();
         
         connection2.start();
         
         // Step 4. We create a session on each connection
         
         Session session0 = connection0.createSession(false, Session.AUTO_ACKNOWLEDGE);
         
         Session session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
         
         Session session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
         
         // Step 5. We create a topic subscriber on each server
         
         MessageConsumer subscriber0 = session0.createConsumer(topic);
         
         MessageConsumer subscriber1 = session1.createConsumer(topic);
         
         MessageConsumer subscriber2 = session2.createConsumer(topic);
         
         // Step 6. We create a queue consumer on server 0
         
         MessageConsumer consumer0 = session0.createConsumer(queue);
         
         // Give a little time for consumers to propagate throughout cluster
         Thread.sleep(2000);
         
         // Step 7. We create an anonymous message producer on just one server 2
         
         MessageProducer producer2 = session2.createProducer(null);
            
         // Step 8. We send 500 messages each to the queue and topic
       
         final int numMessages = 500;
                  
         for (int i = 0; i < numMessages; i++)
         {
            TextMessage message1 = session2.createTextMessage("Topic message 1");
         
            producer2.send(topic, message1);
            
            TextMessage message2 = session2.createTextMessage("Queue message 1");
            
            producer2.send(queue, message2);
         }
         
         // Step 9. We kill live server 1, this will cause connection1 to transparently fail over onto server 4 
         
         killServer(1);
         
         Thread.sleep(500);
         
         // Step 9. Verify all subscribers receive the messages
         
         for (int i = 0; i < numMessages; i++)
         {         
            TextMessage received0 = (TextMessage)subscriber0.receive(5000);
            
            if (received0 == null)
            {
               return false;
            }
            
            TextMessage received1 = (TextMessage)subscriber1.receive(5000);
            
            if (received1 == null)
            {
               return false;
            }
            
            TextMessage received2 = (TextMessage)subscriber2.receive(5000);
            
            if (received2 == null)
            {
               return false;
            }
                        
            TextMessage received3 = (TextMessage)consumer0.receive(5000);
            
            if (received3 == null)
            {
               return false;
            }         
         }
         
         // Step 10. Send 500 more messages to the queue and topic
         
         for (int i = 0; i < numMessages; i++)
         {
            // Step 11. Half way through sending we kill server 2
            
            if (i == numMessages / 2)
            {
               killServer(2);
            }
                        
            TextMessage message3 = session2.createTextMessage("Topic message 2");
            
            producer2.send(topic, message3);
                                    
            TextMessage message4 = session2.createTextMessage("Queue message 2");
            
            producer2.send(queue, message4);
         }
                 
         
         //Step 11. Verify all the messages are received by the subscribers
         
         for (int i = 0; i < numMessages; i++)
         {
            // Step 12. Half way through receiving, we kill server 0
            
            if (i == numMessages / 2)
            {
               killServer(0);
            }
         
            TextMessage received0 = (TextMessage)subscriber0.receive(5000);
            
            if (received0 == null)
            {
               return false;
            }
            
            TextMessage received1 = (TextMessage)subscriber1.receive(5000);
            
            if (received1 == null)
            {
               return false;
            }
            
            TextMessage received2 = (TextMessage)subscriber2.receive(5000);
            
            if (received2 == null)
            {
               return false;
            }
            
            TextMessage received3 = (TextMessage)consumer0.receive(5000);
            
            if (received3 == null)
            {
               return false;
            }         
         }
         
         return true;
      }
      finally
      {
         // Step 15. Be sure to close our resources!

         if (connection0 != null)
         {
            connection0.close();
         }

         if (connection1 != null)
         {
            connection1.close();
         }
         
         if (connection2 != null)
         {
            connection2.close();
         }

      }
   }

}
