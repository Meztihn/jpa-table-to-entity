package meztihn.jpa.convert.entity.view

import javax.swing.JFrame

open class DefaultFrame(title: String) : JFrame(title) {
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun start() {
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}