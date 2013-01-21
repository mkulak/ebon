/*
 * Copyright 2013 Michael Kulak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xap4o;


import java.nio.ByteBuffer;

public class ExtendableByteBuffer {
    private static final double SIZE_MULT = 1.5;
    private ByteBuffer buf = ByteBuffer.allocate(1024 * 10);

    public void putInt(int a) {
        ensureFreeSpace(4);
        buf.putInt(a);
    }
    public void putInt(int pos, int value) {
        ensureFreeSpace(4);
        buf.putInt(pos, value);
    }
    public void put(byte a) {
        ensureFreeSpace(1);
        buf.put(a);
    }
    public void putLong(long a) {
        ensureFreeSpace(8);
        buf.putLong(a);
    }

    public void putDouble(double a) {
        ensureFreeSpace(8);
        buf.putDouble(a);
    }

    public void putFloat(float a) {
        ensureFreeSpace(4);
        buf.putFloat(a);
    }

    public void put(byte[] value) {
        ensureFreeSpace(value.length);
        buf.put(value);
    }

    public void flip() {
        buf.flip();
    }

    public int remaining() {
        return buf.remaining();
    }
    
    public void clear() {
        buf.clear();
    }

    public int position() {
        return buf.position();
    }

    public void get(byte[] res, int offset, int length) {
        buf.get(res, offset, length);
    }

    private void ensureFreeSpace(int size) {
        if (buf.position() + size >= buf.capacity()) {
            ByteBuffer oldBuf = buf;
            int newCapacity = Math.max((int) (oldBuf.capacity() * SIZE_MULT), buf.position() + size);
            buf = ByteBuffer.allocate(newCapacity);
            oldBuf.flip();
            buf.put(oldBuf);
        }
    }
}
