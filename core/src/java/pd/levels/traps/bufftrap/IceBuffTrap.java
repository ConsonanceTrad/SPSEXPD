package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;

public class IceBuffTrap extends Trap {
	{ color = TEAL; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, FrostCloud.class)); }
}
