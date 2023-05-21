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

import nyab.test.QTest
import nyab.test.qTest
import nyab.test.shouldBe

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// << Root of the CallChain >>
fun main() {
    qTest()
//    qTestHumanCheck()
}

// << Root of the CallChain >>
@Suppress("UNUSED_VARIABLE")
class QTreeNodeTest {
    // << Root of the CallChain >>
    @QTest
    fun testSingleNode() {
        val node1 = QTreeNode(1)

        node1.tree() shouldBe """
            1
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun testSimpleTree() {
        val node1 = QTreeNode(1)
        val node2 = node1 add 2

        node1.tree() shouldBe """
            1
            └─── 2
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun testParentChildBinding() {
        val node1 = QTreeNode(1)
        val node2 = node1 add 2

        node2.parent shouldBe node1
        node1.children.first() shouldBe node2

        node1.parent = node2

        node1.parent shouldBe node2
        node2.children.first() shouldBe node1
    }

    // << Root of the CallChain >>
    @QTest
    fun testCycleDetection() {
        val node1 = QTreeNode(1)
        val node2 = node1 add 2

        node1.parent = node2

        node1.hasCycle() shouldBe true

        node1.parent = null

        node1.hasCycle() shouldBe false
    }

    // << Root of the CallChain >>
    @QTest
    fun testLargeIntTree() {
        val root = QTreeNode(0)

        val node1 = root add 1
        val node2 = root add 2
        val node3 = node2 add 3
        val node4 = node2 add 4
        val node5 = node4 add 5
        val node6 = node4 add 6
        val node7 = node2 add 7
        val node8 = node7 add 8
        val node9 = root add 9
        val node10 = node9 add 10
        val node11 = node9 add 11
        val node12 = root add 12
        val node13 = node12 add 13
        val node14 = node12 add 14
        val node15 = node14 add 15
        val node16 = node15 add 16
        val node17 = root add 17
        val node18 = node17 add 18
        val node19 = root add 19

        root.tree(color = null) shouldBe """
            0
            ├─── 1
            ├─── 2
            │    ├─── 3
            │    ├─── 4
            │    │    ├─── 5
            │    │    └─── 6
            │    └─── 7
            │         └─── 8
            ├─── 9
            │    ├─── 10
            │    └─── 11
            ├─── 12
            │    ├─── 13
            │    └─── 14
            │         └─── 15
            │              └─── 16
            ├─── 17
            │    └─── 18
            └─── 19
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun testBreadthFirstSearch() {
        val root = QTreeNode(0)

        val node1 = root add 1
        val node2 = root add 2
        val node3 = root add 3

        val node4 = node2 add 4
        val node5 = node4 add 5

        root.descendantsList(QSearchAlgo.BreadthFirst) shouldBe "[0, 1, 2, 3, 4, 5]"
    }

    // << Root of the CallChain >>
    @QTest
    fun testDepthFirstSearch() {
        val root = QTreeNode(0)

        val node1 = root add 1
        val node2 = node1 add 2
        val node3 = node2 add 3
        val node4 = node2 add 4
        val node5 = root add 5

        root.descendantsList(QSearchAlgo.DepthFirst) shouldBe "[0, 1, 2, 3, 4, 5]"
        root.descendantsList(QSearchAlgo.DepthFirstRecursive) shouldBe "[0, 1, 2, 3, 4, 5]"
    }
}