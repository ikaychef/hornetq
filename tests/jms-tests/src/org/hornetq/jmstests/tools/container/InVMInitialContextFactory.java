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

package org.hornetq.jmstests.tools.container;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * An in-VM JNDI InitialContextFactory. Lightweight JNDI implementation used for testing.

 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 * @version <tt>$Revision: 2868 $</tt>
 *
 * $Id: InVMInitialContextFactory.java 2868 2007-07-10 20:22:16Z timfox $
 */
public class InVMInitialContextFactory implements InitialContextFactory
{
   // Constants -----------------------------------------------------

   // Static --------------------------------------------------------

   private static Map initialContexts;

   static
   {
      reset();
   }

   public static Hashtable getJNDIEnvironment()
   {
      return getJNDIEnvironment(0);
   }

   /**
    * @return the JNDI environment to use to get this InitialContextFactory.
    */
   public static Hashtable getJNDIEnvironment(int serverIndex)
   {
      Hashtable env = new Hashtable();
      env.put("java.naming.factory.initial",
              "org.hornetq.jmstests.tools.container.InVMInitialContextFactory");
      env.put("java.naming.provider.url", "org.jboss.naming:org.jnp.interface");
      //env.put("java.naming.factory.url.pkgs", "");
      env.put(Constants.SERVER_INDEX_PROPERTY_NAME, Integer.toString(serverIndex));
      return env;
   }

   // Attributes ----------------------------------------------------

   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   public Context getInitialContext(Hashtable environment) throws NamingException      
   {
      // try first in the environment passed as argument ...
      String s = (String)environment.get(Constants.SERVER_INDEX_PROPERTY_NAME);

      if (s == null)
      {
         // ... then in the global environment
         s = System.getProperty(Constants.SERVER_INDEX_PROPERTY_NAME);

         if (s == null)
         {
            //try the thread name
            String tName = Thread.currentThread().getName();
            if(tName.contains("server"))
            {
               s = tName.substring(6);
            }
            if(s == null)
               throw new NamingException("Cannot figure out server index!");
         }
      }

      int serverIndex;

      try
      {
         serverIndex = Integer.parseInt(s);
      }
      catch(Exception e)
      {
         throw new NamingException("Failure parsing \"" +
                                   Constants.SERVER_INDEX_PROPERTY_NAME +"\". " +
                                   s + " is not an integer");
      }

   	//Note! This MUST be synchronized
   	synchronized (initialContexts)
   	{
	   	      
	      InVMContext ic = (InVMContext)initialContexts.get(new Integer(serverIndex));
	
	      if (ic == null)
	      {
	         ic = new InVMContext(s);
	         ic.bind("java:/", new InVMContext(s));
	         initialContexts.put(new Integer(serverIndex), ic);
	      }
	
	      return ic;
   	}
   }
   
   public static void reset()
   {
       initialContexts = new HashMap();
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------   
}
