package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;

public class ShockBuffTrap extends Trap {
	{ color = YELLOW; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, ElectriShock.class)); }
}
