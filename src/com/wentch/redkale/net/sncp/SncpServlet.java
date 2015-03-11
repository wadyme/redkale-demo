/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wentch.redkale.net.sncp;

import com.wentch.redkale.net.Servlet;
import com.wentch.redkale.util.AnyValue;

/**
 *
 * @author zhangjx
 */
public abstract class SncpServlet implements Servlet<SncpRequest, SncpResponse> {

    AnyValue conf;

    public abstract long getNameid();

    public abstract long getServiceid();

    @Override
    public final boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public final int hashCode() {
        return this.getClass().hashCode();
    }
}
