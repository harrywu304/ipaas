/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */  
package com.github.ipaas.ifw.core.extension;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;

/**
 * @author wudie
 * 
 */
public class Extensions
{
  private static Logger logger = LoggerFactory.getLogger(Extensions.class);

  private static ConcurrentHashMap<String, Object> EXT_INSTANCE_CACHE = new ConcurrentHashMap();

  public static <T> T loadExtension(Class<T> clazz)
  {
    String clazzName = clazz.getName();
    Object inst = EXT_INSTANCE_CACHE.get(clazzName);
    if (inst == null) {
      ServiceLoader serviceLoader = ServiceLoader.load(clazz);
      Iterator serviceIterator = serviceLoader.iterator();

      if (serviceIterator.hasNext()) {
        inst = serviceIterator.next();
        EXT_INSTANCE_CACHE.putIfAbsent(clazzName, inst);
        logger.info("获得" + clazz.getName() + "的实现类" + inst.getClass().getName() + ".");
      } else {
        throw new FwRuntimeException("未找到" + clazz.getName() + "合适的实现");
      }
    }
    return (T) inst;
  }
}
