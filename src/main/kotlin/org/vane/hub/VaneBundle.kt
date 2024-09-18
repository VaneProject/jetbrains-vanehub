package org.vane.hub

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "messages.vane"
object VaneBundle {
    private val DYNAMIC = DynamicBundle(VaneBundle::class.java, BUNDLE)

    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String = DYNAMIC.getMessage(key, *params)
}