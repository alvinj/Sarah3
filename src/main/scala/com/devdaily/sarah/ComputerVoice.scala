package com.devdaily.sarah

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import grizzled.slf4j.Logging

object ComputerVoice extends Logging {

  // most human
  val ALEX       = "ALEX"

  // british voices
  val DANIEL      = "Daniel"
  val KATE        = "Kate"
  val OLIVER      = "Oliver"
  val SERENA      = "Serena"

  // good female voices
  val AGNES       = "AGNES"
  val KATHY       = "KATHY"
  val PRINCESS    = "PRINESS"
  val VICKI       = "VICKI"        // also good
  val VICTORIA    = "VICTORIA"

  // good male voices
  val BRUCE       = "BRUCE"
  val FRED        = "FRED"
  val JUNIOR      = "JUNIOR"
  val RALPH       = "RALPH"

  // other voices
  val ALBERT      = "ALBERT"
  val BAD_NEWS    = "BAD NEWS"
  val BAHH        = "BAHH"
  val BELLS       = "BELLS"
  val BOING       = "BOING"
  val BUBBLES     = "BUBBLES"
  val CELLOS      = "CELLOS"
  val DERANGED    = "DERANGED"
  val GOOD_NEWS   = "GOOD NEWS"
  val HYSTERICAL  = "HYSTERICAL"
  val PIPE_ORGAN  = "PIPE ORGAN"
  val TRINOIDS    = "TRINOIDS"
  val WHISPER     = "WHISPER"
  val ZARVOX      = "ZARVOX"
  
  val VOICES = Array(DANIEL, KATE, OLIVER, SERENA, ALEX, AGNES, KATHY, PRINCESS, VICKI, VICTORIA, BRUCE, FRED, JUNIOR,
                     RALPH, ALBERT, BAD_NEWS, BAHH, BELLS, BOING, BUBBLES, CELLOS, DERANGED,
                     GOOD_NEWS, HYSTERICAL, PIPE_ORGAN, TRINOIDS, WHISPER, ZARVOX)
  
  def isValidVoice(desiredVoice: String): Boolean = {
      for (s <- VOICES) {
          if (s.equals(desiredVoice)) return true
      }
      return false
  }

  def speak(sentence: String) {
      System.out.println("(ComputerVoice) ENTERED SPEAK(" + sentence + ") AT " + System.currentTimeMillis)
      val thingToSay = "say \"" + sentence + "\""
      val scriptEngineManager = new ScriptEngineManager
      val scriptEngine = scriptEngineManager.getEngineByName("AppleScriptEngine")
      try {
          scriptEngine.eval(thingToSay)
      } catch {
          case e: Throwable =>
              logger.error("* AppleScriptEngine was null, stack trace follows *")
              logger.error(e.getStackTrace.mkString("\n"))
      }
  }

  def speak(sentence: String, voice: String) {
      val thingToSay = "say \"" + sentence + "\"" + "using \"" + voice + "\""
      val scriptEngineManager = new ScriptEngineManager
      val scriptEngine = scriptEngineManager.getEngineByName("AppleScript")
      try {
          scriptEngine.eval(thingToSay)
      } catch {
          case e: Throwable =>
              logger.error("* AppleScriptEngine was null, stack trace follows *")
              logger.error(e.getStackTrace.mkString("\n"))
      }
  }

}






