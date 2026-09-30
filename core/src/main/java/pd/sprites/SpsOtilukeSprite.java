/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.actors.mobs.Otiluke;
import pd.effects.MagicMissile;
import render.noosa.TextureFilm;

public class SpsOtilukeSprite extends MobSprite {

	public SpsOtilukeSprite() {
		texture(Assets.Sprites.SPS_OTILUKE);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true);
		idle.frames(frames, 0, 0, 0, 3, 0, 0, 3, 3);
		run = new Animation(15, true);
		run.frames(frames, 0, 1, 2, 0);
		attack = new Animation(12, false);
		attack.frames(frames, 0, 1, 4, 4, 4);
		zap = attack.clone();
		die = new Animation(15, false);
		die.frames(frames, 0, 5, 6, 7, 8, 7);
		play(idle);
	}

	@Override
	public void zap(int cell) {
		turnTo(ch.pos, cell);
		play(zap);
		MagicMissile.boltFromChar(parent, MagicMissile.SHADOW, this, cell,
				() -> ((Otiluke) ch).onZapComplete());
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == zap) idle();
		super.onComplete(anim);
	}
}
