package pd.sprites;

import pd.Assets;
import pd.effects.Splash;
import render.noosa.TextureFilm;

public final class SpsYogSprites {
	private SpsYogSprites() { }

	public static class Yog extends MobSprite {
		public Yog() {
			texture(Assets.Sprites.SPS_YOG);
			TextureFilm f = new TextureFilm(texture, 20, 19);
			idle = new Animation(10, true); idle.frames(f, 0, 1, 2, 2, 1, 0, 3, 4, 4, 3, 0, 5, 6, 6, 5);
			run = new Animation(12, true); run.frames(f, 0);
			attack = new Animation(12, false); attack.frames(f, 0);
			die = new Animation(10, false); die.frames(f, 0, 7, 8, 9);
			play(idle);
		}
		@Override public void die() { super.die(); Splash.at(center(), blood(), 12); }
	}

	private static abstract class Fist extends MobSprite {
		void setup(String texturePath, boolean ranged) {
			texture(texturePath);
			TextureFilm f = new TextureFilm(texture, 24, 17);
			idle = new Animation(2, true); idle.frames(f, 0, 0, 1);
			run = new Animation(3, true); run.frames(f, 0, 1);
			attack = new Animation(ranged ? 15 : 10, false); attack.frames(f, ranged ? new int[]{0, 5, 6} : new int[]{0});
			die = new Animation(10, false); die.frames(f, 0, 2, 3, 4);
			play(idle);
		}
	}
	public static class Burning extends Fist { public Burning() { setup(Assets.Sprites.SPS_BURNING_FIST, true); } }
	public static class Rotting extends Fist { public Rotting() { setup(Assets.Sprites.SPS_ROTTING_FIST, false); } }
	public static class Infecting extends Fist { public Infecting() { setup(Assets.Sprites.SPS_INFECTING_FIST, false); } }
	public static class Pinning extends Fist { public Pinning() { setup(Assets.Sprites.SPS_PINNING_FIST, true); } }

	public static class Larva extends MobSprite {
		public Larva() {
			texture(Assets.Sprites.SPS_YOG_LARVA);
			TextureFilm f = new TextureFilm(texture, 12, 8);
			idle = new Animation(5, true); idle.frames(f, 4, 4, 4, 4, 4, 5, 5);
			run = new Animation(12, true); run.frames(f, 0, 1, 2, 3);
			attack = new Animation(15, false); attack.frames(f, 6, 5, 7);
			die = new Animation(10, false); die.frames(f, 8);
			play(idle);
		}
		@Override public int blood() { return 0xbbcc66; }
	}
}
