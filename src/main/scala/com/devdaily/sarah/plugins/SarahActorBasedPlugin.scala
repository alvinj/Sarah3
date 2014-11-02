package com.devdaily.sarah.plugins

//import scala.actors.Actor
import com.devdaily.sarah.actors.Brain
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

/**
 * This is the main interface for Sarah Actor-based plugins.
 * 
 * If you want to write a plugin for Sarah that uses an Actor, 
 * you'll need to implement this interface.
 */
trait SarahActorBasedPlugin extends Actor with SarahPlugin {

  // TODO actors no longer have a `start` method
  override def startPlugin {
//      this.start
  }
  
}
