package info.jerrinot.hazelcast;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionSetter;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.storage.DataRef;
import com.hazelcast.storage.Storage;
import info.jerrinot.nettyloc.ByteBuf;
import info.jerrinot.nettyloc.PooledByteBufAllocator;

public class NettyStorage implements Storage<DataRef> {
    private PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;


    @Override
    public DataRef put(int hash, Data data) {
        byte[] buffer = data.getBuffer();
        ByteBuf byteBuf = allocator.directBuffer(buffer.length);
        byteBuf.writeBytes(buffer);
        ClassDefinition classDefinition = data.getClassDefinition();

        return new NettyDataRef(byteBuf, data.getType(), classDefinition);
    }

    @Override
    public Data get(int hash, DataRef ref) {
        NettyDataRef nettyDataRef = (NettyDataRef) ref;
        ByteBuf byteBuf = null;
        try {
            byteBuf = nettyDataRef.getByteBuf();
            byteBuf.retain();

            int type = nettyDataRef.getType();
            byte[] buffer = new byte[byteBuf.capacity()];
            byteBuf.readBytes(buffer);
            Data data = new Data(type, buffer);
            ClassDefinitionSetter.setClassDefinition(nettyDataRef.getClassDefinition(), data);
            return data;
        } finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }

    @Override
    public void remove(int hash, DataRef ref) {
        NettyDataRef nettyDataRef = (NettyDataRef) ref;
        nettyDataRef.getByteBuf().release();
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
