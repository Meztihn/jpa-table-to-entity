package meztihn.jpa.convert.entity.view

import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder
import kotlin.math.max

class PanelWithTitledBorder(title: String) : JPanel() {
    companion object {
        /**
         * Default border minimal width is still not enough for full title.
         */
        private const val borderWidthCoefficient = 1.1
    }

    init {
        border = BorderFactory.createTitledBorder(title)
    }

    override fun getMinimumSize(): Dimension {
        val size = super.getPreferredSize()
        val borderMinWidth = (border as? TitledBorder)
            ?.let { (it.getMinimumSize(this).width * borderWidthCoefficient).toInt() }
            ?: 0
        return Dimension(max(size.width, borderMinWidth), size.height)
    }

    override fun getPreferredSize(): Dimension = max(super.getPreferredSize(), this.minimumSize)

    private fun max(first: Dimension, second: Dimension): Dimension {
        return Dimension(max(first.width, second.width), max(first.height, second.height))
    }
}
