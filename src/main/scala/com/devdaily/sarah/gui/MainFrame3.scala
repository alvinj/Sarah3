package com.devdaily.sarah.gui

import javax.swing._
import java.awt._
import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.builder.PanelBuilder
import com.jgoodies.forms.layout.CellConstraints

trait StatusIndicator
case object RedLight extends StatusIndicator
case object YellowLight extends StatusIndicator
case object GreenLight extends StatusIndicator
case object NeutralLight extends StatusIndicator

class MainFrame3 extends JFrame {
  
    import SwingUtils.invokeLater
  
    // status icon images 
    val trafficLightRed = new ImageIcon(classOf[MainFrame3].getResource("trafficLightRedSmall.jpg"))
    val trafficLightYellow = new ImageIcon(classOf[MainFrame3].getResource("trafficLightYellowSmall.jpg"))
    val trafficLightGreen = new ImageIcon(classOf[MainFrame3].getResource("trafficLightGreenSmall.jpg"))
    val trafficLightNeutral = new ImageIcon(classOf[MainFrame3].getResource("trafficLightNeutralSmall.jpg"))
    
    setLayout(new BorderLayout)

    // input area
    val statusLabel = new JLabel
    val inputField = new JTextField
    statusLabel.setIcon(trafficLightNeutral)
    val inputPanel = buildInputPanel
    
    // output area
    val outputArea = new JEditorPane
    val scrollPane = new JScrollPane(outputArea)
    
    configureInputField
    configureOutputArea
    
    getContentPane.add(inputPanel, BorderLayout.NORTH)
    getContentPane.add(scrollPane, BorderLayout.CENTER)
    
    def setStatusIndicator(status: StatusIndicator): Unit = status match {
        case RedLight => invokeLater(statusLabel.setIcon(trafficLightRed))
        case YellowLight => invokeLater(statusLabel.setIcon(trafficLightYellow))
        case GreenLight => invokeLater(statusLabel.setIcon(trafficLightGreen))
        case NeutralLight => invokeLater(statusLabel.setIcon(trafficLightNeutral))
    }
    
    /** 
     *  the "input panel" that goes in the North section of the Sarah3 mainframe BorderLayout.
     */
    private def buildInputPanel: JPanel = {
      
        val layout = new FormLayout(
                //    label       textfield
                //    -----       ---------
                "3px, 48px,  8px, pref:grow, 2dlu",  //columns
                //    -----       ---------
                "3dlu, p, 3dlu"                      //rows
        )
        val builder = new PanelBuilder(layout)
        builder.setDefaultDialogBorder
    
        val cc = new CellConstraints    
        builder.add(statusLabel, cc.xy(2, 2))
        builder.add(inputField,  cc.xy(4, 2))

        return builder.getPanel
    
    }

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
        invokeLater(new Runnable {
            def run {
                mainFrame.requestFocusInWindow
                inputField.requestFocusInWindow
                Thread.sleep(500)
            }
        })
    }
}
