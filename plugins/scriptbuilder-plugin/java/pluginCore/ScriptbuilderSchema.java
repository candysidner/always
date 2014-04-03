package pluginCore;

import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class ScriptbuilderSchema extends ActivityStateMachineSchema<RAGStateContext> {
	
	private final RAGStateContext context;
	
	public ScriptbuilderSchema(ScriptbuilderCoreScript init,
			BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor,  UIMessageDispatcher dispatcher) {
		super(init, behaviorReceiver, behaviorHistory, resourceMonitor,
				menuPerceptor);
		this.context = init.context;
	}

	@Override
	public void run() {
	   if( context.isDone ){
		   context.firstRun = true;
		   context.isDone = false;
		   stop();
	   }
		else super.run();
	}
}