package com.devdaily.sarah.gui

import javax.swing._
import java.awt._

class MainFrame3 extends JFrame {
  
    setLayout(new BorderLayout)

    val inputField = new JTextField
    val outputArea = new JEditorPane
    val scrollPane = new JScrollPane(outputArea)
    
    configureInputField
    configureOutputArea
    
    getContentPane.add(inputField, BorderLayout.NORTH)
    getContentPane.add(scrollPane, BorderLayout.CENTER)
    
    private def configureOutputArea {
        outputArea.setEditable(false)
        outputArea.setFont(inputField.getFont.deriveFont(20.0f))
        outputArea.setBackground(new Color(230, 230, 230))
        outputArea.setMargin(new Insets(20, 20, 20, 20))
        outputArea.setText("Welcome to Sarah III")
    }

    private def configureInputField {
        inputField.setFont(inputField.getFont.deriveFont(24.0f));
        inputField.setBorder(BorderFactory.createCompoundBorder(inputField.getBorder, BorderFactory.createEmptyBorder(5, 12, 5, 5)))
        inputField.setBounds(20, 20, inputField.getHeight, inputField.getWidth)
        inputField.setBackground(new Color(250, 250, 250))
    }
    
    /**
     * TODO i don't know if this is 100% correct. see the "snippets" for more.
     */
    def setFocusInTextField {
        val mainFrame = this
        SwingUtilities.invokeLater(new Runnable {
            def run {
                mainFrame.requestFocusInWindow
                inputField.requestFocusInWindow
                Thread.sleep(500)
            }
        })
    }
}
