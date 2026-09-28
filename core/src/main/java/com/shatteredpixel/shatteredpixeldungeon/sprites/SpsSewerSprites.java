package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ShitBall;
import com.watabou.noosa.MovieClip.Animation;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public final class SpsSewerSprites {
	private SpsSewerSprites() { }
	private static void setup(MobSprite s, String texture, int w, int h,
			int idleRate, int runRate, int attackRate, int dieRate,
			int[] idle, int[] run, int[] attack, int[] die) {
		s.texture(texture);
		TextureFilm f = new TextureFilm(s.texture, w, h);
		s.idle = new Animation(idleRate, true); s.idle.frames(f, idle);
		s.run = new Animation(runRate, true); s.run.frames(f, run);
		s.attack = new Animation(attackRate, false); s.attack.frames(f, attack);
		s.die = new Animation(dieRate, false); s.die.frames(f, die);
		s.play(s.idle);
	}
	public static class BrownBat extends MobSprite { public BrownBat() { setup(this, Assets.Sprites.SPS_SEWER_BAT, 15, 15, 8, 12, 12, 12, new int[]{16,17}, new int[]{16,17}, new int[]{18,19,16,17}, new int[]{20,21,22}); } }
	public static class DustElement extends MobSprite {
		public DustElement() { setup(this, Assets.Sprites.SPS_SEWER_ELEMENTAL, 12, 14, 10, 12, 15, 15, new int[]{42,43,44}, new int[]{42,43,45}, new int[]{46,47,48}, new int[]{49,50,51,52,53,54,55,54}); zap = attack.clone(); }
		@Override public int blood() { return 0x999999; }
	}
	public static class RatBoss extends MobSprite { public RatBoss() { setup(this, Assets.Sprites.SPS_SEWER_RAT, 16, 15, 2, 10, 15, 10, new int[]{80,80,80,81}, new int[]{86,87,88,89,90}, new int[]{82,83,84,85,80}, new int[]{91,92,93,94}); } }
	public static class Shit extends MobSprite {
		private final Animation cast;
		public Shit() { setup(this, Assets.Sprites.SPS_SHIT, 16, 16, 10, 20, 12, 20, new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, new int[]{0}, new int[]{0,2,3}, new int[]{0}); cast = attack.clone(); }
		@Override public void attack(int cell) {
			if (ch != null && Dungeon.level != null && !Dungeon.level.adjacent(cell, ch.pos)) {
				((MissileSprite) parent.recycle(MissileSprite.class)).reset(ch.pos, cell, new ShitBall(), new Callback() {
					@Override public void call() { ch.onAttackComplete(); }
				});
				play(cast);
				turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
	}
	public static class LiveMoss extends MobSprite { public LiveMoss() { setup(this, Assets.Sprites.SPS_LIVE_MOSS, 16, 15, 2, 10, 15, 10, new int[]{0,0,0,1}, new int[]{0,1,1,1,0,0}, new int[]{1,2,2,1,1}, new int[]{2,2,3,3}); } }
	public static class PatrolUAV extends MobSprite { public PatrolUAV() { setup(this, Assets.Sprites.SPS_PATROL_UAV, 16, 16, 4, 12, 12, 12, new int[]{0,1,2,3,4}, new int[]{0,5,6,7}, new int[]{0,8,9,8}, new int[]{0,10,11,11}); } }
	public static class Vagrant extends MobSprite { public Vagrant() { setup(this, Assets.Sprites.SPS_VAGRANT, 12, 16, 2, 15, 12, 8, new int[]{0,0,0,1,0,0,1,1}, new int[]{2,3,4,5,6,7}, new int[]{8,9,10}, new int[]{11,12,13,14}); } }
	public static class ExVagrant extends MobSprite { public ExVagrant() { setup(this, Assets.Sprites.SPS_VAGRANT, 12, 16, 2, 15, 12, 8, new int[]{15,15,15,16,15,15,16,16}, new int[]{17,18,19,20,21,22}, new int[]{23,24,25}, new int[]{26,27,28,29}); } }
}
