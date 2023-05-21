/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.match

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=11] = QM <-[Ref]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAt ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal interface QM {
    // CallChain[size=11] = QM.matches() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qS ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    fun matches(text: String): Boolean

    // CallChain[size=11] = QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrc ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    fun isAny(): Boolean = this == QMatchAny

    // CallChain[size=11] = QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSr ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    fun isNone(): Boolean = this == QMatchNone

    companion object {
        // CallChain[size=10] = QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[C ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        fun exact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase)

        // CallChain[size=8] = QM.startsWith() <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Cal ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        fun startsWith(text: String, ignoreCase: Boolean = false): QM = QStartsWithMatch(text, ignoreCase)

        
    }
}

// CallChain[size=11] = QExactMatch <-[Call]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcF ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private class QExactMatch(val textExact: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=12] = QExactMatch.matches() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call]- ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun matches(text: String): Boolean {
        return text.equals(textExact, ignoreCase)
    }

    // CallChain[size=12] = QExactMatch.toString() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call] ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textExact=$textExact, ignoreCase=$ignoreCase)"
    }
}

// CallChain[size=12] = QMatchNone <-[Call]- QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAt ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal object QMatchNone : QM {
    // CallChain[size=13] = QMatchNone.matches() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag] ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun matches(text: String): Boolean {
        return false
    }

    // CallChain[size=13] = QMatchNone.toString() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=12] = QMatchAny <-[Call]- QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFr ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal object QMatchAny : QM {
    // CallChain[size=13] = QMatchAny.matches() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]- Q ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun matches(text: String): Boolean {
        return true
    }

    // CallChain[size=13] = QMatchAny.toString() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]-  ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=12] = String.qMatches() <-[Call]- Path.qFind() <-[Call]- Collection<Path>.qFind()  ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qMatches(matcher: QM): Boolean = matcher.matches(this)

// CallChain[size=9] = QStartsWithMatch <-[Call]- QM.startsWith() <-[Call]- QMyPath.src_root <-[Call ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private class QStartsWithMatch(val textStartsWith: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=10] = QStartsWithMatch.matches() <-[Propag]- QStartsWithMatch <-[Call]- QM.startsW ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun matches(text: String): Boolean {
        return text.startsWith(textStartsWith, ignoreCase)
    }

    // CallChain[size=10] = QStartsWithMatch.toString() <-[Propag]- QStartsWithMatch <-[Call]- QM.starts ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textStartsWith=$textStartsWith, ignoreCase=$ignoreCase)"
    }
}