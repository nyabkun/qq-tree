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
import kotlin.math.min
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.isSuperclassOf
import nyab.conf.QE
import nyab.conf.QMyLog
import nyab.conf.QMyToString
import nyab.match.QMFunc
import nyab.match.and

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = Any?.qToLogString() <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun Any?.qToLogString(maxLineLength: Int = 80): String {
    if (QMyLog.no_format) {
        return this.toString()
    }

    if (this == null)
        return "null".light_gray
    if (this == "")
        return "".qClarifyEmptyOrBlank()

    val str = this.qToString()

    val isListOrArray =
            this !is CharSequence && (str.startsWith("[") && str.endsWith("]")) || (str.startsWith("{") && str.endsWith("}"))
    val isMultiline = isListOrArray && str.qIsMultiLine()
    val isNestedListOrArray = isListOrArray && str.startsWith("[[")

    val comma = ",".light_gray
    val separator = "----".light_gray

    return if (isNestedListOrArray) { // Nested list always add line breaks for consistent formatting.
        val str2 = str.replaceRange(1, 1, "\n")

        val masked = str2.replaceRange(str.length, str.length, "\n").qMask(
                QMask.INNER_BRACKETS
        )

        masked.replaceAndUnmask(", ".re, "$comma\n").trim()
    } else if (isListOrArray && (maxLineLength < str.length || isMultiline) && str.length < qMASK_LENGTH_LIMIT) { // qMask is slow, needs limit length
        val str2 = str.replaceRange(1, 1, "\n")

        val masked = str2.replaceRange(str.length, str.length, "\n").qMask(
                QMask.PARENS,
                QMask.KOTLIN_STRING
        )

        masked.replaceAndUnmask(", ".re, if (isMultiline) "\n$separator\n" else "$comma\n").trim()
    } else {
        str.trim()
    }.qClarifyEmptyOrBlank()
}

// CallChain[size=6] = String.qWithSpacePrefix() <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qWithSpacePrefix(numSpace: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.SingleLine): String {
    if (!onlyIf.matches(this)) return this

    return " ".repeat(numSpace) + this.trimStart()
}

// CallChain[size=6] = QOnlyIfStr <-[Ref]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal enum class QOnlyIfStr(val matches: (String) -> Boolean) {
    // CallChain[size=6] = QOnlyIfStr.Multiline <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Multiline({ it.qIsMultiLine() }),
    // CallChain[size=6] = QOnlyIfStr.SingleLine <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    SingleLine({ it.qIsSingleLine() }),
    // CallChain[size=7] = QOnlyIfStr.Empty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QException.qToStr ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Empty({ it.isEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.Blank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QException.qToStr ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Blank({ it.isBlank() }),
    // CallChain[size=7] = QOnlyIfStr.NotEmpty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QException.qTo ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    NotEmpty({ it.isNotEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.NotBlank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QException.qTo ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    NotBlank({ it.isNotBlank() }),
    // CallChain[size=7] = QOnlyIfStr.Always <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QException.qToSt ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Always({ true })
}

// CallChain[size=6] = String.qWithNewLinePrefix() <-[Call]- QException.qToString() <-[Call]- QExcep ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qWithNewLinePrefix(
        numNewLine: Int = 1,
        onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline,
        lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeWhile { it == '\n' || it == '\r' }.count()

    return lineSeparator.value.repeat(numNewLine) + substring(nCount)
}

// CallChain[size=7] = String.qIsMultiLine() <-[Call]- QOnlyIfStr.Multiline <-[Call]- QException.qTo ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=7] = QLineSeparator <-[Ref]- String.qWithNewLinePrefix() <-[Call]- QException.qToS ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal enum class QLineSeparator(val value: String) {
    // CallChain[size=7] = QLineSeparator.LF <-[Call]- String.qWithNewLinePrefix() <-[Call]- QException. ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LF("\n"),
    // CallChain[size=8] = QLineSeparator.CRLF <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Str ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    CRLF("\r\n"),
    // CallChain[size=8] = QLineSeparator.CR <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Strin ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    CR("\r");

    companion object {
        // CallChain[size=11] = QLineSeparator.DEFAULT <-[Call]- Path.qLineSeparator() <-[Call]- Path.qFetch ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        val DEFAULT = QLineSeparator.LF
    }
}

// CallChain[size=7] = String.qIsSingleLine() <-[Call]- QOnlyIfStr.SingleLine <-[Call]- QException.q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=15] = String.qWithNewLineSurround() <-[Call]- QMaskResult.toString() <-[Propag]- Q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qWithNewLineSurround(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    return qWithNewLinePrefix(numNewLine, QOnlyIfStr.Always).qWithNewLineSuffix(numNewLine, QOnlyIfStr.Always)
}

// CallChain[size=16] = String.qWithNewLineSuffix() <-[Call]- String.qWithNewLineSurround() <-[Call] ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qWithNewLineSuffix(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeLastWhile { it == '\n' || it == '\r' }.count()

    return substring(0, length - nCount) + "\n".repeat(numNewLine)
}

// CallChain[size=8] = String.qCountLeftSpace() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStack ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qCountLeftSpace(): Int = takeWhile { it == ' ' }.count()

// CallChain[size=8] = CharSequence.qEndsWith() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStack ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun CharSequence.qEndsWith(suffix: Regex, length: Int = 100): Boolean {
    return takeLast(min(length, this.length)).matches(suffix)
}

// CallChain[size=10] = String.qWithSpaceSuffix() <-[Call]- String.qBracketStartOrMiddle() <-[Call]- ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qWithSpaceSuffix(numSpace: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.SingleLine): String {
    if (!onlyIf.matches(this)) return this

    return this.trimEnd() + " ".repeat(numSpace)
}

// CallChain[size=4] = String.qClarifyEmptyOrBlank() <-[Call]- Any?.qToLogString() <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qClarifyEmptyOrBlank(): String {
    return if (this.isEmpty()) {
        "(EMPTY STRING)".qColor(QShColor.LIGHT_GRAY)
    } else if (this.isBlank()) {
        "$this(BLANK STRING)".qColor(QShColor.LIGHT_GRAY)
    } else {
        this
    }
}

// CallChain[size=4] = Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun Any?.qToString(): String {
    if (this == null)
        return "null".light_gray

    for (r in qToStringRegistry) {
        if (r.okToApply(this)) {
            return r.toString(this)
        }
    }

    return toString()
}

// CallChain[size=4] = qMASK_LENGTH_LIMIT <-[Call]- Any?.qToLogString() <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal const val qMASK_LENGTH_LIMIT: Int = 100_000

// CallChain[size=5] = qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private val qToStringRegistry: MutableList<QToString> by lazy {
    val toStrings =
            QMyToString::class.qFunctions(
                    QMFunc.nameExact("qToString") and
//                            QMFunc.returnType(String::class, false) and
//                            QMFunc.NoParams and
                            QMFunc.DeclaredOnly and
                            QMFunc.IncludeExtensionsInClass
            )

    toStrings.map { func ->
        QToString(
                okToApply = { value ->
                    func.extensionReceiverParameter?.type?.qIsSuperclassOf(value::class) ?: false
                },
                toString = { value ->
                    func.call(QMyToString, value) as String
                }
        )
    }.toMutableList()
}

// CallChain[size=6] = QToString <-[Ref]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal class QToString(val okToApply: (Any) -> Boolean, val toString: (Any) -> String)