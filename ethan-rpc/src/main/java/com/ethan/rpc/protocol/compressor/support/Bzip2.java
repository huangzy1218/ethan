package com.ethan.rpc.protocol.compressor.support;

import com.ethan.rpc.RpcException;
import com.ethan.rpc.protocol.compressor.Compressor;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Bzip2 compressor, faster compression efficiency.
 *
 * @author Huang Z.Y.
 * @link https://commons.apache.org/proper/commons-compress/
 */
public class Bzip2 implements Compressor {

    public static final String BZIP2 = "bzip2";

    @Override
    public String getMessageEncoding() {
        return BZIP2;
    }

    @Override
    public byte[] compress(byte[] payloadByteArr) throws RpcException {
        if (null == payloadByteArr || 0 == payloadByteArr.length) {
            return new byte[0];
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BZip2CompressorOutputStream cos;
        try {
            cos = new BZip2CompressorOutputStream(out);
            cos.write(payloadByteArr);
            cos.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return out.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] payloadByteArr) {
        if (null == payloadByteArr || 0 == payloadByteArr.length) {
            return new byte[0];
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(payloadByteArr);
        try {
            BZip2CompressorInputStream unZip = new BZip2CompressorInputStream(in);
            byte[] buffer = new byte[2048];
            int n;
            while ((n = unZip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return out.toByteArray();
    }

}
