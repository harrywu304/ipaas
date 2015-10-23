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

public interface _IxeCheckServiceOperations
{
    String echo(String echoString, Ice.Current __current);

    String checkBiz(Ice.Current __current);
}
