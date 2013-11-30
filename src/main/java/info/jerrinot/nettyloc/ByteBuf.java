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

@SuppressWarnings("ClassMayBeInterface")
public abstract class ByteBuf implements ReferenceCounted {

    /**
     * Returns the number of bytes (octets) this buffer can contain.
     */
    public abstract int capacity();

    /**
     * Returns the {@link ByteBufAllocator} which created this buffer.
     */
    public abstract ByteBufAllocator alloc();


    /**
     * Return the underlying buffer instance if this buffer is a wrapper of another buffer.
     *
     * @return {@code null} if this buffer is not a wrapper
     */
    public abstract ByteBuf unwrap();


    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + dst.length} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf getBytes(int index, byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code dstIndex + length} is greater than
     *            {@code dst.length}
     */
    public abstract ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than
     *            {@code this.capacity}
     * @throws java.io.IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract ByteBuf getBytes(int index, OutputStream out, int length) throws IOException;

    /**
     * Transfers this buffer's data to the specified channel starting at the
     * specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes written out to the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than
     *            {@code this.capacity}
     * @throws java.io.IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int getBytes(int index, GatheringByteChannel out, int length) throws IOException;


    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.length} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf setBytes(int index, byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code srcIndex + length} is greater than {@code src.length}
     */
    public abstract ByteBuf setBytes(int index, byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index} until the source buffer's position
     * reaches its limit.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.remaining()} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf setBytes(int index, ByteBuffer src);

    /**
     * Transfers the content of the specified source stream to this buffer
     * starting at the specified absolute {@code index}.
     *
     * @param length the number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified channel is closed.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     * @throws java.io.IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract int setBytes(int index, InputStream in, int length) throws IOException;

    /**
     * Transfers the content of the specified source channel to this buffer
     * starting at the specified absolute {@code index}.
     *
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified channel is closed.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     * @throws java.io.IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int  setBytes(int index, ScatteringByteChannel in, int length) throws IOException;



    /**
     * Transfers this buffer's data to the specified destination.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code dst.length} is greater than {@code this.readableBytes}
     */
    public abstract ByteBuf readBytes(byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination.
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code length} is greater than {@code this.readableBytes}, or
     *         if {@code dstIndex + length} is greater than {@code dst.length}
     */
    public abstract ByteBuf readBytes(byte[] dst, int dstIndex, int length);


    /**
     * Transfers this buffer's data to the specified stream .
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     * @throws java.io.IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract ByteBuf readBytes(OutputStream out, int length) throws IOException;


    /**
     * Transfers the specified source array's data to this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code src.length} is greater than {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(byte[] src);

    /**
     * Transfers the specified source array's data to this buffer.
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code srcIndex + length} is greater than
     *            {@code src.length}, or
     *         if {@code length} is greater than {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code src.remaining()} is greater than
     *            {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(ByteBuffer src);

    /**
     * Transfers the content of the specified stream to this buffer.
     *
     * @param length the number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified stream
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.writableBytes}
     * @throws java.io.IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract int  writeBytes(InputStream in, int length) throws IOException;


    /**
     * Returns the maximum number of NIO {@link java.nio.ByteBuffer}s that consist this buffer.  Note that {@link #nioBuffers()}
     * or {@link #nioBuffers(int, int)} might return a less number of {@link java.nio.ByteBuffer}s.
     *
     * @return {@code -1} if this buffer has no underlying {@link java.nio.ByteBuffer}.
     *         the number of the underlying {@link java.nio.ByteBuffer}s if this buffer has at least one underlying
     *         {@link java.nio.ByteBuffer}.  Note that this method does not return {@code 0} to avoid confusion.
     *
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    public abstract int nioBufferCount();

    /**
     * Exposes this buffer's readable bytes as an NIO {@link java.nio.ByteBuffer}.  The returned buffer
     * shares the content with this buffer, while changing the position and limit of the returned
     * NIO buffer does not affect the indexes and marks of this buffer.  This method is identical
     * to {@code buf.nioBuffer(buf.readerIndex(), buf.readableBytes())}.  This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.  Please note that the
     * returned NIO buffer will not see the changes of this buffer if this buffer is a dynamic
     * buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    public abstract ByteBuffer nioBuffer();

    /**
     * Exposes this buffer's sub-region as an NIO {@link java.nio.ByteBuffer}.  The returned buffer
     * shares the content with this buffer, while changing the position and limit of the returned
     * NIO buffer does not affect the indexes and marks of this buffer.  This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.  Please note that the
     * returned NIO buffer will not see the changes of this buffer if this buffer is a dynamic
     * buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    public abstract ByteBuffer nioBuffer(int index, int length);


    /**
     * Exposes this buffer's readable bytes as an NIO {@link java.nio.ByteBuffer}'s.  The returned buffer
     * shares the content with this buffer, while changing the position and limit of the returned
     * NIO buffer does not affect the indexes and marks of this buffer. This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.  Please note that the
     * returned NIO buffer will not see the changes of this buffer if this buffer is a dynamic
     * buffer and it adjusted its capacity.
     *
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     */
    public abstract ByteBuffer[] nioBuffers();

    /**
     * Exposes this buffer's bytes as an NIO {@link java.nio.ByteBuffer}'s for the specified index and length
     * The returned buffer shares the content with this buffer, while changing the position and limit
     * of the returned NIO buffer does not affect the indexes and marks of this buffer. This method does
     * not modify {@code readerIndex} or {@code writerIndex} of this buffer.  Please note that the
     * returned NIO buffer will not see the changes of this buffer if this buffer is a dynamic
     * buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     */
    public abstract ByteBuffer[] nioBuffers(int index, int length);

    /**
     * Returns the string representation of this buffer.
     */
    @Override
    public abstract String toString();

    @Override
    public abstract ByteBuf retain();
}
