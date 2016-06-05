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

import java.util.Arrays
import java.util.HashSet
import java.util.LinkedList

object SimulateTM {

    @Throws(InvalidSpecificationException::class)
    @JvmStatic fun main(args: Array<String>) {
        val a = Symbol("a")
        val b = Symbol("b")
        val c = Symbol("c")
        val x = Symbol("x")
        val blank = Symbol("_")

        val states = arrayOfNulls<State>(7)
        for (i in states.indices) {
            states[i] = State(i)
        }

        // Finite, non-empty set of states, in our case q0...q6
        val Q = HashSet(Arrays.asList<State>(*states))

        // Sigma: finite input alphabet for our example - must not include blank.
        val Sigma = HashSet(Arrays.asList(a, b, c))

        // Finite tape of the alphabet, must contain a blank symbol and all other symbols. Gamma \cup Sigma.
        val Gamma = HashSet(Arrays.asList(a, b, c, x, blank))

        // Defining Transition function delta : (Q \ E) x Gamma -> Q x Gamma x {L, R, N}
        val delta = TransitionFunction()

        // E is our end state, which is q6 in our example, so we need 6th element of states array.
        val E = HashSet(listOf<State>(states[6] as State))

        // read an 'a' in q0, go to q1, replace 'a' with 'x' and move right.
        delta.put(states[0] as State, a, states[1] as State, x, Direction.RIGHT)
        delta.put(states[0] as State, b, states[0] as State, b, Direction.NEUTRAL)
        delta.put(states[0] as State, c, states[0] as State, c, Direction.NEUTRAL)
        delta.put(states[0] as State, x, states[0] as State, x, Direction.NEUTRAL)
        delta.put(states[0] as State, blank, states[6] as State, blank, Direction.NEUTRAL)

        // q1 outgoing edges
        delta.put(states[1] as State, a, states[1] as State, a, Direction.RIGHT)
        delta.put(states[1] as State, b, states[2] as State, x, Direction.RIGHT)
        delta.put(states[1] as State, c, states[1] as State, c, Direction.NEUTRAL)
        delta.put(states[1] as State, x, states[1] as State, x, Direction.RIGHT)
        delta.put(states[1] as State, blank, states[1] as State, blank, Direction.NEUTRAL)

        // q2 outgoing edges
        delta.put(states[2] as State, a, states[2] as State, a, Direction.NEUTRAL)
        delta.put(states[2] as State, b, states[2] as State, b, Direction.RIGHT)
        delta.put(states[2] as State, c, states[3] as State, x, Direction.NEUTRAL)
        delta.put(states[2] as State, x, states[2] as State, x, Direction.RIGHT)
        delta.put(states[2] as State, blank, states[2] as State, blank, Direction.NEUTRAL)

        // q3 state outgoing edges
        delta.put(states[3] as State, a, states[4] as State, a, Direction.LEFT)
        delta.put(states[3] as State, b, states[3] as State, b, Direction.LEFT)
        delta.put(states[3] as State, c, states[3] as State, c, Direction.NEUTRAL)
        delta.put(states[3] as State, x, states[3] as State, x, Direction.LEFT)
        delta.put(states[3] as State, blank, states[5] as State, blank, Direction.RIGHT)

        // q4 outgoing edges
        delta.put(states[4] as State, a, states[4] as State, a, Direction.LEFT)
        delta.put(states[4] as State, b, states[4] as State, b, Direction.LEFT)
        delta.put(states[4] as State, c, states[4] as State, c, Direction.LEFT)
        delta.put(states[4] as State, x, states[0] as State, x, Direction.RIGHT)
        delta.put(states[4] as State, blank, states[4] as State, blank, Direction.LEFT)

        // q5 outgoing edges
        delta.put(states[5] as State, a, states[5] as State, a, Direction.NEUTRAL)
        delta.put(states[5] as State, b, states[5] as State, b, Direction.NEUTRAL)
        delta.put(states[5] as State, c, states[5] as State, c, Direction.NEUTRAL)
        delta.put(states[5] as State, x, states[5] as State, x, Direction.RIGHT)
        delta.put(states[5] as State, blank, states[6] as State, blank, Direction.NEUTRAL)

        // q6 no outgoing edges - pass

        val turingMachine = TuringMachine(Q, Sigma, Gamma, delta, states[0] as State, blank, E)


        println("Checking implementation for aabbcc: ")
        turingMachine.initialize(Arrays.asList(a, a, b, b, c, c))
        turingMachine.run() // 29 steps, 8 tape cells.

        println("Checking implementation for empty word:")
        turingMachine.initialize(LinkedList<Symbol>())
        turingMachine.run() // steps used 1, cell used 1.

        println("Simulating word abc:")
        turingMachine.initialize(Arrays.asList(a, b, c))
        turingMachine.run() // 11 steps, 5 tape cells.

        println("Simulating word aaabbbccc:")
        turingMachine.initialize(Arrays.asList(a, a, a, b, b, b, c, c, c))
        turingMachine.run() // 55 steps, 11 tape cells.

        println("Simulating word aaaabbbbcccc:")
        turingMachine.initialize(Arrays.asList(a, a, a, a, b, b, b, b, c, c, c, c))
        turingMachine.run() // 89 steps, 14 tape cells.


        println("Simulating word aabc:")
        turingMachine.initialize(Arrays.asList(a, a, b, c))
        turingMachine.run(15) // infinity loop after 12 step, state=q1.

        println("Simulating word abca:")
        turingMachine.initialize(Arrays.asList(a, b, c, a))
        turingMachine.run(17) // infinity loop after 11 step, state=q5.

        println("Simulating word aaa:")
        turingMachine.initialize(Arrays.asList(a, a, a))
        turingMachine.run(10) // infinity loop after 4 step, state=q1.
    }

}

