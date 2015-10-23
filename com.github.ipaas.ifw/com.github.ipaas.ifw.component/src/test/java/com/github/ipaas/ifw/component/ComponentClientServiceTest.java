package com.github.ipaas.ifw.component;

import static org.junit.Assert.assertNotNull;

import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;

import com.github.ipaas.ifw.component.ComponentClientService;
import com.github.ipaas.ifw.component.test.TestZeroPrx;

  

public class ComponentClientServiceTest {
	
	protected ComponentClientService ccs;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetProxyStringClassOfK() {
		TestZeroPrx prx = ccs.getProxy(TestZeroPrx.class);
		prx.ice_ping();	
		assertNotNull(prx);
	}
 
	
	@Test
	public void testGetProxyClassOfKInt() {
		TestZeroPrx prx = ccs.getProxy(TestZeroPrx.class, 1000);
		prx.ice_ping();
		assertNotNull(prx);
	}

}
