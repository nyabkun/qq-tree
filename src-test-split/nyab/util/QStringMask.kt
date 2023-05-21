/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package nyab.util

import kotlin.math.max
import kotlin.math.min

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=7] = String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable() <-[Call]- Str ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun String.qMaskAndReplace(
    mask: QMask,
    ptn: Regex,
    replacement: String = "$1",
    replaceAll: Boolean = true,
): String {
    val maskResult = mask.apply(this)

    return qMaskAndReplace(maskResult.maskedStr, ptn, replacement, replaceAll)
}

// CallChain[size=8] = QMaskBetween <-[Call]- qMASK_COLORED <-[Call]- String.qApplyColorNestable() < ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal class QMaskBetween(
    val startSequence: String,
    val endSequence: String,
    val nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    val escapeChar: Char? = null,
    val allowEOFEnd: Boolean = false,
    val targetNestDepth: Int = 1,
    val maskIncludeStartAndEndSequence: Boolean = false,
    val invert: Boolean = false,
    val noMaskChars: CharArray? = null, // charArrayOf('\u0020', '\t', '\n', '\r'),
    // U+E000..U+F8FF BMP (0) Private Use Area
    val maskChar: Char = '\uee31',
) : QMask {

    // CallChain[size=9] = QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_C ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    override fun apply(text: String): QMaskResult {
        return applyMore(text, null)
    }

    // CallChain[size=10] = QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBet ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun applyMore(text: String, orgText: String? = null): QMaskResult {
        val regions = text.qFindBetween(
            startSequence,
            endSequence,
            nestStartSequence,
            escapeChar,
            allowEOFEnd,
            targetNestDepth,
            maskIncludeStartAndEndSequence
        )

        val sb = StringBuilder(text.length)

        val iter = text.iterator()

        var idx = -1

        while (iter.hasNext()) {
            idx++

            var masked = false

            val ch = iter.nextChar()

            if (noMaskChars?.contains(ch) == true) {
                sb.append(ch)
                continue
            }

            for (region in regions) {
                if (idx < region.start) {
                    break
                }

                if (region.contains(idx)) {
                    sb.append(if (!invert) maskChar else ch)
                    masked = true
                    break
                }
            }

            if (!masked) {
                sb.append(if (!invert) ch else maskChar)
            }
        }

        val maskedStr = sb.toString()

        return QMaskResult(maskedStr, orgText ?: text, maskChar)
    }
}

// CallChain[size=10] = QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetw ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal class QMaskResult(val maskedStr: String, val orgText: String, val maskChar: Char) {
    // CallChain[size=4] = QMaskResult.replaceAndUnmask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    /**
     * Apply regex to masked string.
     * Apply replacement to original text.
     */
    fun replaceAndUnmask(ptn: Regex, replacement: String, findAll: Boolean = true): String {
        return orgText.qMaskAndReplace(maskedStr, ptn, replacement, findAll)
    }

    // CallChain[size=7] = QMaskResult.applyMoreMask() <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMa ... Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun applyMoreMask(mask: QMaskBetween): QMaskResult {
        return mask.applyMore(maskedStr, orgText)
    }

    // CallChain[size=11] = QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMaskBetween.apply() ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    override fun toString(): String {
        val original = orgText.qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        val masked = maskedStr.replace(maskChar, '*').qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

        return "${QMaskResult::class.simpleName} : $original ${"->".cyan} $masked"
    }
}

// CallChain[size=11] = String.qFindBetween() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetw ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun String.qFindBetween(
    startSequence: String,
    endSequence: String,
    nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    escapeChar: Char? = null,
    allowEOFEnd: Boolean = false,
    nestingDepth: Int = 1,
    regionIncludesStartAndEndSequence: Boolean = false,
): List<QRegion> {
    val finder = QBetween(
        startSequence,
        endSequence,
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence
    )

    return finder.find(this)
}

// CallChain[size=13] = QRegion <-[Ref]- QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call] ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * [start] inclusive, [end] exclusive
 */
internal open class QRegion(open val start: Int, open val end: Int) {
    // CallChain[size=12] = QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween. ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun toMutRegion(): QMutRegion {
        return QMutRegion(start, end)
    }

    // CallChain[size=12] = QRegion.toRange() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun toRange(): IntRange {
        return IntRange(start, end + 1)
    }

    // CallChain[size=12] = QRegion.length <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    val length: Int
        get() = end - start

    // CallChain[size=12] = QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.ap ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun intersect(region: QRegion): QRegion? {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        return if (start <= end) {
            QRegion(end, start)
        } else {
            null
        }
    }

    // CallChain[size=11] = QRegion.contains() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun contains(idx: Int): Boolean {
        return idx in start until end
    }

    // CallChain[size=12] = QRegion.cut() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMor ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun cut(text: String): String {
        return text.substring(start, end)
    }

    // CallChain[size=12] = QRegion.remove() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.apply ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun remove(text: String): String {
        return text.removeRange(start, end)
    }

    // CallChain[size=12] = QRegion.replace() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun replace(text: String, replacement: String): String {
        return text.replaceRange(start, end, replacement)
    }

    // CallChain[size=12] = QRegion.mask() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun mask(text: String, maskChar: Char = '*'): String {
        return text.replaceRange(this.toRange(), maskChar.toString().repeat(end - start))
    }
}

// CallChain[size=13] = QMutRegion <-[Ref]- QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[ ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal open class QMutRegion(override var start: Int, override var end: Int) : QRegion(start, end) {
    // CallChain[size=14] = QMutRegion.intersectMut() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegio ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun intersectMut(region: QRegion) {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        if (start <= end) {
            this.start = start
            this.end = end
        }
    }

    // CallChain[size=14] = QMutRegion.addOffset() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun addOffset(offset: Int) {
        start += offset
        end += offset
    }

    // CallChain[size=14] = QMutRegion.shift() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() <-[ ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun shift(length: Int) {
        this.start += length
        this.end += length
    }
}

// CallChain[size=12] = QBetween <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.applyMore()  ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private class QBetween(
    val startSequence: String,
    val endSequence: String,
    val nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    val escapeChar: Char? = null,
    val allowEOFEnd: Boolean = false,
    val nestingDepth: Int = 1,
    val regionIncludeStartAndEndSequence: Boolean = false,
) {

    // CallChain[size=12] = QBetween.find() <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.apply ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun find(text: CharSequence): List<QRegion> {
        val reader = QSequenceReader(text)

        val ranges: MutableList<QRegion> = mutableListOf()

        val startChArr = startSequence.toCharArray()
        val nestStartChArr = nestStartSequence?.toCharArray()
        val endChArr = endSequence.toCharArray()

        var nNest = 0

        var startSeqOffset = -1

        while (reader.hasNextChar()) {
            val ch = reader.peekNextChar()

            if (ch == escapeChar) {
                reader.moveOffset(2)
                continue
            } else {

                val startSequenceDetected = if (nNest == 0) {
                    reader.detectSequence(startChArr, allowEOFEnd)
                } else if (nestStartChArr != null) {
                    reader.detectSequence(nestStartChArr, allowEOFEnd)
                } else {
                    false
                }

                if (startSequenceDetected) {
                    nNest++

                    if (nestingDepth == nNest) {
                        startSeqOffset = reader.offset
                    }
                } else if (nNest > 0 && reader.detectSequence(endChArr, allowEOFEnd)) {
                    if (nestingDepth == nNest) {
                        val endSeqOffset = reader.offset - endChArr.size // exclusive

                        ranges += if (!regionIncludeStartAndEndSequence) {
                            QRegion(startSeqOffset, endSeqOffset)
                        } else {
                            val end = min(endSeqOffset + endChArr.size, text.length)
                            QRegion(startSeqOffset - startChArr.size, end)
                        }
                    }

                    nNest--
                } else {
                    reader.moveOffset()
                }
            }
        }

        return ranges
    }
}

// CallChain[size=13] = QSequenceReader <-[Call]- QBetween.find() <-[Call]- String.qFindBetween() <- ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal class QSequenceReader(text: CharSequence) : QCharReader(text) {
    // CallChain[size=15] = QSequenceReader.sequenceOffset <-[Call]- QSequenceReader.offsetInSequence()  ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    var sequenceOffset = 0

    // CallChain[size=15] = QSequenceReader.sequence <-[Call]- QSequenceReader.peekCurrentCharInSequence ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    var sequence: CharArray? = null

    // CallChain[size=14] = QSequenceReader.startReadingSequence() <-[Call]- QSequenceReader.detectSeque ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    private fun startReadingSequence(sequence: CharArray): Boolean {
        return if (!hasNextChar(sequence.size)) {
            false
        } else {
            this.sequence = sequence
            sequenceOffset = offset
            true
        }
    }

    // CallChain[size=14] = QSequenceReader.endReadingSequence() <-[Call]- QSequenceReader.detectSequenc ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    private fun endReadingSequence(success: Boolean): Boolean {

        if (!success) {
            offset = sequenceOffset
        }

        sequenceOffset = -1

        return success
    }

    // CallChain[size=14] = QSequenceReader.hasNextCharInSequence() <-[Call]- QSequenceReader.detectSequ ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun hasNextCharInSequence(): Boolean {
        return if (sequence == null) {
            false
        } else {
            (offsetInSequence() < sequence!!.size) &&
                hasNextChar()
        }
    }

//    inline fun peekNextCharInSequence(): Char {
//        return sequence!![offset - sequenceOffset]
//    }

    // CallChain[size=14] = QSequenceReader.peekCurrentCharInSequence() <-[Call]- QSequenceReader.detect ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun peekCurrentCharInSequence(): Char {
        return sequence!![offsetInSequence()]
    }

    // CallChain[size=14] = QSequenceReader.offsetInSequence() <-[Call]- QSequenceReader.detectSequence( ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    /**
     * 0 to sequence.size - 1
     */
    fun offsetInSequence(): Int {
        return offset - sequenceOffset
    }

    // CallChain[size=13] = QSequenceReader.detectSequence() <-[Call]- QBetween.find() <-[Call]- String. ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequence(sequence: CharArray, eofAllowed: Boolean = false): Boolean {
        if (!startReadingSequence(sequence)) return false

        while (hasNextCharInSequence()) {
            val seqChar = peekCurrentCharInSequence()
            val ch = nextChar()

            if (ch != seqChar) {
                endReadingSequence(false)
                return eofAllowed && isOffsetEOF()
            }
        }

        return if (offsetInSequence() == sequence.size) {
            endReadingSequence(true)
            true
        } else {
            val success = eofAllowed && isOffsetEOF()
            endReadingSequence(success)
            success
        }
    }
}

// CallChain[size=14] = QCharReader <-[Call]- QSequenceReader <-[Call]- QBetween.find() <-[Call]- St ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal open class QCharReader(val text: CharSequence) {
    // CallChain[size=15] = QCharReader.offset <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    var offset = 0

    // CallChain[size=15] = QCharReader.lineNumber() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun lineNumber(): Int {
        // Consider caret to be between the character on the offset and the character preceding it
        //
        // ex. ( [ ] indicate offsets )
        // [\n]abc\n --> 1
        // \n[\n] --> 2

        var lineBreakCount = 0

        var tmpOffset = offset

        while (tmpOffset >= 0) {
            if (tmpOffset != offset && text[tmpOffset] == '\n') {
                lineBreakCount++
            }

            tmpOffset--
        }

        return lineBreakCount + 1
    }

    // CallChain[size=15] = QCharReader.countIndentSpaces() <-[Propag]- QCharReader <-[Call]- QSequenceR ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun countIndentSpaces(space: Char = ' '): Int {
        var count = 0

        var tmpOffset = offset

        // read backward until previous line break
        while (tmpOffset >= 0) {
            if (text[tmpOffset] == '\n') {
                tmpOffset++
                break
            }

            tmpOffset--
        }

        var ch: Char

        while (true) {
            ch = text[tmpOffset]
            if (ch == space) {
                count++
            } else if (ch == '\n') {
                break
            } else {
                continue
            }

            tmpOffset--

            if (tmpOffset == -1)
                break
        }

        return count
    }

    // CallChain[size=15] = QCharReader.hasNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun hasNextChar(len: Int = 1): Boolean {
        return offset + len - 1 < text.length
    }

    // CallChain[size=15] = QCharReader.isOffsetEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun isOffsetEOF(): Boolean {
        return offset == text.length - 1
    }

    // CallChain[size=15] = QCharReader.isValidOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun isValidOffset(): Boolean {
        return 0 <= offset && offset < text.length
    }

    // CallChain[size=15] = QCharReader.hasPreviousChar() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun hasPreviousChar(len: Int = 1): Boolean {
        return 0 < offset - len + 1
    }

    // CallChain[size=15] = QCharReader.previousChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun previousChar(len: Int = 1) {
        offset -= len
    }

    // CallChain[size=15] = QCharReader.currentChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun currentChar(): Char {
        return text[offset]
    }

    // CallChain[size=15] = QCharReader.peekNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun peekNextChar(): Char {
        return text[offset]
    }

    // CallChain[size=15] = QCharReader.moveOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    inline fun moveOffset(plus: Int = 1) {
        offset += plus
    }

    // CallChain[size=15] = QCharReader.nextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[ ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    /**
     * Read current offset char and add offset by 1.
     */
    inline fun nextChar(): Char {
        return text[offset++]
    }
}

// CallChain[size=9] = QMask <-[Ref]- QMaskBetween <-[Call]- qMASK_COLORED <-[Call]- String.qApplyCo ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal interface QMask {
    // CallChain[size=10] = QMask.apply() <-[Propag]- QMask <-[Ref]- QMaskBetween <-[Call]- qMASK_COLORE ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun apply(text: String): QMaskResult

    companion object {
        // CallChain[size=5] = QMask.THREE_DOUBLE_QUOTES <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
        val THREE_DOUBLE_QUOTES by lazy {
            QMaskBetween(
                "\"\"\"", "\"\"\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=5] = QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
        val DOUBLE_QUOTE by lazy {
            QMaskBetween(
                "\"", "\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=4] = QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
        val KOTLIN_STRING by lazy {
            QMultiMask(
                THREE_DOUBLE_QUOTES,
                DOUBLE_QUOTE
            )
        }
        // CallChain[size=4] = QMask.PARENS <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
        val PARENS by lazy {
            QMaskBetween(
                "(", ")",
                nestStartSequence = "(", escapeChar = '\\'
            )
        }
        // CallChain[size=4] = QMask.INNER_BRACKETS <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
        val INNER_BRACKETS by lazy {
            QMaskBetween(
                "[", "]",
                nestStartSequence = "[", escapeChar = '', // shell color
                targetNestDepth = 2,
                maskIncludeStartAndEndSequence = true
            )
        }

        
    }
}

// CallChain[size=8] = String.qMaskAndReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String. ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun String.qMaskAndReplace(
    maskedStr: String,
    ptn: Regex,
    replacement: String = "$1",
    replaceAll: Boolean = true,
): String {
    // Apply Regex pattern to maskedStr
    val findResults: Sequence<MatchResult> = if (replaceAll) {
        ptn.findAll(maskedStr)
    } else {
        val result = ptn.find(maskedStr)
        if (result == null) {
            emptySequence()
        } else {
            sequenceOf(result)
        }
    }

    val replacers: MutableList<QReplacer> = mutableListOf()

    for (r in findResults) {
        val g = r.qResolveReplacementGroup(replacement, this)
        replacers += QReplacer(
            r.range.first,
            r.range.last + 1,
            g
        )
    }

    // Apply replacements to this String instead of maskedStr
    return qMultiReplace(replacers)
}

// CallChain[size=9] = QReplacer <-[Ref]- String.qMaskAndReplace() <-[Call]- String.qMaskAndReplace( ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal class QReplacer(start: Int, end: Int, val replacement: String) : QMutRegion(start, end)

// CallChain[size=9] = MatchResult.qResolveReplacementGroup() <-[Call]- String.qMaskAndReplace() <-[ ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun MatchResult.qResolveReplacementGroup(replacement: String, orgText: String): String {
    var resolveGroup = replacement

    for ((i, g) in groups.withIndex()) {
        if (g == null) continue

        val gValue = if (g.range.last - g.range.first == 0 || !resolveGroup.contains("$")) {
            ""
        } else {
            orgText.substring(g.range)
        }

        resolveGroup = resolveGroup.qReplace("$$i", gValue, '\\')
    }

    return resolveGroup
}

// CallChain[size=9] = CharSequence.qMultiReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- Str ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * currently does not support region overlap
 */
internal fun CharSequence.qMultiReplace(replacers: List<QReplacer>): String {
    // TODO Use StringBuilder
    val sb = StringBuilder(this)
    var offset = 0
    for (r in replacers) {
        sb.replace(r.start + offset, r.end + offset, r.replacement)
        offset += r.replacement.length - (r.end - r.start)
    }

    return sb.toString()
}

// CallChain[size=10] = CharSequence.qReplace() <-[Call]- MatchResult.qResolveReplacementGroup() <-[ ... [Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun CharSequence.qReplace(oldValue: String, newValue: String, escapeChar: Char): String {
    return replace(Regex("""(?<!\Q$escapeChar\E)\Q$oldValue\E"""), newValue)
}

// CallChain[size=10] = String.qCountOccurrence() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogSta ... [Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun String.qCountOccurrence(word: String): Int {
    return windowed(word.length) {
        if (it == word)
            1
        else
            0
    }.sum()
}

// CallChain[size=4] = CharSequence.qMask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun CharSequence.qMask(vararg mask: QMask): QMaskResult {
    mask.size.qaNotZero()

    return if (mask.size == 1) {
        mask[0].apply(this.toString())
    } else {
        val masks = mutableListOf<QMaskBetween>()
        for (m in mask) {
            if (m is QMaskBetween) {
                masks += m
            } else if (m is QMultiMask) {
                masks += m.masks
            }
        }

        QMultiMask(*masks.toTypedArray()).apply(this.toString())
    }
}

// CallChain[size=5] = QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal class QMultiMask(vararg mask: QMaskBetween) : QMask {
    // CallChain[size=7] = QMultiMask.masks <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMask <-[Call] ... Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    val masks: Array<QMaskBetween>

    // CallChain[size=6] = QMultiMask.init { <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    init {
        masks = arrayOf(*mask)
    }

    // CallChain[size=6] = QMultiMask.apply() <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Ca ... Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    override fun apply(text: String): QMaskResult {
        var result: QMaskResult? = null
        for (mask in masks) {
            result = result?.applyMoreMask(mask) ?: mask.apply(text)
        }

        return result!!
    }
}