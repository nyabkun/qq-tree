/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.conf

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = QE <-[Ref]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal typealias QE = QMyException

// CallChain[size=4] = QMyException <-[Ref]- QE <-[Ref]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal enum class QMyException {
    // CallChain[size=6] = QMyException.Other <-[Call]- QException.QException() <-[Ref]- QE.throwItBrack ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    Other,

    // CallChain[size=12] = QMyException.Unreachable <-[Call]- qUnreachable() <-[Call]- QFetchRule.SINGL ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    Unreachable,
    // CallChain[size=3] = QMyException.ShouldBeTrue <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    ShouldBeTrue,
    // CallChain[size=3] = QMyException.ShouldBeFalse <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    ShouldBeFalse,
    // CallChain[size=13] = QMyException.ShouldNotBeNull <-[Call]- T.qaNotNull() <-[Call]- qSrcFileAtFra ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    ShouldNotBeNull,
    // CallChain[size=6] = QMyException.ShouldNotBeZero <-[Call]- Int.qaNotZero() <-[Call]- CharSequence ... - Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    ShouldNotBeZero,
    // CallChain[size=3] = QMyException.ShouldBeEqual <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    ShouldBeEqual,
    // CallChain[size=4] = QMyException.ShouldBeEvenNumber <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    ShouldBeEvenNumber,
    // CallChain[size=12] = QMyException.FileNotFound <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLine ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    FileNotFound,
    // CallChain[size=12] = QMyException.FetchLinesFail <-[Call]- Path.qFetchLinesAround() <-[Call]- qSr ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    FetchLinesFail,
    // CallChain[size=13] = QMyException.LineNumberExceedsMaximum <-[Call]- Path.qLineAt() <-[Call]- Pat ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    LineNumberExceedsMaximum,
    // CallChain[size=5] = QMyException.TrySetAccessibleFail <-[Call]- AccessibleObject.qTrySetAccessible() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    TrySetAccessibleFail,
    // CallChain[size=7] = QMyException.IllegalArgument <-[Call]- String.qWithMaxLength() <-[Call]- Stri ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    IllegalArgument,
    // CallChain[size=6] = QMyException.ConstructorNotFound <-[Call]- Class<T>.qConstructor() <-[Call]- Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    ConstructorNotFound,
    // CallChain[size=5] = QMyException.CycleDetected <-[Call]- N.depthFirst() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    CycleDetected,

    // CallChain[size=3] = QMyException.TestFail <-[Call]- qTest() <-[Call]- main()[Root]
    TestFail;

    
}

// CallChain[size=6] = qSTACK_FRAME_FILTER <-[Call]- QException.QException() <-[Ref]- QE.throwItBrac ... -[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal val qSTACK_FRAME_FILTER: (StackWalker.StackFrame) -> Boolean = {
    !it.className.startsWith("org.gradle") &&
            !it.className.startsWith("org.testng") &&
            !it.className.startsWith("worker.org.gradle") &&
            !it.methodName.endsWith("\$default") && it.fileName != null &&
//            && !it.className.startsWith(QException::class.qualifiedName!!)
//            && it.methodName != "invokeSuspend"
            it.declaringClass != null
//            && it.declaringClass.canonicalName != null
//            && !it.declaringClass.canonicalName.startsWith("kotlin.coroutines.jvm.internal.")
//            && !it.declaringClass.canonicalName.startsWith("kotlinx.coroutines")
}