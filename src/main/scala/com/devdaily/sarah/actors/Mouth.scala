package com.devdaily.sarah.actors

import akka.actor._
import com.devdaily.sarah.Sarah
import com.devdaily.sarah.ComputerVoice
import com.devdaily.sarah.plugins.PluginUtils
import com.devdaily.sarah.plugins.PlaySoundFileRequest
import grizzled.slf4j.Logging
import com.valleyprogramming.littlelogger.LittleLogger

case class InitMouthMessage

/**
 * Mouth has the responsibility of speaking whatever it is told to speak,
 * in the order the requests are given. It also sends priority messages to
 * the Brain so the Brain will always know the current Mouth state.
 */
class Mouth(sarah: Sarah) extends akka.actor.Actor with Logging {
  
  // this assumes that the Brain is alive before the Mouth
  val brain: ActorRef = context.actorFor("../Brain")

  var mouthHelper: ActorRef = _

  def receive = {

    case Hello =>
         logger.info("Mouth got `Hello` message")
    
    case InitMouthMessage =>
         logger.info("Mouth got `init` message, starting MouthHelper")
         startMouthHelper
         mouthHelper ! Hello
    
    case message: SpeakMessageFromBrain =>
         logger.info(s"Mouth got SpeakMessageFromBrain message: '${message}'")
         mouthHelper ! message

    case playSoundFileRequest: PlaySoundFileRequest =>
         logger.info(s"Mouth got PlaySoundFileRequest message")
         mouthHelper ! playSoundFileRequest

    case MouthIsSpeaking =>
         // get this from our helper, pass it on
         brain ! MouthIsSpeaking
         
    case MouthIsFinishedSpeaking =>
         // get this from our helper, pass it on
         brain ! MouthIsFinishedSpeaking

    case unknown => 
         logger.info(format("got an unknown request(%s), ignoring it", unknown.toString))
  }

  /**
   * TODO i think i did this like this so the helper could have a reference to Sarah;
   * there are better ways to do this.
   */
  def startMouthHelper {
      mouthHelper = context.actorOf(Props(new MouthHelper(sarah)), name = "MouthHelper")
  }

}











