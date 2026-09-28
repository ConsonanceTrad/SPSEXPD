package com.shatteredpixel.shatteredpixeldungeon.levels.traps.bufftrap;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.ShadowGas;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class DarkBuffTrap extends Trap {
	{ color = VIOLET; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, ShadowGas.class)); }
}
