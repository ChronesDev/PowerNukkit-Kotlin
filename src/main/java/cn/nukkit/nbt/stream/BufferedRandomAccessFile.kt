/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.nukkit.nbt.stream

import java.io.File

/**
 * A `BufferedRandomAccessFile` is like a
 * `RandomAccessFile`, but it uses a private buffer so that most
 * operations do not require a disk access.
 * <P>
 *
 * Note: The operations on this class are unmonitored. Also, the correct
 * functioning of the `RandomAccessFile` methods that are not
 * overridden here relies on the implementation of those methods in the
 * superclass.
 * @author Avinash Lakshman ( alakshman@facebook.com) &amp; Prashant Malik ( pmalik@facebook.com )
</P> */
class BufferedRandomAccessFile : RandomAccessFile {
    /*
     * This implementation is based on the buffer implementation in Modula-3's
     * "Rd", "Wr", "RdClass", and "WrClass" interfaces.
     */
    private var dirty_ // true iff unflushed bytes exist
            = false
    private var closed_ // true iff the file is closed
            = false
    private var curr_ // current position in file
            : Long = 0
    private var lo_: Long = 0
    private var hi_ // bounds on characters in "buff"
            : Long = 0
    private var buff_ // local buffer
            : ByteArray
    private var maxHi_ // this.lo + this.buff.length
            : Long = 0
    private var hitEOF_ // buffer contains last file block?
            = false
    private var diskPos_ // disk position
            : Long = 0
    /*
     * To describe the above fields, we introduce the following abstractions for
     * the file "f":
     *
     * len(f) the length of the file curr(f) the current position in the file
     * c(f) the abstract contents of the file disk(f) the contents of f's
     * backing disk file closed(f) true iff the file is closed
     *
     * "curr(f)" is an index in the closed interval [0, len(f)]. "c(f)" is a
     * character sequence of length "len(f)". "c(f)" and "disk(f)" may differ if
     * "c(f)" contains unflushed writes not reflected in "disk(f)". The flush
     * operation has the effect of making "disk(f)" identical to "c(f)".
     *
     * A file is said to be *valid* if the following conditions hold:
     *
     * V1. The "closed" and "curr" fields are correct:
     *
     * f.closed == closed(f) f.curr == curr(f)
     *
     * V2. The current position is either contained in the buffer, or just past
     * the buffer:
     *
     * f.lo <= f.curr <= f.hi
     *
     * V3. Any (possibly) unflushed characters are stored in "f.buff":
     *
     * (forall i in [f.lo, f.curr): c(f)[i] == f.buff[i - f.lo])
     *
     * V4. For all characters not covered by V3, c(f) and disk(f) agree:
     *
     * (forall i in [f.lo, len(f)): i not in [f.lo, f.curr) => c(f)[i] ==
     * disk(f)[i])
     *
     * V5. "f.dirty" is true iff the buffer contains bytes that should be
     * flushed to the file; by V3 and V4, only part of the buffer can be dirty.
     *
     * f.dirty == (exists i in [f.lo, f.curr): c(f)[i] != f.buff[i - f.lo])
     *
     * V6. this.maxHi == this.lo + this.buff.length
     *
     * Note that "f.buff" can be "null" in a valid file, since the range of
     * characters in V3 is empty when "f.lo == f.curr".
     *
     * A file is said to be *ready* if the buffer contains the current position,
     * i.e., when:
     *
     * R1. !f.closed && f.buff != null && f.lo <= f.curr && f.curr < f.hi
     *
     * When a file is ready, reading or writing a single byte can be performed
     * by reading or writing the in-memory buffer without performing a disk
     * operation.
     */
    /**
     * Open a new `BufferedRandomAccessFile` on `file`
     * in mode `mode`, which should be "r" for reading only, or
     * "rw" for reading and writing.
     */
    constructor(file: File?, mode: String?) : super(file, mode) {
        init(0)
    }

    constructor(file: File?, mode: String?, size: Int) : super(file, mode) {
        init(size)
    }

    /**
     * Open a new `BufferedRandomAccessFile` on the file named
     * `name` in mode `mode`, which should be "r" for
     * reading only, or "rw" for reading and writing.
     */
    constructor(name: String?, mode: String?) : super(name, mode) {
        init(0)
    }

    constructor(name: String?, mode: String?, size: Int) : super(name, mode) {
        init(size)
    }

    private fun init(size: Int) {
        closed_ = false
        dirty_ = closed_
        hi_ = 0
        curr_ = hi_
        lo_ = curr_
        buff_ = if (size > BuffSz_) ByteArray(size) else ByteArray(BuffSz_)
        maxHi_ = BuffSz_.toLong()
        hitEOF_ = false
        diskPos_ = 0L
    }

    @Override
    @Throws(IOException::class)
    fun close() {
        flush()
        closed_ = true
        super.close()
    }

    /**
     * Flush any bytes in the file's buffer that have not yet been written to
     * disk. If the file was created read-only, this method is a no-op.
     */
    @Throws(IOException::class)
    fun flush() {
        flushBuffer()
    }

    /* Flush any dirty bytes in the buffer to disk. */
    @Throws(IOException::class)
    private fun flushBuffer() {
        if (dirty_) {
            if (diskPos_ != lo_) super.seek(lo_)
            val len = (curr_ - lo_).toInt()
            super.write(buff_, 0, len)
            diskPos_ = curr_
            dirty_ = false
        }
    }

    /*
     * Read at most "this.buff.length" bytes into "this.buff", returning the
     * number of bytes read. If the return result is less than
     * "this.buff.length", then EOF was read.
     */
    @Throws(IOException::class)
    private fun fillBuffer(): Int {
        var cnt = 0
        var rem = buff_.size
        while (rem > 0) {
            val n: Int = super.read(buff_, cnt, rem)
            if (n < 0) break
            cnt += n
            rem -= n
        }
        if (cnt < 0 && (cnt < buff_.size).also { hitEOF_ = it }) {
            // make sure buffer that wasn't read is initialized with -1
            Arrays.fill(buff_, cnt, buff_.size, 0xff.toByte())
        }
        diskPos_ += cnt.toLong()
        return cnt
    }

    /*
     * This method positions <code>this.curr</code> at position <code>pos</code>.
     * If <code>pos</code> does not fall in the current buffer, it flushes the
     * current buffer and loads the correct one.<p>
     *
     * On exit from this routine <code>this.curr == this.hi</code> iff <code>pos</code>
     * is at or past the end-of-file, which can only happen if the file was
     * opened in read-only mode.
     */
    @Override
    @Throws(IOException::class)
    fun seek(pos: Long) {
        if (pos >= hi_ || pos < lo_) {
            // seeking outside of current buffer -- flush and read
            flushBuffer()
            lo_ = pos and BuffMask_ // start at BuffSz boundary
            maxHi_ = lo_ + buff_.size.toLong()
            if (diskPos_ != lo_) {
                super.seek(lo_)
                diskPos_ = lo_
            }
            val n = fillBuffer()
            hi_ = lo_ + n.toLong()
        } else {
            // seeking inside current buffer -- no read required
            if (pos < curr_) {
                // if seeking backwards, we must flush to maintain V4
                flushBuffer()
            }
        }
        curr_ = pos
    }

    /*
     * Does not maintain V4 (i.e. buffer differs from disk contents if previously written to)
     *  - Assumes no writes were made
     * @param pos
     * @throws IOException
     */
    @Throws(IOException::class)
    fun seekUnsafe(pos: Long) {
        if (pos >= hi_ || pos < lo_) {
            // seeking outside of current buffer -- flush and read
            flushBuffer()
            lo_ = pos and BuffMask_ // start at BuffSz boundary
            maxHi_ = lo_ + buff_.size.toLong()
            if (diskPos_ != lo_) {
                super.seek(lo_)
                diskPos_ = lo_
            }
            val n = fillBuffer()
            hi_ = lo_ + n.toLong()
        }
        curr_ = pos
    }

    @Override
    fun getFilePointer(): Long {
        return curr_
    }

    @Override
    @Throws(IOException::class)
    fun length(): Long {
        return Math.max(curr_, super.length())
    }

    @Override
    @Throws(IOException::class)
    fun read(): Int {
        if (curr_ >= hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (hitEOF_) return -1

            // slow path -- read another buffer
            seek(curr_)
            if (curr_ == hi_) return -1
        }
        val res = buff_[(curr_ - lo_).toInt()]
        curr_++
        return res.toInt() and 0xFF // convert byte -> int
    }

    @Throws(IOException::class)
    fun read1(): Byte {
        if (curr_ >= hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (hitEOF_) return -1

            // slow path -- read another buffer
            seek(curr_)
            if (curr_ == hi_) return -1
        }
        return buff_[(curr_++ - lo_).toInt()]
    }

    @Override
    @Throws(IOException::class)
    fun read(b: ByteArray): Int {
        return this.read(b, 0, b.size)
    }

    @Override
    @Throws(IOException::class)
    fun read(b: ByteArray?, off: Int, len: Int): Int {
        var len = len
        if (curr_ >= hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (hitEOF_) return -1

            // slow path -- read another buffer
            seek(curr_)
            if (curr_ == hi_) return -1
        }
        len = Math.min(len, (hi_ - curr_).toInt())
        val buffOff = (curr_ - lo_).toInt()
        System.arraycopy(buff_, buffOff, b, off, len)
        curr_ += len.toLong()
        return len
    }

    @Throws(IOException::class)
    fun readCurrent(): Byte {
        if (curr_ >= hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (hitEOF_) return -1

            // slow path -- read another buffer
            seek(curr_)
            if (curr_ == hi_) return -1
        }
        return buff_[(curr_ - lo_).toInt()]
    }

    @Throws(IOException::class)
    fun writeCurrent(b: Byte) {
        if (curr_ >= hi_) {
            if (hitEOF_ && hi_ < maxHi_) {
                // at EOF -- bump "hi"
                hi_++
            } else {
                // slow path -- write current buffer; read next one
                seek(curr_)
                if (curr_ == hi_) {
                    // appending to EOF -- bump "hi"
                    hi_++
                }
            }
        }
        buff_[(curr_ - lo_).toInt()] = b
        dirty_ = true
    }

    @Override
    @Throws(IOException::class)
    fun write(b: Int) {
        if (curr_ >= hi_) {
            if (hitEOF_ && hi_ < maxHi_) {
                // at EOF -- bump "hi"
                hi_++
            } else {
                // slow path -- write current buffer; read next one
                seek(curr_)
                if (curr_ == hi_) {
                    // appending to EOF -- bump "hi"
                    hi_++
                }
            }
        }
        buff_[(curr_ - lo_).toInt()] = b.toByte()
        curr_++
        dirty_ = true
    }

    @Override
    @Throws(IOException::class)
    fun write(b: ByteArray) {
        this.write(b, 0, b.size)
    }

    @Override
    @Throws(IOException::class)
    fun write(b: ByteArray, off: Int, len: Int) {
        var off = off
        var len = len
        while (len > 0) {
            val n = writeAtMost(b, off, len)
            off += n
            len -= n
            dirty_ = true
        }
    }

    /*
     * Write at most "len" bytes to "b" starting at position "off", and return
     * the number of bytes written.
     */
    @Throws(IOException::class)
    private fun writeAtMost(b: ByteArray, off: Int, len: Int): Int {
        var len = len
        if (curr_ >= hi_) {
            if (hitEOF_ && hi_ < maxHi_) {
                // at EOF -- bump "hi"
                hi_ = maxHi_
            } else {
                // slow path -- write current buffer; read next one
                seek(curr_)
                if (curr_ == hi_) {
                    // appending to EOF -- bump "hi"
                    hi_ = maxHi_
                }
            }
        }
        len = Math.min(len, (hi_ - curr_).toInt())
        val buffOff = (curr_ - lo_).toInt()
        System.arraycopy(b, off, buff_, buffOff, len)
        curr_ += len.toLong()
        return len
    }

    companion object {
        const val LogBuffSz_ = 16 // 64K buffer
        const val BuffSz_ = 1 shl LogBuffSz_
        const val BuffMask_ = -BuffSz_.toLong()
    }
}