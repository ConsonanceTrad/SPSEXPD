/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.actors.mobs.AdultDragonViolet;
import pd.effects.MagicMissile;
import render.noosa.TextureFilm;

/** Original ten-frame town guardian dragon sheet. */
public class AdultDragonVioletSprite extends MobSprite {

	public AdultDragonVioletSprite() {
		texture(Assets.Sprites.SPS_ADULT_DRAGON);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true);
		idle.frames(frames, 0, 1, 0, 1, 0, 1, 0);
		run = new Animation(10, false);
		run.frames(frames, 0, 2, 1);
		attack = new Animation(15, false);
		attack.frames(frames, 0, 2, 3, 4, 5);
		zap = attack.clone();
		die = new Animation(10, false);
		die.frames(frames, 0, 5, 6, 7, 8, 9, 9, 9);
		play(run.clone());
	}

	@Override
	public void zap(int cell) {
		turnTo(ch.pos, cell);
		play(zap);
		MagicMissile.boltFromChar(parent, MagicMissile.POISON, this, cell,
				() -> ((AdultDragonViolet) ch).onZapComplete());
	}
}
