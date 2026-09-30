/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.effects.particles.ElmoParticle;
import pd.items.weapon.missiles.arrows.GlassFruit;
import watabou.noosa.TextureFilm;

public final class SpsHallsSprites {
	private SpsHallsSprites() { }
	private static void setup(MobSprite sprite, String texture, int width, int height,
			int idleRate, int runRate, int attackRate, int dieRate,
			int[] idleFrames, int[] runFrames, int[] attackFrames, int[] dieFrames) {
		sprite.texture(texture); TextureFilm film = new TextureFilm(sprite.texture, width, height);
		sprite.idle = new MobSprite.Animation(idleRate, true); sprite.idle.frames(film, idleFrames);
		sprite.run = new MobSprite.Animation(runRate, true); sprite.run.frames(film, runFrames);
		sprite.attack = new MobSprite.Animation(attackRate, false); sprite.attack.frames(film, attackFrames);
		sprite.die = new MobSprite.Animation(dieRate, false); sprite.die.frames(film, dieFrames);
		sprite.play(sprite.idle);
	}

	public static class DemonGoo extends MobSprite {
		public DemonGoo() { setup(this, Assets.Sprites.SPS_DEMON_GOO, 20, 14, 10, 15, 10, 10,
				new int[]{2,1,0,0,1}, new int[]{3,2,1,2}, new int[]{8,9,10}, new int[]{5,6,7}); }
		@Override public int blood() { return 0xFF000000; }
	}
	public static class ThiefImp extends MobSprite {
		public ThiefImp() { setup(this, Assets.Sprites.SPS_THIEF_IMP, 16, 16, 5, 10, 15, 10,
				new int[]{0,1,2,3,0,1,2,3,0,1,2,3,4,5,6,7}, new int[]{0,1,2,3},
				new int[]{8,9,10,11,12}, new int[]{16,17,18,19,20,21});
			zap = new Animation(10, false); zap.frames(new TextureFilm(texture, 16, 16), 13,14,15,0);
		}
	}
	public static class DemonFlower extends MobSprite {
		public DemonFlower() { setup(this, Assets.Sprites.SPS_DEMON_FLOWER, 16, 16, 8, 12, 15, 8,
				new int[]{0,1,2}, new int[]{5,6}, new int[]{4,3}, new int[]{7,8,9}); }
	}
	public static class Sufferer extends MobSprite {
		public Sufferer() { setup(this, Assets.Sprites.SPS_SUFFERER, 12, 14, 8, 12, 12, 12,
				new int[]{0,0,0,1,0,0,1,1}, new int[]{2,3,4,5,6,7}, new int[]{8,9,10}, new int[]{11,12,13,14}); }
		@Override public void onComplete(Animation animation) { if (animation == die) emitter().burst(ElmoParticle.FACTORY, 4); super.onComplete(animation); }
	}
	public static class DemonRabbit extends MobSprite {
		private Animation cast;
		public DemonRabbit() { setup(this, Assets.Sprites.SPS_DEMON_RABBIT, 12, 15, 6, 15, 12, 15,
				new int[]{1,0,1,0}, new int[]{2,3,2,3}, new int[]{4,5}, new int[]{1,6,7}); cast = attack.clone(); }
		@Override public void attack(int cell) {
			if (ch != null && !Dungeon.level.adjacent(ch.pos, cell)) {
				((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos, cell, new GlassFruit(), ch::onAttackComplete);
				play(cast); turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
	}
}
