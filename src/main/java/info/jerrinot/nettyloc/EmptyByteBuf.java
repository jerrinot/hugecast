/*
 * Copyright 2013 The Netty Project
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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * An empty {@link ByteBuf} whose capacity and maximum capacity are all {@code 0}.
 */
public final class EmptyByteBuf extends ByteBuf {

    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocateDirect(0);

    private final ByteBufAllocator alloc;
    private final String str;

    public EmptyByteBuf(ByteBufAllocator alloc) {
        this(alloc, ByteOrder.BIG_ENDIAN);
    }

    private EmptyByteBuf(ByteBufAllocator alloc, ByteOrder order) {
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }

        this.alloc = alloc;
        str = getClass().getSimpleName() + (order == ByteOrder.BIG_ENDIAN? "BE" : "LE");
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public ByteBufAllocator alloc() {
        return alloc;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return checkIndex(index, dst.length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) {
        return checkIndex(index, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) {
        checkIndex(index, length);
        return 0;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return checkIndex(index, src.length);
    }


    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return checkIndex(index, src.remaining());
    }

    @Override
    public int setBytes(int index, InputStream in, int length) {
        checkIndex(index, length);
        return 0;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) {
        checkIndex(index, length);
        return 0;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return checkLength(dst.length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return checkLength(src.length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return checkLength(src.remaining());
    }

    @Override
    public int writeBytes(InputStream in, int length) {
        checkLength(length);
        return 0;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer() {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        return nioBuffer();
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return new ByteBuffer[] { EMPTY_BYTE_BUFFER };
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        checkIndex(index, length);
        return nioBuffers();
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public ByteBuf retain() {
        return this;
    }

    @Override
    public boolean release() {
        return false;
    }

    private ByteBuf checkIndex(int index, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (index != 0 || length != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    private ByteBuf checkLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length + " (expected: >= 0)");
        }
        if (length != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }
}
