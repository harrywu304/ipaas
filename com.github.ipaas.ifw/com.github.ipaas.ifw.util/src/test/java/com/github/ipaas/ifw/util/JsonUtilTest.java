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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ifw.util.JsonUtil;
 

/**
 * 
 * @author Chenql
 */
public class JsonUtilTest {

	private String userJson;

	private DemoUser demoUser;
	
    private List<DemoUser> DemoUserList;    
    String demoUserListJson;
    
	@Before
	public void setUp() {
		demoUser = new DemoUser("hutianfa", "guangzhou", "content_guangzhou");
		userJson = "{\"loginTime\":null,\"lastActionTime\":null,\"address\":\"guangzhou\",\"name\":\"hutianfa\",\"id\":0,\"content\":\"content_guangzhou\"}";
		
        DemoUserList = new ArrayList<DemoUser>();
        
        DemoUserList.add(new DemoUser("wusy","guangzhou","content1"));
        DemoUserList.add(new DemoUser("wusyy","guangzhou","content2"));
        DemoUserList.add(new DemoUser("wusyyy","guangzhou","content3"));

        demoUserListJson = "[{\"id\":0,\"name\":\"wusy\",\"address\":\"guangzhou\",\"content\":\"content1\",\"loginTime\":null,\"lastActionTime\":null},{\"id\":0,\"name\":\"wusyy\",\"address\":\"guangzhou\",\"content\":\"content2\",\"loginTime\":null,\"lastActionTime\":null},{\"id\":0,\"name\":\"wusyyy\",\"address\":\"guangzhou\",\"content\":\"content3\",\"loginTime\":null,\"lastActionTime\":null}]";
	}

	@Test
	public final void testToBean() {
		assertEquals(demoUser, JsonUtil.toBean(userJson, DemoUser.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testToBeanException() {
		String errorJson = "{\"sex\":\"male\",\"firstname\":\"hutianfa\",\"id\":0}";
		JsonUtil.toBean(errorJson, DemoUser.class);
	}
	
	@Test
	public final void testToBeanGeneric(){
	    assertEquals(demoUser,JsonUtil.toBean(userJson, new TypeReference<DemoUser>(){}));
	}

  @Test
  public final void testToBeanGeneric2(){
      assertEquals(DemoUserList.get(0),JsonUtil.toBean(demoUserListJson, new TypeReference<List<DemoUser>>(){}).get(0));
  }

	@Test(expected =IllegalArgumentException.class )
	public final void testToBeanGenericException(){
	    JsonUtil.toBean(null, new TypeReference<DemoUser>(){});
	}
	
    @Test(expected =IllegalArgumentException.class )
    public final void testToBeanGenericException2(){
        JsonUtil.toBean("asd", new TypeReference<DemoUser>(){});
    }

    @Test
    public final void testToBeanList(){
        DemoUser user = JsonUtil.toBeanList(demoUserListJson, DemoUser.class).get(0);
        assertEquals(DemoUserList.get(0),JsonUtil.toBeanList(demoUserListJson, DemoUser.class).get(0));
    }
}
