package edu.wpi.always.cm.perceptors.fake;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;
import java.awt.Point;
import javax.swing.JTextField;

public class FakeMovementPerceptor implements MovementPerceptor {

   private final JTextField txtX;
   private final JTextField txtY;
   private volatile MovementPerception latest;

   public FakeMovementPerceptor (JTextField txtX, JTextField txtY) {
      this.txtX = txtX;
      this.txtY = txtY;
   }

   @Override
   public void run () {
      Point p = tryParsePoint();
      if ( p == null )
         latest = null;
      else
         latest = new MovementPerception(DateTime.now(), true, p);
   }

   private Point tryParsePoint () {
      return tryParsePoint(txtX.getText(), txtY.getText());
   }

   public static Point tryParsePoint (String xText, String yText) {
      int x, y;
      try {
         x = Integer.parseInt(xText);
      } catch (NumberFormatException ex) {
         return null;
      }
      try {
         y = Integer.parseInt(yText);
      } catch (NumberFormatException ex) {
         return null;
      }
      return new Point(x, y);
   }

   @Override
   public MovementPerception getLatest () {
      return latest;
   }
}