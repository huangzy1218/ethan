package com.ethan.remoting.transport.vertx;

import com.ethan.remoting.exchange.codec.ExchangeCodec;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * TCP buffer handler wrapper/<br/>
 * Use recordParser to enhance the original buffer processing capabilities.
 *
 * @author Huang Z.Y.
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    /**
     * Parser, used to solve half-packet and sticky packet problems
     */
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);

    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // Build parser
        RecordParser parser = RecordParser.newFixed(ExchangeCodec.HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // Init
            int size = -1;
            // A complete read (head + body)
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                // 1. Each loop, first read the message header
                if (-1 == size) {
                    // Read message body length
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // Write header information to the result
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 2. Read the message body
                    // Write body information to the result
                    resultBuffer.appendBuffer(buffer);
                    // Spliced into a complete Buffer, processing is performed
                    bufferHandler.handle(resultBuffer);
                    // Reset round
                    parser.fixedSizeMode(ExchangeCodec.HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }

}
