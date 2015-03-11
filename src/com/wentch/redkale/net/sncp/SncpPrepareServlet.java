/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wentch.redkale.net.sncp;

import com.wentch.redkale.net.PrepareServlet;
import com.wentch.redkale.net.Context;
import com.wentch.redkale.util.AnyValue;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 *
 * @author zhangjx
 */
public class SncpPrepareServlet extends PrepareServlet<SncpRequest, SncpResponse> {

    private static final ByteBuffer pongBuffer = ByteBuffer.wrap("PONG".getBytes()).asReadOnlyBuffer();

    private final Map<Long, Map<Long, SncpServlet>> maps = new HashMap<>();

    private final Map<Long, SncpServlet> singlemaps = new HashMap<>();

    public void addSncpServlet(SncpServlet servlet) {
        if (servlet.getNameid() == 0) {
            singlemaps.put(servlet.getServiceid(), servlet);
        } else {
            Map<Long, SncpServlet> m = maps.get(servlet.getServiceid());
            if (m == null) {
                m = new HashMap<>();
                maps.put(servlet.getServiceid(), m);
            }
            m.put(servlet.getNameid(), servlet);
        }
    }

    @Override
    public void init(Context context, AnyValue config) {
        Collection<Map<Long, SncpServlet>> values = this.maps.values();
        values.stream().forEach((en) -> {
            en.values().stream().forEach(s -> s.init(context, s.conf));
        });
    }

    @Override
    public void destroy(Context context, AnyValue config) {
        Collection<Map<Long, SncpServlet>> values = this.maps.values();
        values.stream().forEach((en) -> {
            en.values().stream().forEach(s -> s.destroy(context, s.conf));
        });
    }

    @Override
    public void execute(SncpRequest request, SncpResponse response) throws IOException {
        if (request.isPing()) {
            response.finish(pongBuffer.duplicate());
            return;
        }
        SncpServlet servlet;
        if (request.getNameid() == 0) {
            servlet = singlemaps.get(request.getServiceid());
        } else {
            Map<Long, SncpServlet> m = maps.get(request.getServiceid());
            if (m == null) {
                response.finish(SncpResponse.RETCODE_ILLSERVICEID, null);  //无效serviceid
                return;
            }
            servlet = m.get(request.getNameid());
        }
        if (servlet == null) {
            response.finish(SncpResponse.RETCODE_ILLNAMEID, null);  //无效nameid
        } else {
            servlet.execute(request, response);
        }
    }

}
