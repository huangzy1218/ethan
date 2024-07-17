//package com.ethan.rpc.protocol.codec;
//
//import lombok.extern.slf4j.Slf4j;
//
//
///**
// * The ethan implement of {@link Codec}.
// *
// * @author Huang Z,Y.
// */
//@Slf4j
//public class EthanCodec extends ExchangeCodec {
//
////    public Short getMagicCode() {
////        return MAGIC;
////    }
////
////    @Override
////    public void encode(ByteBuf out, Object msg) throws IOException {
////        if (msg instanceof Request) {
////            encodeRequest(channel, buffer, (Request) msg);
////        } else if (msg instanceof Response) {
////            encodeResponse(channel, buffer, (Response) msg);
////        }
////    }
////
////    @Override
////    public Object decode(ByteBuf in) throws Exception {
////        // Must read ByteBuf in order
////        checkMagicNumber(in);
////        checkVersion(in);
////        int fullLength = in.readInt();
////        // Build RpcMessage object
////        byte messageType = in.readByte();
////        byte codecType = in.readByte();
////        byte compressType = in.readByte();
////        int requestId = in.readInt();
////        Message message = Message.builder()
////                .codec(codecType)
////                .requestId(requestId)
////                .messageType(messageType).build();
////        if (messageType == HEARTBEAT_REQUEST_TYPE) {
////            message.setData(PING);
////            return message;
////        }
////        if (messageType == HEARTBEAT_RESPONSE_TYPE) {
////            message.setData(PONG);
////            return message;
////        }
////        int bodyLength = fullLength - HEAD_LENGTH;
////        if (bodyLength > 0) {
////            byte[] bs = new byte[bodyLength];
////            in.readBytes(bs);
////            // Decompress the bytes
////            Compressor compress = CodecSupport.getCompressor(compressType);
////            bs = compress.decompress(bs);
////            // Deserialize the object
////            Serialization serializer = CodecSupport.getSerialization(codecType);
////            ByteArrayInputStream inputStream = new ByteArrayInputStream(bs);
////            ObjectInput objectInput = serializer.deserialize(inputStream);
////            if (messageType == REQUEST_TYPE) {
////                Invocation invocation = objectInput.readObject(Invocation.class);
////                message.setData(invocation);
////            } else {
////                AppResponse result = objectInput.readObject(AppResponse.class);
////                message.setData(result);
////            }
////        }
////        return message;
////
////    }
////
////    private void checkVersion(ByteBuf in) {
////        // Read the version and compare
////        byte version = in.readByte();
////        if (version != VERSION) {
////            throw new RuntimeException("Version isn't compatible" + version);
////        }
////    }
////
////    private void checkMagicNumber(ByteBuf in) {
////        // Read the first 4 bit, which is the magic number, and compare
////        int len = MAGIC_NUMBER.length;
////        byte[] bs = new byte[len];
////        in.readBytes(bs);
////        for (int i = 0; i < len; i++) {
////            if (bs[i] != MAGIC_NUMBER[i]) {
////                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(bs));
////            }
////        }
////    }
//
//}
