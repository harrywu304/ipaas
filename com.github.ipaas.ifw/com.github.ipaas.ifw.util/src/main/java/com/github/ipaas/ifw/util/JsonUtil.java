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
package com.github.ipaas.ifw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 *JSON 工具类
 * @author Chenql
 */
public final class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static JsonFactory jsonFactory = new JsonFactory();

    private static ObjectMapper mapper = null;

    static {
        jsonFactory.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jsonFactory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper = new ObjectMapper(jsonFactory);
    }

    /**
     * 获取jackson json lib的ObjectMapper对象
     * 
     * @return -- ObjectMapper对象
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * 获取jackson json lib的JsonFactory对象
     * 
     * @return -- JsonFactory对象
     */
    public static JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    /**
     * 将json转成java bean
     * 
     * @param <T>
     *            -- 多态类型
     * @param json
     *            -- json字符串
     * @param clazz
     *            -- java bean类型(Class)
     * @return -- java bean对象
     */
    public static <T> T toBean(String json, Class<T> clazz) {

        T rtv = null;
        try {
            rtv = mapper.readValue(json, clazz);
        } catch (Exception ex) {
            throw new IllegalArgumentException("json字符串转成java bean异常", ex);
        }
        return rtv;
    }

    /**
     * 将java bean转成json
     * 
     * @param bean
     *            -- java bean
     * @return -- json 字符串
     */
    public static String toJson(Object bean) {

        String rtv = null;
        try {
            rtv = mapper.writeValueAsString(bean);
        } catch (Exception ex) {
            throw new IllegalArgumentException("java bean转成json字符串异常", ex);
        }
        return rtv;
    }

    /**
     * 将json转成java bean
     * @author 胡添发 2012-01-07 
     * 
     * @param <T>
     *            -- 多态类型,可以是简单的,也可以是复合的.例如,可以是TypeReference&ltDemoUser&gt,
     *            也可以是TypeReference&ltList&ltDemoUser&gt&gt
     * @param json
     *            -- json字符串
     * @throws IllegalArgumentException
     *             如果参数json为null或者转换过程出错.
     * @return -- java bean对象,可以是简单bean对象,也可以是对象List.例如,List&ltDemoUser&gt
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String json, TypeReference<T> refer) {
        if (StringUtil.isNullOrBlank(json)) {
            throw new IllegalArgumentException("json can not null");
        }
        T entity = null;
        try {
            entity = (T)mapper.reader(refer).readValue(json);

        } catch (Exception e) {
            throw new IllegalArgumentException("json字符串转成java bean异常", e);
        }
        return entity;
    }

    /**
     * 
     * 把数组类型的json转为list&ltBean&gt
     * 
     * @author 胡添发 2012-01-07
     * @param <T>
     *            -- 多态类型
     * @param json
     *            -- json字符串
     * @param clazz
     *            -- java bean类型(Class)
     * @throws IllegalArgumentException
     *             如果参数json为null,或者转换过程出错.
     * 
     * @return List&ltT&gt
     */
    public static <T> List<T> toBeanList(String json, Class<T> clazz) {
        if (StringUtil.isNullOrBlank(json)) {
            throw new IllegalArgumentException("json can not null");
        }
        List<T> result = null;
        try {
            JsonNode jn = mapper.readTree(json);
            result = new ArrayList<T>();
            if (jn.isArray()) {
                Iterator<JsonNode> iter = jn.iterator();
                while (iter.hasNext()) {
                    parseBeanAddToList(iter.next(), result, clazz);
                }
            } else {
                parseBeanAddToList(jn, result, clazz);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("json字符串转成java List<bean>异常", e);
        }
        return result;
    }

    private static <T> void parseBeanAddToList(JsonNode js, List<T> list, Class<T> clazz) throws JsonParseException,
            JsonMappingException, IOException {
        T rtv = mapper.readValue(js.toString(), clazz);
        list.add(rtv);
    }

}