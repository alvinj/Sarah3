package com.devdaily.sarah

object Global {

    val appleScriptCmdToStartSpeechRecognition = """
tell application "System Events"
    key code 63
    key code 63
end tell
"""

    val appleScriptCmdToStopSpeechRecognition = """
tell application "System Events"
    key code 63
end tell
"""


}