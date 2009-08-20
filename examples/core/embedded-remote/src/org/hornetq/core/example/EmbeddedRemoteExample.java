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
package org.hornetq.core.example;

import java.util.Date;

import org.hornetq.common.example.SpawnedVMSupport;
import org.hornetq.core.client.ClientConsumer;
import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;


/**
 * 
 * This exammple shows how to run a JBoss Messaging core client and server embedded in your
 * own application
 *
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 *
 */
public class EmbeddedRemoteExample
{

   public static void main(String[] args)
   {

      Process process = null;
      try
      {
         
         // Step 1. start a server remotely
         // Step 2 and 3 on EmbeddedServer
         process = startRemoteEmbedded();
         
         // Step 4. As we are not using a JNDI environment we instantiate the objects directly         
         ClientSessionFactory sf = new ClientSessionFactoryImpl (new TransportConfiguration(NettyConnectorFactory.class.getName()));
         
         // Step 5. Create a core queue
         ClientSession coreSession = sf.createSession(false, false, false);
         
         final String queueName = "queue.exampleQueue";
         
         coreSession.createQueue(queueName, queueName, true);
         
         coreSession.close();
                  
         ClientSession session = null;
   
         try
         {
   
            // Step 6. Create the session, and producer
            session = sf.createSession();
                                   
            ClientProducer producer = session.createProducer(queueName);
   
            // Step 7. Create and send a message
            ClientMessage message = session.createClientMessage(false);
            
            final String propName = "myprop";
            
            message.putStringProperty(propName, "Hello sent at " + new Date());
            
            System.out.println("Sending the message.");
            
            producer.send(message);

            // Step 8. Create the message consumer and start the connection
            ClientConsumer messageConsumer = session.createConsumer(queueName);
            session.start();
   
            // Step 9. Receive the message. 
            ClientMessage messageReceived = messageConsumer.receive(1000);
            System.out.println("Received TextMessage:" + messageReceived.getProperty(propName));
         }
         finally
         {
            // Step 10. Be sure to close our resources!
            if (session != null)
            {
               session.close();
            }

            if (process != null)
            {
               process.destroy();
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(-1);
      }
   }

   /**
    * @return
    * @throws Exception
    */
   private static Process startRemoteEmbedded() throws Exception
   {
      Process process;
      String remoteClasspath= System.getProperty("remote-classpath");
      
      if (remoteClasspath == null)
      {
         System.out.println("remote-classpath system property needs to be specified");
         System.exit(-1);
      }
      
      process = SpawnedVMSupport.spawnVM(remoteClasspath, EmbeddedServer.class.getName(), "", true, 
                               "STARTED::",
                               "FAILED::",
                               ".",
                               new String[]{});
      return process;
   }

}
