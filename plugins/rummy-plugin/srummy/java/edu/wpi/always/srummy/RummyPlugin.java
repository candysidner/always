package edu.wpi.always.srummy;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

//SRUMMY, delete this cm later

public class RummyPlugin extends Plugin {
   
   public RummyPlugin (UserModel userModel, CollaborationManager cm) {
      super("Rummy", userModel, cm);
      addActivity("PlayRummy", 0, 0, 0, 0, RummySchema.class, RummyClient.class); 
   }
   
   /**
    * For testing Rummy by itself
    */
   public static void main (String[] args) {
	   Plugin.main(args, RummyPlugin.class, "PlayRummy");
   }
  

  
}