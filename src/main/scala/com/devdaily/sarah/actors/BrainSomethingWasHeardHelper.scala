package com.devdaily.sarah.actors

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.devdaily.sarah.plugins.PleaseSay
import collection.JavaConversions._
import java.util.ArrayList
import java.util.HashMap
import com.devdaily.sarah.VoiceCommand
import javax.script.ScriptEngineManager
import com.devdaily.sarah.plugins.PluginUtils
import java.util.StringTokenizer
import com.devdaily.sarah.SarahJavaHelper
import java.io.IOException
import scala.collection.mutable.ArrayBuffer
import javax.script.ScriptException
import com.devdaily.sarah.Sarah
import java.io.File
import com.devdaily.sarah.plugins.PlaySoundFileRequest
import com.devdaily.sarah.actors._
import grizzled.slf4j.Logging
import com.valleyprogramming.littlelogger.LittleLogger

/**
 * 
 * With the new Sarah2 UI, all of this "go to sleep", "wake up",
 * and state-management code can go away.
 * 
 */
class BrainSomethingWasHeardHelper(sarah: Sarah)
extends Actor
with Logging
{

  logger.info("* The BrainHeardSomethingHelper is Alive *")
  
  // TODO an assumption here is that the Mouth actor exists before we do; may not be safe, so i used a var earlier
  val brain: ActorRef = context.parent                    // Brain is our parent
  //val mouth: ActorRef = context.actorFor("../../Mouth")   
  var mouth: ActorRef = null 
  val pathToMouthActor = "../../Mouth"   // Mouth is Brain's sibling (up to Brain, up to System, down to Mouth)
  
  // map(sentence, appleScriptKey)
  var phraseCommandMapFiles: Array[String] = null
  var allPossibleSentences: List[String] = null
  var commandFiles: Array[String] = null
  
  // these need to be initialized
  val allVoiceCommands = new ArrayList[VoiceCommand]
  var phraseToCommandMap = new HashMap[String, String]
  
  def receive = {
    case Hello => 
         logger.info("BrainSomethingWasHeardHelper got 'Hello' message")

    case SomethingWasHeard(t, s, a) =>
         handleSomethingWeHeard(t, s, a)

    case pleaseSay: PleaseSay =>
         logger.info(format("got a please-say request (%s) at (%d)", pleaseSay.textToSay, System.currentTimeMillis))
         handlePleaseSayRequest(pleaseSay)
      
    case playSoundFileRequest: PlaySoundFileRequest =>
         handleSoundFileRequest(playSoundFileRequest)
      
    case unknown => 
         logger.info(format("got an unknown request(%s), ignoring it", unknown.toString))
  }
  
  private def handlePleaseSayRequest(pleaseSay: PleaseSay) {
      logger.info(format("sending msg (%s) to mouth at (%d)", pleaseSay.textToSay, System.currentTimeMillis))
      getMouth ! SpeakMessageFromBrain(pleaseSay.textToSay)
  }  
  
  /**
   * TODO move this to another actor, or rename this actor.
   */
  def handleSoundFileRequest(playSoundFileRequest: PlaySoundFileRequest) {
      getMouth ! playSoundFileRequest
  }
  
  /**
   * The main handler for when the ears hear something and send it to us.
   */
  private def handleSomethingWeHeard(whatWeHeard: String,
                                     inSleepMode: Boolean,
                                     awarenessState: Int) {
    if (whatWeHeard==null || whatWeHeard.trim().equals("")) return
    if (inSleepMode) {
        logger.info("in sleep mode, checking to see if this is a WakeUp request")
        // if we're sleeping, the only request we respond to is "wake up"
        brain ! SetEarsState(Brain.EARS_STATE_HEARD_SOMETHING)
        handleWakeUpRequestIfReceived(whatWeHeard, awarenessState)
    } else {
        logger.info("calling handleVoiceCommand")
        brain ! SetEarsState(Brain.EARS_STATE_NOT_LISTENING)
        handleVoiceCommand(whatWeHeard)
        brain ! SetEarsState(Brain.EARS_STATE_LISTENING)
    }
  }
  
  /**
   * handle the wake-up request if it was received.
   * otherwise, go back to sleep.
   */
  private def handleWakeUpRequestIfReceived(whatTheComputerThinksISaid: String,
                                            awarenessState: Int) {
    val prevSleepState = awarenessState
    if (whatTheComputerThinksISaid.matches(".*wake up.*")) {
        doWakeUpActions
    } else {
        brain ! SetEarsState(Brain.EARS_STATE_LISTENING)
    }
  }

  def getAppleScriptEngine: javax.script.ScriptEngine = {
      val scriptEngineManager = new ScriptEngineManager
      return scriptEngineManager.getEngineByName("AppleScript")
  }

  // handle the text the computer thinks the user said
  private def handleVoiceCommand(whatTheComputerThinksISaid: String) {
    logger.info("entered handleVoiceCommand, text is: " + whatTheComputerThinksISaid)

    val textTheUserSaid = whatTheComputerThinksISaid.toLowerCase

    // re-load these to let the user change commands while we run
    loadAllUserConfigurationFilesOrDie

    if (handleSpecialVoiceCommands(textTheUserSaid)) {
        logger.info("Handled a special voice command, returning.")
        return
    }

    // if the command phrase is in the map, do some work
    if (phraseToCommandMap.containsKey(textTheUserSaid)) {
        logger.info("phraseToCommandMap contained key, trying to process")
        // handle whatever the user said
        logger.info("handleVoiceCommand, found your phrase in the map: " + textTheUserSaid)
        val handled = handleUserDefinedVoiceCommand(textTheUserSaid)
    }
    else {
        // there were no matches; check the plugins registered with sarah
        logger.info(format("phraseToCommandMap didn't have key (%s), trying plugins", textTheUserSaid))
        val handled = sarah.tryToHandleTextWithPlugins(textTheUserSaid)
        if (handled) {
            brain ! SetBrainStates(getAwarenessState, Brain.EARS_STATE_LISTENING, Brain.MOUTH_STATE_NOT_SPEAKING)
        } else {
            // TODO a bit of a kludge to update the ui; i don't really use these states in Sarah2
            brain ! PleaseSay(getRandomImSorryPhrase)
            brain ! SetBrainStates(getAwarenessState, Brain.EARS_STATE_LISTENING, Brain.MOUTH_STATE_NOT_SPEAKING)
        }
    }
  }

  private def getRandomImSorryPhrase = {
      val r = new scala.util.Random
      val phrases = Array(
          "Sorry, I don't know how to do that.",
          "Sorry, I don't understand you.",
          "Sorry, you're on your own.",
          "Bugger, I don't know how to do that.",
          "I ... can't ... do ... that.",
          "I got nothin'.",
          "Good luck with that.",
          "No comprendo.",
          "Who is asking?",
          "Huh?",
          "What?",
          "You got me.")
      phrases(r.nextInt(phrases.size))
  }
  
  // TODO there's probably a better way to do this
  def getAwarenessState:Int = {
    implicit val timeout = Timeout(2 seconds)
    val future = brain ? GetAwarenessState
    val result = Await.result(future, timeout.duration).asInstanceOf[Int]
    result
  }

  // TODO there's probably a better way to do this
  def getEarsState:Int = {
    implicit val timeout = Timeout(2 seconds)
    val future = brain ? GetEarsState
    val result = Await.result(future, timeout.duration).asInstanceOf[Int]
    result
  }

  // TODO there's probably a better way to do this
  def inSleepMode:Boolean = {
    implicit val timeout = Timeout(2 seconds)
    val future = brain ? GetInSleepMode
    val result = Await.result(future, timeout.duration).asInstanceOf[Boolean]
    result
  }
  
  /**
   * A function to handle "special commands" that are not available to the 
   * user via configuration files, like "go to sleep", "wake up", and
   * "shut down". Returns true if the voice command was handled.
   */
  private def handleSpecialVoiceCommands(textTheUserSaid: String):Boolean = {
    logger.info("entered handleSpecialVoiceCommands")

    if (textTheUserSaid.trim().equals("")) { 
        logger.info("(Brain) Got a blank string from Ears, ignoring it.")
        return true
    }

    else if (textTheUserSaid.trim.equals("thanks") || textTheUserSaid.trim.equals("thank you")) { 
        replyToUserSayingThankYou
        return true
    }

    else if (textTheUserSaid.trim.equals("computer")) { 
        replyToUserSayingComputer
        return true
    }

    // "close the window", "hide the window", "close window"
    else if (textTheUserSaid.trim.toLowerCase.matches("(close|hide) .*window")) { 
        brain ! HideTextWindow
        brain ! MouthIsFinishedSpeaking  //TODO i don't like handling this as a special case
        return true
    }

    else if (textTheUserSaid.trim.toLowerCase.matches("(bye|goodbye|adios|later) *(sarah)*")
             || textTheUserSaid.trim.toLowerCase.matches("laters* (baby|sarah)")) { 
        speak("Live long, and prosper.")
        PluginUtils.sleep(2000)
        sarah.shutdown
        return true
    }

    else if (!inSleepMode && textTheUserSaid.matches(".*go to sleep.*")) {
        doGoToSleepActions
        return true
    }

    else if (!inSleepMode && textTheUserSaid.matches(".*what can i say.*")) {
        listAvailableVoiceCommands
        return true
    }
    
    return false
  }

  /**
   * handle the wake-up request if it was received.
   * otherwise, go back to sleep.
   */
  private def handleWakeUpRequestIfReceived(whatTheComputerThinksISaid: String) {
      val prevSleepState = getAwarenessState
      if (whatTheComputerThinksISaid.matches(".*wake up.*")) {
          doWakeUpActions
      } else {
          brain ! SetEarsState(Brain.EARS_STATE_LISTENING)
          brain ! SetAwarenessState(prevSleepState)
      }
  }
  
  // TODO this is probably a bug here; probably need to separate sleeping and listening concepts
  private def doGoToSleepActions {
      speak("Going to sleep")
      // always need to tell intermediary to start listening after we've finished speaking
      brain ! SetBrainStates(Brain.AWARENESS_STATE_LIGHT_SLEEP, Brain.EARS_STATE_LISTENING, Brain.MOUTH_STATE_NOT_SPEAKING)
      printMode
  }

  private def doWakeUpActions {
      speak("I'm awake now.")
      brain ! SetBrainStates(Brain.AWARENESS_STATE_AWAKE, Brain.EARS_STATE_LISTENING, Brain.MOUTH_STATE_NOT_SPEAKING)
  }

  /**
   * "speak" functionality has been moved to the mouth,
   * just passing it through here
   * -------------------------------------------------
   */
  private def speak(textToSpeak: String) {
      logger.info("entered speak, text is: " + textToSpeak)
      println(format("sending message (%s) to mouth at (%d)", textToSpeak, System.currentTimeMillis))
      getMouth ! SpeakMessageFromBrain(textToSpeak)
  }
  
  /**
   * get the `mouth` reference when we're first called.
   * i created this because the Mouth may not be instantiated before the Brain (hmm ... though this 
   * may still fail)
   */
  private def getMouth: ActorRef = {
      if (mouth == null) mouth = context.actorFor(pathToMouthActor)
      if (mouth == null) logger.info("in getMouth, the mouth was still null (this is bad news)")
      mouth
  }
  
  // TODO move to Utils
  def getCurrentTime = System.currentTimeMillis

  /**
   * 
   * TODO Get this method out of the Brain.
   * 
   * Run the AppleScript command encapsulated in the AppleScriptCommand object.
   * (This is currently just a wrapper around a string.)
   * 
   * TODO speaking can happen through here also, which is a problem.
   * 
   */
  private def runAppleScriptCommand(command: String) {
    // TODO handle the sarah awareness states properly
    logger.info("entered runAppleScriptCommand")
    // sarah is probably going to speak here
    val prevAwarenessState = getAwarenessState
    brain ! SetBrainStates(prevAwarenessState, Brain.EARS_STATE_NOT_LISTENING, Brain.MOUTH_STATE_SPEAKING)
    try {
      logger.info("calling appleScriptEngine.eval(command)")
      logger.info(format("  timestamp = %d", getCurrentTime))
      val appleScriptEngine = getAppleScriptEngine
      appleScriptEngine.eval(command)
    } catch {
      case e: ScriptException => logger.error(e.getMessage)
    } finally {
      // TODO is it correct to set this back to the previous state, or
      //      should i set it to 'listening'?
      brain ! SetBrainStates(prevAwarenessState, Brain.EARS_STATE_LISTENING, Brain.MOUTH_STATE_NOT_SPEAKING)
      brain ! MouthIsFinishedSpeaking
      logger.info("finished appleScriptEngine.eval(command)")
      logger.info(format("  timestamp = %d", getCurrentTime))
      // TODO should be able to get rid of this at some point
      PluginUtils.sleep(Brain.SHORT_DELAY)
      logger.info("LEAVING appleScriptEngine.eval(command)")
      logger.info(format("  timestamp = %d", getCurrentTime))
    }
  }
  
  /**
   * Runs the AppleScript command given by the VoiceCommand.
   * I moved this function here from the AppleScriptUtils class 
   * because of multithreading concerns.
   */
  private def runUserDefinedCommand(vc: VoiceCommand) {
    logger.info("(Brain) vc.command:     " + vc.getCommand())
    logger.info("(Brain) vc.applescript: " + vc.getAppleScript())
    var appleScriptCommand = vc.getAppleScript()
    // split up multiline commands:
    // tell app iTunes to play next track | say "Next track"
    if (appleScriptCommand.indexOf("|") >0)
    {
      val sb = new StringBuilder()
      // create a newline wherever there was a pipe symbol
      val st = new StringTokenizer(appleScriptCommand, "|")
      while (st.hasMoreTokens()) {
        sb.append(st.nextToken().trim())
        if (st.hasMoreTokens()) sb.append("\n")
      }
      appleScriptCommand = sb.toString
    }
    
    runAppleScriptCommand(appleScriptCommand)
  }
  
  private def loadAllUserConfigurationFilesOrDie() {
      logger.info("entered loadAllUserConfigurationFilesOrDie")
      if (allVoiceCommands != null) allVoiceCommands.clear()
      if (phraseToCommandMap != null) phraseToCommandMap.clear()

      // (appleScriptKey, appleScriptToExecute)
      commandFiles = SarahJavaHelper.getAllFilenames(sarah.getDataFileDirectory, "commands")
      if (commandFiles.length == 0) {
          logger.error("Could not find any command files, aborting.")
          System.exit(1)
      }
    
      loadAllVoiceCommands()
      // load the map of sentences to commands (sentence, appleScriptKey)
      phraseCommandMapFiles = SarahJavaHelper.getAllFilenames(sarah.getDataFileDirectory, "c2p")
      if (phraseCommandMapFiles.length == 0) {
          logger.error("Could not find any phrase command map files, aborting.")
          System.exit(1)
      }

      SarahJavaHelper.loadAllSentenceToCommandMaps(phraseCommandMapFiles, ":", sarah.getDataFileDirectory, phraseToCommandMap);
  }

  
  private def loadAllVoiceCommands() {
      logger.info("entered loadAllVoiceCommands")
      for (cmdFile <- commandFiles) {
          var canonFilename = sarah.getDataFileDirectory + File.separator + cmdFile
          try
          {
              var commands = SarahJavaHelper.getCurrentVoiceCommands(canonFilename, ":")
              allVoiceCommands.addAll(commands)
          }
          catch
          {
            case e:IOException => logger.info("Error trying to load voice commands.")
                                  e.printStackTrace()
          }
      }
  }

  /**
   * List all the voice command the user can say.
   */
  private def listAvailableVoiceCommands() {
      // get all voice commands from the config files (populates allVoiceCommands)
      loadAllUserConfigurationFilesOrDie
    
      val voiceCommandsAsStrings = allVoiceCommands.map(_.getCommand)
      val voiceCommandListForSarah = ArrayBuffer[String]()
      voiceCommandListForSarah.addAll(voiceCommandsAsStrings)
      voiceCommandListForSarah += "go to sleep"
      voiceCommandListForSarah += "wake up"
      voiceCommandListForSarah += "what can i say?"
      voiceCommandListForSarah += "soylent green is people"
      voiceCommandListForSarah += "please listen"
      
      sarah.displayAvailableVoiceCommands(voiceCommandListForSarah.toList)
  }
  
  private def handleUserDefinedVoiceCommand(textTheUserSaid: String): Boolean = {
      logger.info("entered handleUserDefinedVoiceCommand")
      val commandFileKey = phraseToCommandMap.get(textTheUserSaid)  // ex: COMPUTER, JUST_CHECKING
      logger.info("Brain::handleUserDefinedVoiceCommand, commandFileKey = " + commandFileKey)
      // foreach is enabled by importing JavaConversions._ above
      allVoiceCommands.foreach{ voiceCommand =>
          val voiceCommandKey = voiceCommand.getCommand()
          if (voiceCommandKey.equalsIgnoreCase(commandFileKey)) {
              if (voiceCommand.getAppleScript==null || voiceCommand.getAppleScript.trim.equals("")) {
                  logger.info("handleUserDefinedVoiceCommand, appleScript is not defined, passing on it")
                  return false
              }
              if (!inSleepMode || voiceCommand.worksInSleepMode()) {
                  logger.info("running runUserDefinedCommand(voiceCommand)")
                  runUserDefinedCommand(voiceCommand)
                  printMode
                  return true
              }
              else
              {
                  printMode
                  logger.info("In sleep mode, ignoring command.")
                  return false
              }
          }
      }
      return false
  }
  
  private def replyToUserSayingThankYou {
      logger.info("entered replyToUserSayingThankYou")
      val textToSay = PluginUtils.getRandomStringFromFile(sarah.getDataFileDirectory + "/" + Brain.REPLY_TO_THANK_YOU_FILE)
      speak(textToSay)
  }
  
  private def replyToUserSayingComputer {
      logger.info("entered replyToUserSayingComputer")
      val textToSay = PluginUtils.getRandomStringFromFile(sarah.getDataFileDirectory + "/" + Brain.SAY_YES_FILE)
      speak(textToSay)
  }  
  
  private def printMode() {
      System.out.format ("listeningMode: %s\n", if (inSleepMode) "QUIET/SLEEP" else "NORMAL")
  }  
  
}









