package edu.wpi.always.client;

import java.awt.Point;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class GazeRealizer extends SingleRunPrimitiveRealizer<GazeBehavior> {

   private final ClientProxy proxy;

   public GazeRealizer (GazeBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   @Override
   protected void singleRun () {
      proxy.gaze(translateToAgentTurnHor(getParams().getPoint()), translateToAgentTurnVer(getParams().getPoint()));
      fireDoneMessage();
   }

   public static float translateToAgentTurnHor (Point p) {
      return (160f-p.x)*0.5f/160f;
   }

   public static float translateToAgentTurnVer(Point p) {
      return (120f-p.y)*0.75f/120f;
   }
}
