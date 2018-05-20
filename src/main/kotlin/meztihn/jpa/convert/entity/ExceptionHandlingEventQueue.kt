package meztihn.jpa.convert.entity

import java.awt.AWTEvent
import java.awt.EventQueue
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane.ERROR_MESSAGE
import javax.swing.JOptionPane.showMessageDialog

object ExceptionHandlingEventQueue : EventQueue() {
    override fun dispatchEvent(event: AWTEvent?) {
        try {
            super.dispatchEvent(event)
        } catch (exception: Exception) {
            showError("Unexpected error", exception.asString())
        }
    }

}

private fun showError(title: String, message: String) {
    showMessageDialog(null, message, title, ERROR_MESSAGE)
}

private fun Exception.asString(): String {
    StringWriter().use { stringWriter ->
        PrintWriter(stringWriter).use { printWriter ->
            printStackTrace(printWriter)
            return stringWriter.toString()
        }
    }
}