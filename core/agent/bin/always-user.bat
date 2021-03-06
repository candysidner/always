@ECHO OFF
SETLOCAL

:: print out user model in human-readable form
::
:: always-user [USER_FILE]
:: 
:: default file is User.owl in always/user or current directory

SET folder="%~d1%~p0"
CD %folder%
java -cp "../class;../../../plugins/about-plugin/about/class;../../../plugins/calendar-plugin/calendar/class;../../../plugins/enroll-plugin/enroll/class;../../../plugins/explain-plugin/explain/class;../../../plugins/greetings-plugin/greetings/class;../../../plugins/rummy-plugin/srummy/class;../../../plugins/checkers-plugin/checkers/class;../../../plugins/scriptbuilder-plugin/class;../../../plugins/skype-plugin/skype/class;../../../plugins/ttt-plugin/ttt/class;../../../plugins/story-plugin/story/class;../../../plugins/weather-plugin/weather/class;../../discoRT/class;../../../user;../../../core/discoRT/lib/picocontainer-2.14.1.jar;../../../core/discoRT/lib/joda-time-2.0.jar;../../../core/discoRT/lib/disco1.6.10/lib/disco.jar;../lib/log4j-1.2.17.jar;../lib/owlapi/owlapi-bin.jar;../lib/owlapi/pellet-rules.jar;../lib/owlapi/pellet-datatypes.jar;../lib/owlapi/pellet-core.jar;../lib/owlapi/jgrapht-jdk1.5.jar;../lib/owlapi/aterm-java-1.6.jar;../lib/owlapi/pellet-owlapiv3.jar;../lib/owlapi/pellet-el.jar" edu.wpi.always.user.UserUtils "%1" 
