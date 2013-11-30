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
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;


/**
 * A skeletal implementation of a buffer.
 */
public abstract class AbstractByteBuf extends ByteBuf {

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        getBytes(index, dst, 0, dst.length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        setBytes(index, src, 0, src.length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        checkReadableBytes(length);
        getBytes(0, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        readBytes(dst, 0, dst.length);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        checkReadableBytes(length);
        getBytes(0, out, length);
        return this;
    }


    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        setBytes(0, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        writeBytes(src, 0, src.length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        int length = src.remaining();
        setBytes(0, src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length)
            throws IOException {
        return setBytes(0, in, length);
    }

    @Override
    public ByteBuffer nioBuffer() {
        return nioBuffer(0, capacity());
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return nioBuffers(0, capacity());
    }


    @Override
    public String toString() {
        if (refCnt() == 0) {
            return getClass().getSimpleName() + "(freed)";
        }

        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append(", cap: ");
        buf.append(capacity());

        ByteBuf unwrapped = unwrap();
        if (unwrapped != null) {
            buf.append(", unwrapped: ");
            buf.append(unwrapped);
        }
        buf.append(')');
        return buf.toString();
    }

    protected final void checkIndex(int index) {
        ensureAccessible();
        if (index < 0 || index >= capacity()) {
            throw new IndexOutOfBoundsException(String.format(
                    "index: %d (expected: range(0, %d))", index, capacity()));
        }
    }

    protected final void checkIndex(int index, int fieldLength) {
        ensureAccessible();
        if (fieldLength < 0) {
            throw new IllegalArgumentException("length: " + fieldLength + " (expected: >= 0)");
        }
        if (index < 0 || index > capacity() - fieldLength) {
            throw new IndexOutOfBoundsException(String.format(
                    "index: %d, length: %d (expected: range(0, %d))", index, fieldLength, capacity()));
        }
    }

    protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
        checkIndex(index, length);
        if (srcIndex < 0 || srcIndex > srcCapacity - length) {
            throw new IndexOutOfBoundsException(String.format(
                    "srcIndex: %d, length: %d (expected: range(0, %d))", srcIndex, length, srcCapacity));
        }
    }

    protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
        checkIndex(index, length);
        if (dstIndex < 0 || dstIndex > dstCapacity - length) {
            throw new IndexOutOfBoundsException(String.format(
                    "dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dstCapacity));
        }
    }

    /**
     * Should be called by every method that tries to access the buffers content to check
     * if the buffer was released before.
     */
    protected final void ensureAccessible() {
        if (refCnt() == 0) {
            throw new IllegalReferenceCountException(0);
        }
    }

    protected final void checkReadableBytes(int minimumReadableBytes) {
        ensureAccessible();
        if (minimumReadableBytes < 0) {
            throw new IllegalArgumentException("minimumReadableBytes: " + minimumReadableBytes + " (expected: >= 0)");
        }
    }
}
