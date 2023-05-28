/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package nyab.test

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import nyab.conf.QE
import nyab.conf.QMyMark
import nyab.conf.QMyTest
import nyab.match.QMMethod
import nyab.match.and
import nyab.match.or
import nyab.util.QException
import nyab.util.QLogStyle
import nyab.util.QOnlyIfStr
import nyab.util.QOut
import nyab.util.QShColor
import nyab.util.QSrcCut
import nyab.util.blue
import nyab.util.green
import nyab.util.light_blue
import nyab.util.light_gray
import nyab.util.light_green
import nyab.util.light_red
import nyab.util.light_yellow
import nyab.util.noColor
import nyab.util.qBrackets
import nyab.util.qCallerFileName
import nyab.util.qColor
import nyab.util.qFormatDuration
import nyab.util.qIsDebugging
import nyab.util.qIsInstanceMethod
import nyab.util.qIsTesting
import nyab.util.qLogStackFrames
import nyab.util.qMethods
import nyab.util.qName
import nyab.util.qNewInstance
import nyab.util.qSeparatorWithLabel
import nyab.util.qStackFrameEntryMethod
import nyab.util.qThisFileMainClass
import nyab.util.qTimeIt
import nyab.util.qToHex
import nyab.util.qToLogString
import nyab.util.qTrySetAccessible
import nyab.util.qWithNewLinePrefix
import nyab.util.qWithNewLineSurround
import nyab.util.re
import nyab.util.red
import nyab.util.separator
import nyab.util.throwIt
import nyab.util.yellow

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=2] = QTest <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class QTest(val testOnlyThis: Boolean = false)

// CallChain[size=3] = QTestHumanCheckRequired <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class QTestHumanCheckRequired(val testOnlyThis: Boolean = false)

// CallChain[size=3] = QBeforeEach <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class QBeforeEach

// CallChain[size=3] = QAfterEach <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class QAfterEach

// CallChain[size=5] = QTestResultElement <-[Ref]- QTestResult.QTestResult() <-[Call]- QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
internal data class QTestResultElement(val method: Method, val cause: Throwable?) {
    // CallChain[size=4] = QTestResultElement.success <-[Call]- QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
    val success: Boolean
        get() = cause == null
}

// CallChain[size=5] = allTestedMethods <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal val List<QTestResultElement>.allTestedMethods: String
    get() =
        "\n[${"Tested".light_blue}]\n" +
                this.joinToString("\n") {
                    if (it.success) {
                        it.method.qName().green
                    } else {
                        it.method.qName().light_red
                    }
                }

// CallChain[size=3] = QTestResult <-[Ref]- qTest() <-[Call]- main()[Root]
internal class QTestResult(val elements: List<QTestResultElement>, val time: Long) {
    // CallChain[size=5] = QTestResult.targetClasses <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val targetClasses = elements.map { it.method.declaringClass.canonicalName }

    // CallChain[size=6] = QTestResult.numSuccess <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val numSuccess = elements.count { it.success }
    // CallChain[size=3] = QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
    val numFail = elements.count { !it.success }

    // CallChain[size=5] = QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val str = qBrackets("Result".blue, "$numSuccess / ${numFail + numSuccess}", "Time".blue, time.qFormatDuration())

    // CallChain[size=4] = QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    fun printIt(out: QOut = QOut.CONSOLE) {
        out.separator(start = "")

        if (numFail > 0) {
            out.println("Fail ...".red)
            out.println(str)
            out.println(elements.allTestedMethods)

            elements.filter { !it.success }.forEach { ele ->
                out.separator()

                val cause = if (ele.cause != null && ele.cause is QException) {
                    ele.cause
                } else if (ele.cause?.cause != null && ele.cause.cause is QException) {
                    ele.cause.cause
                } else {
                    ele.cause
                }

                val causeStr = if (cause != null && cause is QException) {
                    cause.type.name
                } else if (cause != null) {
                    cause::class.simpleName ?: cause::class.java.simpleName
                } else {
                    "null"
                }

                val msg = qBrackets("Failed".light_blue, ele.method.name.red, "Cause".light_blue, causeStr.red)

                val mySrcAndMsg = if (cause != null && cause is QException) {
                    val stackColoringRegex = targetClasses.joinToString("|") { ta ->
                        """(.*($ta|${ta}Kt).*?)\("""
                    }.re

                    val stackStr = stackColoringRegex.replace(cause.mySrcAndStack, "$1".qColor(QShColor.BLUE) + "(")

                    cause.message + "\n\n" + stackStr
                } else {
                    ""
                }

                if (mySrcAndMsg.isNotEmpty()) {
                    out.println(msg + "\n")
                    out.println(mySrcAndMsg)
                } else if (cause != null) {
                    out.println(msg)
                    out.println("StackTrace >>>>>")
                    out.println(cause.stackTraceToString())
                }
            }
        } else {
            out.println("${"✨".yellow} ${" Success ".green} ${"✨".yellow}".green + "\n")
            out.println(str)
            out.println(elements.allTestedMethods)
        }
    }
}

// CallChain[size=3] = qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qTestMethods(
    methodsToTest: List<Method>,
    beforeMethod: List<Method> = emptyList(),
    afterMethod: List<Method> = emptyList(),
    out: QOut = QOut.CONSOLE
): QTestResult {
    val results = mutableListOf<QTestResultElement>()

    val timeItResult = qTimeIt(quiet = true) {
        for (method in methodsToTest) {
            // "⭐"
            out.println(qSeparatorWithLabel("${QMyMark.TEST_METHOD} " + method.qName(true)))

            method.qTrySetAccessible()

            val cause =
                if (method.qIsInstanceMethod()) {
                    val testInstance = method.declaringClass.qNewInstance()

                    try {
                        for (before in beforeMethod) {
                            before.invoke(testInstance)
                        }

                        method.invoke(testInstance)

                        for (after in afterMethod) {
                            after.invoke(testInstance)
                        }
                        null
                    } catch (e: Throwable) {
                        e
                    }
                } else {
                    try {
                        for (before in beforeMethod) {
                            before.invoke(null)
                        }

                        method.invoke(null)

                        for (after in afterMethod) {
                            after.invoke(null)
                        }
                        null
                    } catch (e: Throwable) {
                        e
                    }
                }

            results += if (cause?.cause != null && cause is InvocationTargetException) {
                QTestResultElement(method, cause.cause)
            } else {
                QTestResultElement(method, cause)
            }
        }
    }

    val result = QTestResult(results, timeItResult.time)
    result.printIt()

    return result
}

// CallChain[size=2] = qTest() <-[Call]- main()[Root]
internal fun qTest(
    vararg targetClasses: Class<*> = arrayOf(qThisFileMainClass),

    targetMethodFilter: QMMethod =
        (QMMethod.annotation(QTest::class) or QMMethod.annotation("Test")) and
                QMMethod.notAnnotation(QTestHumanCheckRequired::class) and
//                QMMethod.notAnnotation(QIgnore::class) and
                QMMethod.DeclaredOnly and
                QMMethod.NoParams and
                QMMethod.nameNotExact("main"),

    beforeMethodFilter: QMMethod =
        (
                QMMethod.annotation(QBeforeEach::class) or QMMethod.annotation("BeforeTest")
                        or QMMethod.annotation("BeforeEach")
                        or QMMethod.annotation("BeforeMethod")
                )
                and QMMethod.DeclaredOnly and QMMethod.NoParams and QMMethod.nameNotExact(
            "main"
        ),

    afterMethodFilter: QMMethod =
        (
                QMMethod.annotation(QAfterEach::class) or QMMethod.annotation("AfterTest")
                        or QMMethod.annotation("AfterEach")
                        or QMMethod.annotation("AfterMethod")
                ) and QMMethod.DeclaredOnly and QMMethod.NoParams and QMMethod.nameNotExact(
            "main"
        ),

    out: QOut = QOut.CONSOLE,

    throwsException: Boolean = true,
): QTestResult {
    val targets = targetClasses.joinToString(", ") { it.simpleName }

    out.separator()

    val callerFileName = qCallerFileName()

    val methodsToTestImmediately = targetClasses.flatMap { cls ->
        cls.qMethods().filter { method ->
            (QMMethod.DeclaredOnly and (
                    QMMethod.annotation(QTest::class) { it.testOnlyThis } or
                            QMMethod.annotation(QTestHumanCheckRequired::class) { it.testOnlyThis })).matches(method)
        }.sortedBy {
            it.name // TODO sort by line number
        }
    }

    val methodsToTest = methodsToTestImmediately.ifEmpty {
        targetClasses.flatMap {
            it.qMethods().filter { method ->
                targetMethodFilter.matches(method)
            }
        }.sortedBy {
            it.name // TODO sort by line number
        }
    }

    qLogStackFrames(
        // "🚀"
        msg = "${QMyMark.TEST_START} Test Start ${QMyMark.TEST_START}\n$targets".light_blue,
        style = QLogStyle.MSG_AND_STACK,
        frames = listOf(
            qStackFrameEntryMethod { frame ->
                frame.fileName == callerFileName
            }
        )
    )

    val before = targetClasses.flatMap {
        it.qMethods().filter { method ->
            beforeMethodFilter.matches(method)
        }
    }

    val after = targetClasses.flatMap {
        it.qMethods().filter { method ->
            afterMethodFilter.matches(method)
        }
    }

    val result = qTestMethods(methodsToTest, before, after)

    if (result.numFail > 0 && throwsException) {
        QE.TestFail.throwIt()
    } else {
        return result
    }
}

// CallChain[size=3] = qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun qFailMsg(msg: String = "it is null"): String {
    val cMsg = "[$msg]".colorIt
    return "${QMyMark.WARN} $cMsg"
}

// CallChain[size=3] = qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun qFailMsg(actual: Any?, msg: String = "is not equals to", expected: Any?): String {
    val cMsg = "[$msg]".colorIt
    val actualStr = actual.qToLogString() + " " + "(actual)".light_green
    val expectedStr = expected.qToLogString() + " " + "(expected)".blue
    return "${QMyMark.WARN} ${actualStr.qWithNewLineSurround(onlyIf = QOnlyIfStr.Always)}$cMsg${
        expectedStr.qWithNewLinePrefix(onlyIf = QOnlyIfStr.Always)
    }"
}

// CallChain[size=4] = colorIt <-[Call]- qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private val String.colorIt: String
    get() = this.light_yellow

// CallChain[size=3] = qThrowIt() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun qThrowIt(msg: String, exception: QE) {
    throw QException(exception, msg, null, stackDepth = 2, srcCut = QSrcCut.MULTILINE_INFIX_NOCUT)
}

// CallChain[size=2] = Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal infix fun Any?.shouldBe(expected: Any?) {
    if (!qOkToTest()) return

    if (expected is Boolean && this is Boolean && this != expected) {
        if (expected) {
            val msg = qFailMsg("it is false")

            qThrowIt(msg, QE.ShouldBeTrue)
        } else {
            val msg = qFailMsg("it is true")

            qThrowIt(msg, QE.ShouldBeFalse)
        }
    }

    val thisStr = this.qToLogString()
    val expectedStr = expected.qToLogString()

    if (thisStr.trim().noColor != expectedStr.trim().noColor) {
        val msg = qFailMsg(thisStr, "is not equals to", expectedStr)

        val diffIdx =
            if (expectedStr.length < thisStr.length) {
                expectedStr.mapIndexedNotNull { idx, ch ->
                    if (thisStr.length > idx && thisStr[idx] != ch) {
                        idx
                    } else {
                        null
                    }
                }.firstOrNull()
            } else {
                thisStr.mapIndexedNotNull { idx, ch ->
                    if (expectedStr.length > idx && expectedStr[idx] != ch) {
                        idx
                    } else {
                        null
                    }
                }.firstOrNull()
            }

        if (diffIdx != null) {
            val expectedChar = expectedStr[diffIdx]
            val actualChar = thisStr[diffIdx]

            qThrowIt(
                msg + "\n" + qBrackets(
                    "expected char at idx=$diffIdx".blue,
                    expectedChar.toString().blue + (" ( CharCodeHex = " + expectedChar.qToHex() + " )").light_gray,
                    "actual char at idx=$diffIdx".light_green,
                    actualChar.toString().light_green + (" ( CharCodeHex = " + actualChar.qToHex() + " )").light_gray
                ),
                QE.ShouldBeEqual
            )
        } else {
            qThrowIt(msg, QE.ShouldBeEqual)
        }
    }
}

// CallChain[size=3] = qOkToTest() <-[Call]- Any?.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private inline fun qOkToTest(): Boolean {
    return QMyTest.forceTestMode || qIsTesting || qIsDebugging
}