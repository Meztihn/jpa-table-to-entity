package meztihn.jpa.convert.entity.view

import meztihn.jpa.convert.entity.java.Constructor.*
import meztihn.jpa.convert.entity.java.DateTimePackage
import meztihn.jpa.convert.entity.java.Explicitness.*
import meztihn.jpa.convert.entity.java.Mutability.*
import meztihn.jpa.convert.entity.transform.Options
import net.java.dev.designgridlayout.DesignGridLayout
import javax.swing.ButtonGroup
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JRadioButton

private const val fourSpaces = "    "

class ClassOptionsPanel : JPanel() {
    private val immutableCheckbox: JCheckBox = JCheckBox("Immutable", true)
    private val constructorCheckbox: JCheckBox = JCheckBox("With constructor", true)
    private val namesExplicitnessCheckbox: JCheckBox = JCheckBox("Explicit names", true)
    private val dateTimePackageCheckbox: ButtonGroup = ButtonGroup().apply {
        add(JRadioButton(DateTimePackage.sql.path, true))
        add(JRadioButton(DateTimePackage.util.path))
//        add(JRadioButton(DateTimePackage.time.path)) // TODO
    }
    private val indentTextField: TextFieldWithLabel =
        TextFieldWithLabel("Indent: ", fourSpaces)

    val options: Options
        get() = Options(
            if (immutableCheckbox.isSelected) Immutable else Mutable,
            if (constructorCheckbox.isSelected) Full else Default,
            if (namesExplicitnessCheckbox.isSelected) Explicit else Implicit,
            DateTimePackage.fromPath((dateTimePackageCheckbox.elements.asSequence().first { it.isSelected } as JRadioButton).text),
            indentTextField.text
        )

    init {
        DesignGridLayout(this).apply {
            row().left().add(immutableCheckbox)
            row().left().add(constructorCheckbox)
            row().left().add(namesExplicitnessCheckbox)
            row().left().add(
                PanelWithTitledBorder("Use temporal classes from:").apply {
                    DesignGridLayout(this).apply {
                        dateTimePackageCheckbox.elements.iterator().forEach { row().left().add(it) }
                    }
                }
            )
            row().left().add(indentTextField)
        }
    }
}
