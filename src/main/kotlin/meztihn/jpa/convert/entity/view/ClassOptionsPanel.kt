package meztihn.jpa.convert.entity.view

import meztihn.jpa.convert.entity.java.Constructor.*
import meztihn.jpa.convert.entity.java.DateTimePackage
import meztihn.jpa.convert.entity.java.Explicitness.*
import meztihn.jpa.convert.entity.java.Mutability.*
import meztihn.jpa.convert.entity.transform.Options
import net.java.dev.designgridlayout.DesignGridLayout
import javax.swing.JCheckBox
import javax.swing.JPanel

private const val fourSpaces = "    "

class ClassOptionsPanel : JPanel() {
    private val immutableCheckbox: JCheckBox = JCheckBox("Immutable", true)
    private val constructorCheckbox: JCheckBox = JCheckBox("With constructor", true)
    private val namesExplicitnessCheckbox: JCheckBox = JCheckBox("Explicit names", true)
    private val indentTextField: TextFieldWithLabel =
        TextFieldWithLabel("Indent: ", fourSpaces)

    val options: Options
        get() = Options(
            if (immutableCheckbox.isSelected) Immutable else Mutable,
            if (constructorCheckbox.isSelected) Full else Default,
            if (namesExplicitnessCheckbox.isSelected) Explicit else Implicit,
            DateTimePackage.util, // TODO
            indentTextField.text
        )

    init {
        DesignGridLayout(this).apply {
            row().left().add(immutableCheckbox)
            row().left().add(constructorCheckbox)
            row().left().add(namesExplicitnessCheckbox)
            row().left().add(indentTextField)
        }
    }
}
