package meztihn.jpa.convert.entity

import meztihn.jpa.convert.entity.view.showError
import net.sf.jsqlparser.JSQLParserException
import java.awt.AWTEvent
import java.awt.EventQueue
import java.security.PrivilegedActionException

object ExceptionHandlingEventQueue : EventQueue() {
    override fun dispatchEvent(event: AWTEvent?) {
        try {
            super.dispatchEvent(event)
        } catch (exception: Exception) {
            val cause = realCauseOf(exception)
            showError("Unexpected error", cause.message ?: "No message provided.")
        }
    }

    private tailrec fun realCauseOf(throwable: Throwable): Throwable {
        return when (throwable) {
            is PrivilegedActionException -> realCauseOf(throwable.exception)
            is JSQLParserException -> realCauseOf(throwable.cause!!)
            else -> throwable
        }
    }
}