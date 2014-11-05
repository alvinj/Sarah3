package com.devdaily.sarah.tests

import javax.swing.JPanel
import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.builder.PanelBuilder
import com.jgoodies.forms.layout.CellConstraints
import javax.swing.JLabel
import java.awt.Dimension
import javax.swing.JTextField
import javax.swing.JFrame

object InputPanelTest extends App {
  
    val f = new JFrame
    f.getContentPane.add(buildInputPanel)
    f.pack
    f.setLocationRelativeTo(null)
    f.setVisible(true)

    /** 
     *  the "input panel" that goes in the North section of the Sarah3 mainframe BorderLayout.
     */
    def buildInputPanel: JPanel = {
      
        val statusLabel = new JLabel
        statusLabel.setPreferredSize(new Dimension(48, 48))
        val inputField = new JTextField
      
        val layout = new FormLayout(
                //    label       textfield
                //    -----       ---------
                "3px, 48px, 2dlu, pref:grow, 2dlu",  //columns
                //    -----       ---------
                "3dlu, p, 3dlu"                      //rows
        )
        val builder = new PanelBuilder(layout)
        builder.setDefaultDialogBorder
    
        val cc = new CellConstraints    
        builder.add(statusLabel, cc.xy(2,2))
        builder.add(inputField,  cc.xy(4,2))

        return builder.getPanel
    
    }

}



