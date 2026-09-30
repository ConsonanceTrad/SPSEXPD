/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.actors.mobs.Fiend;
import pd.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;

public class FiendSprite extends MobSprite {

	public FiendSprite() {
		texture(Assets.Sprites.SPS_FIEND);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(5, true);
		idle.frames(frames, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 4, 10, 11, 10, 4, 1, 1);
		run = new Animation(5, true);
		run.frames(frames, 0, 1, 2, 3);
		attack = new Animation(15, false);
		attack.frames(frames, 10, 11, 12, 13, 14, 15);
		zap = attack.clone();
		die = new Animation(10, false);
		die.frames(frames, 16, 17, 18, 19, 19, 19);
		play(idle);
	}

	@Override
	public void zap(int cell) {
		super.zap(cell);
		MagicMissile.boltFromChar(parent, MagicMissile.SHADOW, this, cell,
				() -> ((Fiend)ch).onZapComplete());
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == zap) idle();
		super.onComplete(anim);
	}
}
