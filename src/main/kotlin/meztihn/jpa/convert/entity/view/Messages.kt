package meztihn.jpa.convert.entity.view

import javax.swing.JOptionPane.ERROR_MESSAGE
import javax.swing.JOptionPane.showMessageDialog

fun showError(title: String, message: String) {
    showMessageDialog(null, message, title, ERROR_MESSAGE)
}