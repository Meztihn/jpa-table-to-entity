package meztihn.jpa.convert.entity.view.options

import meztihn.jpa.convert.entity.java.DateTimePackage
import meztihn.jpa.convert.entity.view.designGridLayout
import javax.swing.ButtonGroup
import javax.swing.JRadioButton

class DateTimePackagePanel(title: String) : PanelWithTitledBorder(title) {
    private val radioButtons = listOf(
        JRadioButton(DateTimePackage.sql.path, true),
        JRadioButton(DateTimePackage.util.path)
//        add(JRadioButton(DateTimePackage.time.path)) // TODO
    )

    val selected: DateTimePackage
        get() = DateTimePackage.fromPath(radioButtons.first { it.isSelected }.text)

    init {
        ButtonGroup().apply { radioButtons.forEach { add(it) } }
        designGridLayout {
            radioButtons.forEach { row().left().add(it) }
        }
    }
}