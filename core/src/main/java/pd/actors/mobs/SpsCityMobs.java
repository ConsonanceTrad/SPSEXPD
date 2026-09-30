/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.TarGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.BugSlow;
import pd.actors.buffs.LightShootAttack;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Terror;
import pd.effects.Pushing;
import pd.effects.particles.EnergyParticle;
import pd.effects.particles.ShadowParticle;
import pd.items.ExpOre;
import pd.items.Generator;
import pd.items.Item;
import pd.items.LevelDown;
import pd.items.RedDewdrop;
import pd.items.StoneOre;
import pd.items.bombs.DungeonBomb;
import pd.items.food.BugMeat;
import pd.items.food.meatfood.Meat;
import pd.items.medicine.BlueMilk;
import pd.items.medicine.DeathCap;
import pd.items.medicine.Earthstar;
import pd.items.medicine.GoldenJelly;
import pd.items.medicine.GreenSpore;
import pd.items.medicine.JackOLantern;
import pd.items.medicine.PixieParasol;
import pd.items.potions.PotionOfHealing;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.wands.Wand;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.SkeletonSprite;
import pd.sprites.SpsCitySprites;
import pd.plants.Sungrass;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;
import render.utils.Reflection;

import java.util.ArrayList;

public final class SpsCityMobs {
	private SpsCityMobs() { }

	private abstract static class DualLootMob extends LegacyDualLootMob {
		protected final void setupDualLoot(Object first, float firstChance, Object second, float secondChance) {
			setupLegacyDualLoot(first, firstChance, second, secondChance);
		}
	}

	public static class DragonRider extends Mob {
		{
			spriteClass = SpsCitySprites.DragonRider.class;
			baseSpeed = 1.5f;
			HP = HT = 140 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7);
			defenseSkill = 30 + legacyDepthAdjustment(1);
			EXP = 14;
			maxLvl = 30;
			flying = true;
			properties.add(Property.DRAGON);
			resistances.add(ToxicGas.class);
			resistances.add(pd.actors.buffs.Poison.class);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(40, 50); }
		@Override public int attackSkill(Char target) { return 36 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(20, 40); }
		@Override public void die(Object cause) {
			Mob rider = randomRider();
			if (rider != null && Dungeon.level.insideMap(pos) && Actor.findChar(pos) == this) {
				rider.pos = pos;
				rider.state = rider.HUNTING;
				GameScene.add(rider, 1f);
			}
			super.die(cause);
		}
	}

	@SuppressWarnings("unchecked")
	static Class<? extends Mob>[] riderPool() {
		return new Class[]{
				Rat.class, BrownBat.class, DustElement.class, LiveMoss.class,
				Swarm.class, Crab.class, PatrolUAV.class,
				Thief.class, Gnoll.class, TimeKeeper.class,
				Guard.class, Assassin.class, TrollWarrior.class, Zombie.class,
				Bat.class, Skeleton.class, Brute.class, GnollShaman.class, Spinner.class,
				BrokenRobot.class, SandMob.class,
				FireElemental.class, Warlock.class, Monk.class, Golem.class,
				pd.actors.mobs.SpiderBot.class,
				pd.actors.mobs.Musketeer.class,
				DwarfLich.class, BambooMob.class,
				pd.actors.mobs.Greatmoss.class, Piranha.class,
				pd.actors.mobs.DragonRider.class
		};
	}

	private static Mob randomRider() {
		return Reflection.newInstance(Random.element(riderPool()));
	}

	public static class SpiderBot extends Mob {
		private boolean parasiteFired;
		{
			spriteClass = SpsCitySprites.SpiderBot.class;
			HP = HT = 150 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
			defenseSkill = 25 + legacyDepthAdjustment(1);
			EXP = 12;
			maxLvl = 30;
			loot = Meat.class;
			lootChance = 0.3f;
			properties.add(Property.BEAST);
			resistances.add(Bleeding.class);
			immunities.add(Roots.class);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(15 + legacyDepthAdjustment(0), 40 + legacyDepthAdjustment(1)); }
		@Override public int attackSkill(Char target) { return 30 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 20); }
		@Override public int attackProc(Char enemy, int damage) {
			if (!parasiteFired) { deliverParasite(pos); parasiteFired = true; }
			return damage;
		}
		@Override public int defenseProc(Char enemy, int damage) { Buff.affect(enemy, BugSlow.class); return super.defenseProc(enemy, damage); }
		@Override public void die(Object cause) {
			super.die(cause);
			BugMeat parasite = new BugMeat();
			for (int offset : PathFinder.NEIGHBOURS8) {
				int cell = pos + offset;
				if (!Dungeon.level.insideMap(cell)) continue;
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.isAlive()) Buff.affect(ch, BugSlow.class);
				if (ch == Dungeon.hero) {
					if (!parasite.collect(Dungeon.hero.belongings.backpack)) Dungeon.level.drop(parasite, Dungeon.hero.pos).sprite.drop();
					else GLog.n(Messages.get(this, "yell"));
				}
			}
			if (Dungeon.level.heroFOV[pos]) Sample.INSTANCE.play(Assets.Sounds.BONES);
		}
		private void deliverParasite(int origin) {
			BugMeat parasite = new BugMeat();
			if (Dungeon.hero != null && Dungeon.level.distance(origin, Dungeon.hero.pos) < 2) {
				if (!parasite.collect(Dungeon.hero.belongings.backpack)) Dungeon.level.drop(parasite, Dungeon.hero.pos).sprite.drop();
				else GLog.n(Messages.get(this, "yell"));
			} else Dungeon.level.drop(parasite, origin).sprite.drop();
		}
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put("parasite_fired", parasiteFired); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); parasiteFired = bundle.getBoolean("parasite_fired"); }
	}

	public static class Musketeer extends DualLootMob {
		private boolean shotCharged;
		{
			spriteClass = SpsCitySprites.Musketeer.class;
			HP = HT = 160 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
			defenseSkill = 30 + legacyDepthAdjustment(1);
			EXP = 12;
			maxLvl = 30;
			setupDualLoot(StoneOre.class, 0.2f, DungeonBomb.class, 0.1f);
			properties.add(Property.DWARF);
			immunities.add(Amok.class);
			immunities.add(Terror.class);
		}
		@Override protected boolean act() { if (!enemySeen) shotCharged = false; return super.act(); }
		@Override protected boolean canAttack(Char enemy) { return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos; }
		@Override protected boolean doAttack(Char enemy) {
			if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy);
			if (enemySeen && state != SLEEPING && paralysed == 0 && !shotCharged) {
				shotCharged = true;
				if (sprite != null && Dungeon.level.heroFOV[pos]) sprite.centerEmitter().burst(EnergyParticle.FACTORY, 15);
				spend(attackDelay());
				return true;
			}
			shotCharged = false;
			return super.doAttack(enemy);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(35, 60 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 35 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
		@Override public int attackProc(Char enemy, int damage) {
			int distance = Dungeon.level.distance(pos, enemy.pos);
			if (distance > 1 && Random.Int(4) == 0) Buff.affect(enemy, ArmorBreak.class, 5f).level(25);
			if (distance == 1 && Random.Int(2) == 1) Buff.affect(enemy, LightShootAttack.class).level(5);
			return damage;
		}
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put("shot_charged", shotCharged); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); shotCharged = bundle.getBoolean("shot_charged"); }
	}

	public static class ManySkeleton extends Mob {
		{
			spriteClass = SpsCitySprites.ManySkeleton.class;
			HP = HT = 100;
			defenseSkill = 5;
			EXP = 10;
			maxLvl = 30;
			flying = true;
			properties.add(Property.UNDEAD);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(4, 7); }
		@Override public int attackSkill(Char target) { return 30 + legacyDepthAdjustment(1); }
		@Override public int defenseProc(Char enemy, int damage) {
			if (HP >= damage + 2) {
				ArrayList<Integer> cells = orthogonalSpawnCells(pos);
				if (!cells.isEmpty()) {
					SommonSkeleton clone = new SommonSkeleton();
					clone.pos = Random.element(cells);
					clone.state = clone.HUNTING;
					GameScene.add(clone, 1f);
					clone.HP = Math.max(1, (HP - damage) / 2);
					Actor.add(new Pushing(clone, pos, clone.pos));
					Dungeon.level.occupyCell(clone);
					HP -= clone.HP;
				}
			}
			return super.defenseProc(enemy, damage);
		}
		@Override public void die(Object cause) { spawnSummonedAround(pos); super.die(cause); }
	}

	public static class SummonedSkeleton extends Mob {
		private static final String LEVEL = "level";
		private int level;
		{
			spriteClass = SkeletonSprite.class;
			baseSpeed = 0.8f;
			EXP = 1;
			maxLvl = 30;
			properties.add(Property.UNDEAD);
			adjustStats(Math.max(1, Dungeon.legacyDepth()));
		}
		@Override public int damageRoll() { return Random.NormalIntRange(15, 22 + level); }
		@Override public int attackSkill(Char target) { return 16 + level; }
		@Override public int drRoll() { return Random.NormalIntRange(0, 4); }
		@Override protected boolean spawnsNightmareVirusOnDeath() { return false; }
		public void adjustStats(int depth) {
			level = Math.max(1, depth);
			HP = HT = 80 + level * Random.NormalIntRange(2, 5);
			defenseSkill = (16 + level) * 5;
			enemySeen = true;
		}
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = Math.max(1, bundle.getInt(LEVEL)); defenseSkill = (16 + level) * 5; }
	}

	private static ArrayList<Integer> orthogonalSpawnCells(int center) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells;
	}

	private static void spawnSummonedAround(int center) {
		for (int cell : orthogonalSpawnCells(center)) {
			SommonSkeleton.spawnAt(cell);
		}
	}

	public static class LevelChecker extends Mob {
		private boolean checked;
		{
			spriteClass = SpsCitySprites.LevelChecker.class;
			int level = heroLevel();
			HP = HT = 200 + Math.min(800, level * 20);
			defenseSkill = level / 2;
			flying = true;
			loot = StoneOre.class;
			lootChance = 0.1f;
			properties.add(Property.MECH);
			weaknesses.add(Wand.class);
		}
		private static int heroLevel() { return Dungeon.hero == null ? Math.max(1, Dungeon.depth) : Math.max(1, Dungeon.hero.lvl); }
		@Override public int damageRoll() { return (int)(heroLevel() * 1.5f); }
		@Override public int attackSkill(Char target) { return heroLevel(); }
		@Override public int drRoll() { return heroLevel(); }
		@Override public int attackProc(Char enemy, int damage) {
			if (enemy == Dungeon.hero) {
				if (!checked) { checked = true; Dungeon.hero.lvl++; }
				else enemy.damage(1, Item.class);
			} else enemy.damage(1, Item.class);
			return damage;
		}
		@Override public void damage(int damage, Object source) { super.damage(source instanceof Wand ? Math.round(damage * 1.5f) : damage, source); }
		@Override public void die(Object cause) { if (Random.Int(2) == 0) Dungeon.level.drop(new LevelDown(), pos).sprite.drop(); super.die(cause); }
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put("checked", checked); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); checked = bundle.getBoolean("checked"); }
	}

	public static class GreatMoss extends Mob {
		{
			spriteClass = SpsCitySprites.GreatMoss.class;
			HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7);
			defenseSkill = legacyDepthAdjustment(0);
			EXP = 11;
			maxLvl = 30;
			baseSpeed = 0.5f;
			loot = Generator.Category.MUSHROOM;
			lootChance = 0.3f;
			state = PASSIVE;
			properties.add(Property.PLANT);
			immunities.add(Amok.class);
			immunities.add(Terror.class);
			immunities.add(Sleep.class);
			immunities.add(TarGas.class);
			immunities.add(Tar.class);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(20 + legacyDepthAdjustment(0), 60 + legacyDepthAdjustment(1)); }
		@Override public int attackSkill(Char target) { return 28 + legacyDepthAdjustment(1); }
		@Override public float attackDelay() { return 2f; }
		@Override public int drRoll() { return Random.NormalIntRange(20, 25); }
		@Override public void damage(int damage, Object source) { if (state == PASSIVE) state = HUNTING; super.damage(damage, source); }
		@Override public int defenseProc(Char enemy, int damage) {
			Class<? extends Blob> gas;
			switch (Random.Int(4)) {
				case 0: gas = ToxicGas.class; break;
				case 1: gas = ConfusionGas.class; break;
				case 2: gas = ParalyticGas.class; break;
				default: gas = DarkGas.class; break;
			}
			GameScene.add(Blob.seed(pos, 25, gas));
			return super.defenseProc(enemy, damage);
		}
		@Override public void beckon(int cell) { }
		@Override public Item SupercreateLoot() { return new Sungrass.Seed(); }
	}

	public static class RedWraith extends Wraith {
		{
			spriteClass = SpsCitySprites.RedWraith.class;
			HP = HT = 10 + Math.max(1, Dungeon.legacyDepth());
			EXP = 1;
			maxLvl = 30;
			flying = true;
			setupLegacyDualLoot(RedDewdrop.class, 0.5f, Generator.Category.RING, 0.1f);
			immunities.add(Terror.class);
			immunities.add(Amok.class);
			immunities.add(Charm.class);
			immunities.add(Sleep.class);
			immunities.add(ToxicGas.class);
			immunities.add(Vertigo.class);
			immunities.add(Burning.class);
			immunities.add(Paralysis.class);
			immunities.add(Roots.class);
			immunities.add(Frost.class);
			immunities.add(ScrollOfPsionicBlast.class);
			adjustStats(Math.max(1, Dungeon.legacyDepth()));
		}
		@Override public int damageRoll() { return Random.NormalIntRange(1, 3 + level); }
		@Override public int attackSkill(Char target) { return 10 + level; }
		@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(4) == 0) {
				Buff.prolong(enemy, Vertigo.class, 5f);
				Buff.affect(enemy, Terror.class, Terror.DURATION).object = id();
			}
			return damage;
		}
		@Override public Item createLoot() {
			return Random.Float() < 0.5f / 0.55f ? new RedDewdrop() : Generator.random(Generator.Category.RING);
		}
		public static RedWraith spawnAt(int cell) {
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
			RedWraith wraith = new RedWraith();
			wraith.pos = cell;
			wraith.state = wraith.HUNTING;
			GameScene.add(wraith, 2f);
			if (wraith.sprite != null) {
				wraith.sprite.alpha(0);
				wraith.sprite.emitter().burst(ShadowParticle.CURSE, 5);
			}
			return wraith;
		}
	}
}
