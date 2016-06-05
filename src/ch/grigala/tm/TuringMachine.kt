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

import java.util.HashSet

/**
 * A deterministic Turing machine that can be simulated for a finite number of
 * steps or until resources run out.
 */
class TuringMachine
/**
 * @param Q a set of states
 * @param Sigma input alphabet
 * @param Gamma tape alphabet
 * @param delta transition function
 * @param q0 initial state
 * @param blank blank symbol
 * @param E set of final states
 * @throws InvalidSpecificationException
 */
@Throws(InvalidSpecificationException::class)
constructor(Q: Set<State>,
            Sigma: Set<Symbol>,
            Gamma: Set<Symbol>,
            delta: TransitionFunction,
            private val q0: State // start state.
            ,
            private val blank: Symbol,
            E: Set<State>) {
    private val Sigma: Set<Symbol>
    private val E: Set<State> // set of final states.
    private var activeState: State? = null // current state.
    private val delta: TransitionFunction // transition function.
    private var tape: Tape? = null
    private var steps: Int = 0 // custom steps for TM.

    init {
        // Construct Turing Machine.
        this.Sigma = HashSet(Sigma)
        this.E = HashSet(E)
        this.activeState = null
        this.delta = TransitionFunction(delta)
        this.tape = null

        val states = HashSet(Q)
        states.removeAll(E)
        for (q in states) {
            for (s in Gamma) {
                val transitionCondition = TransitionCondition(q, s)

                if (!delta.containsKey(transitionCondition)) {
                    throw InvalidSpecificationException("Delta is not a total function.")
                }

                val transitionEffect = delta[transitionCondition]
                if (!Gamma.contains(transitionEffect!!.symbol)) {
                    throw InvalidSpecificationException("Invalid mapping symbol.")
                }
                if (!Q.contains(transitionEffect.state)) {
                    throw InvalidSpecificationException("Invalid mapping state.")
                }

            }
        }

        if (!Gamma.containsAll(Sigma)) {
            throw InvalidSpecificationException("Sigma is not a subset of Gamma!")
        }

        if (!Gamma.contains(blank)) {
            throw InvalidSpecificationException("Blank symbol is not in Gamma!")
        }
        if (Sigma.contains(blank)) {
            throw InvalidSpecificationException("Sigma contains blank symbol!")
        }

        if (!Q.containsAll(E)) {
            throw InvalidSpecificationException("E is not a subset of Q!")
        }
    }

    /**
     * Reset the Turing machine to the initial configuration for the given word.
     * @param word the input of the Turing machine simulation.
     * @throws InvalidSpecificationException if the given word contains symbols not in Sigma.
     */
    @Throws(InvalidSpecificationException::class)
    fun initialize(word: List<Symbol>) {
        // Check that all symbols of word are from the input alphabet.
        if (!Sigma.containsAll(word)) {
            throw InvalidSpecificationException("Not all symbols of the word are from input alphabet")
        }
        // Reset configuration.
        activeState = q0
        tape = Tape(word, blank)
        steps = 0
    }

    /**
     * Simulate one step of the Turing machine. The machine must have been initialized and before
     * calling this function. Once the simulation is over, i.e., the Turing machine stops, the
     * machine must be re-initialized before calling step() again.
     * @throws RuntimeException if the Turing machine is not initialized or is in a final state.
     */
    private fun step() {
        // Check if the machine is not initialized.
        try {
            if (tape == null)
                throw RuntimeException("Machine is not initialized!")
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        // assert tape != null : "RuntimeException";

        try {
            if (E.contains(activeState))
                throw RuntimeException("Machine is already in final state!")
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        // assert !E.contains(activeState) : "RuntimeException";

        // Simulate one step
        val s = tape!!.read()
        // Passing current state and transition symbol to TransitionCondition constructor
        val transitionCondition = TransitionCondition(activeState, s)
        // and getting transition function effect.
        val transitionEffect = delta[transitionCondition]

        tape!!.write(transitionEffect!!.symbol!!)
        when (transitionEffect.direction) {
            Direction.LEFT -> tape!!.moveLeft()
            Direction.RIGHT -> tape!!.moveRight()
            Direction.NEUTRAL -> assert(transitionEffect.direction === Direction.NEUTRAL)
            else -> {
            }
        }
        activeState = transitionEffect.state
        steps++
    }

    /**
     * Simulate the Turing machine for up to maxSteps steps, or until it stops,
     * or resources run out.
     * The Turing machine must be initialized before calling this function.
     * @param maxSteps maximal number of steps to simulate.
     */
    @JvmOverloads fun run(maxSteps: Int = Integer.MAX_VALUE) {
        // Manually defining TuringMachine steps i.e. run(29);
        dumpConfiguration()
        while (!E.contains(activeState) && steps < maxSteps) {
            step()
            dumpConfiguration()
        }
        dumpStatistics()
        println("==============================")
    }

    fun dumpConfiguration() {
        try {
            if (tape == null) {
                println("Tape is not initialized")
            } else {
                tape!!.dumpAlpha()
                print("|$activeState|")
                tape!!.dumpBeta()
                println()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            println("Tape is not initialized.")
        }

    }

    /**
     * Print the number of steps simulated so far and the current size of the used tape.
     */
    fun dumpStatistics() {
        try {
            if (tape == null) {
                println("Tape is not initialized.")
            } else {
                // steps
                println("Turing Machine terminated manually or reached the final state, statistics:")
                println("Required steps: " + steps)
                // size of the used tape
                println("Tape cells used: " + tape!!.usedSpace())

            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            println("Tape is not initialized.")
        }
    }
}
