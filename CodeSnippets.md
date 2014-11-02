Code Snippets
=============

This is a collection of code snippets that may be useful to know when building Sarah.


Getting Input Focus
-------------------

I used this code in Sarah2 to get input focus:

````
  if (key == "F1") {
      SwingUtilities.invokeLater(new Runnable() {
          def run() {
            if (!mainWindowIsShowing) {
                mainFrame.setVisible(true)
                mainFrame.requestFocusInWindow
                mainFrame.getTextField.requestFocusInWindow
                mainWindowIsShowing = true
                Thread.sleep(500)
            } else {
                mainFrame.setVisible(false)
                mainWindowIsShowing = false
            }
          //startDictation
          }
      });
  }
````


How to create a file for logging/logger
---------------------------------------

From my TypewriterFX code:
  
````
/**
 * create the logfile dir if needed.
 * if it can't be created, write to "/var/tmp/TypewriterFX.log" (or similar)
 */
private def initializeLogfile {
    val logfileDir = new File(Global.CANON_LOG_DIR)
    if (logfileDir.exists) {
        // if the Logs directory exists, use it
        logger.init(Global.CANON_LOG_DIR + Global.FILE_PATH_SEPARATOR + Global.APP_NAME + ".log", LittleLogger.DEBUG)
    } else {
        // if it does not exist, try to create it
        val successful = logfileDir.mkdirs
        if (successful) {
            logger.init(Global.CANON_LOG_DIR + Global.FILE_PATH_SEPARATOR + Global.APP_NAME + ".log", LittleLogger.DEBUG)
        } else {
            logger.init("/var/tmp/" + Global.APP_NAME + ".log", LittleLogger.DEBUG)
        }
    }
}
````   
  
  
  