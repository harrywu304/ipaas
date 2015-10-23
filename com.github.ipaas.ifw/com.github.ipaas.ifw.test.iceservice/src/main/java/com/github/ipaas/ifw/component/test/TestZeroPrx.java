// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package com.github.ipaas.ifw.component.test;

public interface TestZeroPrx extends Ice.ObjectPrx
{
    public String sayHello(String name);
    public String sayHello(String name, java.util.Map<String, String> __ctx);
}
