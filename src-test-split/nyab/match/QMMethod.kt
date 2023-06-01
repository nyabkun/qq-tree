/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.match

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import nyab.util.QFlagEnum
import nyab.util.qIsAssignableFrom

// qq-tree is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=4] = QMMethod.not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private val QMMethod.not: QMMethod
    get() = QMatchMethodNot(this)

// CallChain[size=4] = qAnd() <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qAnd(vararg matches: QMMethod): QMMethod = QMatchMethodAnd(*matches)

// CallChain[size=4] = qOr() <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qOr(vararg matches: QMMethod): QMMethod = QMatchMethodOr(*matches)

// CallChain[size=3] = QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
internal infix fun QMMethod.and(match: QMMethod): QMMethod {
    return if (this is QMatchMethodAnd) {
        QMatchMethodAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=3] = QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
internal infix fun QMMethod.or(match: QMMethod): QMMethod {
    return if (this is QMatchMethodOr) {
        QMatchMethodOr(*matchList, match)
    } else {
        qOr(this, match)
    }
}

// CallChain[size=5] = QMatchMethodNot <-[Call]- QMMethod.not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodNot(val matcher: QMMethod) : QMMethodA() {
    // CallChain[size=6] = QMatchMethodNot.matches() <-[Propag]- QMatchMethodNot <-[Call]- QMMethod.not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean = !matcher.matches(value)
}

// CallChain[size=5] = QMatchMethodAny <-[Call]- QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodAny : QMMethodA() {
    // CallChain[size=6] = QMatchMethodAny.matches() <-[Propag]- QMatchMethodAny <-[Call]- QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return true
    }
}

// CallChain[size=5] = QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodNone : QMMethodA() {
    // CallChain[size=6] = QMatchMethodNone.matches() <-[Propag]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return false
    }
}

// CallChain[size=4] = QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodDeclaredOnly : QMMethodA() {
    // CallChain[size=5] = QMatchMethodDeclaredOnly.declaredOnly <-[Propag]- QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = true

    // CallChain[size=5] = QMatchMethodDeclaredOnly.matches() <-[Propag]- QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return true
    }
}

// CallChain[size=4] = QMatchMethodAnd <-[Ref]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnd(vararg match: QMMethod) : QMMethodA() {
    // CallChain[size=4] = QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    val matchList = match

    // CallChain[size=5] = QMatchMethodAnd.declaredOnly <-[Propag]- QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=5] = QMatchMethodAnd.matches() <-[Propag]- QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=4] = QMatchMethodOr <-[Ref]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodOr(vararg match: QMMethod) : QMMethodA() {
    // CallChain[size=4] = QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    val matchList = match

    // CallChain[size=5] = QMatchMethodOr.declaredOnly <-[Propag]- QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=5] = QMatchMethodOr.matches() <-[Propag]- QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return matchList.any { it.matches(value) }
    }
}

// CallChain[size=6] = QMMethodA <-[Call]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private abstract class QMMethodA : QMMethod {
    // CallChain[size=7] = QMMethodA.declaredOnly <-[Propag]- QMMethodA <-[Call]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly: Boolean = false
}

// CallChain[size=3] = QMMethod <-[Ref]- qTest() <-[Call]- main()[Root]
internal interface QMMethod {
    // CallChain[size=4] = QMMethod.declaredOnly <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    val declaredOnly: Boolean

    // CallChain[size=3] = QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun matches(value: Method): Boolean

    // CallChain[size=4] = QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun isAny(): Boolean = this == QMatchMethodAny

    // CallChain[size=4] = QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun isNone(): Boolean = this == QMatchMethodNone

    companion object {
        // CallChain[size=3] = QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
        val DeclaredOnly: QMMethod = QMatchMethodDeclaredOnly

        // CallChain[size=3] = QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
        val NoParams: QMMethod = QMatchMethodParams(arrayOf())

        // TODO vararg, param names, type parameter

        // CallChain[size=3] = QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun <T : Annotation> annotation(annotation: KClass<T>, matcher: (T) -> Boolean = { true }): QMMethod {
            return QMatchMethodAnnotation(annotation.java, matcher)
        }

        // CallChain[size=3] = QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun <T : Annotation> notAnnotation(annotation: KClass<T>, matcher: (T) -> Boolean = { true }): QMMethod {
            return QMatchMethodAnnotation(annotation.java, matcher).not
        }

        // CallChain[size=3] = QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun annotation(annotationName: String): QMMethod {
            return QMatchMethodAnnotationName(QM.exact(annotationName))
        }

        // CallChain[size=3] = QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
        fun nameNotExact(text: String, ignoreCase: Boolean = false): QMMethod {
            return QMatchMethodName(QM.notExact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=4] = QMatchMethodName <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodName(val nameMatcher: QM) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodName.matches() <-[Propag]- QMatchMethodName <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return nameMatcher.matches(value.name)
    }
}

// CallChain[size=4] = QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodParams(val params: Array<Class<*>?>) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodParams.matches() <-[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        if (value.parameters.size != params.size)
            return false

        if (params.isEmpty())
            return true

        return params.withIndex().all { (idx, actualParam) ->
            // Number is assignable from Integer
            if (actualParam == null) {
                true
            } else {
                // Number is assignable from Integer
                value.parameters[idx].type.qIsAssignableFrom(actualParam)
            }
        }
    }
}

// CallChain[size=4] = QMatchMethodAnnotation <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnnotation<T : Annotation>(val annotation: Class<T>, val matcher: (T) -> Boolean) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodAnnotation.matches() <-[Propag]- QMatchMethodAnnotation <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        val anno = value.getAnnotation(annotation) ?: return false

        return matcher(anno)
    }
}

// CallChain[size=4] = QMatchMethodAnnotationName <-[Call]- QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnnotationName(val nameMatcher: QM, val declaredAnnotationsOnly: Boolean = false) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodAnnotationName.matches() <-[Propag]- QMatchMethodAnnotationName <-[Call]- QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        val annotations = if (declaredAnnotationsOnly) {
            value.declaredAnnotations
        } else {
            value.annotations
        }

        return annotations.any { anno: Annotation ->
            nameMatcher.matches(anno.annotationClass.java.simpleName)
        }
    }
}