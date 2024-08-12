package com.ethan.remoting.compressor.support;

import com.ethan.common.enumeration.CompressType;
import com.ethan.remoting.compressor.Compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip compressor.
 *
 * @author Huang Z.Y.
 */
public class Gzip implements Compressor {

    public static final String GZIP = "gzip";

    @Override
    public String getMessageEncoding() {
        return GZIP;
    }

    @Override
    public byte getContentTypeId() {
        return CompressType.getCode(GZIP);
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
