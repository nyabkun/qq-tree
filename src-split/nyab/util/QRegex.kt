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

import org.intellij.lang.annotations.Language

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=10] = re <-[Call]- String.qApplyColorNestable() <-[Call]- String.qColorLine() <-[C ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
internal val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

// CallChain[size=10] = String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNest ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qReplaceFirstIfNonEmptyStringGroup(@Language("RegExp") regex: String, nonEmptyGroupIdx: Int, replace: String = "$1", vararg opts: RO): String {
    val re = qRe(regex, *opts)

    return if (re.find(this)?.groups?.get(nonEmptyGroupIdx)?.value?.isNotEmpty() == true) {
        re.replaceFirst(this, replace)
    } else {
        this
    }
}

// CallChain[size=11] = RO <-[Ref]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qAp ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal typealias RO = RegexOption

// CallChain[size=11] = qRe() <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}