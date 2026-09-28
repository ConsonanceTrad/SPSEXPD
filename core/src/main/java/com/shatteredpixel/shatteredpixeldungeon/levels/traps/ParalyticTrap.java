package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

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
