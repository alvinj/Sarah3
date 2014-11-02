package com.devdaily.sarah

import javax.swing._
import java.awt._

object MainFrame3Test extends App {

    val mainFrame = new MainFrame3
    mainFrame.setPreferredSize(getDesiredFrameSize)
    mainFrame.pack
    mainFrame.setLocationRelativeTo(null)
    mainFrame.setVisible(true)
    mainFrame.setTitle("Sarah")

    // this might be necessary later
    mainFrame.setFocusInTextField
    
    def getDesiredFrameSize: Dimension = {
        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
        val height = screenSize.height * 2 / 3
        //val width = screenSize.width * 2 / 3
        val width = (1.6 * height).asInstanceOf[Int]
        new Dimension(width, height)
    }

}

class MainFrame3 extends JFrame {
  
    setLayout(new BorderLayout)

    val inputField = new JTextField
    val outputArea = new JEditorPane
    
    configureInputField
    configureOutputArea
    
    getContentPane.add(inputField, BorderLayout.NORTH)
    getContentPane.add(outputArea, BorderLayout.CENTER)
    
    private def configureOutputArea {
        outputArea.setEditable(false)
        outputArea.setFont(inputField.getFont.deriveFont(20.0f))
        outputArea.setBackground(new Color(230, 230, 230))
        outputArea.setMargin(new Insets(20, 20, 20, 20))
        outputArea.setText("Four score and seven years ago\nour fathers did some things\nand so on and so forth...")
    }

    private def configureInputField {
        inputField.setFont(inputField.getFont.deriveFont(24.0f));
        inputField.setBorder(BorderFactory.createCompoundBorder(inputField.getBorder, BorderFactory.createEmptyBorder(5, 12, 5, 5)))
        inputField.setBounds(20, 20, inputField.getHeight, inputField.getWidth)
        inputField.setBackground(new Color(250, 250, 250))
    }
    
    val mainFrame = this
    def setFocusInTextField {
        SwingUtilities.invokeLater(new Runnable {
            def run {
                mainFrame.requestFocusInWindow
                inputField.requestFocusInWindow
                Thread.sleep(500)
            }
        })
    }
}


