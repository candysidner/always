package edu.neu.always.skype;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

// this is the schema for initiating a Skype call

public class SkypeSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {

   public final static Logger.Activity LOGGER_NAME = Logger.Activity.SKYPE;

   public enum Direction { INCOMING, OUTGOING }
   public enum Appointment { APPOINTMENT, NOT_APPOINTMENT }
   
   /* TODO for logging:
    * 
    * Note: If you are satisfied with the log messages that are already
    * automatically generated for start/end of activity and for all
    * user model updates, then you can delete the log method below
    * (and already defined enums above, if any) and go directly to (4) below.
    *
    * (1) Add arguments to log method below as needed (use enums instead of
    *     string constants to avoid typos and ordering errors!)
    *     
    * (2) Update always/docs/log-format.txt with any new logging fields
    * 
    * (3) Call log method at appropriate places in code
    * 
    * (4) Remove this comment!
    *
    */
   public static void log (Direction direction, Appointment appointment, int duration) {
      Logger.logActivity(LOGGER_NAME, direction, appointment, duration, CALLER);
   }
     
   private final ShoreFacePerceptor shore;
   
   public SkypeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, ShoreFacePerceptor shore, Always always) {
      super(new Test(shore),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            LOGGER_NAME);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
      always.getUserModel().setProperty(SkypePlugin.PERFORMED, true);
   }

   // set this variable re incoming call, since used in _SkypeInterruption script
   public static String CALLER ="someone you know";

   // call this method to interrupt current session/activity for incoming call
   public static boolean interrupt () {
      // see definition of _SkypeInterruption in edu.wpi.always.resources.Always.d4g.xml
      return SessionSchema.interrupt("_SkypeInterruption");
   }
   
   @Override
   public void dispose () { 
      super.dispose();
      // this is here so it is run even if schema throws an error
      if ( shore != null ) shore.start(); 
   }
   
   // to test camera release and restart
   
   public static class Test extends MultithreadAdjacencyPair<AdjacencyPair.Context> {

      private final ShoreFacePerceptor shore;
      
      public Test (ShoreFacePerceptor shore) {
         super("I am taking the camera until you say ok", new AdjacencyPair.Context());
         this.shore = shore;
         choice("Ok", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () { 
               getContext().getSchema().stop();
               return null;
            }
         });
      }
      
      @Override
      public void enter () {
         if ( shore != null ) shore.stop();
      }
   }
}
