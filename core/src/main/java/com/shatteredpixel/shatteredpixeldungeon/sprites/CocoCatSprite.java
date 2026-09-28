/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ShitBall;
import com.watabou.noosa.TextureFilm;

public class CocoCatSprite extends MobSprite {
	public CocoCatSprite() {
		texture("sprites/npcs/sps_town_coconut.png");
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, 0, 0, 1, 1, 0, 0, 2, 2, 3, 3, 2, 2);
		run = new Animation(20, true); run.frames(frames, 4, 5, 7, 5, 7, 5);
		attack = new Animation(12, false); attack.frames(frames, 0, 8, 9, 10, 11);
		zap = attack.clone();
		die = new Animation(20, false); die.frames(frames, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15);
		play(idle);
	}

	@Override public void attack(int cell) {
		if (ch != null && parent != null && Dungeon.level != null && !Dungeon.level.adjacent(cell, ch.pos)) {
			((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos, cell, new ShitBall(), ch::onAttackComplete);
			play(zap);
			turnTo(ch.pos, cell);
		} else {
			super.attack(cell);
		}
	}
}
