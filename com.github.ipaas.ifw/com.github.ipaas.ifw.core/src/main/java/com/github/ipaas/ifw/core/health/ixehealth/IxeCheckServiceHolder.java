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

public final class IxeCheckServiceHolder
{
    public
    IxeCheckServiceHolder()
    {
    }

    public
    IxeCheckServiceHolder(IxeCheckService value)
    {
        this.value = value;
    }

    public class Patcher implements IceInternal.Patcher
    {
        public void
        patch(Ice.Object v)
        {
            try
            {
                value = (IxeCheckService)v;
            }
            catch(ClassCastException ex)
            {
                IceInternal.Ex.throwUOE(type(), v.ice_id());
            }
        }

        public String
        type()
        {
            return "::ixehealth::IxeCheckService";
        }
    }

    public Patcher
    getPatcher()
    {
        return new Patcher();
    }

    public IxeCheckService value;
}
