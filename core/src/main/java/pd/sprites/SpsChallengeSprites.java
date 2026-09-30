/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.effects.Beam;
import pd.effects.MagicMissile;
import pd.effects.Speck;
import pd.items.StoneOre;
import pd.items.weapon.missiles.Bolas;
import pd.tiles.DungeonTilemap;
import watabou.noosa.TextureFilm;
import watabou.noosa.audio.Sample;

/** Original SPS-PD challenge-arena sprite sheets and animation cuts. */
public final class SpsChallengeSprites {
	private SpsChallengeSprites() {
	}

	private static void setup(MobSprite sprite, String texture, int width, int height,
			int idleRate, int runRate, int attackRate, int dieRate,
			int[] idleFrames, int[] runFrames, int[] attackFrames, int[] dieFrames) {
		sprite.texture(texture);
		TextureFilm film = new TextureFilm(sprite.texture, width, height);
		sprite.idle = new MobSprite.Animation(idleRate, true);
		sprite.idle.frames(film, idleFrames);
		sprite.run = new MobSprite.Animation(runRate, true);
		sprite.run.frames(film, runFrames);
		sprite.attack = new MobSprite.Animation(attackRate, false);
		sprite.attack.frames(film, attackFrames);
		sprite.die = new MobSprite.Animation(dieRate, false);
		sprite.die.frames(film, dieFrames);
		sprite.play(sprite.idle);
	}

	public static class ForestProtector extends MobSprite {
		public ForestProtector() {
			setup(this, Assets.Sprites.SPS_FOREST_PROTECTOR, 16, 16, 15, 10, 20, 20,
					new int[]{0, 0, 4, 4}, new int[]{0, 1, 2, 3},
					new int[]{4, 4, 4, 0}, new int[]{0, 0, 0});
			zap = attack.clone();
		}
		@Override public void zap(int cell) {
			turnTo(ch.pos, cell);
			play(zap);
			MagicMissile.boltFromChar(parent, MagicMissile.EARTH, this, cell,
					() -> ((pd.actors.mobs.ForestProtector)ch)
							.onZapComplete());
			Sample.INSTANCE.play(Assets.Sounds.ZAP);
		}
		@Override public void onComplete(Animation animation) {
			if (animation == zap) idle();
			super.onComplete(animation);
		}
		@Override public int blood() { return 0xFFCDCDB7; }
	}

	public static class MossySkeleton extends MobSprite {
		public MossySkeleton() {
			setup(this, Assets.Sprites.SPS_MOSSY_SKELETON, 12, 15, 12, 15, 15, 12,
					new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,3},
					new int[]{4,5,6,7,8,9}, new int[]{14,15,16}, new int[]{10,11,12,13});
		}
		@Override public void die() {
			super.die();
			if (ch != null && Dungeon.level != null && Dungeon.level.heroFOV[ch.pos]) {
				emitter().burst(Speck.factory(Speck.BONE), 6);
			}
		}
		@Override public int blood() { return 0xFFCCCCCC; }
	}

	public static class GraveProtector extends MobSprite {
		private final Animation cast;
		public GraveProtector() {
			setup(this, Assets.Sprites.SPS_GRAVE_PROTECTOR, 12, 16, 2, 15, 12, 8,
					new int[]{0,0,0,1,0,0,1,1}, new int[]{2,3,4,5,6,7},
					new int[]{8,9,10}, new int[]{11,12,13,14});
			cast = attack.clone();
		}
		@Override public void attack(int cell) {
			if (ch != null && !Dungeon.level.adjacent(ch.pos, cell)) {
				((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos, cell,
						new StoneOre(), ch::onAttackComplete);
				play(cast);
				turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
		@Override public int blood() { return 0xFFCDCDB7; }
	}

	public static class AlbinoPiranha extends MobSprite {
		public AlbinoPiranha() {
			setup(this, Assets.Sprites.SPS_ALBINO_PIRANHA, 12, 16, 8, 20, 20, 4,
					new int[]{0,1,2,1}, new int[]{0,1,2,1},
					new int[]{3,4,5,6,7,8,9,10,11}, new int[]{12,13,14});
		}
		@Override public void onComplete(Animation animation) {
			super.onComplete(animation);
			if (animation == attack && ch != null) pd.scenes.GameScene.ripple(ch.pos);
		}
	}

	public static class FishProtector extends MobSprite {
		public FishProtector() {
			setup(this, Assets.Sprites.SPS_FISH_PROTECTOR, 12, 15, 2, 15, 12, 5,
					new int[]{0,0,0,0,0,1,1}, new int[]{2,3,4,5,6,7},
					new int[]{8,9,10}, new int[]{11,12,13,14,15,15});
		}
		@Override public int blood() { return 0xFFCDCDB7; }
	}

	public static class GoldThief extends MobSprite {
		public GoldThief() {
			setup(this, Assets.Sprites.SPS_GOLD_THIEF, 12, 13, 1, 15, 12, 10,
					new int[]{0,0,0,1,0,0,0,0,1}, new int[]{0,0,2,3,3,4},
					new int[]{10,11,12,0}, new int[]{5,6,7,8,9});
		}
	}

	public static class VaultProtector extends MobSprite {
		private final Animation cast;
		public VaultProtector() {
			setup(this, Assets.Sprites.SPS_VAULT_PROTECTOR, 16, 16, 15, 20, 12, 20,
					new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, new int[]{0},
					new int[]{0,2,3}, new int[]{0});
			cast = attack.clone();
		}
		@Override public void attack(int cell) {
			if (ch != null && !Dungeon.level.adjacent(ch.pos, cell)) {
				((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos, cell,
						new Bolas(), ch::onAttackComplete);
				play(cast);
				turnTo(ch.pos, cell);
			} else super.attack(cell);
		}
		@Override public int blood() { return 0xFFCDCDB7; }
	}

	public static class IceBall extends MobSprite {
		private int attackPos;
		public IceBall() {
			setup(this, Assets.Sprites.MRDESTRUCTO, 16, 16, 2, 12, 15, 15,
					new int[]{15,16,17,18}, new int[]{16,17,18},
					new int[]{15,19}, new int[]{15,14,20});
		}
		@Override public void attack(int cell) {
			attackPos = cell;
			super.attack(cell);
		}
		@Override public void onComplete(Animation animation) {
			super.onComplete(animation);
			if (animation == attack && ch != null && parent != null
					&& Dungeon.level != null && (Dungeon.level.heroFOV[ch.pos]
					|| Dungeon.level.heroFOV[attackPos])) {
				parent.add(new Beam.DeathRay(center(), DungeonTilemap.tileCenterToWorld(attackPos)));
			}
		}
	}
}
