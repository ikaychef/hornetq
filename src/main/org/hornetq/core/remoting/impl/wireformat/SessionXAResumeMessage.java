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

package org.hornetq.core.remoting.impl.wireformat;

import javax.transaction.xa.Xid;

import org.hornetq.core.remoting.spi.MessagingBuffer;

/**
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * 
 * @version <tt>$Revision$</tt>
 */
public class SessionXAResumeMessage extends PacketImpl
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   private Xid xid;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   public SessionXAResumeMessage(final Xid xid)
   {
      super(SESS_XA_RESUME);

      this.xid = xid;
   }

   public SessionXAResumeMessage()
   {
      super(SESS_XA_RESUME);
   }

   // Public --------------------------------------------------------

   public Xid getXid()
   {
      return xid;
   }

   public int getRequiredBufferSize()
   {
      return BASIC_PACKET_SIZE + XidCodecSupport.getXidEncodeLength(xid);
   }

   @Override
   public void encodeBody(final MessagingBuffer buffer)
   {
      XidCodecSupport.encodeXid(xid, buffer);
   }

   @Override
   public void decodeBody(final MessagingBuffer buffer)
   {
      xid = XidCodecSupport.decodeXid(buffer);
   }

   @Override
   public boolean equals(final Object other)
   {
      if (other instanceof SessionXAResumeMessage == false)
      {
         return false;
      }

      SessionXAResumeMessage r = (SessionXAResumeMessage)other;

      return super.equals(other) && xid.equals(r.xid);
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
