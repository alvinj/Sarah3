package com.devdaily.sarah.utils

import javax.script.ScriptEngineManager
import grizzled.slf4j.Logging

object AppleScriptUtils extends Logging {

    /**
     * Executes the AppleScript command you supply as a String.
     * Returns `true` on success, `false` otherwise.
     */
    def executeAppleScriptCommand(cmd: String): Boolean = {
        val scriptEngineManager = new ScriptEngineManager
        val scriptEngine = scriptEngineManager.getEngineByName("AppleScript")
        try {
            scriptEngine.eval(cmd)
            true
        } catch {
            case e: Throwable =>
                logger.error("EXCEPTION in AppleScriptUtils::executeAppleScriptCommand")
                logger.error(e.getStackTrace.mkString("\n"))
                false
        }
    }

}