/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("UNUSED_VARIABLE")

package tree

import nyab.util.QLazyTreeNode
import nyab.util.QTreeNode
import nyab.util.QSearchAlgo
import nyab.util.QShColor
import nyab.util.QTreeStyle
import nyab.util.add
import nyab.util.descendantsList
import nyab.util.fillTree
import nyab.util.tree
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.name

fun main() {
    // First, you have to create the root node.
    val root = QTreeNode(0)

    val node1 = root add 1
    val node2 = root add 2
    val node3 = node2 add 3
    val node4 = node2 add 4
    val node5 = node4 add 5
    val node6 = node4 add 6
    val node7 = node2 add 7

    val unicodeTree = root.tree(color = QShColor.GREEN, style = QTreeStyle.UNICODE)

    println(unicodeTree)

    val asciiTree = root.tree(color = QShColor.BLUE, style = QTreeStyle.ASCII)

    println(asciiTree)

    println()

    val depthFirstResult = root.descendantsList(QSearchAlgo.DepthFirst).toString()

    println("DepthFirst   : $depthFirstResult") // [0, 1, 2, 3, 4, 5, 6, 7]

    val breadthFirstResult = root.descendantsList(QSearchAlgo.BreadthFirst).toString()

    println("BreadthFirst : $breadthFirstResult") // [0, 1, 2, 3, 4, 7, 5, 6]

    println()

    // node can store anything
    val rootA = QTreeNode("A")
    val nodeB = rootA add "B"
    val nodeC = nodeB add "C"
    val nodeD = nodeB add "D"
    val nodeE = nodeD add "E"
    val nodeF = nodeE add "F"
    val nodeG = nodeC add "G"

    val textTree = rootA.tree(color = QShColor.CYAN, style = QTreeStyle.UNICODE)

    println(textTree)

    // You can implement QLazyNode for more complicated situations.
    class QFileNode(override val value: Path) : QLazyTreeNode<Path> {
        override fun hasChildNodesToFill(): Boolean {
            return value.isDirectory()
        }

        override fun fillChildNodes(): List<QFileNode> = Files.walk(value, 1).filter {
            it != value
        }.map {
            QFileNode(it)
        }.toList()

        override fun toTreeNodeString(): String {
            return value.name
        }
    }

    val rootDir = Paths.get("rsc-test/root-dir").toAbsolutePath()

    val fileTree = QFileNode(rootDir).fillTree(maxDepth = 2).tree()

    println(fileTree)
}
