/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class SpsSokobanSheepSprite extends MobSprite {

	public SpsSokobanSheepSprite() {
		configure(0);
	}

	protected final void configure(int firstFrame) {
		texture(Assets.Sprites.SPS_SOKOBAN_SHEEP);
		TextureFilm frames = new TextureFilm(texture, 16, 15);
		idle = new Animation(15, true);
		idle.frames(frames,
				firstFrame, firstFrame, firstFrame, firstFrame,
				firstFrame, firstFrame, firstFrame, firstFrame,
				firstFrame, firstFrame, firstFrame, firstFrame,
				firstFrame + 1, firstFrame + 2, firstFrame + 3, firstFrame);
		run = idle.clone();
		attack = idle.clone();
		die = new Animation(20, false);
		die.frames(frames, firstFrame);
		play(idle);
		curFrame = Random.Int(curAnim.frames.length);
	}

	public static class Corner extends SpsSokobanSheepSprite {
		public Corner() { configure(4); }
	}

	public static class Switch extends SpsSokobanSheepSprite {
		public Switch() { configure(8); }
	}

	public static class Black extends SpsSokobanSheepSprite {
		public Black() { configure(12); }
	}
}
