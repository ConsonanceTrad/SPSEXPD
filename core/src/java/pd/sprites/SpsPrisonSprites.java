/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.effects.MagicMissile;
import pd.items.weapon.missiles.Shuriken;
import render.noosa.TextureFilm;

public final class SpsPrisonSprites {
	private SpsPrisonSprites() { }
	private static void setup(MobSprite s, String texture, int w, int h,
			int ir, int rr, int ar, int dr, int[] idle, int[] run, int[] attack, int[] die) {
		s.texture(texture);
		TextureFilm f = new TextureFilm(s.texture, w, h);
		s.idle = new MobSprite.Animation(ir, true); s.idle.frames(f, idle);
		s.run = new MobSprite.Animation(rr, true); s.run.frames(f, run);
		s.attack = new MobSprite.Animation(ar, false); s.attack.frames(f, attack);
		s.die = new MobSprite.Animation(dr, false); s.die.frames(f, die);
		s.play(s.idle);
	}

	public static class GhostPhoto extends MobSprite { public GhostPhoto() { setup(this, Assets.Sprites.SPS_LIVE_PHOTO, 16, 16, 4, 20, 12, 20, new int[]{0,1,2,1,0}, new int[]{0,0,0,1,1,1,2,2,2,1,1,1,0,0,0}, new int[]{3,4,5,6,7,6,7}, new int[]{0,5,0,5,0,5}); } }
	public static class Assassin extends MobSprite {
		private final Animation cast;
		public Assassin() { setup(this, Assets.Sprites.SPS_ASSASSIN, 14, 16, 2, 15, 15, 8, new int[]{0,0,0,1}, new int[]{2,3,4,5,0}, new int[]{6,7,7,0}, new int[]{8,9,10,10}); run.looped = false; cast = attack.clone(); play(run.clone()); }
		@Override public void attack(int cell) {
			if (ch != null && Dungeon.level != null && !Dungeon.level.adjacent(cell, ch.pos)) {
				((MissileSprite) parent.recycle(MissileSprite.class)).reset(this, cell, new Shuriken(), ch::onAttackComplete);
				play(cast); turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
		@Override public void onComplete(Animation anim) { if (anim == run) { isMoving = false; idle(); } else super.onComplete(anim); }
	}
	public static class TrollWarrior extends MobSprite { public TrollWarrior() { setup(this, Assets.Sprites.SPS_TROLL_WARRIOR, 16, 16, 2, 12, 12, 12, new int[]{0,0,0,1}, new int[]{0,2,3,4,5,6,7}, new int[]{0,8,9,10,11}, new int[]{0,11,12}); } }
	public static class FireRabbit extends MobSprite {
		private int target;
		public FireRabbit() { setup(this, Assets.Sprites.SPS_PRISON_FIRE_RABBIT, 16, 16, 3, 20, 20, 20, new int[]{0,0,0,1,1,1}, new int[]{0,2,3,4,5,6,7}, new int[]{0,8,9,9,9,10}, new int[]{0,11,11,12,12,13}); zap = attack.clone(); }
		@Override public void attack(int cell) { target = cell; super.attack(cell); }
		@Override public void onComplete(Animation anim) {
			if (anim == attack && ch != null && parent != null) {
				MagicMissile.boltFromChar(parent, MagicMissile.FIRE, this, target, ch::onAttackComplete);
				idle();
			} else super.onComplete(anim);
		}
	}
	public static class Bamboo extends MobSprite { public Bamboo() { setup(this, Assets.Sprites.SPS_BAMBOO, 16, 16, 15, 20, 12, 20, new int[]{0,0,0,0,0,0,0,1,2,2,2,3}, new int[]{0}, new int[]{0,2,3}, new int[]{0}); } }
	public static class GoldCollector extends MobSprite { public GoldCollector() { setup(this, Assets.Sprites.SPS_GOLD_COLLECTOR, 12, 16, 10, 20, 15, 20, new int[]{0,0,0,1,1,1}, new int[]{0,4,5,6,7,0}, new int[]{0,2,3}, new int[]{0,7,8}); } }
	public static class Zombie extends MobSprite { public Zombie() { setup(this, Assets.Sprites.SPS_ZOMBIE, 12, 16, 5, 10, 15, 8, new int[]{0,1}, new int[]{2,3,4,5,6}, new int[]{7,8,9,8}, new int[]{1,10,11,12}); } @Override public int blood() { return 0x99FF99; } }
	public static class BanditKing extends MobSprite {
		public BanditKing() { setup(this, Assets.Sprites.SPS_BANDIT_KING, 12, 13, 1, 15, 12, 10, new int[]{0,0,0,1,0,0,0,0,1}, new int[]{0,0,2,3,3,4}, new int[]{10,11,12,0}, new int[]{1,1,5,5,5}); }
		@Override public void link(pd.actors.Char ch) { super.link(ch); add(State.LEVITATING); }
		@Override public void die() { super.die(); remove(State.LEVITATING); }
	}
}
