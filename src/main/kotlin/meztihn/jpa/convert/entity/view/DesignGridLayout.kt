package meztihn.jpa.convert.entity.view

import net.java.dev.designgridlayout.DesignGridLayout
import java.awt.Container

fun Container.designGridLayout(init: DesignGridLayout.() -> Unit) {
    DesignGridLayout(this).apply(init)
}