package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.items.equipment.weapon.missiles.darts.PoisonDart;
import render.noosa.TextureFilm;

public class ElderAvatarSprite extends MobSprite {
	private final Animation cast;

	public ElderAvatarSprite() {
		texture(Assets.Sprites.SPS_ELDER_AVATAR);
		TextureFilm frames = new TextureFilm(texture, 12, 15);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 0, 0, 1, 1);
		run = new Animation(15, true); run.frames(frames, 2, 3, 4, 5, 6, 7);
		attack = new Animation(12, false); attack.frames(frames, 8, 9, 10);
		cast = attack.clone();
		die = new Animation(5, false); die.frames(frames, 11, 12, 13, 14, 15, 15);
		play(idle);
	}

	@Override
	public void attack(int cell) {
		if (ch != null && Dungeon.level != null && !Dungeon.level.adjacent(ch.pos, cell)) {
			((MissileSprite) parent.recycle(MissileSprite.class))
					.reset(this, cell, new PoisonDart(), ch::onAttackComplete);
			play(cast);
			turnTo(ch.pos, cell);
		} else {
			super.attack(cell);
		}
	}
}
