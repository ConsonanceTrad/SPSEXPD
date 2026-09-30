/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.items.weapon.missiles.ThrowingKnife;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class GnollArcherSprite extends MobSprite {
	private final Animation cast;
	public GnollArcherSprite() {
		texture(Assets.Sprites.SPS_GNOLL_ARCHER);
		TextureFilm frames = new TextureFilm(texture, 12, 15);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
		run = new Animation(12, true); run.frames(frames, 4, 5, 6, 7);
		attack = new Animation(12, false); attack.frames(frames, 2, 3, 0);
		cast = attack.clone();
		die = new Animation(12, false); die.frames(frames, 8, 9, 10);
		play(idle);
	}
	@Override public void attack(int cell) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {
			((MissileSprite) parent.recycle(MissileSprite.class)).reset(this, cell,
					new ThrowingKnife(), new Callback() {
						@Override public void call() { ch.onAttackComplete(); }
					});
			play(cast);
			turnTo(ch.pos, cell);
		} else super.attack(cell);
	}
}
