package wpi.edu.always.ttt;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.SyncSayBuilder;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class TTTSchema extends ActivityStateMachineSchema {

   public TTTSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, TTTUI tttUI,
         UIMessageDispatcher dispatcher,PlaceManager placeManager, PeopleManager peopleManager) {
      super(new WannaPlay(new TTTStateContext(
            keyboard, tttUI, dispatcher, placeManager, peopleManager)),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
   } 
   
   @Override
   public void run() {
      
      super.run();
      
      if(TTTClient.gazeLeft){
         BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
         SyncSayBuilder b = new SyncSayBuilder(
               "<GAZE horizontal=\"-2\" vertical=\"-1\"/>$");
         b.setMetaData(metadata);
         BehaviorMetadata m = b.getMetadata();
         Behavior gazeBehavior = b.build();
         propose(gazeBehavior, m);
         TTTClient.gazeLeft = false;
      }
      else if(TTTClient.gazeBack){
         BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
         SyncSayBuilder b = new SyncSayBuilder(
               "<GAZE horizontal=\"0\" vertical=\"0\"/>$");
         b.setMetaData(metadata);
         BehaviorMetadata m = b.getMetadata();
         Behavior gazeBehavior = b.build();
         propose(gazeBehavior, m);
         TTTClient.gazeBack = false;
      }
      if(TTTClient.nod){
         //TODO fill me in later
         TTTClient.nod = false;
      }
         
   }

   // always adds focus
   @Override
   public void setNeedsFocusResource (boolean focus) {} 
   

}

