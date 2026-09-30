/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.AdamantRing;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.misc.GnollMark;
import pd.items.quest.GnollClothes;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.levels.FieldBossLevel;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.GnollKeeperSprite;
import pd.sprites.GnollKingSprite;
import pd.ui.BossHealthBar;
import render.utils.Bundle;
import render.utils.Random;

public class GnollKing extends Mob {
	private int breaks;

	{
		spriteClass = GnollKingSprite.class;
		HP = HT = 1000;
		defenseSkill = 0;
		EXP = 30;
		baseSpeed = 0.75f;
		loot = new GnollClothes();
		lootChance = 1f;
		properties.add(Property.ORC);
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		immunities.add(EnchantmentDark.class);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Burning.class);
		immunities.add(ToxicGas.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(Vertigo.class);
		immunities.add(Paralysis.class);
		immunities.add(Bleeding.class);
		immunities.add(CorruptGas.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(15, 25); }
	@Override public int attackSkill(Char target) { return 100; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 5); }
	@Override public float speed() { return breaks == 1 ? 4f * super.speed() : super.speed(); }
	@Override public GnollMark SupercreateLoot() { return new GnollMark(); }

	@Override
	protected boolean act() {
		if (3 - breaks > 4 * HP / HT) {
			breaks++;
			yell(Messages.get(this, "angry"));
			return true;
		}
		if (breaks == 1) state = FLEEING;
		else if (breaks >= 2) state = HUNTING;
		if (breaks > 0 && keeperCount() < 3 && Dungeon.level instanceof FieldBossLevel) {
			int cell = ((FieldBossLevel) Dungeon.level).safeSpawnCell(pos);
			GnollKeeper.spawnAroundChance(cell);
		}
		return super.act();
	}

	private int keeperCount() {
		int count = 0;
		for (Mob mob : Dungeon.level.mobs) if (mob instanceof GnollKeeper) count++;
		return count;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (enemy == Dungeon.hero && Random.Int(2) == 0) {
			if (breaks == 0) Buff.prolong(enemy, Cripple.class, 3f);
			else if (breaks == 2) Buff.prolong(enemy, Roots.class, 3f);
			else if (breaks >= 3) Buff.prolong(enemy, STRDown.class, 3f);
		}
		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (breaks >= 3 && enemy != null) {
			int reflected = Random.IntRange(0, 20);
			if (reflected > 0 || Random.Int(3) == 0) enemy.damage(reflected, this);
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int damage, Object source) {
		damage = Math.min(damage, 20);
		if (breaks == 2 && damage > 15) GameScene.add(Blob.seed(pos, 30, CorruptGas.class));
		super.damage(damage, source);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level.distance(pos, enemy.pos) <= (breaks == 2 ? 2 : 1);
	}

	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Heap ring = Dungeon.level.drop(new AdamantRing(), pos);
		if (ring != null && ring.sprite != null) ring.sprite.drop();
		Heap gold = Dungeon.level.drop(new Gold(Random.Int(1000, 1500)), pos);
		if (gold != null && gold.sprite != null) gold.sprite.drop();
		if (Dungeon.level instanceof FieldBossLevel) ((FieldBossLevel) Dungeon.level).kingDefeated();
		else Dungeon.gnollKingKilled = true;
		yell(Messages.get(this, "die"));
	}

	private static final String BREAKS = "breaks";
	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BREAKS, breaks);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		breaks = bundle.getInt(BREAKS);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static class GnollKeeper extends Mob {
		private static final float SPAWN_DELAY = 10f;
		{
			spriteClass = GnollKeeperSprite.class;
			HP = HT = 5;
			defenseSkill = 10;
			EXP = 0;
			state = WANDERING;
			properties.add(Property.BOSS_MINION);
		}
		@Override public int attackSkill(Char target) { return 36; }
		@Override public int damageRoll() { return 0; }
		@Override public int drRoll() { return 1; }
		@Override public int attackProc(Char enemy, int damage) {
			yell(Messages.get(this, "safe"));
			return super.attackProc(enemy, damage);
		}
		public static void spawnAroundChance(int pos) {
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null && Random.Float() < 0.75f) spawnAt(cell);
			}
		}
		private static void spawnAt(int pos) {
			GnollKeeper keeper = new GnollKeeper();
			keeper.pos = pos;
			keeper.state = keeper.HUNTING;
			GameScene.add(keeper, SPAWN_DELAY);
		}
	}
}
