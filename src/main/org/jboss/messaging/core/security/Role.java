/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005-2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */ 

package org.jboss.messaging.core.security;

import java.io.Serializable;

/**
 * A role is used by the security store to define access rights and is configured on a connection factory or destination
 *
 * @author <a href="ataylor@redhat.com">Andy Taylor</a>
 */
public class Role implements Serializable
{
   private static final long serialVersionUID = 3560097227776448872L;

   final private String name;

   final private boolean send;

   final private boolean consume;

   final private boolean createDurableQueue;

   final private boolean deleteDurableQueue;

   final private boolean createTempQueue;

   final private boolean deleteTempQueue;

   final private boolean manage;

   public Role(final String name,
               final boolean send,
               final boolean consume,
               final boolean createDurableQueue,
               final boolean deleteDurableQueue,
               final boolean createTempQueue,
               final boolean deleteTempQueue,
               boolean manage)
   {
      if(name == null)
      {
         throw new NullPointerException("name is null");
      }
      this.name = name;
      this.send = send;
      this.consume = consume;
      this.createDurableQueue = createDurableQueue;
      this.deleteDurableQueue = deleteDurableQueue;
      this.createTempQueue = createTempQueue;
      this.deleteTempQueue = deleteTempQueue;
      this.manage = manage;
   }


   public String getName()
   {
      return name;
   }


   public boolean isSend()
   {
      return send;
   }

   public boolean isConsume()
   {
      return consume;
   }

   public boolean isCreateDurableQueue()
   {
      return createDurableQueue;
   }

   public boolean isDeleteDurableQueue()
   {
      return deleteDurableQueue;
   }

   public boolean isCreateTempQueue()
   {
      return createTempQueue;
   }

   public boolean isDeleteTempQueue()
   {
      return deleteTempQueue;
   }

   public String toString()
   {
      return "Role {name=" + name + ";" +
             "read=" + send + ";" +
             "write=" + consume + ";" +
             "createDurableQueue=" + createDurableQueue + "}" +
            "deleteDurableQueue=" + deleteDurableQueue + "}" +
            "createTempQueue=" + createTempQueue + "}" +
            "deleteTempQueue=" + deleteTempQueue + "}";
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      Role role = (Role) o;

      if (consume != role.consume)
      {
         return false;
      }
      if (createDurableQueue != role.createDurableQueue)
      {
         return false;
      }
      if (createTempQueue != role.createTempQueue)
      {
         return false;
      }
      if (deleteDurableQueue != role.deleteDurableQueue)
      {
         return false;
      }
      if (deleteTempQueue != role.deleteTempQueue)
      {
         return false;
      }
      if (send != role.send)
      {
         return false;
      }
      if (!name.equals(role.name))
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = name.hashCode();
      result = 31 * result + (send ? 1 : 0);
      result = 31 * result + (consume ? 1 : 0);
      result = 31 * result + (createDurableQueue ? 1 : 0);
      result = 31 * result + (deleteDurableQueue ? 1 : 0);
      result = 31 * result + (createTempQueue ? 1 : 0);
      result = 31 * result + (deleteTempQueue ? 1 : 0);
      return result;
   }

   public boolean isManage()
   {
      return manage;
   }
}
