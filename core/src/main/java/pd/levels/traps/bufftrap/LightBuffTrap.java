package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.HolyLight;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;

public class LightBuffTrap extends Trap {
	{ color = WHITE; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, HolyLight.class)); }
}
