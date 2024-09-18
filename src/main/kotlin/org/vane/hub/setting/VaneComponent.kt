package org.vane.hub.setting

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.BrowseFolderDescriptor.Companion.withFileToTextConvertor
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.ClearableLazyValue
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColorPanel
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.webSymbols.utils.applyIfNotNull
import java.awt.Color
import java.util.function.Predicate
import java.util.function.Supplier
import javax.swing.JComponent
import javax.swing.text.JTextComponent
import kotlin.reflect.KMutableProperty0

abstract class VaneComponent(private val display: String) : Configurable {
    private val validators: MutableList<ComponentValidator> = mutableListOf()

    private var disposable: Disposable? = null
    private val panel = object : ClearableLazyValue<DialogPanel>() {
        override fun compute(): DialogPanel {
            if (disposable == null)
                disposable = Disposer.newDisposable()
            val panel: DialogPanel = createPanel()
            panel.registerValidators(disposable!!)
            return panel
        }
    }

    protected fun getDisposable(): Disposable? = this.disposable

    abstract fun createPanel(): DialogPanel

    override fun getDisplayName(): String = this.display
    override fun getPreferredFocusedComponent(): JComponent? = panel.value.preferredFocusedComponent
    override fun createComponent(): JComponent = panel.value
    override fun reset() = panel.value.reset()
    override fun isModified(): Boolean = panel.value.isModified()
    override fun apply() {
        var component: JComponent? = null
        for (validator: ComponentValidator in this.validators) {
            validator.revalidate()
            if (component === null) component = validator.validationInfo?.component
        }
        if (component === null)
            panel.value.apply()
        else component.grabFocus()
    }

    override fun disposeUIResources() {
        this.disposable?.let(Disposer::dispose)
        this.disposable = null
        this.panel.drop()
    }

    @JvmOverloads
    protected fun Row.textFieldWithBrowseButton(
        @NlsContexts.DialogTitle browseDialogTitle: String? = null,
        @NlsContexts.Label description: String? = null,
        project: Project? = null,
        fileChooserDescriptor: FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
        fileChosen: ((chosenFile: VirtualFile) -> String)? = null
    ): Cell<TextFieldWithBrowseButton> {
        val browseButton = TextFieldWithBrowseButton(null, getDisposable())
        browseButton.addBrowseFolderListener(
            browseDialogTitle, description, project,
            fileChooserDescriptor.applyIfNotNull(fileChosen) {
                this.withFileToTextConvertor(it)
            }
        )
        return cell(browseButton).applyToComponent {
            isOpaque = false
            textField.isOpaque = false
        }.columns(COLUMNS_SHORT)
    }

    protected fun Row.intTextField(
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE
    ): Cell<JBTextField> = intTextField(IntRange(min, max))

    protected fun Row.colorPanel(): Cell<ColorPanel> = cell(ColorPanel())

    // binding supporters
    @JvmName("bindTextNull")
    protected fun <T : JTextComponent> Cell<T>.bindText(prop: KMutableProperty0<String?>): Cell<T> {
        return bindText(prop.toNonNullableProperty(""))
    }

    @JvmName("editTextFieldBindText")
    protected fun <T : EditorTextField> Cell<T>.bindText(prop: KMutableProperty0<String?>): Cell<T> = bind(
        EditorTextField::getText,
        EditorTextField::setText,
        prop.toNonNullableProperty("")
    )

    protected fun Cell<ColorPanel>.bindColor(prop: MutableProperty<Color?>): Cell<ColorPanel> {
        return this.bind(ColorPanel::getSelectedColor, ColorPanel::setSelectedColor, prop)
    }

    protected fun Cell<ColorPanel>.bindColor(prop: MutableProperty<Color?>, defaultColor: Color): Cell<ColorPanel> {
        return this.bind({ it.selectedColor ?: defaultColor }, { panel: ColorPanel, color: Color? ->
            panel.selectedColor = color ?: defaultColor
        }, prop)
    }

    /**
     * add custom validation check login
     */
    protected fun <J: JComponent> Cell<J>.addValidation(message: String, check: Predicate<J>, isDocument: Boolean = false): Cell<J> {
        val validator = ComponentValidator(getDisposable()!!).withValidator(Supplier<ValidationInfo> {
            if (check.test(component)) null else ValidationInfo(message, component)
        }).andStartOnFocusLost().installOn(component)

        // change document valid check
        if (isDocument && component is JTextComponent)
            validator.andRegisterOnDocumentListener(component as JTextComponent)
        validators.add(validator)
        return this.onChanged {
            if (validator.validationInfo !== null)
                validator.updateInfo(null)
        }
    }
}
