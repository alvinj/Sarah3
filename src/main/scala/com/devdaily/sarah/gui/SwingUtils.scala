package com.devdaily.sarah.gui

import javax.swing.SwingUtilities

object SwingUtils {

    def invokeLater[A](blockOfCode: => A) = {
        SwingUtilities.invokeLater(new Runnable {
            def run {
                blockOfCode
            }
        })
    }

}