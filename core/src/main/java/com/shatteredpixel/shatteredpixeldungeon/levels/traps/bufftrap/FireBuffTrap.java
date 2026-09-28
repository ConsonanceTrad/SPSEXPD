package com.shatteredpixel.shatteredpixeldungeon.levels.traps.bufftrap;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class FireBuffTrap extends Trap {
	{ color = ORANGE; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, Fire.class)); }
}
