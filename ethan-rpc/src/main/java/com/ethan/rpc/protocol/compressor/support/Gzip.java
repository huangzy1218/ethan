package com.ethan.rpc.protocol.compressor.support;

import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.rpc.protocol.compressor.Decompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip compressor.
 *
 * @author Huang Z.Y.
 */
public class Gzip implements Compressor, Decompressor {

    public static final String GZIP = "gzip";

    @Override
    public String getMessageEncoding() {
        return GZIP;
    }

    @Override
    public byte[] compress(byte[] payloadByteArr) {
        if (null == payloadByteArr || 0 == payloadByteArr.length) {
            return new byte[0];
        }

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteOutStream)) {
            gzipOutputStream.write(payloadByteArr);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return byteOutStream.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] payloadByteArr) {
        if (null == payloadByteArr || 0 == payloadByteArr.length) {
            return new byte[0];
        }

        ByteArrayInputStream byteInStream = new ByteArrayInputStream(payloadByteArr);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteInStream)) {
            int readByteNum;
            byte[] bufferArr = new byte[256];
            while ((readByteNum = gzipInputStream.read(bufferArr)) >= 0) {
                byteOutStream.write(bufferArr, 0, readByteNum);
            }
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return byteOutStream.toByteArray();
    }

}
