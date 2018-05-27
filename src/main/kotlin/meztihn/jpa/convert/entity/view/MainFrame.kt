package meztihn.jpa.convert.entity.view

import com.squareup.javapoet.JavaFile
import meztihn.jpa.convert.entity.parse.ParseException
import meztihn.jpa.convert.entity.parse.parseCreateTable
import meztihn.jpa.convert.entity.transform.toClass
import net.java.dev.designgridlayout.DesignGridLayout
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.SOUTH
import java.io.StringWriter
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

private const val padding: Int = 16

class MainFrame : DefaultFrame("Table to class converter") {
    private val textAreaHeight = 32
    private val textAreaWidth = 32

    private val tableTextArea = JTextArea(exampleTableDefinition, textAreaHeight, textAreaWidth)
    private val classOptionsPanel = ClassOptionsPanel()
    private val convertButton = JButton("Convert").apply { addActionListener { convert() } }
    private val classTextArea = JTextArea("Class will appear here", textAreaHeight, textAreaWidth).apply { isEditable = false }

    init {
        val options = JPanel().apply {
            layout = BorderLayout(padding, padding)
            add(classOptionsPanel, CENTER)
            add(convertButton, SOUTH)
        }
        DesignGridLayout(this).apply {
            row().grid().add(JScrollPane(tableTextArea), 2).add(options).add(JScrollPane(classTextArea), 2)
        }
    }

    private fun convert() {
        try {
            val createTable = parseCreateTable(tableTextArea.text)
            val options = classOptionsPanel.options
            val typeSpec = createTable.toClass(options)
            StringWriter().use { writer ->
                JavaFile.builder("", typeSpec)
                    .indent(options.indent)
                    .build()
                    .writeTo(writer)
                classTextArea.text = writer.toString()
            }
        } catch (e: ParseException) {
            showError("A parsing error occurred", e.message)
        }
    }
}

private val exampleTableDefinition = """
CREATE TABLE IF NOT EXISTS table_name (
    uuid VARCHAR(36) NOT NULL,
    index INTEGER NOT NULL,
    count BIGINT NOT NULL,
    time TIME,
    date DATE,
    timestamp TIMESTAMP(4) with time zone,
    integer INTEGER,
    big_decimal NUMERIC(16, 2)
);
""".trimIndent()
