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

// CallChain[size=9] = qBG_JUMP <-[Call]- QShColor.bg <-[Propag]- QShColor.Yellow <-[Call]- String.y ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private const val qBG_JUMP = 10

// CallChain[size=9] = qSTART <-[Call]- QShColor.bg <-[Propag]- QShColor.Yellow <-[Call]- String.yel ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private const val qSTART = "\u001B["

// CallChain[size=10] = qEND <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine() ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private const val qEND = "${qSTART}0m"

// CallChain[size=10] = String.qApplyEscapeNestable() <-[Call]- String.qApplyEscapeLine() <-[Call]-  ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private fun String.qApplyEscapeNestable(start: String): String {
    val lastEnd = this.endsWith(qEND)

    return if( lastEnd ) {
            start + this.substring(0, this.length - 1).replace(qEND, qEND + start) + this[this.length - 1]
        } else {
            start + this.replace(qEND, qEND + start) + qEND
        }
}

// CallChain[size=7] = String.qColor() <-[Call]- String.yellow <-[Call]- QException.qToString() <-[C ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qColor(fg: QShColor? = null, bg: QShColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// CallChain[size=8] = String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- String.yellow < ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private fun String.qApplyEscapeLine(fg: QShColor?, bg: QShColor?, nestable: Boolean): String {
    return this.qApplyEscapeLine(
        listOfNotNull(fg?.fg, bg?.bg).toTypedArray(),
        nestable
    )
}

// CallChain[size=9] = String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine() <-[Call]- Strin ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
private fun String.qApplyEscapeLine(
    startSequences: Array<String>,
    nestable: Boolean
): String {
    val nest = nestable && this.contains(qEND)

    var text = this

    for (start in startSequences) {
        text = if (nest) {
            text.qApplyEscapeNestable(start)
        } else {
            "$start$text$qEND"
        }
    }

    return text
}

// CallChain[size=7] = QShColor <-[Ref]- String.yellow <-[Call]- QException.qToString() <-[Call]- QE ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
enum class QShColor(val code: Int) {
    // CallChain[size=8] = QShColor.Black <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]-  ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Black(30),
    // CallChain[size=8] = QShColor.Red <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]- QE ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Red(31),
    // CallChain[size=8] = QShColor.Green <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]-  ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Green(32),
    // CallChain[size=7] = QShColor.Yellow <-[Call]- String.yellow <-[Call]- QException.qToString() <-[C ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Yellow(33),
    // CallChain[size=8] = QShColor.Blue <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]- Q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Blue(34),
    // CallChain[size=8] = QShColor.Purple <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]- ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Purple(35),
    // CallChain[size=8] = QShColor.Cyan <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]- Q ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    Cyan(36),
    // CallChain[size=8] = QShColor.LightGray <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Cal ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightGray(37),

    // CallChain[size=8] = QShColor.DefaultFG <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Cal ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    DefaultFG(39),
    // CallChain[size=8] = QShColor.DefaultBG <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Cal ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    DefaultBG(49),

    // CallChain[size=8] = QShColor.DarkGray <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    DarkGray(90),
    // CallChain[size=8] = QShColor.LightRed <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightRed(91),
    // CallChain[size=8] = QShColor.LightGreen <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Ca ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightGreen(92),
    // CallChain[size=8] = QShColor.LightYellow <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[C ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightYellow(93),
    // CallChain[size=8] = QShColor.LightBlue <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Cal ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightBlue(94),
    // CallChain[size=8] = QShColor.LightPurple <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[C ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightPurple(95),
    // CallChain[size=8] = QShColor.LightCyan <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Cal ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    LightCyan(96),
    // CallChain[size=8] = QShColor.White <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]-  ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    White(97);

    // CallChain[size=8] = QShColor.fg <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]- QEx ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    val fg: String = "$qSTART${code}m"

    // CallChain[size=8] = QShColor.bg <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]- QEx ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    companion object {
        // CallChain[size=8] = QShColor.random() <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        fun random(seed: String, colors: Array<QShColor> = arrayOf(Yellow, Green, Blue, Purple, Cyan)): QShColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }

        // CallChain[size=8] = QShColor.get() <-[Propag]- QShColor.Yellow <-[Call]- String.yellow <-[Call]-  ... oString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
        fun get(ansiEscapeCode: Int): QShColor {
            return QShColor.values().find {
                it.code == ansiEscapeCode
            }!!
        }
    }
}

// CallChain[size=6] = String.qColorTarget() <-[Call]- QException.mySrcAndStack <-[Call]- QException ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal fun String.qColorTarget(ptn: Regex, fg: QShColor? = null, bg: QShColor? = null): String {
    return ptn.replace(this, "$0".qColor(fg, bg))
}

// CallChain[size=6] = String.yellow <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.yellow: String
    get() = this?.qColor(QShColor.Yellow) ?: "null".qColor(QShColor.Yellow)

// CallChain[size=8] = String.cyan <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QE ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.cyan: String
    get() = this?.qColor(QShColor.Cyan) ?: "null".qColor(QShColor.Cyan)

// CallChain[size=3] = String.light_gray <-[Call]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.light_gray: String
    get() = this?.qColor(QShColor.LightGray) ?: "null".qColor(QShColor.LightGray)

// CallChain[size=9] = String.light_green <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyle.S <-[C ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.light_green: String
    get() = this?.qColor(QShColor.LightGreen) ?: "null".qColor(QShColor.LightGreen)

// CallChain[size=11] = String.light_cyan <-[Call]- qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qL ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String?.light_cyan: String
    get() = this?.qColor(QShColor.LightCyan) ?: "null".qColor(QShColor.LightCyan)

// CallChain[size=12] = String.noStyle <-[Call]- QConsole.print() <-[Propag]- QConsole <-[Call]- QOu ... ckTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt() <-[Call]- N.depthFirst()[Root]
internal val String.noStyle: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,2}m""".re, "")
    }