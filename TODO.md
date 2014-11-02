To-Do List for Sarah3
=====================

---++ Bug Fixes

* the way i'm starting the actors and connecting them can be improved 
* get plugins and plugin api completely working (currently stops when i have too many plugins, or perhaps a bad one)


---++ Short Term

* add properties file for things like username, zip code, preferred voice, which can be used by multiple plugins
* match possible stock phrases with regular expressions
  (*stock prices*)
  ([check|get]) stock* (price|prices) 
* add "What Can I Say" functionality
* create new "current time" function as plugin
* create new "current date" function as plugin
* get a weather plugin working
* get email plugin working (requires a database)
* let user move the frame, and also let it slide back to (0,0)
* let the user control the voice that's used
* build all the voice files, including those from plugins, at startup, or on each speech request
* check for updates on startup
* clean up Sarah.scala, it's gotten really big

---++ Thinking About

* let plugins add their own "say" functions, so they don't have to use applescript


---++ Long Term

* use JavaFX for the ui?
* run all speech text through scala so the ui can know what's being said
  (need this for animations, shorter pauses)
* can i reload commands and sarah.gram on each request?
* rotate the background ui color randomly
* add Wikipedia Reader as a plugin



