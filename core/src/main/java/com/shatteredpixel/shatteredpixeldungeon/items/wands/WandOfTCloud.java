/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.ElectriShock;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KeKeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TCloudSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD's all-charge thundercloud wand. */
public class WandOfTCloud extends Wand {

	public static final int SUMMON_CHARGE = 10;
	public static final int TCLOUD_LIFETIME = 20;
	public static final int STCLOUD_LIFETIME = 40;

	{
		image = ItemSpriteSheet.WAND_TCLOUD;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public void onZap(Ballistica bolt) {
		if (canSummon(curCharges)) {
			int spawnCell = findSpawnCell(bolt.collisionPos);
			if (spawnCell >= 0) {
				ThunderCloud cloud;
				if (Dungeon.hero.subClass == HeroSubClass.LEADER) {
					cloud = new STCloud();
				} else {
					cloud = new TCloud();
				}
				cloud.pos = spawnCell;
				cloud.wandLevel = level();
				GameScene.add(cloud);
				Dungeon.level.occupyCell(cloud);
				CellEmitter.get(spawnCell).burst(Speck.factory(Speck.WOOL), 4);
			}
		} else {
			GLog.w(Messages.get(this, "more_charge"));
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = bolt.collisionPos + offset;
				if (Dungeon.level.insideMap(cell)
						&& Dungeon.level.distance(bolt.collisionPos, cell) <= 1) {
					GameScene.add(Blob.seed(cell, curCharges, ElectriShock.class));
					CellEmitter.get(cell).burst(EnergyParticle.FACTORY, 5);
				}
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.shockhit();
	}

	private int findSpawnCell(int target) {
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

	public static boolean canSummon(int charges) {
		return charges >= SUMMON_CHARGE;
	}

	@Override public int initialCharges() { return 1; }
	@Override protected int chargesPerCast() { return Math.max(1, curCharges); }

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.WOOL,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	public abstract static class ThunderCloud extends NPC implements Callback {

		private static final float TIME_TO_ZAP = 1f;
		private static final String WAND_LEVEL = "wand_level";
		private static final String REMAINING_LIFE = "remaining_life";

		protected int wandLevel;
		protected int remainingLife;

		{
			state = HUNTING;
			flying = true;
			alignment = Alignment.ALLY;
			viewDistance = 6;
			properties.add(Property.ELECTRIC);
		}

		protected abstract int rangedMin();
		protected abstract int rangedMax();

		@Override
		protected boolean act() {
			if (--remainingLife <= 0) {
				HP = 0;
				destroy();
				if (sprite != null) sprite.die();
				return true;
			}
			return super.act();
		}

		@Override
		protected Char chooseEnemy() {
			if (enemy == null || !enemy.isAlive() || enemy.alignment != Alignment.ENEMY) {
				ArrayList<Mob> enemies = new ArrayList<>();
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.alignment == Alignment.ENEMY && mob.isAlive()
							&& Dungeon.level.heroFOV[mob.pos]) enemies.add(mob);
				}
				enemy = enemies.isEmpty() ? null : Random.element(enemies);
			}
			return enemy;
		}

		@Override public int attackSkill(Char target) { return 500; }
		@Override public float attackDelay() { return 0.5f; }
		@Override public int drRoll() { return 0; }

		@Override
		protected boolean canAttack(Char enemy) {
			return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}

		@Override
		protected boolean doAttack(Char enemy) {
			if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy);

			spend(TIME_TO_ZAP);
			if (hit(this, enemy, true)) {
				int damage = Random.Int(rangedMin(), rangedMax());
				if (Dungeon.level.water[enemy.pos] && !enemy.flying) {
					damage = (int)(damage * 1.5f);
				}
				enemy.damage(damage, this);
				if (enemy.sprite != null && enemy.sprite.visible) {
					enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
					enemy.sprite.flash();
				}
				damage(damageRoll(), this);
			} else if (enemy.sprite != null) {
				enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			}

			if (isAlive() && sprite != null
					&& (sprite.visible || (enemy.sprite != null && enemy.sprite.visible))) {
				sprite.zap(enemy.pos);
				return false;
			}
			return true;
		}

		@Override public void call() { next(); }
		@Override public boolean add(Buff buff) { return false; }

		public int remainingLife() { return remainingLife; }
		public int wandLevel() { return wandLevel; }

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WAND_LEVEL, wandLevel);
			bundle.put(REMAINING_LIFE, remainingLife);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			wandLevel = bundle.getInt(WAND_LEVEL);
			if (bundle.contains(REMAINING_LIFE)) remainingLife = bundle.getInt(REMAINING_LIFE);
		}
	}

	public static class TCloud extends ThunderCloud {
		{
			spriteClass = TCloudSprite.class;
			HP = HT = 200;
			remainingLife = TCLOUD_LIFETIME;
		}

		@Override public int damageRoll() {
			return Random.NormalIntRange(10 + wandLevel, 15 + 3 * wandLevel);
		}
		@Override protected int rangedMin() { return 6 + wandLevel; }
		@Override protected int rangedMax() { return 20 + 3 * wandLevel; }
		@Override protected boolean getCloser(int target) { return false; }
	}

	public static class STCloud extends ThunderCloud {
		{
			spriteClass = KeKeSprite.class;
			HP = HT = 100;
			remainingLife = STCLOUD_LIFETIME;
		}

		@Override public int damageRoll() {
			return Random.NormalIntRange(20 + wandLevel, 30 + 5 * wandLevel);
		}
		@Override protected int rangedMin() { return 4 + wandLevel; }
		@Override protected int rangedMax() { return 12 + 3 * wandLevel; }
	}
}
