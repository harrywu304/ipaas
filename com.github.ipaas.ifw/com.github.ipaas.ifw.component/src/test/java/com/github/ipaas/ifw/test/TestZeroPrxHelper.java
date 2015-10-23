// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package com.github.ipaas.ifw.test;

public final class TestZeroPrxHelper extends Ice.ObjectPrxHelperBase implements TestZeroPrx
{
    public String
    sayHello(String name)
    {
        return sayHello(name, null, false);
    }

    public String
    sayHello(String name, java.util.Map<String, String> __ctx)
    {
        return sayHello(name, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    sayHello(String name, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("sayHello");
                __delBase = __getDelegate(false);
                _TestZeroDel __del = (_TestZeroDel)__delBase;
                return __del.sayHello(name, __ctx);
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

    public static TestZeroPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        TestZeroPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (TestZeroPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::test::TestZero"))
                {
                    TestZeroPrxHelper __h = new TestZeroPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static TestZeroPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        TestZeroPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (TestZeroPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::test::TestZero", __ctx))
                {
                    TestZeroPrxHelper __h = new TestZeroPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static TestZeroPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        TestZeroPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::test::TestZero"))
                {
                    TestZeroPrxHelper __h = new TestZeroPrxHelper();
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

    public static TestZeroPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        TestZeroPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::test::TestZero", __ctx))
                {
                    TestZeroPrxHelper __h = new TestZeroPrxHelper();
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

    public static TestZeroPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        TestZeroPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (TestZeroPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                TestZeroPrxHelper __h = new TestZeroPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static TestZeroPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        TestZeroPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            TestZeroPrxHelper __h = new TestZeroPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _TestZeroDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _TestZeroDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, TestZeroPrx v)
    {
        __os.writeProxy(v);
    }

    public static TestZeroPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            TestZeroPrxHelper result = new TestZeroPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
