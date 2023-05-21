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

import kotlin.math.abs

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=5] = QAlign <-[Ref]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal enum class QAlign {
    // CallChain[size=5] = QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    LEFT,
    // CallChain[size=6] = QAlign.RIGHT <-[Propag]- QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    RIGHT,
    // CallChain[size=6] = QAlign.CENTER <-[Propag]- QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    CENTER
}

// CallChain[size=7] = String.qAlignCenter() <-[Call]- String.qWithMinLength() <-[Call]- String.qWit ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun String.qAlignCenter(
    vararg places: Regex = arrayOf("(.*)".re),
    onlyFirstMatch: Boolean = true,
    oddLengthTuning: QLR = if (qIsNumber()) QLR.RIGHT else QLR.LEFT,
    groupIdx: QGroupIdx = QGroupIdx.FIRST
): String {
    return qAlign(QAlign.CENTER, *places, onlyFirstMatch = onlyFirstMatch, oddLengthTuning = oddLengthTuning, groupIdx = groupIdx)
}

// CallChain[size=8] = String.qIsNumber() <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinL ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun String.qIsNumber(): Boolean {
    return this.trim().matches("""[\d./eE+-]+""".re)
}

// CallChain[size=8] = QGroupIdx <-[Ref]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal enum class QGroupIdx(val idx: Int) {
    // CallChain[size=9] = QGroupIdx.ENTIRE_MATCH <-[Call]- String.qAlign() <-[Call]- String.qAlignCente ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    ENTIRE_MATCH(0),
    // CallChain[size=8] = QGroupIdx.FIRST <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLeng ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FIRST(1),
    // CallChain[size=12] = QGroupIdx.SECOND <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SECOND(2),
    // CallChain[size=12] = QGroupIdx.THIRD <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    THIRD(3),
    // CallChain[size=12] = QGroupIdx.FOURTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FOURTH(4),
    // CallChain[size=12] = QGroupIdx.FIFTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FIFTH(5),
    // CallChain[size=12] = QGroupIdx.SIXTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SIXTH(6),
    // CallChain[size=12] = QGroupIdx.SEVENTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResu ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SEVENTH(7),
    // CallChain[size=12] = QGroupIdx.EIGHTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    EIGHTH(8),
    // CallChain[size=12] = QGroupIdx.NINTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    NINTH(9),
    // CallChain[size=12] = QGroupIdx.TENTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    TENTH(10);
}

// CallChain[size=8] = String.qAlign() <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLeng ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qAlign(
    align: QAlign = QAlign.RIGHT,
    vararg places: Regex,
    onlyFirstMatch: Boolean = true,
    keepLength: Boolean = false,
    oddLengthTuning: QLR = if (qIsNumber()) QLR.RIGHT else QLR.LEFT,
    groupIdx: QGroupIdx = QGroupIdx.ENTIRE_MATCH
): String {
    var text = this
    for (p in places) {
        text = QLineMatchResult(p, text, onlyFirstMatch, groupIdx).align(align, keepLength, oddLengthTuning)
    }

    return text
}

// CallChain[size=9] = QLineMatchResult <-[Call]- String.qAlign() <-[Call]- String.qAlignCenter() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal class QLineMatchResult(
    val regex: Regex,
    val text: String,
    val onlyFirstMatch: Boolean = false,
    val groupIdx: QGroupIdx
) {
    // CallChain[size=10] = QLineMatchResult.curText <-[Call]- QLineMatchResult.align() <-[Call]- String ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    var curText: String = text

    // CallChain[size=10] = QLineMatchResult.updateResult() <-[Call]- QLineMatchResult.align() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateResult(align: QAlign) {
        updateRowResult()
        updateColResult()
        updateColDestPos(align)
    }

    // CallChain[size=11] = QLineMatchResult.rowResults <-[Call]- QLineMatchResult.matchedRange() <-[Cal ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // line index -> match index
    lateinit var rowResults: List<List<MatchResult>>

    // CallChain[size=12] = QLineMatchResult.colResults <-[Call]- QLineMatchResult.updateColDestPos() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // col index -> match index
    lateinit var colResults: List<List<MatchResult>>

    // CallChain[size=10] = QLineMatchResult.colDestPos <-[Call]- QLineMatchResult.align() <-[Call]- Str ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // col index -> max left index / right index of matched region
    lateinit var colDestPos: List<QLeftRight>

    // CallChain[size=11] = QLineMatchResult.updateRowResult() <-[Call]- QLineMatchResult.updateResult() ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateRowResult() {
        rowResults = if (onlyFirstMatch) {
            curText.lineSequence().map { regex.find(it, 0) }.map {
                if (it == null) {
                    emptyList()
                } else {
                    listOf(it)
                }
            }.toList()
        } else {
            curText.lineSequence().map { regex.findAll(it, 0).toList() }.toList()
        }
    }

    // CallChain[size=11] = QLineMatchResult.updateColResult() <-[Call]- QLineMatchResult.updateResult() ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateColResult() {
        colResults = mutableListOf<List<MatchResult>>().also { list ->
            val maximumRowMatchCount = rowResults.maxOfOrNull { it.size } ?: -1

            for (iColumn in 0 until maximumRowMatchCount) {
                list += rowResults.mapNotNull { result ->
                    result.getOrNull(iColumn)
                }
            }
        }
    }

    // CallChain[size=11] = QLineMatchResult.updateColDestPos() <-[Call]- QLineMatchResult.updateResult( ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateColDestPos(align: QAlign) {
        colDestPos = if (align == QAlign.RIGHT) {
            colResults.map { it.qMinOrMaxIndexLR(groupIdx, QMinOrMax.MAX) }
        } else {
            colResults.map { it.qMinOrMaxIndexLR(groupIdx, QMinOrMax.MIN) }
        }
    }

    // CallChain[size=10] = QLineMatchResult.matchedRange() <-[Call]- QLineMatchResult.align() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun matchedRange(rowIdx: Int, colIdx: Int): IntRange? {
        val groups = rowResults.getOrNull(rowIdx)?.getOrNull(colIdx)?.groups ?: return null
        return if (groupIdx.idx < groups.size) {
            groups[groupIdx.idx]?.range
        } else {
            null
        }
    }

    // CallChain[size=12] = QLineMatchResult.QMinOrMax <-[Ref]- QLineMatchResult.updateColDestPos() <-[C ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    enum class QMinOrMax {
        MIN, MAX
    }

    // CallChain[size=12] = QLineMatchResult.List<MatchResult>.qMinOrMaxIndexLR() <-[Call]- QLineMatchRe ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun List<MatchResult>.qMinOrMaxIndexLR(
        groupIdx: QGroupIdx,
        minOrMax: QMinOrMax
    ): QLeftRight {
        val leftList = mapNotNull {
            if (groupIdx.idx < it.groups.size) {
                it.groups[groupIdx.idx]?.range?.first
            } else {
                -1
            }
        }

        val left =
            if (minOrMax == QMinOrMax.MIN) {
                leftList.minOrNull() ?: -1
            } else {
                leftList.maxOrNull() ?: -1
            }

        val rightList = mapNotNull {
            if (groupIdx.idx < it.groups.size) {
                it.groups[groupIdx.idx]?.range?.last
            } else {
                -1
            }
        }

        val right =
            if (minOrMax == QMinOrMax.MIN) {
                rightList.minOrNull() ?: -1
            } else {
                rightList.maxOrNull() ?: -1
            }

        return QLeftRight(left, right)
    }

    // CallChain[size=9] = QLineMatchResult.align() <-[Call]- String.qAlign() <-[Call]- String.qAlignCen ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    fun align(align: QAlign = QAlign.RIGHT, keepLength: Boolean = false, oddLengthTuning: QLR): String {
        updateResult(align)

        var colIdx = 0

        while (colIdx < colDestPos.size) {
            val lines = curText.lineSequence().mapIndexed { rowIdx, line ->
                val range = matchedRange(rowIdx, colIdx) ?: return@mapIndexed line

                if (align == QAlign.CENTER) {
                    line.qMoveCenter(range, oddLengthTuning)
                } else {
                    val maxLR = colDestPos[colIdx]

                    val destLeft = when (align) {
                        QAlign.RIGHT -> maxLR.right - range.qSize + 1
                        QAlign.LEFT -> maxLR.left
                        else -> qUnreachable()
                    }

                    if (range.first < destLeft) {
                        line.qMoveRight(range, destLeft, keepLength)
                    } else {
                        line.qMoveLeft(range, destLeft, keepLength)
                    }
                }
            }

            curText = lines.joinToString("\n")

            if (!keepLength && align != QAlign.CENTER) {
                updateResult(align)
            }

            colIdx++
        }

        return curText
    }
}

// CallChain[size=10] = String.qMoveCenter() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAl ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
// always keep length
private fun String.qMoveCenter(range: IntRange, oddLengthTuning: QLR): String {
    val regionText = qSubstring(range) // includes spaces

    val nLeftSpace = regionText.qCountLeftSpace()
    val nRightSpace = regionText.qCountRightSpace()

    val nonSpaceChars = regionText.substring(nLeftSpace, regionText.length - nRightSpace)

    val nLeftSpaceTarget =
        if (oddLengthTuning == QLR.LEFT || (nLeftSpace + nRightSpace) % 2 == 0) {
            abs(nLeftSpace + nRightSpace) / 2
        } else {
            abs(nLeftSpace + nRightSpace) / 2 + 1
        }
    val nRightSpaceTarget = (nLeftSpace + nRightSpace) - nLeftSpaceTarget

    return replaceRange(range, " ".repeat(nLeftSpaceTarget) + nonSpaceChars + " ".repeat(nRightSpaceTarget))
//    return substring(
//        0, range.first
//    ) + " ".repeat(nLeftSpaceTarget) + nonSpaceChars + " ".repeat(nRightSpaceTarget) + substring(range.last + 1, length)
}

// CallChain[size=11] = QLeftRight <-[Ref]- QLineMatchResult.colDestPos <-[Call]- QLineMatchResult.a ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal data class QLeftRight(val left: Int, val right: Int)

// CallChain[size=10] = qSize <-[Call]- QLineMatchResult.align() <-[Call]- String.qAlign() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val IntRange.qSize: Int
    get() = abs(last - first) + 1

// CallChain[size=10] = String.qMoveRight() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAli ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qMoveRight(range: IntRange, destRangeLeft: Int, keepLength: Boolean): String {
    val regionText = qSubstring(range)

    val nSpaces = destRangeLeft - range.first

    if (nSpaces <= 0) return this

    val spaces = " ".repeat(nSpaces)

    return if (keepLength) {
        if (range.last + 1 > length) return this

        if (range.last + 1 + nSpaces > length) return this

        if (substring(range.last + 1, range.last + 1 + nSpaces).isNotBlank()) return this

        replaceRange(IntRange(range.first, range.last - nSpaces), spaces + regionText)
    } else {
        replaceRange(range, spaces + regionText)
    }
}

// CallChain[size=10] = String.qMoveLeft() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAlig ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qMoveLeft(range: IntRange, destRangeLeft: Int, keepLength: Boolean): String {
    if (destRangeLeft >= range.first) {
        return this
    }

    if (substring(destRangeLeft, range.first).isNotBlank()) {
        // can't move. already has some contents.
        return this
    }

    val regionText = qSubstring(range)

    val nSpaces = range.first - destRangeLeft
    if (nSpaces <= 0) return this

    // when keepLength is true, add as many spaces to the right as removed
    val rightSpaces = if (keepLength) " ".repeat(nSpaces) else ""

    // cut left spaces
    val first = substring(0, range.first - nSpaces) + regionText

    // add spaces to the right
    return first + rightSpaces + substring(range.last + 1, length)
}