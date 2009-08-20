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

package org.hornetq.jms.server.management.jmx.impl;

import java.util.Map;

import javax.management.MBeanInfo;

import org.hornetq.core.logging.Logger;
import org.hornetq.core.management.ReplicationOperationInvoker;
import org.hornetq.core.management.ResourceNames;
import org.hornetq.core.management.impl.MBeanInfoHelper;
import org.hornetq.core.management.jmx.impl.ReplicationAwareStandardMBeanWrapper;
import org.hornetq.jms.server.management.JMSQueueControl;
import org.hornetq.jms.server.management.impl.JMSQueueControlImpl;

/**
 * A ReplicationAwareJMSQueueControlWrapper
 *
 * @author <a href="jmesnil@redhat.com">Jeff Mesnil</a>
 */
public class ReplicationAwareJMSQueueControlWrapper extends ReplicationAwareStandardMBeanWrapper implements
         JMSQueueControl
{

   // Constants -----------------------------------------------------
   
   private static final Logger log = Logger.getLogger(ReplicationAwareJMSQueueControlWrapper.class);

   // Attributes ----------------------------------------------------

   private final JMSQueueControlImpl localControl;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   public ReplicationAwareJMSQueueControlWrapper(final JMSQueueControlImpl localControl,
                                                 final ReplicationOperationInvoker replicationInvoker) throws Exception
   {
      super(ResourceNames.JMS_QUEUE + localControl.getName(), JMSQueueControl.class, replicationInvoker);
      this.localControl = localControl;
   }

   // JMSQueueControlMBean implementation ---------------------------

   public int getConsumerCount()
   {
      return localControl.getConsumerCount();
   }

   public String getDeadLetterAddress()
   {
      return localControl.getDeadLetterAddress();
   }
   
   public void setDeadLetterAddress(String deadLetterAddress) throws Exception
   {
      replicationAwareInvoke("setDeadLetterAddress", deadLetterAddress);
   }

   public int getDeliveringCount()
   {
      return localControl.getDeliveringCount();
   }

   public String getExpiryAddress()
   {
      return localControl.getExpiryAddress();
   }

   public int getMessageCount()
   {
      return localControl.getMessageCount();
   }

   public int getMessagesAdded()
   {
      return localControl.getMessagesAdded();
   }

   public String getName()
   {
      return localControl.getName();
   }

   public long getScheduledCount()
   {
      return localControl.getScheduledCount();
   }

   public boolean isDurable()
   {
      return localControl.isDurable();
   }

   public boolean isTemporary()
   {
      return localControl.isTemporary();
   }

   public String listMessageCounter()
   {
      return localControl.listMessageCounter();
   }

   public String listMessageCounterAsHTML()
   {
      return localControl.listMessageCounterAsHTML();
   }

   public String listMessageCounterHistory() throws Exception
   {
      return localControl.listMessageCounterHistory();
   }

   public String listMessageCounterHistoryAsHTML()
   {
      return localControl.listMessageCounterHistoryAsHTML();
   }

   public Map<String, Object>[] listMessages(String filter) throws Exception
   {
      return localControl.listMessages(filter);
   }
   
   public String listMessagesAsJSON(String filter) throws Exception
   {
      return localControl.listMessagesAsJSON(filter);
   }

   public int countMessages(final String filter) throws Exception
   {
      return localControl.countMessages(filter);
   }

   public String getAddress()
   {
      return localControl.getAddress();
   }

   public String getJNDIBinding()
   {
      return localControl.getJNDIBinding();
   }

   public boolean changeMessagePriority(final String messageID, int newPriority) throws Exception
   {
      return (Boolean)replicationAwareInvoke("changeMessagePriority", messageID, newPriority);
   }

   public boolean expireMessage(final String messageID) throws Exception
   {
      return (Boolean)replicationAwareInvoke("expireMessage", messageID);
   }

   public int expireMessages(final String filter) throws Exception
   {
      return (Integer)replicationAwareInvoke("expireMessages", filter);
   }

   public int moveMessages(final String filter, final String otherQueueName) throws Exception
   {
      return (Integer)replicationAwareInvoke("moveMessages", filter, otherQueueName);
   }

   public boolean moveMessage(final String messageID, final String otherQueueName) throws Exception
   {
      return (Boolean)replicationAwareInvoke("moveMessage", messageID, otherQueueName);
   }

   public int removeMessages(final String filter) throws Exception
   {
      return (Integer)replicationAwareInvoke("removeMessages", filter);
   }

   public boolean removeMessage(final String messageID) throws Exception
   {
      return (Boolean)replicationAwareInvoke("removeMessage", messageID);
   }

   public boolean sendMessageToDeadLetterAddress(final String messageID) throws Exception
   {
      return (Boolean)replicationAwareInvoke("sendMessageToDeadLetterAddress", messageID);
   }

   public void setExpiryAddress(final String expiryAddress) throws Exception
   {
      replicationAwareInvoke("setExpiryAddress", expiryAddress);
   }

   // StandardMBean overrides ---------------------------------------

   @Override
   public MBeanInfo getMBeanInfo()
   {
      MBeanInfo info = super.getMBeanInfo();
      return new MBeanInfo(info.getClassName(),
                           info.getDescription(),
                           info.getAttributes(),
                           info.getConstructors(),
                           MBeanInfoHelper.getMBeanOperationsInfo(JMSQueueControl.class),
                           info.getNotifications());
   }

   // Public --------------------------------------------------------

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------

}
