/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

/** Alternate source frames used only by SPS exit-room guards. */
public final class SpsExitSprites {
	private SpsExitSprites() { }

	public static class ExBamboo extends MobSprite {
		public ExBamboo() {
			texture(Assets.Sprites.SPS_BAMBOO);
			TextureFilm frames = new TextureFilm(texture, 16, 16);
			idle = new Animation(15, true); idle.frames(frames, 4, 4, 4, 4, 4, 4, 4, 5, 6, 6, 6, 7);
			run = new Animation(24, true); run.frames(frames, 4);
			attack = new Animation(12, false); attack.frames(frames, 4, 6, 7);
			die = new Animation(24, false); die.frames(frames, 4);
			play(idle);
		}
	}

	public static class BombBug extends MobSprite {
		public BombBug() {
			texture(Assets.Sprites.SPS_ICE_BUG);
			TextureFilm frames = new TextureFilm(texture, 16, 16);
			idle = new Animation(3, true); idle.frames(frames, 4, 4, 4, 5, 5, 5);
			run = new Animation(3, true); run.frames(frames, 4, 5);
			attack = new Animation(12, false); attack.frames(frames, 4, 6, 7);
			die = new Animation(20, false); die.frames(frames, 4);
			play(idle);
		}
	}
}
