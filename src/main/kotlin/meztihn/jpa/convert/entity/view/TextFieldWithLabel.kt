package meztihn.jpa.convert.entity.view

import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class TextFieldWithLabel(label: JLabel, private val textField: JTextField) : JPanel() {
    var text: String = textField.text
        get() = textField.text
        set(value) {
            field = textField.text
        }

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        add(label)
        add(textField)
    }

    constructor(label: String, defaultValue: String = "") : this(JLabel(label), JTextField(defaultValue))
}