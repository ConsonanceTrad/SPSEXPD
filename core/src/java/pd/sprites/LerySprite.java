package pd.sprites;

import pd.Assets;
import pd.effects.MagicMissile;
import render.noosa.TextureFilm;
import render.utils.data.Callback;

public class LerySprite extends MobSprite {
	public LerySprite() {
		texture(Assets.Sprites.ELEMENTAL);
		TextureFilm frames = new TextureFilm(texture, 12, 14);
		idle = new Animation(10, true); idle.frames(frames, 21, 22, 23);
		run = new Animation(12, true); run.frames(frames, 21, 22, 24);
		attack = new Animation(15, false); attack.frames(frames, 25, 26, 27);
		zap = attack.clone();
		die = new Animation(15, false); die.frames(frames, 28, 29, 30, 31, 32, 33, 34, 33);
		play(idle);
	}

	@Override
	public synchronized void zap(int cell, Callback callback) {
		super.zap(cell);
		MagicMissile.boltFromChar(parent, MagicMissile.FIRE, this, cell, callback);
	}

	@Override public int blood() { return 0xFFFF7D13; }
}
