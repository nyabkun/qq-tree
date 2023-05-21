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

import java.nio.charset.Charset
import java.nio.file.Path

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=8] = QOut <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QException ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
interface QOut {
    // CallChain[size=10] = QOut.isAcceptColoredText <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Ca ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    val isAcceptColoredText: Boolean

    // CallChain[size=10] = QOut.print() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    fun print(msg: Any? = "")

    // CallChain[size=10] = QOut.println() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogS ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    fun println(msg: Any? = "")

    // CallChain[size=10] = QOut.close() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    fun close()

    companion object {
        // CallChain[size=9] = QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_ ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        val CONSOLE: QOut = QConsole(true)

        
    }
}

// CallChain[size=10] = QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[ ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private class QConsole(override val isAcceptColoredText: Boolean) : QOut {
    // CallChain[size=11] = QConsole.print() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun print(msg: Any?) {
        if (isAcceptColoredText) {
            kotlin.io.print(msg.toString())
        } else {
            kotlin.io.print(msg.toString().noColor)
        }
    }

    // CallChain[size=11] = QConsole.println() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMy ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun println(msg: Any?) {
        kotlin.io.println(msg.toString())
    }

    // CallChain[size=11] = QConsole.close() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun close() {
        // Do nothing
    }
}