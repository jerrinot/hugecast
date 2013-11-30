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

/**
 * Skeletal {@link ByteBufAllocator} implementation to extend.
 */
public abstract class AbstractByteBufAllocator implements ByteBufAllocator {

    private final ByteBuf emptyBuf;

    /**
     * Create new instance
     *
     */
    protected AbstractByteBufAllocator() {
        emptyBuf = new EmptyByteBuf(this);
    }


    @Override
    public ByteBuf directBuffer(int capacity) {
        if (capacity == 0) {
            return emptyBuf;
        }
        validate(capacity);
        return newDirectBuffer(capacity);
    }

    private static void validate(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity: " + capacity + " (expectd: 0+)");
        }
    }

    /**
     * Create a direct {@link ByteBuf} with the given capacity.
     */
    protected abstract ByteBuf newDirectBuffer(int capacity);
}
