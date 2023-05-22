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

// CallChain[size=3] = QExProps <-[Call]- Any.qSetExProp() <-[Call]- parent[Root]
/**
 * Minimal Version of IdentityWeakHashMap.
 */
private object QExProps {
    // CallChain[size=4] = QExProps.map <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- parent[Root]
    val map: MutableMap<WeakKey, HashMap<String, Any?>> = HashMap()

    // CallChain[size=4] = QExProps.removeGarbageCollectedEntries() <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- parent[Root]
    fun removeGarbageCollectedEntries() {
        map.keys.removeIf { it.get() == null }
    }

    // CallChain[size=3] = QExProps.get() <-[Call]- Any.qSetExProp() <-[Call]- parent[Root]
    fun get(key: Any): HashMap<String, Any?>? {
        removeGarbageCollectedEntries()

        return map[WeakKey(key)]
    }

    // CallChain[size=3] = QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- parent[Root]
    fun put(key: Any, value: HashMap<String, Any?>) {
        removeGarbageCollectedEntries()

        map[WeakKey(key)] = value
    }

    // CallChain[size=4] = QExProps.WeakKey <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- parent[Root]
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

// CallChain[size=2] = Any.qSetExProp() <-[Call]- parent[Root]
internal fun Any.qSetExProp(key: String, value: Any?) = synchronized(QExProps) {
    var props = QExProps.get(this)
    if (props == null) {
        props = HashMap(2)
        QExProps.put(this, props)
    }
    props[key] = value
}

// CallChain[size=2] = Any.qGetExProp() <-[Call]- children[Root]
internal fun Any.qGetExProp(key: String): Any? = synchronized(QExProps) {
    val props = QExProps.get(this) ?: return null

    return props[key]
}

// CallChain[size=3] = Any.qGetExPropOrDefault() <-[Call]- Any.qGetExPropOrNull() <-[Call]- parent[Root]
internal fun <T> Any.qGetExPropOrDefault(key: String, default: T): T = synchronized(QExProps) {
    val props = QExProps.get(this) ?: return default

    return props.getOrDefault(key, default) as T
}

// CallChain[size=2] = Any.qGetExPropOrNull() <-[Call]- parent[Root]
internal fun Any.qGetExPropOrNull(key: String): Any? = synchronized(QExProps) {
    return qGetExPropOrDefault(key, null)
}