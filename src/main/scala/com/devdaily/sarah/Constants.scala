package com.devdaily.sarah

object Constants {

  val APP_NAME = "SARAH"
  val EXIT_CODE_NOT_RUNNING_ON_MAC = 2

  val REL_SARAH_ROOT_DIR    = "Sarah"
  val REL_DATA_DIR          = "Sarah/data"
  val REL_LOGFILE_DIR       = "Sarah/logs"
  val REL_PLUGINS_DIR       = "Sarah/plugins"
  val JSGF_FILENAME         = "sarah.config.xml"
  val LOG_FILENAME          = "sarah.log"
  val FILE_PATH_SEPARATOR   = System.getProperty("file.separator")
  val USER_HOME_DIR         = System.getProperty("user.home")
  val CANON_DATA_DIR        = USER_HOME_DIR + FILE_PATH_SEPARATOR + REL_DATA_DIR
  val CANON_LOGFILE_DIR     = USER_HOME_DIR + FILE_PATH_SEPARATOR + REL_LOGFILE_DIR 
  val CANON_LOGFILE         = CANON_LOGFILE_DIR + FILE_PATH_SEPARATOR + LOG_FILENAME 
  val CANON_PLUGINS_DIR     = USER_HOME_DIR + FILE_PATH_SEPARATOR + REL_PLUGINS_DIR
  val CANON_DEBUG_FILENAME  = CANON_LOGFILE_DIR + FILE_PATH_SEPARATOR + LOG_FILENAME
  val SARAH_ROOT_DIR        = USER_HOME_DIR + FILE_PATH_SEPARATOR + REL_SARAH_ROOT_DIR
  val SARAH_CONFIG_FILE     = CANON_DATA_DIR + FILE_PATH_SEPARATOR + JSGF_FILENAME
  
  // properties file
  val REL_SARAH_PROPERTIES_FILENAME      = "Sarah.properties"
  val CANON_SARAH_PROPERTIES_FILENAME    = SARAH_ROOT_DIR + FILE_PATH_SEPARATOR + REL_SARAH_PROPERTIES_FILENAME
  val PROPS_USERNAME_KEY                 = "your_name"
  val PROPS_TIME_TO_SLEEP_AFTER_SPEAKING = "sleep_after_speaking"

}