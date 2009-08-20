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

package org.hornetq.jmstests.stress;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.hornetq.core.logging.Logger;

/**
 * 
 * A Sender.
 * 
 * Sends messages to a destination, used in stress testing
 * 
 * @author <a href="tim.fox@jboss.com">Tim Fox</a>
 * @version <tt>$Revision$</tt>
 *
 * $Id$
 */
public class Sender extends Runner
{
   private static final Logger log = Logger.getLogger(Sender.class);
   
   protected MessageProducer prod;
   
   protected String prodName;
   
   protected int count;
   
   public Sender(String prodName, Session sess, MessageProducer prod, int numMessages)
   {
      super(sess, numMessages);
      this.prod = prod;
      this.prodName = prodName;
   }
   
   public void run()
   {
      try
      {
         while (count < numMessages)
         {
            Message m = sess.createMessage();
            m.setStringProperty("PROD_NAME", prodName);
            m.setIntProperty("MSG_NUMBER", count);
            prod.send(m);
            count++;
         }
      }
      catch (Exception e)
      {
         log.error("Failed to send message", e);
         setFailed(true);
      }
   }

}
