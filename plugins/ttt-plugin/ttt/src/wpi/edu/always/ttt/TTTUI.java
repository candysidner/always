package wpi.edu.always.ttt;

import java.util.List;

public interface TTTUI {

   public void playAgentMove (TTTUIListener listener);
   public void prepareMoveAndComment();
   public String getCurrentAgentComment ();
   public List<String> getCurrentHumanCommentOptions ();
   public void startPluginForTheFirstTime(TTTUIListener listener);
   public void x(TTTUIListener listener);

}
