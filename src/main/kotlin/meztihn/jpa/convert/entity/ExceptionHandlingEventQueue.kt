package meztihn.jpa.convert.entity

import meztihn.jpa.convert.entity.view.showError
import java.awt.AWTEvent
import java.awt.EventQueue
import java.io.PrintWriter
import java.io.StringWriter

object ExceptionHandlingEventQueue : EventQueue() {
    override fun dispatchEvent(event: AWTEvent?) {
        try {
            super.dispatchEvent(event)
        } catch (exception: Exception) {
            showError("Unexpected error", exception.asString())
        }
    }

}

private fun Exception.asString(): String {
    StringWriter().use { stringWriter ->
        PrintWriter(stringWriter).use { printWriter ->
            printStackTrace(printWriter)
            return stringWriter.toString()
        }
    }
}