/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("UNCHECKED_CAST")

package nyab.util

import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import nyab.conf.QE

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=2] = QTreeNode <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Represents a node in a tree structure.
 */
internal class QTreeNode<T : Any?>(override val value: T) : QTreeNodeI<T> {
    // CallChain[size=3] = QTreeNode.toString() <-[Propag]- QTreeNode <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    override fun toString(): String {
        return toTreeNodeString()
    }
}

// CallChain[size=3] = QTreeNodeI <-[Ref]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Represents a node in a tree data structure. [value] can be of any type, but within a single tree,
 * the type of [value] needs to be consistent.
 */
internal interface QTreeNodeI<V : Any?> {
    // CallChain[size=4] = QTreeNodeI.value <-[Propag]- QTreeNodeI <-[Ref]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    val value: V

    // CallChain[size=4] = QTreeNodeI.toTreeNodeString() <-[Propag]- QTreeNodeI <-[Ref]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun toTreeNodeString(): String {
        return value.toString()
    }
}

// CallChain[size=2] = N.hasCycle() <-[Call]- QTreeNodeTest.testCycleDetection()[Root]
/**
 * Returns true if this node has a cycle and does not form a valid tree structure.
 */
internal fun <N : QTreeNodeI<*>> N.hasCycle(): Boolean {

    try {
        for (n in this.depthFirst(true)) {
            // do nothing
        }
    } catch (e: QException) {
        if (e.type == QE.CycleDetected) {
            return true
        } else {
            throw e
        }
    }

    return false
}

// CallChain[size=4] = N.parent <-[Call]- N.add() <-[Call]- N.add() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal var <N : QTreeNodeI<*>> N.parent: N?
    get() =
        this.qGetExPropOrNull("#parent") as N?
    set(value) {
        val oldParent = this.parent
        if (oldParent != null) {
            val oldChildren = oldParent.children
            (oldChildren as MutableList<N>).remove(this)
        }

        if (value != null && !value.children.contains(this)) {
            (value.children as MutableList<N>).add(this)
        }

        this.qSetExProp("#parent", value)
    }

// CallChain[size=5] = N.children <-[Call]- N.depthFirstRecursive() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Obtain the child nodes of this node.
 */
internal val <N : QTreeNodeI<*>> N.children: List<N>
    get() {
        return if (this.qGetExPropOrNull("#children") == null) {
            val emptyList = mutableListOf<N>()
            this.qSetExProp("#children", emptyList)
            emptyList
        } else {
            this.qGetExProp("#children") as List<N>
        }
    }

// CallChain[size=3] = N.depth() <-[Call]- N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
/**
 * Returns the depth of this node from the root node. If this node is the root node, it returns 0.
 */
internal fun <N : QTreeNodeI<*>> N.depth(): Int {
    var count = 0
    var parent = this.parent

    while (parent != null) {
        count++
        parent = parent.parent
    }

    return count
//    return ancestorsList().size
//    return ancestorsSeq().count()
}

// CallChain[size=3] = QTreeStyle <-[Ref]- N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
/**
 * This represents the style used when outputting a tree structure as a string.
 * When using Unicode characters, there is a possibility of character encoding issues
 * depending on the environment.
 */
internal enum class QTreeStyle(val plus: String, val vert: String, val end: String, val hyphen: String) {
    // CallChain[size=4] = QTreeStyle.ASCII <-[Propag]- QTreeStyle.QTreeStyle() <-[Call]- N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
    ASCII("+", "|", "\\", "-"),
    // CallChain[size=3] = QTreeStyle.UNICODE <-[Call]- N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
    UNICODE("├", "│", "└", "─")
}

// CallChain[size=2] = N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
/**
 * Output the contents of the tree structure as a string.
 */
internal fun <N : QTreeNodeI<*>> N.tree(
    style: QTreeStyle = QTreeStyle.UNICODE,
    color: QShColor? = QShColor.LightYellow,
    visitChecker: HashSet<N> = HashSet(),
): String {
    val hyphen = style.hyphen.repeat(3)

    val sb = StringBuilder()

    this.mark(visitChecker)

    // print root node
    sb += this.toTreeNodeString() + "\n"

    for (node in depthFirst().drop(1)) {
        if (node.isMarked(visitChecker))
            continue

        val depth = node.depth()

        node.mark(visitChecker)

        val head = if (node.parent?.isChildrenMarked(visitChecker) == true) {
            style.end.qColor(color)
        } else {
            style.plus.qColor(color)
        }

        sb += if (depth == 1) {
            head + hyphen.qColor(color) + " " + node.toTreeNodeString() + "\n"
        } else {
            val preHead = node.ancestors().drop(1).map {
                if (it.isChildrenMarked(visitChecker)) {
                    "     "
                } else {
                    style.vert.qColor(color) + "    "
                }
            }.toList().reversed().joinToString("")

            preHead + head + hyphen.qColor(color) + " " + node.toTreeNodeString() + "\n"
        }
    }
    return sb.toString()
}

// CallChain[size=3] = N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * It traverses the descendant nodes in the order specified by the [algorithm].
 * The return value is of type [Sequence].
 */
internal fun <N : QTreeNodeI<*>> N.descendants(
    algorithm: QSearchAlgo = QSearchAlgo.BreadthFirst,
): Sequence<N> {
    return when (algorithm) {
        QSearchAlgo.BreadthFirst -> breadthFirst()
        QSearchAlgo.DepthFirst -> depthFirst()
        QSearchAlgo.DepthFirstRecursive -> depthFirstRecursive()
    }
}

// CallChain[size=5] = N.mark() <-[Call]- N.depthFirstRecursive() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun <N : QTreeNodeI<*>> N.mark(marked: HashSet<N>) {
    marked += this
}

// CallChain[size=3] = N.isChildrenMarked() <-[Call]- N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
private fun <N : QTreeNodeI<*>> N.isChildrenMarked(marked: HashSet<N>): Boolean =
    children.all {
        it.isMarked(marked)
    }

// CallChain[size=5] = N.isMarked() <-[Call]- N.depthFirstRecursive() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun <N : QTreeNodeI<*>> N.isMarked(marked: HashSet<N>): Boolean =
    marked.contains(this)

// CallChain[size=4] = N.breadthFirst() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun <N : QTreeNodeI<*>> N.breadthFirst(): Sequence<N> = sequence {
    val check: HashSet<N> = HashSet()

    var curNodes = mutableListOf<N>()
    curNodes += this@breadthFirst
    var nextDepthNodes = mutableListOf<N>()

    while (curNodes.isNotEmpty()) {
        for (node in curNodes) {
            if (node.isMarked(check)) {
                // already visited
                continue
            }

            yield(node)

            node.mark(check)

            nextDepthNodes += node.children
        }

        curNodes = nextDepthNodes
        nextDepthNodes = mutableListOf()
    }
}

// CallChain[size=4] = N.depthFirstRecursive() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun <N : QTreeNodeI<*>> N.depthFirstRecursive(check: HashSet<N> = HashSet()): Sequence<N> = sequence {
    val thisNode = this@depthFirstRecursive

    thisNode.mark(check)

    yield(thisNode)

    for (node in thisNode.children) {
        if (node.isMarked(check)) {
            // already visited
            continue
        }

        node.mark(check)

        // recursive call
        yieldAll(node.depthFirstRecursive(check))
    }
}

// CallChain[size=4] = N.depthFirst() <-[Call]- N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
private fun <N : QTreeNodeI<*>> N.depthFirst(raiseExceptionIfCyclic: Boolean = false): Sequence<N> = sequence {
    val check: HashSet<N> = HashSet()
    val stack = mutableListOf<N>()

    stack += this@depthFirst

    this@depthFirst.mark(check)

    while (stack.isNotEmpty()) {
        val node = stack.removeAt(stack.size - 1)

        yield(node)

        for (n in node.children.reversed()) {
            if (n.isMarked(check)) {
                if (raiseExceptionIfCyclic) {
                    QE.CycleDetected.throwIt(n)
                }

                // already visited
                continue
            }

            stack += n

            node.mark(check)
        }
    }
}

// CallChain[size=3] = N.add() <-[Call]- N.add() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Add a child node to this node and returns the added child node.
 */
internal infix fun <N : QTreeNodeI<*>> N.add(child: N): N {
    (children as MutableList<N>) += child
    child.parent = this
    return child
}

// CallChain[size=2] = N.add() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Create and add a child node which has [childNodeValue] as a value and returns the added child node.
 */
internal inline infix fun <V, reified N : QTreeNodeI<V>> N.add(childNodeValue: V): N {
    return add(newNode(childNodeValue))
}

// CallChain[size=3] = N.newNode() <-[Call]- N.add() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Create a node of the same type as the calling node.
 */
internal inline infix fun <V, reified N : QTreeNodeI<V>> N.newNode(value: V): N {
    val con = N::class.primaryConstructor!!
    con.isAccessible = true
    return con.call((value))
}

// CallChain[size=3] = N.ancestors() <-[Call]- N.tree() <-[Call]- QTreeNodeTest.testLargeIntTree()[Root]
/**
 *  It traverses the parent nodes up to the root node and returns them as a [Sequence].
 *  The last element in the sequence is the root node of the tree structure.
 *  The node itself is not included in the sequence.
 */
internal fun <N : QTreeNodeI<*>> N.ancestors(check: HashSet<N> = HashSet()): Sequence<N> = generateSequence(
    seedFunction = {
        this@ancestors.mark(check)
        this@ancestors.parent
    },
    nextFunction = {
        it.mark(check)

        val parent = it.parent

        if (parent != null && parent.isMarked(check)) {
            null
        } else {
            parent
        }
    }
)

// CallChain[size=2] = N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * It traverses the descendant nodes in the order specified by the [algorithm].
 * The return value is of type [List].
 */
internal fun <N : QTreeNodeI<*>> N.descendantsList(algorithm: QSearchAlgo = QSearchAlgo.BreadthFirst): List<N> {
    return descendants(algorithm).toList()
}

// CallChain[size=2] = QSearchAlgo <-[Ref]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal enum class QSearchAlgo {
    // CallChain[size=3] = QSearchAlgo.BreadthFirst <-[Propag]- QSearchAlgo.DepthFirstRecursive <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    BreadthFirst,
    // CallChain[size=2] = QSearchAlgo.DepthFirst <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    DepthFirst,
    // CallChain[size=2] = QSearchAlgo.DepthFirstRecursive <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    DepthFirstRecursive
}