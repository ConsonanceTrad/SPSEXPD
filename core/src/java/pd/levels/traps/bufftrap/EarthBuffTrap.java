package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.AcidWater;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;

public class EarthBuffTrap extends Trap {
	{ color = GREEN; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, AcidWater.class)); }
}
