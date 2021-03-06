package edu.wpi.always.explain;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.schemas.DiscoAdjacencyPairSchema;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.rt.DiscoRT;

public class ExplainPlugin extends Plugin {
   
   public ExplainPlugin (UserModel userModel, CollaborationManager cm) {
      super("Explain", userModel, cm);
      addActivity("ExplainSelf", 0, 0, 0, 0, ExplainSchema.class); 
   }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   /**
    * For testing Explain by itself
    */
   public static void main (String[] args) {
      explainInteraction.load("edu/wpi/always/explain/resources/Explain.xml");
      Plugin.main(args, ExplainPlugin.class, "ExplainSelf");
   }

   // preload task model
   final static DiscoRT.Interaction explainInteraction = new DiscoAdjacencyPairSchema.Interaction();
   static { if ( Always.ALL_PLUGINS ) explainInteraction.load("edu/wpi/always/explain/resources/Explain.xml"); }
   
   // plugin-specific properties
   
   public static final String
      PERFORMED = "ExplainPerformed",
      ABOUT_AGENT = "ExplainAboutAgent",
      PREVIOUS_TALK = "ExplainPreviousTalk",
      PLAN_EXERCISE = "ExplainPlanExercise",
      TALK_ABOUT = "ExplainTalkAbout",
      USE_CALENDAR = "ExplainUseCalendar",
      PLAY_GAMES = "ExplainPlayGames",
      TELL_STORY = "ExplainTellStory",
      DISCUSS_WEATHER = "ExplainDiscussWeather",
      HEAR_ANECDOTES = "ExplainHearAnecdotes",
      ADVISE_NUTRITION = "ExplainAdviseNutrition",
      USE_SKYPE = "ExplainUseSkype";
            
   public static String[] getProperties () {
      return new String[] { PERFORMED, ABOUT_AGENT, PREVIOUS_TALK, PLAN_EXERCISE, TALK_ABOUT, USE_CALENDAR,
                            PLAY_GAMES, TELL_STORY, DISCUSS_WEATHER,
                            HEAR_ANECDOTES, ADVISE_NUTRITION, USE_SKYPE};
   }
  
}
