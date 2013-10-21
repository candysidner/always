package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public abstract class ActivityStateMachineSchema extends ActivitySchema {

   protected final MenuTurnStateMachine stateMachine;

   public ActivityStateMachineSchema (AdjacencyPair initial, 
         BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor) {
      super(behaviorReceiver, behaviorHistory);
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(SPECIFICITY);
      stateMachine.setState(initial);
      stateMachine.setNeedsFocusResource(true); // default value
   }

   @Override
   public void run () {
      propose(stateMachine);
   }
   
   @Override
   public void setNeedsFocusResource (boolean focus) {
      if ( stateMachine != null ) stateMachine.setNeedsFocusResource(focus);
   }
}
