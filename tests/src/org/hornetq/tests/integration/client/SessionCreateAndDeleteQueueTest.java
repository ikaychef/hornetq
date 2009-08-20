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
package org.hornetq.tests.integration.client;

import org.hornetq.core.client.ClientSession;
import org.hornetq.core.exception.MessagingException;
import org.hornetq.core.postoffice.Binding;
import org.hornetq.core.server.MessagingServer;
import org.hornetq.core.server.Queue;
import org.hornetq.core.server.impl.LastValueQueue;
import org.hornetq.core.settings.impl.AddressSettings;
import org.hornetq.tests.util.ServiceTestBase;
import org.hornetq.utils.SimpleString;

/**
 * @author <a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>
 */
public class SessionCreateAndDeleteQueueTest extends ServiceTestBase
{
   private MessagingServer server;

   private SimpleString address = new SimpleString("address");

   private SimpleString queueName = new SimpleString("queue");


   public void testDurableFalse() throws Exception
   {
      ClientSession session = createInVMFactory().createSession(false, true, true);
      session.createQueue(address, queueName, false);
      Binding binding = server.getPostOffice().getBinding(queueName);
      Queue q = (Queue) binding.getBindable();
      assertFalse(q.isDurable());
      
      session.close();
   }

   public void testDurableTrue() throws Exception
   {
      ClientSession session = createInVMFactory().createSession(false, true, true);
      session.createQueue(address, queueName, true);
      Binding binding = server.getPostOffice().getBinding(queueName);
      Queue q = (Queue) binding.getBindable();
      assertTrue(q.isDurable());

      session.close();
   }

   public void testTemporaryFalse() throws Exception
   {
      ClientSession session = createInVMFactory().createSession(false, true, true);
      session.createQueue(address, queueName, false);
      Binding binding = server.getPostOffice().getBinding(queueName);
      Queue q = (Queue) binding.getBindable();
      assertFalse(q.isTemporary());
      
      session.close();
   }

   public void testTemporaryTrue() throws Exception
   {
      ClientSession session = createInVMFactory().createSession(false, true, true);
      session.createTemporaryQueue(address, queueName);
      Binding binding = server.getPostOffice().getBinding(queueName);
      Queue q = (Queue) binding.getBindable();
      assertTrue(q.isTemporary());
      
      session.close();
   }

   public void testcreateWithFilter() throws Exception
   {
      ClientSession session = createInVMFactory().createSession(false, true, true);
      SimpleString filterString = new SimpleString("x=y");
      session.createQueue(address, queueName, filterString, false);
      Binding binding = server.getPostOffice().getBinding(queueName);
      Queue q = (Queue) binding.getBindable();
      assertEquals(q.getFilter().getFilterString(), filterString);
      
      session.close();
   }

    public void testAddressSettingUSed() throws Exception
   {
      AddressSettings addressSettings = new AddressSettings();
      addressSettings.setLastValueQueue(true);
      server.getAddressSettingsRepository().addMatch(address.toString(), addressSettings);
      ClientSession session = createInVMFactory().createSession(false, true, true);
      SimpleString filterString = new SimpleString("x=y");
      session.createQueue(address, queueName, filterString, false);
      Binding binding = server.getPostOffice().getBinding(queueName);
      assertTrue(binding.getBindable() instanceof LastValueQueue);

      session.close();
   }

   public void testDeleteQueue() throws Exception
   {
      ClientSession session = createInVMFactory().createSession(false, true, true);
      session.createQueue(address, queueName, false);
      Binding binding = server.getPostOffice().getBinding(queueName);
      assertNotNull(binding);
      session.deleteQueue(queueName);
      binding = server.getPostOffice().getBinding(queueName);
      assertNull(binding);
      session.close();
   }

   public void testDeleteQueueNotExist() throws Exception
  {
     ClientSession session = createInVMFactory().createSession(false, true, true);
     try
     {
        session.deleteQueue(queueName);
        fail("should throw exception");
     }
     catch (MessagingException e)
     {
        assertEquals(MessagingException.QUEUE_DOES_NOT_EXIST, e.getCode());
     }
     session.close();
  }


   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      server = createServer(false);
      server.start();
   }

   @Override
   protected void tearDown() throws Exception
   {
      if(server != null && server.isStarted())
      {
         server.stop();
      }
      
      server = null;
      
      super.tearDown();

   }
}
