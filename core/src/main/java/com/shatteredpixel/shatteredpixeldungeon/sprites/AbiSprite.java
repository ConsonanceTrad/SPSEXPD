/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Abi;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;

/** Original Abbey animation sheet. */
public class AbiSprite extends MobSprite {
	public AbiSprite() {
		texture(Assets.Sprites.SPS_ABI);
		TextureFilm frames = new TextureFilm(texture, 15, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 1, 2, 1, 0, 1, 3, 1);
		run = new Animation(15, true); run.frames(frames, 0, 4, 5, 6, 5);
		attack = new Animation(12, false); attack.frames(frames, 0, 7, 8, 9);
		zap = new Animation(12, false); zap.frames(frames, 0, 10, 11, 12, 13);
		die = new Animation(15, false); die.frames(frames, 0, 13, 14);
		play(idle);
	}
	@Override public void zap(int cell) {
		turnTo(ch.pos, cell);
		play(zap);
		MagicMissile.boltFromChar(parent, MagicMissile.LIGHT_MISSILE, this, cell,
				() -> ((Abi) ch).onZapComplete());
	}
}
