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
package com.github.ipaas.ifw.core.config;

import org.junit.Assert;
import org.junit.Test;

import com.github.ipaas.ifw.core.config.Config;
import com.github.ipaas.ifw.core.config.FwConfigService;

/**
 * FwConfigService单元测试
 * 
 * @author wudie
 * 
 */
public class FwConfigServiceTest {

	@Test
	public void testGetExistConfig() {
		Assert.assertNotNull(FwConfigService.getConfig("system_properties"));
	}

	@Test
	public void testGetNotExistCondig() {
		Assert.assertNull(FwConfigService.getConfig("system_properties1"));
	}

	@Test
	public void testGetSystemConfig() {
		Config config = FwConfigService.getSystemConfig();
		Assert.assertEquals("spring", config.getItem("service.provider"));
	}

	@Test
	public void testGetAppConfig() {
		Config config = FwConfigService.getAppConfig();
		Assert.assertEquals("hello", config.getItem("myDemoConfig"));
	}

	@Test
	public void testGetConfigWithDefault() {
		Config config = FwConfigService.getAppConfig();
		Assert.assertNull(config.getItem("itemNoexist"));
		Assert.assertEquals("12234", config.getItem("itemNoexist", "12234"));
	}

}
