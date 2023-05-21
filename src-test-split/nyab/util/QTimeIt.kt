/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=4] = qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
@OptIn(ExperimentalContracts::class)
internal inline fun <T> qTimeIt(label: String = qThisSrcLineSignature, quiet: Boolean = false, out: QOut? = QOut.CONSOLE, block: () -> T): QTimeItResult<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val start = System.nanoTime()

    val blockResult = block()

    val time = System.nanoTime() - start

    val result = QTimeItResult(label, time, blockResult)

    if (!quiet)
        out?.println(result.toString())

    return result
}

// CallChain[size=5] = QTimeItResult <-[Ref]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal class QTimeItResult<T>(val label: String, val time: Long, val result: T) {
    // CallChain[size=5] = QTimeItResult.toString() <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return qBrackets(label, time.qFormatDuration())
    }
}