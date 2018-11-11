package meztihn.jpa.convert.entity.view.options

import meztihn.jpa.convert.entity.java.Constructor.*
import meztihn.jpa.convert.entity.java.Explicitness.*
import meztihn.jpa.convert.entity.java.Mutability.*
import meztihn.jpa.convert.entity.transform.Options
import meztihn.jpa.convert.entity.view.TextFieldWithLabel
import meztihn.jpa.convert.entity.view.designGridLayout
import javax.swing.JCheckBox
import javax.swing.JPanel

class ClassOptionsPanel : JPanel() {
    companion object {
        private const val fourSpaces = "    "
    }

    private val immutableCheckbox: JCheckBox = JCheckBox("Immutable", true)
    private val constructorCheckbox: JCheckBox = JCheckBox("With constructor", true)
    private val namesExplicitnessCheckbox: JCheckBox = JCheckBox("Explicit names", true)
    private val dateTimePackagePanel: DateTimePackagePanel = DateTimePackagePanel("Use temporal classes from:")

    private val indentTextField: TextFieldWithLabel =
        TextFieldWithLabel(
            "Indent: ",
            fourSpaces
        )

    val options: Options
        get() = Options(
            if (immutableCheckbox.isSelected) Immutable else Mutable,
            if (constructorCheckbox.isSelected) Full else Default,
            if (namesExplicitnessCheckbox.isSelected) Explicit else Implicit,
            dateTimePackagePanel.selected,
            indentTextField.text
        )

    init {
        designGridLayout {
            row().left().add(immutableCheckbox)
            row().left().add(constructorCheckbox)
            row().left().add(namesExplicitnessCheckbox)
            row().left().add(dateTimePackagePanel)
            row().left().add(indentTextField)
        }
    }
}
