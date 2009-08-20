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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.hornetq.common.example.HornetQExample;

/**
 * This example demonstrates how subsequent connections created from a single connection factory can be load
 * balanced across the different nodes of the cluster.
 * 
 * In this example there are three nodes and we use a round-robin client side load-balancing
 * policy.
 * 
 * Note that the three nodes are deliberately not connected as a cluster so we don't have any server-side
 * load balancing going on.
 *
 * @author <a href="tim.fox@jboss.com>Tim Fox</a>
 */
public class ClientSideLoadBalancingExample extends HornetQExample
{
   public static void main(String[] args)
   {
      new ClientSideLoadBalancingExample().run(args);
   }

   public boolean runExample() throws Exception
   {
      InitialContext initialContext = null;
      
      Connection connectionA = null;
      
      Connection connectionB = null;
      
      Connection connectionC = null;

      try
      {
         // Step 1. Get an initial context for looking up JNDI from server 0
         initialContext = getContext(0);

         // Step 2. Look-up the JMS Queue object from JNDI
         Queue queue = (Queue)initialContext.lookup("/queue/exampleQueue");

         // Step 3. Look-up a JMS Connection Factory object from JNDI on server 0
         ConnectionFactory connectionFactory = (ConnectionFactory)initialContext.lookup("/ConnectionFactory");
         
         // Wait a little while to make sure broadcasts from all nodes have reached the client
         Thread.sleep(2000);

         // Step 4. We create three connections, since we are using round-robin load-balancing this should
         // result in each connection being connected to a different node of the cluster
         
         connectionA = connectionFactory.createConnection();
         
         connectionB = connectionFactory.createConnection();
         
         connectionC = connectionFactory.createConnection();
         
         // Step 5. We create a JMS Session on each of those connections
         Session sessionA = connectionA.createSession(false, Session.AUTO_ACKNOWLEDGE);
         
         Session sessionB = connectionB.createSession(false, Session.AUTO_ACKNOWLEDGE);
         
         Session sessionC = connectionC.createSession(false, Session.AUTO_ACKNOWLEDGE);

         // Step 6. We start the connections to ensure delivery occurs on them
         connectionA.start();

         connectionB.start();
         
         connectionC.start();

         // Step 7. We create JMS MessageConsumer objects on the sessions
         MessageConsumer consumerA = sessionA.createConsumer(queue);
         
         MessageConsumer consumerB = sessionB.createConsumer(queue);
         
         MessageConsumer consumerC = sessionC.createConsumer(queue);

         // Step 8. We create JMS MessageProducer objects on the sessions
         MessageProducer producerA = sessionA.createProducer(queue);
         
         MessageProducer producerB = sessionB.createProducer(queue);
         
         MessageProducer producerC = sessionC.createProducer(queue);

         // Step 9. We send some messages on each producer

         final int numMessages = 10;

         for (int i = 0; i < numMessages; i++)
         {
            TextMessage messageA = sessionA.createTextMessage("A:This is text message " + i);

            producerA.send(messageA);

            System.out.println("Sent message: " + messageA.getText());
            
            TextMessage messageB = sessionB.createTextMessage("B:This is text message " + i);

            producerB.send(messageB);

            System.out.println("Sent message: " + messageB.getText());
            
            TextMessage messageC = sessionC.createTextMessage("C:This is text message " + i);

            producerC.send(messageC);

            System.out.println("Sent message: " + messageC.getText());            
         }
         
         // Step 10. We now consume the messages from each node. The connections must be on different nodes
         // since if they shared nodes then the consumers would receive the messages sent from different connections.

         for (int i = 0; i < numMessages; i ++)
         {
            TextMessage messageA = (TextMessage)consumerA.receive(5000);

            System.out.println("Got message: " + messageA.getText() + " from node A");
            
            TextMessage messageB = (TextMessage)consumerB.receive(5000);

            System.out.println("Got message: " + messageB.getText() + " from node B");
            
            TextMessage messageC = (TextMessage)consumerC.receive(5000);

            System.out.println("Got message: " + messageC.getText() + " from node C");
         }
         
         return true;
      }
      finally
      {
         // Step 11. Be sure to close our resources!

         if (connectionA != null)
         {
            connectionA.close();
         }
         
         if (connectionB != null)
         {
            connectionB.close();
         }
         
         if (connectionC != null)
         {
            connectionC.close();
         }
        
         if (initialContext != null)
         {
            initialContext.close();
         }
      }
   }

}
