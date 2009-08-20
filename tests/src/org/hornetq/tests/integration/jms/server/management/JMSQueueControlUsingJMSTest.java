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

package org.hornetq.tests.integration.jms.server.management;

import static org.hornetq.core.config.impl.ConfigurationImpl.DEFAULT_MANAGEMENT_ADDRESS;

import java.util.Map;

import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.management.ResourceNames;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.jms.HornetQQueue;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.server.management.JMSQueueControl;

/**
 * 
 * A JMSQueueControlUsingJMSTest
 *
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 *
 *
 */
public class JMSQueueControlUsingJMSTest extends JMSQueueControlTest
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   private QueueConnection connection;

   private QueueSession session;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      HornetQConnectionFactory cf = new HornetQConnectionFactory(new TransportConfiguration(InVMConnectorFactory.class.getName()));
      connection = cf.createQueueConnection();
      session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      connection.start();
   }

   @Override
   protected void tearDown() throws Exception
   {
      connection.close();

      connection = null;
      
      session = null;
      
      super.tearDown();
   }

   @Override
   protected JMSQueueControl createManagementControl() throws Exception
   {
      HornetQQueue managementQueue = new HornetQQueue(DEFAULT_MANAGEMENT_ADDRESS.toString(),
                                                  DEFAULT_MANAGEMENT_ADDRESS.toString());
      final JMSMessagingProxy proxy = new JMSMessagingProxy(session,
                                                            managementQueue,
                                                            ResourceNames.JMS_QUEUE + queue.getQueueName());

      return new JMSQueueControl()
      {
         public boolean changeMessagePriority(String messageID, int newPriority) throws Exception
         {
            return (Boolean)proxy.invokeOperation("changeMessagePriority", messageID, newPriority);
         }

         public int countMessages(String filter) throws Exception
         {
            return (Integer)proxy.invokeOperation("countMessages", filter);
         }

         public boolean expireMessage(String messageID) throws Exception
         {
            return (Boolean)proxy.invokeOperation("expireMessage", messageID);
         }

         public int expireMessages(String filter) throws Exception
         {
            return (Integer)proxy.invokeOperation("expireMessages", filter);
         }

         public int getConsumerCount()
         {
            return (Integer)proxy.retrieveAttributeValue("consumerCount");
         }

         public String getDeadLetterAddress()
         {
            return (String)proxy.retrieveAttributeValue("deadLetterAddress");
         }

         public int getDeliveringCount()
         {
            return (Integer)proxy.retrieveAttributeValue("deliveringCount");
         }

         public String getExpiryAddress()
         {
            return (String)proxy.retrieveAttributeValue("expiryAddress");
         }

         public int getMessageCount()
         {
            return (Integer)proxy.retrieveAttributeValue("messageCount");
         }

         public int getMessagesAdded()
         {
            return (Integer)proxy.retrieveAttributeValue("messagesAdded");
         }

         public String getName()
         {
            return (String)proxy.retrieveAttributeValue("name");
         }

         public long getScheduledCount()
         {
            return (Long)proxy.retrieveAttributeValue("scheduledCount");
         }

         public boolean isDurable()
         {
            return (Boolean)proxy.retrieveAttributeValue("durable");
         }

         public boolean isTemporary()
         {
            return (Boolean)proxy.retrieveAttributeValue("temporary");
         }

         public String listMessageCounter() throws Exception
         {
            return (String)proxy.invokeOperation("listMessageCounter");
         }

         public String listMessageCounterAsHTML() throws Exception
         {
            return (String)proxy.invokeOperation("listMessageCounterAsHTML");
         }

         public String listMessageCounterHistory() throws Exception
         {
            return (String)proxy.invokeOperation("listMessageCounterHistory");
         }

         public String listMessageCounterHistoryAsHTML() throws Exception
         {
            return (String)proxy.invokeOperation("listMessageCounterHistoryAsHTML");
         }

         public Map<String, Object>[] listMessages(String filter) throws Exception
         {
            Object[] res = (Object[])proxy.invokeOperation("listMessages", filter);
            Map<String, Object>[] results = new Map[res.length];
            for (int i = 0; i < res.length; i++)
            {
               results[i] = (Map<String, Object>)res[i];
            }
            return results;
         }

         public String listMessagesAsJSON(String filter) throws Exception
         {
            return (String)proxy.invokeOperation("listMessagesAsJSON", filter);
         }
         
         public int moveMessages(String filter, String otherQueueName) throws Exception
         {
            return (Integer)proxy.invokeOperation("moveMessages", filter, otherQueueName);
         }

         public boolean moveMessage(String messageID, String otherQueueName) throws Exception
         {
            return (Boolean)proxy.invokeOperation("moveMessage", messageID, otherQueueName);
         }

         public int removeMessages(String filter) throws Exception
         {
            return (Integer)proxy.invokeOperation("removeMessages", filter);
         }

         public boolean removeMessage(String messageID) throws Exception
         {
            return (Boolean)proxy.invokeOperation("removeMessage", messageID);
         }

         public boolean sendMessageToDeadLetterAddress(String messageID) throws Exception
         {
            return (Boolean)proxy.invokeOperation("sendMessageToDeadLetterAddress", messageID);
         }

         public void setDeadLetterAddress(String deadLetterAddress) throws Exception
         {
            proxy.invokeOperation("setDeadLetterAddress", deadLetterAddress);
         }

         public void setExpiryAddress(String expiryAddress) throws Exception
         {
            proxy.invokeOperation("setExpiryAddress", expiryAddress);
         }

         public String getAddress()
         {
            return (String)proxy.retrieveAttributeValue("address");
         }

         public String getJNDIBinding()
         {
            return (String)proxy.retrieveAttributeValue("JNDIBinding");
         }
      };
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------

}