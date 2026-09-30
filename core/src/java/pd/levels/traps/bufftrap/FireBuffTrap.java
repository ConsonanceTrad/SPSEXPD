package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.Fire;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;

public class FireBuffTrap extends Trap {
	{ color = ORANGE; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, Fire.class)); }
}
