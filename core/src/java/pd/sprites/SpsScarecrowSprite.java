/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.items.equipment.weapon.missiles.darts.PoisonDart;
import render.noosa.TextureFilm;

/** Original SPS scarecrow animation and ranged dart presentation. */
public class SpsScarecrowSprite extends MobSprite {

	private int cellToAttack;

	public SpsScarecrowSprite() {
		texture(Assets.Sprites.SPS_SCARECROW);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(3, true);
		idle.frames(frames, 0, 0, 1, 1, 2, 2, 3, 3, 3, 3, 3, 3);
		run = idle.clone();
		attack = new Animation(12, false);
		attack.frames(frames, 0, 2, 3);
		zap = attack.clone();
		die = new Animation(20, false);
		die.frames(frames, 0);
		play(idle);
	}

	@Override
	public void attack(int cell) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {
			cellToAttack = cell;
			turnTo(ch.pos, cell);
			play(zap);
		} else {
			super.attack(cell);
		}
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == zap) {
			idle();
			((MissileSprite) parent.recycle(MissileSprite.class))
					.reset(this, cellToAttack, new PoisonDart(), ch::onAttackComplete);
		} else {
			super.onComplete(anim);
		}
	}
}
