package meztihn.jpa.convert.entity

import meztihn.jpa.convert.entity.view.MainFrame
import java.awt.Toolkit

fun main(args: Array<String>) {
    Toolkit.getDefaultToolkit().systemEventQueue.push(ExceptionHandlingEventQueue)
    MainFrame().start()
}