package info.jerrinot.hazelcast;

import com.hazelcast.instance.DefaultNodeInitializer;
import com.hazelcast.storage.DataRef;
import com.hazelcast.storage.Storage;

public class NettyNodeInitializer extends DefaultNodeInitializer {

    @Override
    public Storage<DataRef> getOffHeapStorage() {
        return new NettyStorage();
    }
}

