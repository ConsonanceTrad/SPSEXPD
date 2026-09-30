package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.ShadowGas;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;

public class DarkBuffTrap extends Trap {
	{ color = VIOLET; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, ShadowGas.class)); }
}
