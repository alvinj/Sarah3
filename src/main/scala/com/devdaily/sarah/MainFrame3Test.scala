package com.devdaily.sarah

import javax.swing._
import java.awt._
import com.devdaily.sarah.gui.MainFrame3

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



