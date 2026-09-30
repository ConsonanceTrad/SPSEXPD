package pd.sprites;

import pd.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;

public class BatteryTombSprite extends MobSprite {
	public BatteryTombSprite() {
		texture(Assets.Sprites.SPS_BATTERY_TOMB);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(15, true); idle.frames(frames, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3);
		run = new Animation(20, true); run.frames(frames, 0, 1, 2, 3);
		attack = new Animation(12, false); attack.frames(frames, 0, 2, 3);
		die = new Animation(20, false); die.frames(frames, 0);
		play(idle);
	}
	@Override public void update() {
		super.update();
		if (flashTime <= 0) {
			float interval = (Game.timeTotal % 9) / 3f;
			tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
					interval > 1 ? Math.max(0, 2 - interval) : interval,
					interval > 2 ? Math.max(0, 3 - interval) : interval - 1, 0.5f);
		}
	}
}
