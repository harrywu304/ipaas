// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package com.github.ipaas.ifw.core.health.ixehealth;

public interface IxeCheckServicePrx extends Ice.ObjectPrx
{
    public String echo(String echoString);
    public String echo(String echoString, java.util.Map<String, String> __ctx);

    public String checkBiz();
    public String checkBiz(java.util.Map<String, String> __ctx);
}
