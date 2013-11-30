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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledDirectByteBuf extends AbstractReferenceCountedByteBuf {

    private final ByteBufAllocator alloc;

    private ByteBuffer buffer;
    private ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;

    /**
     * Creates a new direct buffer.
     *
     * @param capacity the capacity of the underlying direct buffer
     */
    protected UnpooledDirectByteBuf(ByteBufAllocator alloc, int capacity) {
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity: " + capacity);
        }

        this.alloc = alloc;
        setByteBuffer(ByteBuffer.allocateDirect(capacity));
    }

    private void setByteBuffer(ByteBuffer buffer) {
        ByteBuffer oldBuffer = this.buffer;
        if (oldBuffer != null) {
            if (doNotFree) {
                doNotFree = false;
            } else {
                PlatformDependent.freeDirectBuffer(oldBuffer);
            }
        }

        this.buffer = buffer;
        tmpNioBuf = null;
        capacity = buffer.remaining();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public ByteBufAllocator alloc() {
        return alloc;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        getBytes(index, dst, dstIndex, length, false);
        return this;
    }

    private void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
        checkDstIndex(index, length, dstIndex, dst.length);

        if (dstIndex < 0 || dstIndex > dst.length - length) {
            throw new IndexOutOfBoundsException(String.format(
                    "dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dst.length));
        }

        ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = buffer.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.get(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        checkReadableBytes(length);
        getBytes(0, dst, dstIndex, length, true);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.length);
        ByteBuffer tmpBuf = internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.put(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        ensureAccessible();
        ByteBuffer tmpBuf = internalNioBuffer();
        if (src == tmpBuf) {
            src = src.duplicate();
        }

        tmpBuf.clear().position(index).limit(index + src.remaining());
        tmpBuf.put(src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        getBytes(index, out, length, false);
        return this;
    }

    private void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        ensureAccessible();
        if (length == 0) {
            return;
        }

        if (buffer.hasArray()) {
            out.write(buffer.array(), index + buffer.arrayOffset(), length);
        } else {
            byte[] tmp = new byte[length];
            ByteBuffer tmpBuf;
            if (internal) {
                tmpBuf = internalNioBuffer();
            } else {
                tmpBuf = buffer.duplicate();
            }
            tmpBuf.clear().position(index);
            tmpBuf.get(tmp);
            out.write(tmp);
        }
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        checkReadableBytes(length);
        getBytes(0, out, length, true);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        ensureAccessible();
        if (length == 0) {
            return 0;
        }

        ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = buffer.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        ensureAccessible();
        if (buffer.hasArray()) {
            return in.read(buffer.array(), buffer.arrayOffset() + index, length);
        } else {
            byte[] tmp = new byte[length];
            int readBytes = in.read(tmp);
            if (readBytes <= 0) {
                return readBytes;
            }
            ByteBuffer tmpBuf = internalNioBuffer();
            tmpBuf.clear().position(index);
            tmpBuf.put(tmp, 0, readBytes);
            return readBytes;
        }
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        ensureAccessible();
        ByteBuffer tmpBuf = internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpNioBuf);
        } catch (ClosedChannelException e) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[] { nioBuffer(index, length) };
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = buffer.duplicate();
        }
        return tmpNioBuf;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return (ByteBuffer) buffer.duplicate().position(index).limit(index + length);
    }

    @Override
    protected void deallocate() {
        ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            return;
        }

        this.buffer = null;

        if (!doNotFree) {
            PlatformDependent.freeDirectBuffer(buffer);
        }
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}
