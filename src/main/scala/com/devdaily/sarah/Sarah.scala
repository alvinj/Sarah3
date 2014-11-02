package com.devdaily.sarah

import java.io._
import java.util.Properties
import _root_.com.devdaily.sarah.agents._
import _root_.com.devdaily.sarah.plugins._
import akka.actor._
import actors._
import scala.xml._
import collection.Map
import collection.Traversable
import _root_.com.apple.eawt.Application
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.JFrame
import java.awt.Toolkit
import java.awt.Color
import javax.swing.ImageIcon
import scala.collection.mutable.ArrayBuffer
import java.awt.BorderLayout
import java.util.logging.FileHandler
import java.util.logging.Logger
import javax.swing.JEditorPane
import java.awt.Dimension
import java.awt.Insets
import javax.swing.JScrollPane
import javax.swing.JOptionPane
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import java.awt.Frame
import Constants._
import grizzled.slf4j.Logging
import java.awt.Point
import com.devdaily.sarah.gui.TextWindowBuilder
import com.devdaily.sarah.gui.SwingUtils
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.Timer
import com.valleyprogramming.littlelogger.LittleLogger
import com.devdaily.sarah.gui.MainFrame3Controller

/**
 * Sarah starts running in the `main` method here.
 */
object Sarah extends Logging {

  //System.setOut(new PrintStream("/Users/al/Projects/Scala/Sarah3/std.out"))
  //System.setErr(new PrintStream("/Users/al/Projects/Scala/Sarah3/std.err"))

  /* kick off the app, and hold on */
  def main(args: Array[String]) {
      val sarah = new Sarah
      sarah.startRunning
  }
  
}


/**
 * This is the main Sarah class. Along with its companion object, everything starts here.
 * TODO - this class has grown out of control, and needs to be refactored.
 */
// don't need native key stuff any more
//class Sarah extends Logging with NativeKeyListener {
class Sarah extends Logging {
  
  logger.info("* Sarah is Alive *")
  
  var pluginInstances = ArrayBuffer[SarahPlugin]()
  var akkaPluginInstances = ArrayBuffer[SarahAkkaActorBasedPlugin]()
  
  // load properties
  // TODO this should not be hard-coded
  var usersName = "Al"
  var timeToWaitAfterSpeaking = 1250

  loadSarahPropertiesFile(CANON_SARAH_PROPERTIES_FILENAME)
  logger.debug("AFTER LOADING SARAH'S PROPERTIES FILE:")
  logger.debug("    USERNAME:            " + usersName)
  logger.debug("    WAIT AFTER SPEAKING: " + timeToWaitAfterSpeaking)

  // main window status
  var mainWindowIsShowing = false

  // ACTORS
  logger.debug("creating actorsystem and actors")
  val system = ActorSystem("Sarah")
  val brain = system.actorOf(Props(new Brain(this)), name = "Brain")
  val mouth = system.actorOf(Props(new Mouth(this)), name = "Mouth")
  
  logger.debug("sending waitTime message to Brain")
  brain ! Hello
  brain ! SetMinimumWaitTimeAfterSpeaking(timeToWaitAfterSpeaking)
  brain ! ConnectToSiblings
  
  // MAIN FRAME
  logger.debug("about to display main frame")
  val mainFrameController = new MainFrame3Controller(this)
  val mainFrame = mainFrameController.getMainFrame
  configureMainFrame
  displayMainFrame
  
  // this is the secondary window i use in Sarah2 to display output
  val textWindow = new TextWindowBuilder(
          "",
          new Dimension(mainFrame.getWidth, 400), 
          new Point(372, 395))  // MBP
//          new Point(610, 484))  // iMac


  // ----- end constructor -----

  // HANDLE SPEECH
  def sendPhraseToBrain(whatWasHeard: String) {
      logger.debug(s"sending message to Brain: '${whatWasHeard}'")
      brain ! MessageFromEars(whatWasHeard)
  }

  // TODO probably a better way to do these
  def getDataFileDirectory = CANON_DATA_DIR
  def getLogFileDirectory  = CANON_LOGFILE_DIR
  def getFilePathSeparator = FILE_PATH_SEPARATOR

  def startRunning {    
      loadPlugins
      mouth ! InitMouthMessage
      logger.debug("SARAH:startRunning IS COMPLETE ...")
      brain ! PleaseSay("Hello, Al.")
  }

  /**
   * State Management (now in Brain)
   * ---------------------------------------------
   */

  def getAwarenessState:Int = {
      getStateFromBrain(GetAwarenessState)
  }

  def getMouthState:Int = {
      getStateFromBrain(GetMouthState)
  }
  
  private def getStateFromBrain(stateRequestMessage: StateRequestMessage):Int = {
//    implicit val timeout = Timeout(5 seconds)
//    val future = brain ? stateRequestMessage
//    val result = Await.result(future, timeout.duration).asInstanceOf[Int]
//    result
    1
  }
  
  // use this method when setting multiple states at the same time
  def setStates(awareness: Int, mouth: Int) {
      mainFrameController.updateUIBasedOnStates
  }

  
  /**
   * UI and Other Code
   * ---------------------------------------------
   */

  // TODO figure out what *really* needs to be done to get focus back to the input window.
  // this works, but as you can see, it's a kludge.
  // (right now building Sarah is a pain b/c of the Accessibility stuff, and I need a break from this)
  def requestFocusInWindowAndTextField {
      // (1) 1st attempt
      val block = {
          mainFrame.setVisible(true)
          mainFrame.toFront
          mainFrame.requestFocus
          mainFrame.requestFocusInWindow
          mainFrameController.getTextField.requestFocus
          mainFrameController.getTextField.requestFocusInWindow
      }
      SwingUtils.invokeLater(block)
      
      // (1) 2nd attempt
      // TODO this is a kludge
      val listener = new ActionListener {
          def actionPerformed(e: ActionEvent){
              SwingUtils.invokeLater(block)
          }
      }
      val waitTime = 2000
      var timer = new Timer(waitTime, listener)
      timer.setRepeats(false)
      timer.setCoalesce(true)
      timer.start
  }

  def updateUI {
      mainFrameController.updateUIBasedOnStates
  }

  def updateUISpeakingHasEnded {
      mainFrameController.updateUISpeakingHasEnded
      requestFocusInWindowAndTextField
  }
  
  private def loadSarahPropertiesFile(canonConfigFilename: String) {
      val properties = new Properties
      val in = new FileInputStream(canonConfigFilename)
      properties.load(in)
      in.close
      usersName = properties.getProperty(PROPS_USERNAME_KEY)
      val tmp = properties.getProperty(PROPS_TIME_TO_SLEEP_AFTER_SPEAKING)
      timeToWaitAfterSpeaking = tmp.toInt
  }

  def displayAvailableVoiceCommands(voiceCommands: scala.collection.immutable.List[String]) {
    //mainFrameController.displayAvailableVoiceCommands(voiceCommands)
  }
  
  def tryToHandleTextWithPlugins(textTheUserSaid: String): Boolean = {
      logger.debug("tryToHandleTextWithPlugins, TEXT = " + textTheUserSaid)
      logger.debug("about to loop through plugins ...")
      // loop through the plugins, and see if any can handle what was said
      for (plugin <- pluginInstances) {
          // TODO plugins need to be able to update sarah's state 
          logger.debug("plugin: " + plugin.toString)
          val handled = plugin.handlePhrase(textTheUserSaid)
          if (handled) return true
      }
      return false
  }

  def showTextWindow(textToShow: String) {
      // show the text window
      textWindow.setText(textToShow)
      textWindow.setVisible(true)
      requestFocusInWindowAndTextField
  }
  
  def hideTextWindow {
      textWindow.setVisible(false)
      requestFocusInWindowAndTextField
  }
  
  private def loadPlugins {
    // get a list of subdirs in the plugins dir, assume each is a plugin
    logger.debug("Getting list of plugin subdirectories, looking in '" + CANON_PLUGINS_DIR + "'")
    val pluginDirs = getListOfSubDirectories(CANON_PLUGINS_DIR)
    logger.debug("pluginDirs.length = " + pluginDirs.length)
    
    // trying to keep things simple here. if anything goes wrong in the functions we call,
    // they will throw an exception, and we'll log the error and skip that exception.
    try {
      logger.debug("About to loop over pluginDirs ...")
      for (pluginDir <- pluginDirs) {
        val canonPluginDir = CANON_PLUGINS_DIR + FILE_PATH_SEPARATOR + pluginDir
        logger.debug("")
        logger.debug("LOADING PLUGIN: " + canonPluginDir)
        val pluginInfoFilename = getPluginInfoFilename(canonPluginDir)
        logger.debug("pluginInfoFilename = " + pluginInfoFilename)
        val pluginProperties = getPluginProperties(canonPluginDir + FILE_PATH_SEPARATOR + pluginInfoFilename)
        logger.debug("read pluginProperties")
        val pluginJarFilename = getPluginJarFilename(canonPluginDir)
        logger.debug("pluginJarFilename = " + pluginJarFilename)
        val mainClassName = pluginProperties.get("main_class").get
        logger.debug("mainClassName = " + mainClassName)
        val canonJarFilename = canonPluginDir + FILE_PATH_SEPARATOR + pluginJarFilename
        logger.debug("canonJarFilename = " + canonJarFilename)

        logger.debug("creating pluginInstance ...")

        // TODO find a better way to tell the difference, such as reflection or
        // the properties file
        if (mainClassName.contains("Akka")) {
          createAndStartAkkaPlugin(canonJarFilename, canonPluginDir, mainClassName)
        } else {
          createOldPluginInstance(canonJarFilename, canonPluginDir, mainClassName)
        }
        
      } // end for loop
      
      startOlderPlugins
      //startAkkaPlugins
      
    } catch {
      case e: Exception => // ignore, and move on to next plugin
           logger.error("Had a problem loading a plugin:")
           logger.error(e.getMessage)
           e.printStackTrace
      case e: RuntimeException =>
           logger.error("Got a RuntimeException loading a plugin." + e.getMessage)
           e.printStackTrace
      case e: Error =>
           logger.error("Got an Error loading a plugin." + e.getMessage)
           e.printStackTrace
    }
  }
  
  private def createOldPluginInstance(canonJarFilename:String, canonPluginDir:String, mainClassName:String) {
    val pluginInstance = getPluginInstance(canonJarFilename, mainClassName)
    logger.debug("created pluginInstance, setting canonPluginDir")
    pluginInstance.setPluginDirectory(canonPluginDir)
    pluginInstances += pluginInstance
  }
  
  private def createAndStartAkkaPlugin(canonJarFilename:String, canonPluginDir:String, mainClassName:String) {
    try {
      logger.debug("In getAkkaPluginInstance, creating classLoader ...")
      logger.debug("  canonicalJarFilename = " + canonJarFilename)
      logger.debug("  mainClassName = " + mainClassName)
      logger.debug("  creating classloader ...")
      var classLoader = new java.net.URLClassLoader(Array(new File(canonJarFilename).toURI.toURL), this.getClass.getClassLoader)
      logger.debug("  creating plugin ActorRef ...")
      val pluginRef = system.actorOf(Props(classLoader.loadClass(mainClassName).newInstance.asInstanceOf[SarahAkkaActorBasedPlugin]), name = mainClassName)

      // give the brain and pluginRef references to each other
      pluginRef ! SetPluginDir(canonPluginDir)
      pluginRef ! StartPluginMessage(brain)
      logger.debug("  setting plugin dir to: " + canonPluginDir)
      brain ! HeresANewPlugin(pluginRef)
      
      // TODO add this back in, make it a message
//      akkaPluginInstances += akkaPluginInstance

      //      var pluginInstance:SarahAkkaActorBasedPlugin = classLoader.loadClass(mainClassName).newInstance.asInstanceOf[SarahAkkaActorBasedPlugin]
      logger.debug("returning new plugin instance ...")
    } catch {
      case cce: ClassCastException => logger.error(cce.getMessage())
                                      throw cce
      case ame: AbstractMethodError => logger.error(ame.getMessage())
                                      throw new Exception("GOT AN AbstractMethodError")
      case e:   Exception =>          logger.error(e.getMessage())
                                      throw e
    }

  }

  // TODO/NOTE Actor no longer has a `start` method
  private def startOlderPlugins {
      logger.debug("starting old plugins ...")
      for (plugin <- pluginInstances) {
          logger.debug("Trying to start plugin instance: " + plugin.toString())
          connectInstanceToBrain(plugin)
          //startPlugin(plugin)
      }
  }
  
//  def startAkkaPlugins {
//    logger.debug("starting akka actor plugins ...")
//    for (plugin <- akkaPluginInstances) {
//      logger.debug("Trying to start plugin instance: " + plugin.toString())
//      brain ! StartThisPlugin(plugin)
//    }
//  }
  

  /**
   * Returns the plugin as a ready-to-run instance, or throws an exception.
   */
  private def getPluginInstance(canonicalJarFilename: String, mainClassName: String): SarahPlugin = {
    try {
      logger.debug("creating classLoader ...")
      logger.debug("  canonicalJarFilename = " + canonicalJarFilename)
      logger.debug("  mainClassName = " + mainClassName)
      var classLoader = new java.net.URLClassLoader(Array(new File(canonicalJarFilename).toURI.toURL), this.getClass.getClassLoader)
      logger.debug("creating new plugin instance as a SarahPlugin ...")

      // try to create plugin as an instance of SarahActorBasedPlugin. if that fails, try to create it as an
      // instance of just a SarahPlugin
      var pluginInstance:SarahPlugin = classLoader.loadClass(mainClassName).newInstance.asInstanceOf[SarahPlugin]
      logger.debug("returning new plugin instance ...")
      return pluginInstance
    } catch {
      case cce: ClassCastException => logger.error(cce.getMessage())
                                      throw cce
      case ame: AbstractMethodError => logger.error(ame.getMessage())
                                      throw new Exception("GOT AN AbstractMethodError")
      case e:   Exception =>          logger.error(e.getMessage())
                                      throw e
    }
  }
  
  /**
   * Returns the plugin as a ready-to-run instance, or throws an exception.
   */
//  def getAkkaPluginInstance(canonicalJarFilename: String, mainClassName: String): SarahAkkaActorBasedPlugin = {
//  }
  
  private def connectInstanceToBrain(pluginInstance: SarahPlugin) {
      logger.debug("connecting instance to brain")
      pluginInstance.connectToBrain(brain)
  }

  private def startPlugin(pluginInstance: SarahPlugin) {
      // TODO this isn't really used any more since upgrading Akka
      pluginInstance.startPlugin
      logger.debug("started plugin")
  }

  
  /**
   * Get the plugin properties (plugin_name, main_class), or throw an exception.
   */
  private def getPluginProperties(infoFilename: String): Map[String, String] = {
    try {
      val properties = new Properties
      val in = new FileInputStream(infoFilename)
      properties.load(in)
      in.close()
      val pluginName = properties.getProperty("plugin_name", "[NO NAME]")
      val mainClass = properties.getProperty("main_class", "")
      if (mainClass.trim().equals("")) {
        throw new Exception("main_class not found in .info file (" + infoFilename + ").")
      }
      return Map("main_class" -> mainClass, "plugin_name" -> pluginName)
    } catch {
      case e:Exception => logger.error(e.getMessage())
                          throw e
    }
  }

  /**
   * Get a list representing all the sub-directories in the given directory.
   */
  private def getListOfSubDirectories(directoryName: String): Array[String] = {
      val dir = new File(directoryName)
      val listOfFiles = dir.listFiles
      if (listOfFiles == null) return Array[String]()
      val filteredList = listOfFiles.filter(_.isDirectory).map(_.getName)
      filteredList
  }

  /**
   * Returns the name of the plugin's ".info" file, or throws an exception.
   * Code assumes there is one .info file in the current dir.
   * Throws an Exception if a file is not found.
   * TODO refactor this function to work with the similar '.jar' function.
   */
  private def getPluginInfoFilename(directoryName: String):String = {
      val folder = new File(directoryName)
      val files = folder.listFiles(new FilenameFilter {
          def accept(file: File, filename: String): Boolean = {
              return (filename.endsWith(".info"))
          }
      })
      if (files == null || files.length > 0) {
          return files(0).getName
      } else {
          throw new Exception("No .info file found in directory '" + directoryName + "'")
      }
  }

  /**
   * Returns the name of the plugin's ".jar" file.
   * Code assumes there is one .jar file in the current dir.
   * Throws an Exception if a file is not found.
   * TODO refactor this function to work with the similar '.info' function.
   */
  private def getPluginJarFilename(directoryName: String):String = {
      val folder = new File(directoryName)
      val files = folder.listFiles(new FilenameFilter {
          def accept(file: File, filename: String): Boolean = {
              return (filename.endsWith(".jar"))
          }
      })
      if (files == null || files.length > 0) {
          return files(0).getName
      } else {
          throw new Exception("No .jar file found in directory '" + directoryName + "'")
      }
  }
  
  
  /**
   * If the app is not running on mac os x, die right away.
   */
  private def dieIfNotRunningOnMacOsX
  {
      val mrjVersionExists = System.getProperty("mrj.version") != null
      val osNameExists = System.getProperty("os.name").startsWith("Mac OS")
    
      if ( !mrjVersionExists || !osNameExists)
      {
          System.err.println("SARAH is not running on a Mac OS X system, terminating.")
          System.exit(EXIT_CODE_NOT_RUNNING_ON_MAC)
      }
  }
  
  private def configureForMacOSX
  {
      // set some mac-specific properties; helps when i don't use ant to build the code
      System.setProperty("apple.awt.graphics.EnableQ2DX", "true")
      System.setProperty("apple.laf.useScreenMenuBar", "true")
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME)

      // create an instance of the Mac Application class, so i can handle the 
      // mac quit event with the Mac ApplicationAdapter
      val macApplication = Application.getApplication
      val macAdapter = new MacApplicationAdapter(this)
      macApplication.addApplicationListener(macAdapter)
    
      // TODO - enable when ready (must enable the preferences option manually)
      //macApplication.setEnabledPreferencesMenu(true)
  }
  
  // TODO implement
  def handleMacQuitAction {
      shutdown
  }

  // TODO implement
  def handleMacPreferencesAction {
  }
  
  def handleMacAboutAction {
    // used html here so i can add a hyperlink later
    val ABOUT_DIALOG_MESSAGE = "<html><center><p>SARAH</p></center>\n\n" + "<center><p>Created by Alvin Alexander, devdaily.com</p><center>\n"
    val editor = new JEditorPane
    editor.setContentType("text/html")
    editor.setEditable(false)
    editor.setSize(new Dimension(400,300))
    editor.setFont(UIManager.getFont("EditorPane.font"))
    // note: had to include this line to get it to use my font
    editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
    editor.setMargin(new Insets(5,15,25,15))
    editor.setText(ABOUT_DIALOG_MESSAGE)
    editor.setCaretPosition(0)
    val scrollPane = new JScrollPane(editor)
    // display our message
    JOptionPane.showMessageDialog(mainFrameController.getMainFrame, scrollPane,
        "About Hyde", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private def configureMainFrame {
      try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
      } catch {
          case e: Exception => // ignore
      }
      SwingUtilities.invokeLater(new Runnable()
      {
          def run()
          {
              //mainFrame.setResizable(true)
              //mainFrame.setLocation(getDesiredMainFrameLocation)
          }
      });
  }
  
//  private def getDesiredMainFrameLocation = {
//      val mainFrameWidth = mainFrame.getWidth
//      val mainFrameHeight = mainFrame.getHeight
//      val screenSize = Toolkit.getDefaultToolkit.getScreenSize
//      val screenHeight = screenSize.height
//      val screenWidth = screenSize.width
//      val y0 = screenHeight / 3.0
//      val x0 = (screenWidth - mainFrameWidth) / 2.0
//      new Point(x0.toInt, y0.toInt)
//  }
  
  private def displayMainFrame {
      SwingUtilities.invokeLater(new Runnable()
      {
          def run()
          {
              mainFrame.setVisible(true)
              mainFrame.transferFocus
              mainWindowIsShowing = true
          }
      });
  }

  // TODO get this code to work properly. System.exit isn't really exiting.
  def shutdown {
      logger.debug("Shutting down.")
      brain ! Die
      PluginUtils.sleep(500)
      System.exit(0)
  }
  
}
 





