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

import kotlin.math.absoluteValue

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=9] = qBG_JUMP <-[Call]- QShColor.bg <-[Propag]- QShColor.YELLOW <-[Call]- yellow < ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private const val qBG_JUMP = 10

// CallChain[size=9] = qSTART <-[Call]- QShColor.bg <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[ ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private const val qSTART = "\u001B["

// CallChain[size=9] = qEND <-[Call]- String.qColorLine() <-[Call]- String.qColor() <-[Call]- yellow ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private const val qEND = "${qSTART}0m"

// CallChain[size=10] = qMASK_COLORED <-[Call]- String.qApplyColorNestable() <-[Call]- String.qColor ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private val qMASK_COLORED by lazy {
    QMaskBetween(
        qSTART,
        qEND,
        qSTART,
        escapeChar = '\\',
        targetNestDepth = 1,
        maskIncludeStartAndEndSequence = false
    )
}

// CallChain[size=9] = String.qApplyColorNestable() <-[Call]- String.qColorLine() <-[Call]- String.q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private fun String.qApplyColorNestable(colorStart: String): String {
    val re = "(?s)(\\Q$qEND\\E)(.+?)(\\Q$qSTART\\E|$)".re
    val replace = "$1$colorStart$2$qEND$3"
    val re2 = "^(?s)(.*?)(\\Q$qSTART\\E)"
    val replace2 = "$colorStart$1$qEND$2"

    return this.qMaskAndReplace(
        qMASK_COLORED,
        re,
        replace
    ).qReplaceFirstIfNonEmptyStringGroup(re2, 1, replace2)
}

// CallChain[size=7] = String.qColor() <-[Call]- yellow <-[Call]- QException.qToString() <-[Call]- Q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qColor(fg: QShColor? = null, bg: QShColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qColorLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qColorLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// CallChain[size=8] = String.qColorLine() <-[Call]- String.qColor() <-[Call]- yellow <-[Call]- QExc ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private fun String.qColorLine(
    fg: QShColor? = null,
    bg: QShColor? = null,
    nestable: Boolean = true,
): String {
    val nest = nestable && this.contains(qEND)

    val fgApplied = if (fg != null) {
        val fgStart = fg.fg

        if (nest) {
            this.qApplyColorNestable(fgStart)
        } else {
            "$fgStart$this$qEND"
        }
    } else {
        this
    }

    val bgApplied = if (bg != null) {
        val bgStart = bg.bg

        if (nest) {
            fgApplied.qApplyColorNestable(bgStart)
        } else {
            "$bgStart$fgApplied$qEND"
        }
    } else {
        fgApplied
    }

    return bgApplied
}

// CallChain[size=12] = noColor <-[Call]- QConsole.print() <-[Propag]- QConsole <-[Call]- QOut.CONSO ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String.noColor: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,2}m""".re, "")
    }

// CallChain[size=7] = QShColor <-[Ref]- yellow <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
enum class QShColor(val code: Int) {
    // CallChain[size=8] = QShColor.BLACK <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExcept ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    BLACK(30),
    // CallChain[size=8] = QShColor.RED <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExceptio ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    RED(31),
    // CallChain[size=8] = QShColor.GREEN <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExcept ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    GREEN(32),
    // CallChain[size=7] = QShColor.YELLOW <-[Call]- yellow <-[Call]- QException.qToString() <-[Call]- Q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    YELLOW(33),
    // CallChain[size=8] = QShColor.BLUE <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExcepti ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    BLUE(34),
    // CallChain[size=8] = QShColor.MAGENTA <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExce ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    MAGENTA(35),
    // CallChain[size=8] = QShColor.CYAN <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExcepti ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    CYAN(36),
    // CallChain[size=8] = QShColor.LIGHT_GRAY <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QE ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_GRAY(37),

    // CallChain[size=8] = QShColor.DARK_GRAY <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QEx ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    DARK_GRAY(90),
    // CallChain[size=8] = QShColor.LIGHT_RED <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QEx ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_RED(91),
    // CallChain[size=8] = QShColor.LIGHT_GREEN <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- Q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_GREEN(92),
    // CallChain[size=8] = QShColor.LIGHT_YELLOW <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]-  ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_YELLOW(93),
    // CallChain[size=8] = QShColor.LIGHT_BLUE <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QE ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_BLUE(94),
    // CallChain[size=8] = QShColor.LIGHT_MAGENTA <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_MAGENTA(95),
    // CallChain[size=8] = QShColor.LIGHT_CYAN <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QE ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LIGHT_CYAN(96),
    // CallChain[size=8] = QShColor.WHITE <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExcept ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    WHITE(97);

    // CallChain[size=8] = QShColor.fg <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QException ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    /** ANSI modifier string to apply the color to the text itself */
    val fg: String = "$qSTART${code}m"

    // CallChain[size=8] = QShColor.bg <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QException ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    /** ANSI modifier string to apply the color the text's background */
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    companion object {
        // CallChain[size=8] = QShColor.random() <-[Propag]- QShColor.YELLOW <-[Call]- yellow <-[Call]- QExc ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        fun random(seed: String, colors: Array<QShColor> = arrayOf(YELLOW, GREEN, BLUE, MAGENTA, CYAN)): QShColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }
    }
}

// CallChain[size=6] = String.qColorTarget() <-[Call]- QException.mySrcAndStack <-[Call]- QException ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qColorTarget(ptn: Regex, color: QShColor = QShColor.LIGHT_YELLOW): String {
    return ptn.replace(this, "$0".qColor(color))
}

// CallChain[size=6] = yellow <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.yellow: String
    get() = this?.qColor(QShColor.YELLOW) ?: "null".qColor(QShColor.YELLOW)

// CallChain[size=15] = cyan <-[Call]- QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMask ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.cyan: String
    get() = this?.qColor(QShColor.CYAN) ?: "null".qColor(QShColor.CYAN)

// CallChain[size=3] = light_gray <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.light_gray: String
    get() = this?.qColor(QShColor.LIGHT_GRAY) ?: "null".qColor(QShColor.LIGHT_GRAY)

// CallChain[size=9] = light_green <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyle.S <-[Call]- q ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.light_green: String
    get() = this?.qColor(QShColor.LIGHT_GREEN) ?: "null".qColor(QShColor.LIGHT_GREEN)

// CallChain[size=11] = light_cyan <-[Call]- qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.light_cyan: String
    get() = this?.qColor(QShColor.LIGHT_CYAN) ?: "null".qColor(QShColor.LIGHT_CYAN)