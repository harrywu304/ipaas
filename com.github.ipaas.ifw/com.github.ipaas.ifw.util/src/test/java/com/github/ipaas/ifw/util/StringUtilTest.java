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

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.util.StringUtil;

/**
 * 
 * @author Chenql
 */
public class StringUtilTest {

    /** 
     * 方法的描述
     * 
     * @param 参数的描述
     * @return 返回类型的描述
     * @exception 异常信息的描述
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /** 
     * 方法的描述
     * 
     * @param 参数的描述
     * @return 返回类型的描述
     * @exception 异常信息的描述
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /** 
     * 方法的描述
     * 
     * @param 参数的描述
     * @return 返回类型的描述
     * @exception 异常信息的描述
     */
    @Before
    public void setUp() throws Exception {
    }

    /** 
     * 方法的描述
     * 
     * @param 参数的描述
     * @return 返回类型的描述
     * @exception 异常信息的描述
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetExceptionAsStr() {
        //fail("Not yet implemented");
        String eStr = null;
        String expectStr="java.lang.IllegalArgumentException: test";
        Exception e = new IllegalArgumentException("test");
        eStr = StringUtil.getExceptionAsStr(e);
        if(eStr.length() > expectStr.length()){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

}
