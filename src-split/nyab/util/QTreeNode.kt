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

// << Root of the CallChain >>
/**
 * Represents a node in a tree structure.
 */
class QTreeNode<T : Any?>(override val value: T) : QTreeNodeI<T> {
    // << Root of the CallChain >>
    override fun toString(): String {
        return toTreeNodeString()
    }
}

// << Root of the CallChain >>
/**
 * Represents a node in a tree data structure that can fill its child nodes.
 *
 * When the [fillTree] function of the root node is called, it invokes the [fillChildNodes] function
 * of descendant nodes in a breadth-first order to fill the child nodes.
 */
interface QLazyTreeNode<V : Any?> : QTreeNodeI<V> {
    // << Root of the CallChain >>
    /**
     * Checks if the node has child nodes that need to be filled.
     */
    fun hasChildNodesToFill(): Boolean {
        return fillChildNodes().isNotEmpty()
    }

    // << Root of the CallChain >>
    /**
     * Fills and returns the child nodes of the current node.
     */
    fun fillChildNodes(): List<QLazyTreeNode<V>>
}

// << Root of the CallChain >>
/**
 * Represents a node in a tree data structure. [value] can be of any type, but within a single tree,
 * the type of [value] needs to be consistent.
 */
interface QTreeNodeI<V : Any?> {
    // << Root of the CallChain >>
    val value: V

    // << Root of the CallChain >>
    fun toTreeNodeString(): String {
        return value.toString()
    }
}

// << Root of the CallChain >>
/**
 * When the [fillTree] function of the root node is called, it invokes the [fillChildNodes] function
 * of descendant nodes in a breadth-first order.
 */
fun <N : QLazyTreeNode<*>> N.fillTree(maxDepth: Int = Int.MAX_VALUE, maxEntry: Int = 2000): N {
    if (!hasChildNodesToFill())
        return this

    val stack = mutableListOf<N>()

    stack += this

    var nEntry = 1

    while (stack.isNotEmpty()) {
        // pop
        val node = stack.removeAt(stack.size - 1)

        if (!node.hasChildNodesToFill())
            continue

        if (node.depth() >= maxDepth)
            continue

        val children = node.fillChildNodes() as List<N>

        nEntry += children.size

        node add children

        stack += children

        if( nEntry >= maxEntry ) {
            break
        }
    }

    return this
}

// << Root of the CallChain >>
/**
 * Obtain the root node of the tree structure to which this node belongs.
 */
fun <N : QTreeNodeI<*>> N.root(): N {
    return if (isRoot()) {
        this
    } else {
        this.ancestors().last()
    }
}

// << Root of the CallChain >>
/**
 * Returns whether this node is the root node or not.
 */
fun <N : QTreeNodeI<*>> N.isRoot(): Boolean = parent == null

// << Root of the CallChain >>
/**
 * Returns true if this node has a cycle and does not form a valid tree structure.
 */
fun <N : QTreeNodeI<*>> N.hasCycle(): Boolean {

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

// << Root of the CallChain >>
/**
 * Returns true if this node is a leaf node, meaning it does not have any child nodes.
 */
val <N : QTreeNodeI<*>> N.isLeaf: Boolean
    get() = children.isEmpty()

// << Root of the CallChain >>
var <N : QTreeNodeI<*>> N.parent: N?
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

// << Root of the CallChain >>
/**
 * Obtain the child nodes of this node.
 */
val <N : QTreeNodeI<*>> N.children: List<N>
    get() {
        return if (this.qGetExPropOrNull("#children") == null) {
            val emptyList = mutableListOf<N>()
            this.qSetExProp("#children", emptyList)
            emptyList
        } else {
            this.qGetExProp("#children") as List<N>
        }
    }

// << Root of the CallChain >>
/**
 * Returns the depth of this node from the root node. If this node is the root node, it returns 0.
 */
fun <N : QTreeNodeI<*>> N.depth(): Int {
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

// << Root of the CallChain >>
/**
 * This represents the style used when outputting a tree structure as a string.
 * When using Unicode characters, there is a possibility of character encoding issues
 * depending on the environment.
 */
enum class QTreeStyle(val plus: String, val vert: String, val end: String, val hyphen: String) {
    // << Root of the CallChain >>
    ASCII("+", "|", "\\", "-"),
    // << Root of the CallChain >>
    UNICODE("├", "│", "└", "─")
}

// << Root of the CallChain >>
/**
 * Output the contents of the tree structure as a string.
 */
fun <N : QTreeNodeI<*>> N.tree(
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

// << Root of the CallChain >>
/**
 * It traverses the descendant nodes in the order specified by the [algorithm].
 * The return value is of type [Sequence].
 */
fun <N : QTreeNodeI<*>> N.descendants(
    algorithm: QSearchAlgo = QSearchAlgo.BreadthFirst,
): Sequence<N> {
    return when (algorithm) {
        QSearchAlgo.BreadthFirst -> breadthFirst()
        QSearchAlgo.DepthFirst -> depthFirst()
        QSearchAlgo.DepthFirstRecursive -> depthFirstRecursive()
    }
}

// << Root of the CallChain >>
private fun <N : QTreeNodeI<*>> N.mark(marked: HashSet<N>) {
    marked += this
}

// << Root of the CallChain >>
private fun <N : QTreeNodeI<*>> N.isChildrenMarked(marked: HashSet<N>): Boolean =
    children.all {
        it.isMarked(marked)
    }

// << Root of the CallChain >>
private fun <N : QTreeNodeI<*>> N.isMarked(marked: HashSet<N>): Boolean =
    marked.contains(this)

// << Root of the CallChain >>
private fun <N : QTreeNodeI<*>> N.clearMark(marked: HashSet<N>) {
    marked.remove(this)
}

// << Root of the CallChain >>
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

// << Root of the CallChain >>
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

// << Root of the CallChain >>
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

// << Root of the CallChain >>
/**
 * Add a child node to this node and returns the added child node.
 */
infix fun <N : QTreeNodeI<*>> N.add(child: N): N {
    (children as MutableList<N>) += child
    child.parent = this
    return child
}

// << Root of the CallChain >>
/**
 * Add a child nodes to this node and returns the added child nodes.
 */
infix fun <N : QTreeNodeI<*>> N.add(children: List<N>): List<N> {
    (this.children as MutableList<N>) += children

    for (ch in children) {
        ch.parent = this
    }

    return children
}

// << Root of the CallChain >>
/**
 * Create and add a child node which has [childNodeValue] as a value and returns the added child node.
 */
inline infix fun <V, reified N : QTreeNodeI<V>> N.add(childNodeValue: V): N {
    return add(newNode(childNodeValue))
}

// << Root of the CallChain >>
/**
 * Create a node of the same type as the calling node.
 */
inline infix fun <V, reified N : QTreeNodeI<V>> N.newNode(value: V): N {
    val con = N::class.primaryConstructor!!
    con.isAccessible = true
    return con.call((value))
}

// << Root of the CallChain >>
/**
 *  It traverses the parent nodes up to the root node and returns them as a [Sequence].
 *  The last element in the sequence is the root node of the tree structure.
 *  The node itself is not included in the sequence.
 */
fun <N : QTreeNodeI<*>> N.ancestors(check: HashSet<N> = HashSet()): Sequence<N> = generateSequence(
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

// << Root of the CallChain >>
/**
 * It traverses the descendant nodes in the order specified by the [algorithm].
 * The return value is of type [List].
 */
fun <N : QTreeNodeI<*>> N.descendantsList(algorithm: QSearchAlgo = QSearchAlgo.BreadthFirst): List<N> {
    return descendants(algorithm).toList()
}

// << Root of the CallChain >>
/**
 *  It traverses the parent nodes up to the root node and returns them as a [List].
 *  The last element in the list is the root node of the tree structure.
 *  The node itself is not included in the list.
 */
fun <N : QTreeNodeI<*>> N.ancestorsList(check: HashSet<N> = HashSet()): List<N> = ancestors(check).toList()

// << Root of the CallChain >>
enum class QSearchAlgo {
    // << Root of the CallChain >>
    BreadthFirst,
    // << Root of the CallChain >>
    DepthFirst,
    // << Root of the CallChain >>
    DepthFirstRecursive
}