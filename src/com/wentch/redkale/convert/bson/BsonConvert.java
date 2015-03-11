/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wentch.redkale.convert.bson;

import com.wentch.redkale.convert.Convert;
import com.wentch.redkale.convert.Factory;
import com.wentch.redkale.util.ObjectPool;
import java.lang.reflect.Type;

/**
 *
 * @author zhangjx
 */
public final class BsonConvert extends Convert<BsonReader, BsonWriter> {

    private static final ObjectPool<BsonReader> readerPool = new ObjectPool<>(Integer.getInteger("convert.bson.pool.size", 16), BsonReader.class);

    private static final ObjectPool<BsonWriter> writerPool = new ObjectPool<>(Integer.getInteger("convert.bson.pool.size", 16), BsonWriter.class);

    protected BsonConvert(Factory<BsonReader, BsonWriter> factory) {
        super(factory);
    }

    public <T> T convertFrom(final Type type, final byte[] bytes) {
        if (bytes == null) return null;
        return convertFrom(type, bytes, 0, bytes.length);
    }

    public <T> T convertFrom(final Type type, final byte[] bytes, int start, int len) {
        if (type == null) return null;
        final BsonReader in = readerPool.poll();
        in.setBytes(bytes, start, len);
        @SuppressWarnings("unchecked")
        T rs = (T) factory.loadDecoder(type).convertFrom(in);
        readerPool.offer(in);
        return rs;
    }

    public byte[] convertTo(final Type type, Object value) {
        if (type == null) return null;
        final BsonWriter out = writerPool.poll();
        factory.loadEncoder(type).convertTo(out, value);
        byte[] result = out.toArray();
        writerPool.offer(out);
        return result;
    }

    public byte[] convertTo(Object value) {
        if (value == null) {
            final BsonWriter out = writerPool.poll();
            out.writeNull();
            byte[] result = out.toArray();
            writerPool.offer(out);
            return result;
        }
        return convertTo(value.getClass(), value);
    }
}
