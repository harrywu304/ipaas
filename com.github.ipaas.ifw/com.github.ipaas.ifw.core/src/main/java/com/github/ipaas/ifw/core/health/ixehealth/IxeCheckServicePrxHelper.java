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

public final class IxeCheckServicePrxHelper extends Ice.ObjectPrxHelperBase implements IxeCheckServicePrx
{
    public String
    checkBiz()
    {
        return checkBiz(null, false);
    }

    public String
    checkBiz(java.util.Map<String, String> __ctx)
    {
        return checkBiz(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    checkBiz(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("checkBiz");
                __delBase = __getDelegate(false);
                _IxeCheckServiceDel __del = (_IxeCheckServiceDel)__delBase;
                return __del.checkBiz(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String
    echo(String echoString)
    {
        return echo(echoString, null, false);
    }

    public String
    echo(String echoString, java.util.Map<String, String> __ctx)
    {
        return echo(echoString, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    echo(String echoString, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("echo");
                __delBase = __getDelegate(false);
                _IxeCheckServiceDel __del = (_IxeCheckServiceDel)__delBase;
                return __del.echo(echoString, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public static IxeCheckServicePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        IxeCheckServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IxeCheckServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::ixehealth::IxeCheckService"))
                {
                    IxeCheckServicePrxHelper __h = new IxeCheckServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static IxeCheckServicePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        IxeCheckServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IxeCheckServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::ixehealth::IxeCheckService", __ctx))
                {
                    IxeCheckServicePrxHelper __h = new IxeCheckServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static IxeCheckServicePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        IxeCheckServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::ixehealth::IxeCheckService"))
                {
                    IxeCheckServicePrxHelper __h = new IxeCheckServicePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static IxeCheckServicePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        IxeCheckServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::ixehealth::IxeCheckService", __ctx))
                {
                    IxeCheckServicePrxHelper __h = new IxeCheckServicePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static IxeCheckServicePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        IxeCheckServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IxeCheckServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                IxeCheckServicePrxHelper __h = new IxeCheckServicePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static IxeCheckServicePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        IxeCheckServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            IxeCheckServicePrxHelper __h = new IxeCheckServicePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _IxeCheckServiceDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _IxeCheckServiceDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, IxeCheckServicePrx v)
    {
        __os.writeProxy(v);
    }

    public static IxeCheckServicePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            IxeCheckServicePrxHelper result = new IxeCheckServicePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
