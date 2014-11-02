package com.devdaily.sarah.gui

import javax.swing.SwingUtilities
import com.devdaily.sarah.MainFrame2

abstract class BaseMainFrameController {
  
  //def getMainFrame: BaseMainFrame
  def getMainFrame: MainFrame2

  def updateUIBasedOnStates
  
  def invokeLater(callback: => Unit) {
    SwingUtilities.invokeLater(new Runnable() {
      def run() {
        callback
      }
    });
  }



}