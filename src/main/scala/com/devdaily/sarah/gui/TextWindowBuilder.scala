package com.devdaily.sarah.gui

import java.awt._
import javax.swing._

//object TextWindowBuilderTest extends App {
//
//    val b = new TextWindowBuilder("Hello, world")
//    b.setVisible(true)
//    
//    Thread.sleep(3000)
//    b.setText("foo bar baz")
//
//}

/**
 * How to use:
 * 
 *   val b = new TextWindowBuilder("Hello, world")
 *   b.setVisible(true)
 *   
 *   // later
 *   b.setVisible(false)
 * 
 */
class TextWindowBuilder (
        textToDisplay: String,
        windowSize: Dimension = new Dimension(400, 300),
        location: Point = new Point(200, 200),
        textAreaRows: Int = 10,
        textAreaColumns: Int = 40,
        alpha: Float = 0.8f
        ) {

    // textarea and scrollpane
    val textArea = new JTextArea(textAreaRows, textAreaColumns)
    textArea.setFont(new Font("Helvetica Neue", Font.PLAIN, 20))
    textArea.setEditable(false)
    textArea.setMargin(new Insets(12, 12, 12, 12))
    val scrollPane = new JScrollPane(textArea)
    textArea.setText(textToDisplay)
    scrollPane.setPreferredSize(windowSize)
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
 
    // jframe
    val f = new JFrame
    f.setUndecorated(true)
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.getRootPane.putClientProperty("Window.alpha", alpha)
    f.getContentPane.add(scrollPane, BorderLayout.CENTER)
    f.setLocation(location)
    
    def setText(text: String) {
        val code = textArea.setText(text)
        invokeLater(code)
    }

    def setVisible(setVisible: Boolean) {
        if (setVisible) {
            val block = {
                f.pack
                f.setVisible(true)
            }
            invokeLater(block)
        } else {
            val block = f.setVisible(false)
            invokeLater(block)
        }
    }
    
    private def invokeLater[A](blockOfCode: => A) = {
        SwingUtilities.invokeLater(new Runnable {
            def run {
                blockOfCode
            }
        })
    }
  
  
}






