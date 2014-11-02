Sarah2
======

New: This code is very similar to my original "Sarah" project, but with a completely
different user interface. Because a few things have changed since I first 
created Sarah, I'm also going to update this code, hopefully this weekend.


Introduction
------------

This is the README file for the alvinalexander.com project named Sarah2,
which is named after the house/computer SARAH in the tv show Eureka.

This project is an attempt to build a software tool that:

   * You can communicate with using speech recognition.
   * Can communicate with you using computer voices (text to voice).
   * Executes commands you want run in real time.
   * Runs other processes/threads/agents that can report to you on a schedule,
     or when certain events occur.


How Sarah2 Works
----------------

Sarah2 is a combination of:

* A UI that works like Alfred, bringing up a special input window when you hit a special keystroke
* Voice recognition provided by Mac OS X
* Speaking voices, also provided by Mac OS X
* The intelligence and plugin architecture created in the original Sarah project

Because Sarah2 is open source, you can connect it to pretty much anything in your home
or on the internet that has an IP address and is willing to talk to you (that is,
it has an open api, or at least an api that you know).


For Developers
--------------

Some of this code is seriously crappy right now. I need to delete a bunch of it
that is dead as a result of the transition from Sarah to Sarah2, and other code
needs to be refactored (because I've written it all very fast in my spare time).


Demo
----

You can see a demo of Sarah (the older Sarah app) on YouTube here:

  http://www.youtube.com/watch?v=CwMFLkp4dyc

You can also see a demo of my semi-related software robot code on 
YouTube here:

  http://www.youtube.com/watch?v=pfhLdc64cek

As you can imagine, these two projects might connect at some point in
the future.


Running Sarah
-------------

Probably the best way to run Sarah2 at the moment is with the
`comile-run.sh` script. See that script for more info.


Notes
-----

I need to document the "Accessibility" stuff in Mac OS X 10.9 (under "Security & Privacy).


More Information
----------------

Sarah was created by Alvin Alexander of http://alvinalexander.com

See http://alvinalexander.com/sarah for more information.




