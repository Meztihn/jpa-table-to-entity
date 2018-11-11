package meztihn.jpa.convert.entity.view

import com.squareup.javapoet.JavaFile
import meztihn.jpa.convert.entity.parse.ParseException
import meztihn.jpa.convert.entity.parse.parseCreateTable
import meztihn.jpa.convert.entity.transform.toClass
import net.java.dev.designgridlayout.DesignGridLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.StringWriter
import javax.swing.*

class MainFrame : DefaultFrame("Table to class converter") {
    private val textAreaHeight = 32
    private val textAreaWidth = 32

    private val tableTextArea = JTextArea(exampleTableDefinition, textAreaHeight, textAreaWidth)
    private val classOptionsPanel = ClassOptionsPanel()
    private val convertButton = JButton("Convert").apply { addActionListener { convert() } }
    private val copyToClipboardNotificationLabel = JLabel("<html>Class copied to clipboard</html>").apply { isVisible = false }
    private val classTextArea = JTextArea("Class will appear here", textAreaHeight, textAreaWidth).apply { isEditable = false }

    init {
        val options = JPanel().apply {
            DesignGridLayout(this).apply {
                row().grid().add(classOptionsPanel)
                row().grid().add(convertButton)
                row().grid().add(copyToClipboardNotificationLabel)
            }
        }
        DesignGridLayout(this).apply {
            row().grid().add(JScrollPane(tableTextArea), 3).add(options, 2).add(JScrollPane(classTextArea), 3)
        }
    }

    private fun convert() {
        try {
            val createTable = parseCreateTable(tableTextArea.text)
            val options = classOptionsPanel.options
            val typeSpec = createTable.toClass(options)
            val classDefinition = StringWriter().use { writer ->
                JavaFile.builder("", typeSpec)
                    .indent(options.indent)
                    .build()
                    .writeTo(writer)
                writer.toString()
            }
            classTextArea.text = classDefinition
            copyToClipboard(classDefinition)
        } catch (e: ParseException) {
            showError("A parsing error occurred", e.message)
        }
    }

    private fun copyToClipboard(text: String) {
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
        with(copyToClipboardNotificationLabel) {
            isVisible = true
            Timer(5.seconds) { isVisible = false }.start()
        }
    }

    private val Int.seconds: Int
        get() = this * 1000
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
