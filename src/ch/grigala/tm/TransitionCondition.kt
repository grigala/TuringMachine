package ch.grigala.tm

/**
 * MIT License
 *
 * Copyright (c) 2016 Giorgi Grigalashvili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

class TransitionCondition(val state: State?, val symbol: Symbol?) {

    init {
        assert(state != null)
        assert(symbol != null)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + state!!.hashCode()
        result = prime * result + symbol!!.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is TransitionCondition)
            return false
        return state == obj.state && symbol == obj.symbol
    }
}
