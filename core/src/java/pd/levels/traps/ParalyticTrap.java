package pd.levels.traps;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ParalyticGas;
import pd.scenes.GameScene;

public class ParalyticTrap extends Trap {
	{
		color = YELLOW;
		shape = DIAMOND;
	}

	@Override
	public void activate() {
		GameScene.add(Blob.seed(pos, 80 + 5 * Dungeon.legacyDepth(), ParalyticGas.class));
	}
}
