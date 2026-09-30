/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.actors.mobs.SpsCaveMobs;
import pd.effects.Lightning;
import pd.scenes.GameScene;
import watabou.noosa.TextureFilm;

public final class SpsCaveSprites {
	private SpsCaveSprites() { }
	private static void setup(MobSprite s, String texture, int w, int h,
			int ir, int rr, int ar, int dr, int[] idle, int[] run, int[] attack, int[] die) {
		s.texture(texture); TextureFilm f = new TextureFilm(s.texture, w, h);
		s.idle = new MobSprite.Animation(ir, true); s.idle.frames(f, idle);
		s.run = new MobSprite.Animation(rr, true); s.run.frames(f, run);
		s.attack = new MobSprite.Animation(ar, false); s.attack.frames(f, attack);
		s.die = new MobSprite.Animation(dr, false); s.die.frames(f, die); s.play(s.idle);
	}
	public static class GnollShaman extends MobSprite {
		public GnollShaman() { setup(this, Assets.Sprites.SPS_GNOLL_SHAMAN, 12, 15, 2, 12, 12, 12, new int[]{0,0,0,1,0,0,1,1}, new int[]{4,5,6,7}, new int[]{2,3,0}, new int[]{8,9,10}); zap = attack.clone(); }
		@Override public void zap(int cell) { if (parent != null && ch != null) parent.add(new Lightning(ch.pos, cell, () -> ((SpsCaveMobs.GnollShaman)ch).onZapComplete())); turnTo(ch.pos, cell); play(zap); }
	}
	public static class SandMob extends MobSprite {
		public SandMob() { setup(this, Assets.Sprites.SPS_SAND_MOB, 16, 17, 8, 8, 14, 6, new int[]{0,1,2,1}, new int[]{0,1,2,1}, new int[]{3,4,5,6,5,4,3}, new int[]{7,8,9}); }
		@Override public void onComplete(Animation anim) { super.onComplete(anim); if (anim == attack && ch != null) GameScene.ripple(ch.pos); }
	}
	public static class IceBug extends MobSprite { public IceBug() { setup(this, Assets.Sprites.SPS_ICE_BUG, 16, 16, 3, 3, 12, 20, new int[]{0,0,0,1,1,1}, new int[]{0,1}, new int[]{0,2,3}, new int[]{0}); } }
	public static class TimeKeeper extends MobSprite { public TimeKeeper() { setup(this, Assets.Sprites.SPS_TIME_KEEPER, 16, 16, 12, 10, 15, 10, new int[]{1,1,1,5,5,5}, new int[]{0,1,2,2,1,0}, new int[]{1,3,4,5}, new int[]{1,6,7,8}); } }
}
