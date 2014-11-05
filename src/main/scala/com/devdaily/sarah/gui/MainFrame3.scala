package com.devdaily.sarah.gui

import javax.swing._
import java.awt._

trait StatusIndicator
case object RedLight extends StatusIndicator
case object YellowLight extends StatusIndicator
case object GreenLight extends StatusIndicator
case object NeutralLight extends StatusIndicator

class MainFrame3 extends JFrame {
  
    // status icon images 
    val trafficLightRed = new ImageIcon(classOf[MainFrame3].getResource("trafficLightRedSmall.jpg"))
    val trafficLightYellow = new ImageIcon(classOf[MainFrame3].getResource("trafficLightYellowSmall.jpg"))
    val trafficLightGreen = new ImageIcon(classOf[MainFrame3].getResource("trafficLightGreenSmall.jpg"))
    val trafficLightNeutral = new ImageIcon(classOf[MainFrame3].getResource("trafficLightNeutralSmall.jpg"))
    
    setLayout(new BorderLayout)

    // input area
    // TODO fix this layout with jgoodies or other
    val statusLabel = new JLabel
    val inputField = new JTextField
    inputField.setPreferredSize(new Dimension(910, 44))
    val inputPanel = new JPanel
    val inputLayout = new FlowLayout(FlowLayout.LEFT, 10, 0)
    inputPanel.setLayout(inputLayout)
    statusLabel.setIcon(trafficLightNeutral)
    inputPanel.add(statusLabel)
    inputPanel.add(inputField)
    
    // output area
    val outputArea = new JEditorPane
    val scrollPane = new JScrollPane(outputArea)
    
    configureInputField
    configureOutputArea
    
    getContentPane.add(inputPanel, BorderLayout.NORTH)
    getContentPane.add(scrollPane, BorderLayout.CENTER)
    
    def setStatusIndicator(status: StatusIndicator): Unit = status match {
        case RedLight => SwingUtils.invokeLater(statusLabel.setIcon(trafficLightRed))
        case YellowLight => SwingUtils.invokeLater(statusLabel.setIcon(trafficLightYellow))
        case GreenLight => SwingUtils.invokeLater(statusLabel.setIcon(trafficLightGreen))
        case NeutralLight => SwingUtils.invokeLater(statusLabel.setIcon(trafficLightNeutral))
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
        SwingUtilities.invokeLater(new Runnable {
            def run {
                mainFrame.requestFocusInWindow
                inputField.requestFocusInWindow
                Thread.sleep(500)
            }
        })
    }
}
