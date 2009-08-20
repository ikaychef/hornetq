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

package org.hornetq.tests.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.jboss.util.naming.Util;

/**
 * used by the default context when running in embedded local configuration
 * @author <a href="ataylor@redhat.com">Andy Taylor</a>
 */
public class NonSerializableFactory implements ObjectFactory
{

    public NonSerializableFactory()
    {
    }

    public static void unbind(Context ctx, String strName)
            throws NamingException
    {
        Name name = ctx.getNameParser("").parse(strName);
        int size = name.size();
        String atom = name.get(size - 1);
        Context parentCtx = Util.createSubcontext(ctx, name.getPrefix(size - 1));
        String key = (new StringBuilder()).append(parentCtx.getNameInNamespace()).append("/").append(atom).toString();
        getWrapperMap().remove(key);
        Util.unbind(ctx, strName);
    }


    public static void rebind(Context ctx, String strName, Object value)
            throws NamingException
    {
        Name name = ctx.getNameParser("").parse(strName);
        int size = name.size();
        String atom = name.get(size - 1);
        Context parentCtx = Util.createSubcontext(ctx, name.getPrefix(size - 1));
        String key = (new StringBuilder()).append(parentCtx.getNameInNamespace()).append("/").append(atom).toString();
        getWrapperMap().put(key, value);
        String className = value.getClass().getName();
        String factory = NonSerializableFactory.class.getName();
        StringRefAddr addr = new StringRefAddr("nns", key);
        Reference memoryRef = new Reference(className, addr, factory, null);
        parentCtx.rebind(atom, memoryRef);
    }

    public static void bind(Context ctx, String strName, Object value)
            throws NamingException
    {
        Name name = ctx.getNameParser("").parse(strName);
        int size = name.size();
        String atom = name.get(size - 1);
        Context parentCtx = Util.createSubcontext(ctx, name.getPrefix(size - 1));
        String key = (new StringBuilder()).append(parentCtx.getNameInNamespace()).append("/").append(atom).toString();
        getWrapperMap().put(key, value);
        String className = value.getClass().getName();
        String factory = NonSerializableFactory.class.getName();
        StringRefAddr addr = new StringRefAddr("nns", key);
        Reference memoryRef = new Reference(className, addr, factory, null);

        parentCtx.bind(atom, memoryRef);
    }

   public static Object lookup(String name)  throws NamingException
    {
        if(getWrapperMap().get(name) == null)
        {
           throw new NamingException(name + " not found");
        }
        return getWrapperMap().get(name);
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable env)
            throws Exception
    {
        Reference ref = (Reference) obj;
        RefAddr addr = ref.get("nns");
        String key = (String) addr.getContent();
        return getWrapperMap().get(key);
    }

   public static Map getWrapperMap()
   {
      return wrapperMap;
   }

    private static Map wrapperMap = Collections.synchronizedMap(new HashMap());
}