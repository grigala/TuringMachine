package ch.grigala.tm

/**
 * MIT License

 * Copyright (c) 2016 Giorgi Grigalashvili

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


import java.util.Deque
import java.util.LinkedList

/**
 * Tape models the infinite tape of a Turing machine. Obviously, we have limited
 * memory and cannot implement an infinite tape. This implementation increases the
 * size of tape dynamically if the read/write head of the Turing machine reaches
 * the current end of the tape, so the class actually represents the used part of
 * the tape. This will stay finite since we can only simulate a finite number of
 * steps.
 */
class Tape
/**
 * Initialize a new tape that contains just the given word with the head
 * positioned over the first symbol of the word.
 * @param word  content of the tape.
 * @param blank symbol that is used for all unvisited positions.
 */
(word: List<Symbol>, private val blank: Symbol) {
    private val leftSideOfTheTape: Deque<Symbol>
    private val rightSideOfTheTape: Deque<Symbol>

    init {
        this.leftSideOfTheTape = LinkedList<Symbol>()
        this.rightSideOfTheTape = LinkedList(word)

        if (rightSideOfTheTape.isEmpty()) {
            rightSideOfTheTape.add(blank)
        }
    }

    fun read(): Symbol {
        return rightSideOfTheTape.peekFirst()
    }

    fun write(symbol: Symbol) {
        rightSideOfTheTape.removeFirst()
        rightSideOfTheTape.addFirst(symbol)
    }


    fun moveLeft() {
        if (leftSideOfTheTape.isEmpty()) {
            // if leftSideOfTheTape is empty we add blank symbol to rightSideOfTheTape list.
            rightSideOfTheTape.addFirst(blank)
        } else {
            // else we are removing last symbol of leftSideOfTheTape and adding it to head of the rightSideOfTheTape.
            rightSideOfTheTape.addFirst(leftSideOfTheTape.removeLast())
        }
    }

    fun moveRight() {
        leftSideOfTheTape.addLast(rightSideOfTheTape.removeFirst())
        if (rightSideOfTheTape.isEmpty()) {
            rightSideOfTheTape.addFirst(blank)
        }
    }

    fun dumpAlpha() {
        leftSideOfTheTape.forEach { print(it) }
    }


    fun dumpBeta() {
        // Looping each symbol of rightSideOfTheTape tape using Lambda expression
        rightSideOfTheTape.forEach{ print(it) }
    }

    fun usedSpace(): Int {
        return leftSideOfTheTape.size + rightSideOfTheTape.size
    }
}
