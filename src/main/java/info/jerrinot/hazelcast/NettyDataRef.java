package info.jerrinot.hazelcast;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.storage.DataRef;
import info.jerrinot.nettyloc.ByteBuf;

public class NettyDataRef implements DataRef {
    private static final int SIZE = 10; //TODO: Replace dummy values
    private static final int HEAP_COST = 20;; //TODO: Replace dummy values

    private ByteBuf byteBuf;
    private int type;
    private ClassDefinition classDefinition;

    public NettyDataRef(ByteBuf byteBuf, int type, ClassDefinition classDefinition) {
        this.byteBuf = byteBuf;
        this.type = type;
        this.classDefinition = classDefinition;
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public int heapCost() {
        return HEAP_COST;
    }

    public int getType() {
        return type;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }
}
