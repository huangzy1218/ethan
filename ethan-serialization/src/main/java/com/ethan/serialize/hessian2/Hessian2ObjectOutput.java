package com.ethan.serialize.hessian2;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.ethan.serialize.api.Cleanable;
import com.ethan.serialize.api.ObjectOutput;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Hessian2 object output implementation.
 *
 * @author Huang Z.Y.
 */
public class Hessian2ObjectOutput implements ObjectOutput, Cleanable {

    private final Hessian2Output mH2o;

    private final Hessian2FactoryManager hessian2FactoryManager;

    public Hessian2ObjectOutput(OutputStream os) {
        this.hessian2FactoryManager = new Hessian2FactoryManager();
        this.mH2o = new Hessian2Output(os);
        this.mH2o.setSerializerFactory(hessian2FactoryManager.getSerializerFactory());
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        mH2o.writeBoolean(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        mH2o.writeInt(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        mH2o.writeInt(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        mH2o.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        mH2o.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        mH2o.writeDouble(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        mH2o.writeDouble(v);
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        mH2o.writeBytes(b);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        mH2o.writeBytes(b, off, len);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        mH2o.writeString(v);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        mH2o.writeObject(obj);
    }

    @Override
    public void flushBuffer() throws IOException {
        mH2o.flushBuffer();
    }

    @Override
    public void cleanup() {
        if (mH2o != null) {
            mH2o.reset();
        }
    }

}

