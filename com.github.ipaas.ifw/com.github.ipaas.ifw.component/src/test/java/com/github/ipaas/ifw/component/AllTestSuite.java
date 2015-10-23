package com.github.ipaas.ifw.component;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.ipaas.ifw.component.client.ice.FwIceDirectComponentClientServiceTest;
import com.github.ipaas.ifw.component.client.ice.usability.IceHealthCheckTest;

@RunWith(Suite.class)
@SuiteClasses({ FwIceDirectComponentClientServiceTest.class, IceHealthCheckTest.class })
public class AllTestSuite {
}
