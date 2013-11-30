/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package info.jerrinot.nettyloc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class PooledByteBuf<T> extends AbstractReferenceCountedByteBuf {

    private final Recycler.Handle recyclerHandle;

    protected PoolChunk<T> chunk;
    protected long handle;
    protected T memory;
    protected int offset;
    protected int length;
    private int maxLength;

    private ByteBuffer tmpNioBuf;

    protected PooledByteBuf(Recycler.Handle recyclerHandle, int maxCapacity) {
        this.recyclerHandle = recyclerHandle;
    }

    void init(PoolChunk<T> chunk, long handle, int offset, int length, int maxLength) {
        assert handle >= 0;
        assert chunk != null;

        this.chunk = chunk;
        this.handle = handle;
        memory = chunk.memory;
        this.offset = offset;
        this.length = length;
        this.maxLength = maxLength;
        tmpNioBuf = null;
    }

    void initUnpooled(PoolChunk<T> chunk, int length) {
        assert chunk != null;

        this.chunk = chunk;
        handle = 0;
        memory = chunk.memory;
        offset = 0;
        this.length = maxLength = length;
        tmpNioBuf = null;
    }

    @Override
    public final int capacity() {
        return length;
    }

    @Override
    public final ByteBufAllocator alloc() {
        return chunk.arena.parent;
    }

    @Override
    public final ByteBuf unwrap() {
        return null;
    }

    protected final ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = newInternalNioBuffer(memory);
        }
        return tmpNioBuf;
    }

    protected abstract ByteBuffer newInternalNioBuffer(T memory);

    @Override
    protected final void deallocate() {
        if (handle >= 0) {
            final long handle = this.handle;
            this.handle = -1;
            memory = null;
            chunk.arena.free(chunk, handle);
            recycle();
        }
    }

    @SuppressWarnings("unchecked")
    private void recycle() {
        Recycler.Handle recyclerHandle = this.recyclerHandle;
        if (recyclerHandle != null) {
            ((Recycler<Object>) recycler()).recycle(this, recyclerHandle);
        }
    }

    protected abstract Recycler<?> recycler();

    protected final int idx(int index) {
        return offset + index;
    }
}
