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

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists
import kotlin.reflect.KClass
import nyab.conf.QE
import nyab.conf.QMyPath
import nyab.match.QMMethod

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=4] = AccessibleObject.qTrySetAccessible() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun AccessibleObject.qTrySetAccessible() {
    try {
        if (!this.trySetAccessible()) {
            QE.TrySetAccessibleFail.throwIt(this)
        }
    } catch (e: SecurityException) {
        QE.TrySetAccessibleFail.throwIt(this, e = e)
    }
}

// CallChain[size=3] = qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
internal val qThisFileMainClass: Class<*>
    get() = qCallerFileMainClass()

// CallChain[size=3] = Class<*>.qMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun Class<*>.qMethods(matcher: QMMethod = QMMethod.DeclaredOnly): List<Method> {
    val allMethods = if (matcher.declaredOnly) declaredMethods else methods
    return allMethods.filter { matcher.matches(it) }
}

// CallChain[size=4] = Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun <T : Any> Class<T>.qNewInstance(vararg params: Any): T {
    val constructor = qConstructor(*params, declaredOnly = false)
    return constructor.newInstance()
}

// CallChain[size=5] = Class<T>.qConstructor() <-[Call]- Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun <T : Any> Class<T>.qConstructor(vararg params: Any, declaredOnly: Boolean = false): Constructor<T> {
    return if (declaredOnly) {
        this.getDeclaredConstructor(*params.map { it::class.java }.toTypedArray())
            .qaNotNull(QE.ConstructorNotFound)
    } else {
        this.getConstructor(*params.map { it::class.java }.toTypedArray())
            .qaNotNull(QE.ConstructorNotFound)
    }
}

// CallChain[size=7] = Class<*>.qPrimitiveToWrapper() <-[Call]- Class<*>.qIsAssignableFrom() <-[Call ... -[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
internal fun Class<*>.qPrimitiveToWrapper(): Class<*> = qJVMPrimitiveToWrapperMap[this] ?: this

// CallChain[size=8] = qJVMPrimitiveToWrapperMap <-[Call]- Class<*>.qPrimitiveToWrapper() <-[Call]-  ... -[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
internal val qJVMPrimitiveToWrapperMap by lazy {
    val map = HashMap<Class<*>, Class<*>>()

    map[java.lang.Boolean.TYPE] = java.lang.Boolean::class.java
    map[java.lang.Byte.TYPE] = java.lang.Byte::class.java
    map[java.lang.Character.TYPE] = java.lang.Character::class.java
    map[java.lang.Short.TYPE] = java.lang.Short::class.java
    map[java.lang.Integer.TYPE] = java.lang.Integer::class.java
    map[java.lang.Long.TYPE] = java.lang.Long::class.java
    map[java.lang.Double.TYPE] = java.lang.Double::class.java
    map[java.lang.Float.TYPE] = java.lang.Float::class.java
    map
}

// CallChain[size=6] = Class<*>.qIsAssignableFrom() <-[Call]- QMatchMethodParams.matches() <-[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
internal fun Class<*>.qIsAssignableFrom(subclass: Class<*>, autoboxing: Boolean = true): Boolean {
    return if (autoboxing) {
        this.qPrimitiveToWrapper().isAssignableFrom(subclass.qPrimitiveToWrapper())
    } else {
        this.isAssignableFrom(subclass)
    }
}

// CallChain[size=4] = Method.qIsInstanceMethod() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun Method.qIsInstanceMethod(): Boolean {
    return !Modifier.isStatic(this.modifiers)
}

// CallChain[size=4] = qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
internal fun qCallerFileMainClass(stackDepth: Int = 0): Class<*> {
    val frame = qStackFrame(stackDepth + 2)
    val fileName = frame.fileName.substring(0, frame.fileName.lastIndexOf("."))
    val pkgName = frame.declaringClass.packageName

    val clsName = if (fileName.endsWith(".kt", true)) {
        if (pkgName.isNotEmpty())
            "$pkgName.${fileName}Kt"
        else
            "${fileName}Kt"
    } else {
        if (pkgName.isNotEmpty())
            "$pkgName.$fileName"
        else
            fileName
    }

    return Class.forName(clsName)
}