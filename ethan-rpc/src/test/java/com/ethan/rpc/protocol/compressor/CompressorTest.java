package com.ethan.rpc.protocol.compressor;

import com.ethan.common.extension.ExtensionLoader;
import com.ethan.rpc.model.FrameworkModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Huang Z.Y.
 */
class CompressorTest {

    @Test
    void compressTest() {
        ExtensionLoader<Compressor> extensionLoader =
                FrameworkModel.defaultModel().getExtensionLoader(Compressor.class);
        Compressor compressor = extensionLoader.getExtension("identity");
        byte[] bytes = compressor.compress("Hello World".getBytes());
        byte[] decompressBytes = compressor.decompress(bytes);
        Assertions.assertEquals("Hello World", new String(decompressBytes));
    }

}