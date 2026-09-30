/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.mobs.npcs.NPC;
import pd.effects.CellEmitter;
import pd.effects.MagicMissile;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import pd.sprites.CatSheepSprite;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;
import watabou.utils.BArray;
import watabou.utils.Bundle;
import watabou.utils.Callback;
import watabou.utils.PathFinder;
import watabou.utils.Random;

/** Black Meow's single-cat obstruction wand from SPS-PD 0.9.8. */
public class WandOfBlackMeow extends Wand {

	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);

	{
		image = ItemSpriteSheet.WAND_FLOCK;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return WHITE;
	}

	@Override
	public void onZap(Ballistica bolt) {
		int spawnCell = findSpawnCell(bolt.collisionPos);
		if (spawnCell >= 0) {
			MagicMeow cat = new MagicMeow();
			cat.initialize(2f + level(), level());
			cat.pos = spawnCell;
			GameScene.add(cat);
			Dungeon.level.occupyCell(cat);
			CellEmitter.get(spawnCell).burst(Speck.factory(Speck.WOOL), 4);
		}
		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.lighthit();
	}

	int findSpawnCell(int target) {
		boolean[] passable = BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null);
		for (Char ch : Actor.chars()) passable[ch.pos] = false;
		PathFinder.buildDistanceMap(target, passable, 1);
		int distance = Actor.findChar(target) == null ? 0 : 1;
		if (distance == 1) PathFinder.distance[target] = Integer.MAX_VALUE;
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (PathFinder.distance[cell] == distance && Dungeon.level.insideMap(cell)
					&& !Dungeon.level.pit[cell] && Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.WOOL,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	public static int retaliationMax(int wandLevel) {
		return Math.max(0, wandLevel * 3);
	}

	public static class MagicMeow extends NPC {

		private static final String LIFESPAN = "lifespan";
		private static final String INITIALIZED = "initialized";
		private static final String WAND_LEVEL = "wand_level";

		private float lifespan;
		private boolean initialized;
		private int wandLevel;

		{
			spriteClass = CatSheepSprite.class;
			properties.add(Property.UNKNOW);
			flying = true;
			alignment = Alignment.ALLY;
		}

		public void initialize(float lifespan, int wandLevel) {
			this.lifespan = lifespan;
			this.wandLevel = wandLevel;
		}

		public float lifespan() {
			return lifespan;
		}

		public int wandLevel() {
			return wandLevel;
		}

		@Override
		protected boolean act() {
			if (initialized) {
				HP = 0;
				destroy();
				if (sprite != null) sprite.die();
			} else {
				initialized = true;
				spend(lifespan + Random.Float(2f));
			}
			return true;
		}

		@Override public void damage(int damage, Object source) { }
		@Override public boolean add(Buff buff) { return false; }
		@Override public boolean interact(Char ch) { return false; }

		@Override
		public int defenseProc(Char enemy, int damage) {
			int maximum = retaliationMax(wandLevel);
			if (maximum > 0) {
				enemy.damage(Random.IntRange(1, maximum),
						pd.actors.damagetype.DamageType.LIGHT_DAMAGE);
			}
			return super.defenseProc(enemy, damage);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LIFESPAN, lifespan);
			bundle.put(INITIALIZED, initialized);
			bundle.put(WAND_LEVEL, wandLevel);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			lifespan = bundle.getFloat(LIFESPAN);
			initialized = bundle.getBoolean(INITIALIZED);
			wandLevel = bundle.getInt(WAND_LEVEL);
		}
	}
}
