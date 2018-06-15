package meztihn.jpa.convert.entity

import meztihn.jpa.convert.entity.view.showError
import net.sf.jsqlparser.JSQLParserException
import org.apache.logging.log4j.LogManager
import java.awt.AWTEvent
import java.awt.EventQueue
import java.security.PrivilegedActionException

object ExceptionHandlingEventQueue : EventQueue() {
    private val logger = LogManager.getLogger()

    override fun dispatchEvent(event: AWTEvent?) {
        try {
            super.dispatchEvent(event)
        } catch (exception: Exception) {
            logger.catching(exception)
            val cause = realCauseOf(exception)
            showError("Unexpected error", cause.message ?: "No meaningful message found. See logs/app.log for details.")
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