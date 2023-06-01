/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("FunctionName", "UNCHECKED_CAST")

package nyab.util

import java.lang.ref.WeakReference
import java.nio.file.Path

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=7] = QExProps <-[Call]- Any.qGetExProp() <-[Call]- N.children <-[Call]- N.depthFir ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
/**
 * Minimal Version of IdentityWeakHashMap.
 */
private object QExProps {
    // CallChain[size=8] = QExProps.map <-[Call]- QExProps.get() <-[Call]- Any.qGetExProp() <-[Call]- N. ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    val map: MutableMap<WeakKey, HashMap<String, Any?>> = HashMap()

    // CallChain[size=8] = QExProps.removeGarbageCollectedEntries() <-[Call]- QExProps.get() <-[Call]- A ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun removeGarbageCollectedEntries() {
        map.keys.removeIf { it.get() == null }
    }

    // CallChain[size=7] = QExProps.get() <-[Call]- Any.qGetExProp() <-[Call]- N.children <-[Call]- N.de ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun get(key: Any): HashMap<String, Any?>? {
        removeGarbageCollectedEntries()

        return map[WeakKey(key)]
    }

    // CallChain[size=7] = QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.de ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    fun put(key: Any, value: HashMap<String, Any?>) {
        removeGarbageCollectedEntries()

        map[WeakKey(key)] = value
    }

    // CallChain[size=8] = QExProps.WeakKey <-[Call]- QExProps.get() <-[Call]- Any.qGetExProp() <-[Call] ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
    class WeakKey(key: Any) : WeakReference<Any>(key) {
        val hash = System.identityHashCode(key)

        override fun equals(other: Any?): Boolean {
            // If equals, hashCode() must be the same value
            // If both reference objects are null, then the keys are not equals
            val thisValue = this.get() ?: return false

            return thisValue === (other as WeakKey).get()
        }

        override fun hashCode() = hash
    }
}

// CallChain[size=6] = Any.qSetExProp() <-[Call]- N.children <-[Call]- N.depthFirstRecursive() <-[Ca ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun Any.qSetExProp(key: String, value: Any?) = synchronized(QExProps) {
    var props = QExProps.get(this)
    if (props == null) {
        props = HashMap(2)
        QExProps.put(this, props)
    }
    props[key] = value
}

// CallChain[size=6] = Any.qGetExProp() <-[Call]- N.children <-[Call]- N.depthFirstRecursive() <-[Ca ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun Any.qGetExProp(key: String): Any? = synchronized(QExProps) {
    val props = QExProps.get(this) ?: return null

    return props[key]
}

// CallChain[size=7] = Any.qGetExPropOrDefault() <-[Call]- Any.qGetExPropOrNull() <-[Call]- N.childr ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun <T> Any.qGetExPropOrDefault(key: String, default: T): T = synchronized(QExProps) {
    val props = QExProps.get(this) ?: return default

    return props.getOrDefault(key, default) as T
}

// CallChain[size=6] = Any.qGetExPropOrNull() <-[Call]- N.children <-[Call]- N.depthFirstRecursive() ... N.descendants() <-[Call]- N.descendantsList() <-[Call]- QTreeNodeTest.testDepthFirstSearch()[Root]
internal fun Any.qGetExPropOrNull(key: String): Any? = synchronized(QExProps) {
    return qGetExPropOrDefault(key, null)
}