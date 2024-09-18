package org.vane.hub.setting

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout

class EmptyComponent(
    display: String,
    private val message: String,
    private val gray: Boolean = true
) : VaneComponent(display) {
    override fun createPanel(): DialogPanel = DialogPanel(BorderLayout()).apply {
        val label = JBLabel(message)
        label.horizontalAlignment = JBLabel.CENTER
        add(label, BorderLayout.CENTER)
        label.isEnabled = !gray
    }
}