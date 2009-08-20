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

package org.hornetq.utils;

import java.util.StringTokenizer;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 * @version <tt>$Revision$</tt>
 *
 * $Id$
 */
public class JNDIUtil
{
   // Constants -----------------------------------------------------

   // Static --------------------------------------------------------

   /**
    * Create a context path recursively.
    */
   public static Context createContext(Context c, String path) throws NamingException
   {
      Context crtContext = c;
      for(StringTokenizer st = new StringTokenizer(path, "/"); st.hasMoreTokens(); )
      {
         String tok = st.nextToken();

         try
         {
            Object o = crtContext.lookup(tok);
            if (!(o instanceof Context))
            {
               throw new NamingException("Path " + path + " overwrites and already bound object");
            }
            crtContext = (Context)o;
            continue;
         }
         catch(NameNotFoundException e)
         {
            // OK
         }
         crtContext = crtContext.createSubcontext(tok);
      }
      return crtContext;
   }

   public static void tearDownRecursively(Context c) throws Exception
   {
      for(NamingEnumeration ne = c.listBindings(""); ne.hasMore(); )
      {
         Binding b = (Binding)ne.next();
         String name = b.getName();
         Object object = b.getObject();
         if (object instanceof Context)
         {
            tearDownRecursively((Context)object);
         }
         c.unbind(name);
      }
   }

   /**
    * Context.rebind() requires that all intermediate contexts and the target context (that named by
    * all but terminal atomic component of the name) must already exist, otherwise
    * NameNotFoundException is thrown. This method behaves similar to Context.rebind(), but creates
    * intermediate contexts, if necessary.
    */
   public static void rebind(Context c, String jndiName, Object o) throws NamingException
   {
      Context context = c;
      String name = jndiName;

      int idx = jndiName.lastIndexOf('/');
      if (idx != -1)
      {
         context = createContext(c, jndiName.substring(0, idx));
         name = jndiName.substring(idx + 1);
      }
      boolean failed=false;
      try
      {
         context.rebind(name,o);
      }
      catch (Exception ignored)
      {
         failed=true;
      }
      if (failed)
      {
         context.bind(name, o);
      }
   }

   // Attributes ----------------------------------------------------
   
   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------

   // Package protected ---------------------------------------------
   
   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------   
}
