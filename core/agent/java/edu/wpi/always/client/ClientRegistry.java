package edu.wpi.always.client;

import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.reeti.ReetiFaceTrackerRealizer;
import edu.wpi.always.cm.primitives.AudioFileRealizer;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;

public class ClientRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.as(Characteristics.CACHE).addComponent(new UIMessageDispatcherImpl(new TcpConnection(
            "localhost", 11000)));
      container.addComponent(GazeRealizer.class);
      container.addComponent(FaceExpressionRealizer.class);
      container.addComponent(IdleBehaviorRealizer.class);
      
      switch ( Always.getAgentType() ) {
         case Unity:
            container.addComponent(FaceTrackerRealizer.class);
            break;
         case Reeti:
            container.addComponent(ReetiFaceTrackerRealizer.class);
            break;
         case Mirror:
            container.addComponent(MirrorFaceTrackerRealizer.class);
            break;
      }
      container.addComponent(SpeechRealizer.class);
      container.addComponent(AudioFileRealizer.class);
      container.addComponent(ClientMenuRealizer.class);
      container.addComponent(ClientMenuPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(KeyboardMessageHandler.class);
      container.as(Characteristics.CACHE).addComponent(ClientProxy.class);
   }
}
