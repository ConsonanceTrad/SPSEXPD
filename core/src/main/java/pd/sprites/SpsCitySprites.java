/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.items.weapon.missiles.throwing.EmpBola;
import pd.items.weapon.missiles.throwing.Wave;
import watabou.noosa.TextureFilm;

public final class SpsCitySprites {
	private SpsCitySprites() { }

	private static void setup(MobSprite sprite, String texture, int width, int height,
			int idleRate, int runRate, int attackRate, int dieRate,
			int[] idleFrames, int[] runFrames, int[] attackFrames, int[] dieFrames) {
		sprite.texture(texture);
		TextureFilm film = new TextureFilm(sprite.texture, width, height);
		sprite.idle = new MobSprite.Animation(idleRate, true); sprite.idle.frames(film, idleFrames);
		sprite.run = new MobSprite.Animation(runRate, true); sprite.run.frames(film, runFrames);
		sprite.attack = new MobSprite.Animation(attackRate, false); sprite.attack.frames(film, attackFrames);
		sprite.die = new MobSprite.Animation(dieRate, false); sprite.die.frames(film, dieFrames);
		sprite.play(sprite.idle);
	}

	public static class DragonRider extends MobSprite {
		public DragonRider() { setup(this, Assets.Sprites.SPS_DRAGON_RIDER, 22, 18, 10, 20, 12, 20,
				new int[]{0,0,1,1,2,2,1,1,0}, new int[]{0,0,1,1,2,2,1,1,0},
				new int[]{4,4,5,5,6,6,7,7}, new int[]{0,8,8,9,9,10,10,11}); }
	}

	public static class SpiderBot extends MobSprite {
		public SpiderBot() { setup(this, Assets.Sprites.SPS_SPIDER_BOT, 16, 16, 10, 15, 12, 12,
				new int[]{0,0,0,0,0,1,0,1}, new int[]{0,2,0,3}, new int[]{0,4,5,0}, new int[]{6,7,8,9}); }
		@Override public int blood() { return 0xFFFFFF88; }
	}

	public static class Musketeer extends MobSprite {
		private Animation cast;
		public Musketeer() {
			setup(this, Assets.Sprites.SPS_MUSKETEER, 15, 14, 6, 15, 12, 15,
					new int[]{1,0,1,2}, new int[]{9,10,11,12,13,14}, new int[]{3,4,3}, new int[]{1,5,6,7,8});
			cast = attack.clone();
		}
		@Override public void attack(int cell) {
			if (ch != null && !pd.Dungeon.level.adjacent(ch.pos, cell)) {
				((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos, cell, new EmpBola(), ch::onAttackComplete);
				play(cast);
				turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
	}

	public static class ManySkeleton extends MobSprite {
		public ManySkeleton() { setup(this, Assets.Sprites.SPS_MANY_SKELETON, 16, 16, 15, 20, 12, 20,
				new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, new int[]{0}, new int[]{0,2,3}, new int[]{0}); }
	}

	public static class LevelChecker extends MobSprite {
		public LevelChecker() { setup(this, Assets.Sprites.SPS_LEVEL_CHECKER, 16, 16, 10, 20, 12, 20,
				new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, new int[]{0}, new int[]{0,2,3}, new int[]{0}); }
	}

	public static class GreatMoss extends MobSprite {
		public GreatMoss() { setup(this, Assets.Sprites.SPS_GREAT_MOSS, 12, 15, 2, 15, 12, 5,
				new int[]{0,0,0,0,0,1,1}, new int[]{2,3,4,5,6,7}, new int[]{8,9,10}, new int[]{11,12,13,14,15,15}); }
		@Override public int blood() { return 0xFFCDCDB7; }
	}

	public static class RedWraith extends MobSprite {
		private Animation cast;
		public RedWraith() {
			setup(this, Assets.Sprites.SPS_RED_WRAITH, 14, 15, 5, 10, 15, 8,
					new int[]{0,1}, new int[]{0,1}, new int[]{0,2,3}, new int[]{0,4,5,6,7});
			cast = attack.clone();
		}
		@Override public void attack(int cell) {
			if (ch != null && !pd.Dungeon.level.adjacent(ch.pos, cell)) {
				((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos, cell, new Wave(), ch::onAttackComplete);
				play(cast);
				turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
		@Override public int blood() { return 0x88000000; }
	}
}
