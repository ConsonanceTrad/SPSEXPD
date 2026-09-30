package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Silent;
import pd.actors.buffs.SpeedUp;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.LevelDown;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SpsMobRotationTest {
	private SpsMobRotationTest() { }

	public static void main(String[] args) {
		checkDeck(2, new Class[]{Rat.class, BrownBat.class,
				DustElement.class, RatBoss.class},
				new int[]{50, 50, 35, 1});
		checkDeck(3, new Class[]{Rat.class, BrownBat.class,
				Shit.class, DustElement.class,
				LiveMoss.class, Swarm.class, PatrolUAV.class},
				new int[]{10, 10, 10, 8, 6, 4, 7});
		checkDeck(4, new Class[]{Shit.class, DustElement.class,
				LiveMoss.class, Swarm.class, Crab.class,
				PatrolUAV.class, Vagrant.class},
				new int[]{1, 1, 1, 1, 1, 1, 1});
		checkDeck(7, new Class[]{Thief.class, Gnoll.class, GhostPhoto.class,
				PatrolUAV.class, Vagrant.class},
				new int[]{10, 10, 5, 2, 2});
		checkDeck(8, new Class[]{Thief.class, Gnoll.class, Guard.class,
				Assassin.class, TrollWarrior.class,
				GhostPhoto.class, FireRabbit.class,
				BambooMob.class, GoldCollector.class},
				new int[]{10, 10, 5, 4, 3, 10, 5, 10, 5});
		pd.Dungeon.sporkAvailable = false;
		checkDeck(9, new Class[]{Thief.class, Gnoll.class, Guard.class,
				Assassin.class, TrollWarrior.class,
				Zombie.class, FireRabbit.class,
				BambooMob.class, GoldCollector.class},
				new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1});
		pd.Dungeon.sporkAvailable = true;
		checkDeck(9, new Class[]{Assassin.class, TrollWarrior.class,
				BanditKing.class, GoldCollector.class,
				FireRabbit.class}, new int[]{10, 10, 1, 10, 10});
		checkDeck(12, new Class[]{Bat.class, Skeleton.class, GnollShaman.class,
				Spinner.class, SandMob.class, IceBug.class},
				new int[]{10, 9, 5, 3, 9, 10});
		checkDeck(13, new Class[]{Bat.class, Skeleton.class, Brute.class,
				GnollShaman.class, Spinner.class, BrokenRobot.class,
				SandMob.class, IceBug.class, TimeKeeper.class},
				new int[]{10, 10, 3, 7, 6, 7, 4, 10, 3});
		checkDeck(14, new Class[]{Bat.class, Skeleton.class, Brute.class,
				GnollShaman.class, Spinner.class, BrokenRobot.class,
				SandMob.class, IceBug.class, TimeKeeper.class},
				new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1});
		checkDeck(17, new Class[]{FireElemental.class, Monk.class, Golem.class,
				SpiderBot.class, Musketeer.class},
				new int[]{10, 8, 4, 4, 2});
		checkDeck(18, new Class[]{FireElemental.class, Warlock.class, Monk.class,
				DragonRider.class, Golem.class, SpiderBot.class,
				Musketeer.class, DwarfLich.class,
				ManySkeleton.class, LevelChecker.class},
				new int[]{10, 10, 10, 8, 8, 8, 6, 2, 1, 6});
		checkDeck(19, new Class[]{FireElemental.class, Warlock.class, Monk.class,
				DragonRider.class, Golem.class, SpiderBot.class,
				Musketeer.class, DwarfLich.class,
				ManySkeleton.class, LevelChecker.class},
				new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
		checkDeck(22, new Class[]{Succubus.class, Eye.class, DemonGoo.class,
				ThiefImp.class, DemonFlower.class, Sufferer.class}, new int[]{2, 1, 2, 1, 1, 1});
		checkDeck(23, new Class[]{Succubus.class, Eye.class, DemonGoo.class,
				Scorpio.class, ThiefImp.class, DemonFlower.class,
				Sufferer.class, DemonRabbit.class},
				new int[]{2, 2, 2, 1, 1, 1, 1, 1});
		checkDeck(24, new Class[]{Succubus.class, Eye.class, DemonGoo.class,
				Scorpio.class, ThiefImp.class, DemonFlower.class,
				Sufferer.class, DemonRabbit.class},
				new int[]{1, 1, 1, 1, 1, 1, 1, 1});
		checkSpsAlternateRoutes();
		checkCityStatsAndSafety();
		checkHallsStatsAndSafety();
		checkCaveStatsAndSafety();
		checkSewerStatsAndSafety();
		checkPrisonStatsAndSafety();
		checkBrokenRobot();
		checkBrownBat();
		checkSwarm();
		checkGhostQuestMobs();
		checkRareAlternates();
		checkBandit();
		checkPiranhaAndWraith();
		checkMimicAndStatues();
		checkBanditKing();
		checkLegacyBranchScaling();
		checkBlueCat();
		checkSharedSpsBehaviors();
		checkLegacyLootRules();
		checkLegacySpeedUp();
		System.out.println("SPS depth 2-24 migrated mob rotations passed.");
	}

	private static void checkSharedSpsBehaviors() {
		Dungeon.depth = 13;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();

		Thief thief = new Thief();
		if (!thief.properties().contains(Char.Property.GOBLIN)
				|| thief.properties().contains(Char.Property.UNDEAD)) {
			throw new AssertionError("SPS thief faction differs from the source");
		}
		DamageProbe target = new DamageProbe();
		for (int i = 0; i < 200 && target.buff(Locked.class) == null; i++) {
			thief.attackProc(target, 1);
		}
		if (target.buff(Locked.class) == null || thief.state != thief.FLEEING) {
			throw new AssertionError("SPS thief did not lock its target and flee");
		}

		Bat bat = new Bat();
		bat.HP = bat.HT - 10;
		bat.attackProc(target, 10);
		if (bat.HP != bat.HT) throw new AssertionError("SPS bat did not heal for full dealt damage");
		Buff.affect(bat, BeOld.class).set(10f);
		bat.HP -= 10;
		int agedHp = bat.HP;
		bat.attackProc(target, 10);
		if (bat.HP != agedHp) throw new AssertionError("aged SPS bat still healed");

		GnollProbe gnoll = new GnollProbe();
		gnoll.pos = 12;
		target.pos = 14;
		if (!gnoll.canReach(target)) throw new AssertionError("SPS gnoll lost its two-cell attack");
		Buff.affect(gnoll, Locked.class, 10f);
		if (gnoll.canReach(target)) throw new AssertionError("locked SPS gnoll still attacked at range");

		Dungeon.level = new TestLevel();
		Actor.clear();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 0;
		ExplosionProbe blastTarget = new ExplosionProbe();
		blastTarget.pos = 13;
		Actor.add(blastTarget);
		Skeleton skeleton = new Skeleton();
		skeleton.pos = 12;
		skeleton.legacyDeathBurst();
		if (blastTarget.buff(Silent.class) == null) {
			throw new AssertionError("SPS skeleton explosion did not apply silence");
		}
		Actor.clear();

		checkLegacySewerAndPrisonMobs();
		checkLegacyCityMobs();
		checkLegacyHallsMobs();
	}

	private static void checkSwarm() {
		Dungeon.depth = 3;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Swarm swarm = new Swarm();
		if (swarm.HT != 60 || swarm.defenseSkill != 5 || swarm.EXP != 1 || swarm.maxLvl != 10
				|| swarm.loot != pd.items.Generator.Category.SEED
				|| Math.abs(swarm.lootChance - 0.1f) > 0.0001f
				|| Swarm.specialLootType() != pd.items.scrolls.ScrollOfMagicalInfusion.class) {
			throw new AssertionError("SPS swarm stats or rewards differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int damage = swarm.damageRoll();
			if (damage < 4 || damage > 7) throw new AssertionError("SPS swarm damage is out of range");
		}
		Buff.affect(swarm, Burning.class).reignite(swarm, 10f);
		Buff.affect(swarm, pd.actors.buffs.Poison.class).set(10f);
		Swarm clone = swarm.split();
		if (clone.generation != 1 || clone.EXP != 1 || clone.buff(Burning.class) == null
				|| clone.buff(pd.actors.buffs.Poison.class) == null
				|| clone.spawnsNightmareVirusOnDeath()) {
			throw new AssertionError("SPS swarm split inheritance differs from the source");
		}
	}

	private static void checkGhostQuestMobs() {
		Dungeon.depth = 3;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 0;

		FetidRat rat = new FetidRat();
		if (rat.HT != 45 || rat.defenseSkill != 5 || rat.EXP != 4
				|| !rat.properties().contains(Char.Property.BEAST)
				|| !rat.properties().contains(Char.Property.MINIBOSS)
				|| rat.properties().contains(Char.Property.DEMONIC)) {
			throw new AssertionError("SPS fetid rat stats or faction differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int armor = rat.drRoll();
			if (armor < 0 || armor > 2) throw new AssertionError("SPS fetid rat armor is out of range");
		}
		DamageProbe target = new DamageProbe();
		for (int i = 0; i < 100 && target.buff(pd.actors.buffs.Ooze.class) == null; i++) {
			rat.attackProc(target, 1);
		}
		if (target.buff(pd.actors.buffs.Ooze.class) == null) {
			throw new AssertionError("SPS fetid rat did not apply its source ooze");
		}

		GnollTrickster trickster = new GnollTrickster();
		trickster.pos = 11;
		target.pos = 13;
		if (trickster.HT != 60 || trickster.defenseSkill != 5 || trickster.EXP != 5
				|| !(trickster.loot instanceof pd.items.weapon.missiles.darts.PoisonDart)
				|| Math.abs(trickster.lootChance - 1f) > 0.0001f
				|| !trickster.properties().contains(Char.Property.ORC)
				|| !trickster.canAttack(target)) {
			throw new AssertionError("SPS gnoll trickster stats, loot, faction, or range differ from the source");
		}
		Buff.affect(trickster, Locked.class, 10f);
		if (trickster.canAttack(target)) throw new AssertionError("locked SPS gnoll trickster still attacked at range");
		target.pos = 12;
		if (!trickster.canAttack(target)) throw new AssertionError("locked SPS gnoll trickster lost melee attacks");

		GreatCrab crab = new GreatCrab();
		if (crab.HT != 100 || crab.defenseSkill != 0 || crab.EXP != 6
				|| !crab.properties().contains(Char.Property.BEAST)
				|| !crab.properties().contains(Char.Property.MINIBOSS)
				|| crab.loot != pd.items.food.MysteryMeat.class
				|| Math.abs(crab.lootChance - 0.5f) > 0.0001f) {
			throw new AssertionError("SPS great crab stats, faction, or ordinary loot differ from the source");
		}
		crab.enemySeen = true;
		crab.state = crab.HUNTING;
		if (!crab.legacyBlocksDamage(target)
				|| !crab.legacyBlocksDamage(pd.actors.blobs.Electricity.class)) {
			throw new AssertionError("alert SPS great crab failed to block source direct damage");
		}
		crab.enemySeen = false;
		if (crab.legacyBlocksDamage(target)) throw new AssertionError("unaware SPS great crab still blocked direct damage");
	}

	private static void checkRareAlternates() {
		Dungeon.depth = 3;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		for (int i = 0; i < 100; i++) {
			Albino albino = new Albino();
			if (albino.HT < 13 || albino.HT > 19 || albino.EXP != 1
					|| albino.loot != pd.items.food.meatfood.Meat.class
					|| Math.abs(albino.lootChance - 1f) > 0.0001f
					|| !albino.properties().contains(Char.Property.BEAST)
					|| !albino.properties().contains(Char.Property.DEMONIC)
					|| albino.resist(pd.items.wands.Wand.class) >= 1f
					|| !albino.isImmune(Amok.class) || !albino.isImmune(Terror.class)
					|| !albino.isImmune(pd.actors.blobs.CorruptGas.class)
					|| !albino.isImmune(pd.actors.buffs.Vertigo.class)
					|| !albino.isImmune(pd.actors.blobs.SandStorm.class)
					|| Albino.specialLootCategory() != pd.items.Generator.Category.HIGHFOOD) {
				throw new AssertionError("SPS albino stats, faction, defenses, or rewards differ from the source");
			}
		}

		Dungeon.depth = 23;
		Acidic acidic = new Acidic();
		if (!acidic.properties().contains(Char.Property.BEAST)
				|| !acidic.properties().contains(Char.Property.DEMONIC)
				|| acidic.properties().contains(Char.Property.ACIDIC)
				|| !acidic.isImmune(pd.actors.blobs.StenchGas.class)
				|| !Arrays.equals(Acidic.specialLootTypes(), new Class[]{
				pd.items.potions.PotionOfToxicGas.class,
				pd.items.wands.WandOfAcid.class})) {
			throw new AssertionError("SPS acidic scorpio faction, immunity, or rewards differ from the source");
		}
		DamageProbe target = new DamageProbe();
		for (int i = 0; i < 100 && target.lastDamage == 0; i++) acidic.defenseProc(target, 20);
		if (target.lastSource != acidic || target.lastDamage < 1 || target.lastDamage > 10) {
			throw new AssertionError("SPS acidic scorpio retaliation differs from the source");
		}
	}

	private static void checkBandit() {
		Dungeon.depth = 8;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 8;
		Actor.clear();

		BanditProbe bandit = new BanditProbe();
		bandit.pos = 6;
		if (bandit.loot != pd.items.food.vegetable.NutVegetable.class
				|| Math.abs(bandit.lootChance - 0.1f) > 0.0001f
				|| !bandit.properties().contains(Char.Property.GOBLIN)
				|| !bandit.properties().contains(Char.Property.ELF)
				|| !bandit.isImmune(pd.actors.buffs.Blindness.class)
				|| !bandit.isImmune(pd.actors.blobs.DarkGas.class)
				|| Bandit.specialLootCategory() != pd.items.Generator.Category.MELEEWEAPON
				|| !bandit.canReach(Dungeon.hero)) {
			throw new AssertionError("SPS bandit inheritance, range, immunity, or reward differs from the source");
		}

		bandit.pos = 6;
		bandit.emitDarkGas();
		if (pd.actors.blobs.Blob.volumeAt(
				6, pd.actors.blobs.DarkGas.class) != 10) {
			throw new AssertionError("SPS bandit did not seed dark gas on its own cell at a map edge");
		}

		bandit.pos = 6;
		Dungeon.gold = 1000;
		bandit.attackProc(Dungeon.hero, 7);
		pd.actors.buffs.EnergyArmor shield =
				bandit.buff(pd.actors.buffs.EnergyArmor.class);
		if (Dungeon.gold != 950 || shield == null || shield.shielding() != 25) {
			throw new AssertionError("SPS bandit did not steal one twentieth of hero gold and gain its source shield");
		}
		for (int i = 0; i < 100 && Dungeon.hero.buff(
				pd.actors.buffs.Poison.class) == null; i++) {
			bandit.attackProc(Dungeon.hero, 7);
		}
		if (Dungeon.hero.buff(pd.actors.buffs.Poison.class) == null) {
			throw new AssertionError("SPS bandit did not apply its post-theft poison");
		}

		bandit.HP = bandit.HT / 2;
		bandit.takeTurn();
		Dungeon.gold = 1000;
		bandit.attackProc(Dungeon.hero, 7);
		if (Dungeon.gold != 950) {
			throw new AssertionError("SPS bandit did not reset its gold skill at the first health phase");
		}

		com.watabou.utils.Bundle saved = new com.watabou.utils.Bundle();
		bandit.storeInBundle(saved);
		if (saved.getInt("breaks") != 1 || !saved.getBoolean("skill_used")) {
			throw new AssertionError("SPS bandit did not store its phase and skill state");
		}
		BanditProbe restored = new BanditProbe();
		restored.restoreFromBundle(saved);
		Dungeon.gold = 1000;
		restored.attackProc(Dungeon.hero, 7);
		if (Dungeon.gold != 1000) {
			throw new AssertionError("SPS bandit repeated its gold skill after loading");
		}
		restored.HP = restored.HT / 4;
		restored.takeTurn();
		restored.attackProc(Dungeon.hero, 7);
		if (Dungeon.gold != 950) {
			throw new AssertionError("SPS bandit did not restore and advance its second health phase");
		}

		Bandit damageProbe = new Bandit();
		int before = damageProbe.HP;
		damageProbe.damage(damageProbe.HT, bandit);
		if (before - damageProbe.HP != Math.max(1, damageProbe.HT / 6)) {
			throw new AssertionError("SPS bandit incoming damage is not capped at one sixth of maximum health");
		}

		checkBanditText();
		Actor.clear();
	}

	private static void checkSpsAlternateRoutes() {
		Class<?>[][] mappings = {
				{Rat.class, Albino.class},
				{Vagrant.class, ExVagrant.class},
				{Thief.class, Bandit.class},
				{BambooMob.class, ExBambooMob.class},
				{Brute.class, Shielded.class},
				{IceBug.class, BombBug.class},
				{Monk.class, Senior.class},
				{Scorpio.class, Acidic.class},
				{Succubus.class, FireSuccubus.class}
		};
		for (Class<?>[] mapping : mappings) {
			boolean found = false;
			for (int i = 0; i < 4096 && !found; i++) {
				ArrayList<Class<? extends Mob>> rotation = new ArrayList<>();
				rotation.add(mapping[0].asSubclass(Mob.class));
				MobSpawner.swapSpsMobAlts(rotation);
				Class<?> result = rotation.get(0);
				if (result != mapping[0] && result != mapping[1]) {
					throw new AssertionError("SPS alternate route produced a non-source class: " + result.getName());
				}
				found = result == mapping[1];
			}
			if (!found) throw new AssertionError("SPS alternate route is unreachable: " + mapping[1].getName());
		}

		java.util.HashSet<Class<?>> shatteredOnly = new java.util.HashSet<>(Arrays.asList(
				GnollExile.class, HermitCrab.class, CausticSlime.class,
				SpectralNecromancer.class, ArmoredBrute.class, DM201.class,
				Elemental.ChaosElemental.class));
		int[] ordinaryDepths = {2, 3, 4, 7, 8, 9, 12, 13, 14, 17, 18, 19, 22, 23, 24};
		for (int pass = 0; pass < 200; pass++) {
			for (int depth : ordinaryDepths) {
				for (Class<? extends Mob> type : MobSpawner.getMobRotation(depth)) {
					if (shatteredOnly.contains(type)) {
						throw new AssertionError("Shattered-only rare mob is visible in the SPS rotation: " + type.getName());
					}
				}
			}
		}

		Dungeon.depth = 13;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Shielded shielded = new Shielded();
		if (shielded.HT < 133 || shielded.HT > 146 || shielded.defenseSkill != 33
				|| shielded.attackSkill(null) != 16 || shielded.attackDelay() != 1.5f
				|| shielded.loot != pd.items.Gold.class
				|| Math.abs(shielded.lootChance - 0.5f) > 0.0001f
				|| !shielded.properties().contains(Char.Property.ORC)
				|| !shielded.isWeak(pd.items.wands.Wand.class)
				|| !shielded.isImmune(Terror.class)
				|| SpsExitMobs.GuardShielded.secondaryLootCategory()
				!= pd.items.Generator.Category.RANGEWEAPON) {
			throw new AssertionError("SPS shielded brute inheritance, defenses, or loot differs from the source");
		}
		BombBug bombBug = new BombBug();
		if (bombBug.HT < 106 || bombBug.HT > 145 || bombBug.defenseSkill != 28
				|| bombBug.attackSkill(null) != 29
				|| !bombBug.properties().contains(Char.Property.BEAST)
				|| bombBug.properties().contains(Char.Property.ICY)) {
			throw new AssertionError("SPS frost stone bug stats or source faction differs");
		}
		BambooInheritanceProbe bamboo = new BambooInheritanceProbe();
		bamboo.attackProc(new DamageProbe(), 10);
		bamboo.defenseProc(new DamageProbe(), 10);
		if (bamboo.buffRolls != 2 || bamboo.retaliationRolls != 2) {
			throw new AssertionError("SPS evolved bamboo lost one of its subclass-plus-parent combat rolls");
		}

		Dungeon.depth = 18;
		Dungeon.branch = 0;
		SeniorProbe senior = new SeniorProbe();
		if (senior.HT < 214 || senior.HT > 250 || senior.defenseSkill != 39
				|| senior.EXP != 14 || senior.maxLvl != 30
				|| senior.attackSkill(null) != 39 || senior.attackDelay() != 0.5f
				|| !senior.properties().contains(Char.Property.DWARF)
				|| !senior.isImmune(Amok.class) || !senior.isImmune(Terror.class)) {
			throw new AssertionError("SPS senior monk stats, faction, or immunity differs from the source");
		}
		for (int i = 0; i < 100; i++) {
			int damage = senior.damageRoll();
			int armor = senior.drRoll();
			if (damage < 32 || damage > 74 || armor < 2 || armor > 12) {
				throw new AssertionError("SPS senior monk damage or armor is out of source range");
			}
		}
		DamageProbe target = new DamageProbe();
		senior.attackProc(target, 5);
		if (target.buff(pd.actors.buffs.DBurning.class) == null) {
			throw new AssertionError("SPS senior monk did not apply dark burning");
		}
		senior.HP = senior.HT / 4;
		senior.takeTurn();
		pd.actors.buffs.AttackUp attack =
				senior.buff(pd.actors.buffs.AttackUp.class);
		pd.actors.buffs.DefenceUp defense =
				senior.buff(pd.actors.buffs.DefenceUp.class);
		if (senior.HP != senior.HT || attack == null || attack.level() != 50
				|| defense == null || defense.level() != 75) {
			throw new AssertionError("SPS senior monk did not restore health and gain its source phase buffs");
		}
		Senior damageProbe = new Senior();
		int before = damageProbe.HP;
		damageProbe.damage(damageProbe.HT, senior);
		if (before - damageProbe.HP != Math.max(1, damageProbe.HT / 6)) {
			throw new AssertionError("SPS senior monk incoming damage is not capped at one sixth of maximum health");
		}
	}

	private static void checkBanditText() {
		java.nio.file.Path base = java.nio.file.Path.of("src", "main", "assets", "messages", "actors");
		String[][] expected = {
				{"actors.properties", "actors.mobs.bandit.name=Crazy bandit", "actors.mobs.bandit.desc=Crazy bandit can harm target when steal from it.", "actors.mobs.senior.name=senior monk", "actors.mobs.senior.desc=He is stronger."},
				{"actors_zh.properties", "actors.mobs.bandit.name=紫衣大盗", "actors.mobs.bandit.desc=较普通绿衣小贼而言，紫衣大盗在偷窃同时，会狠狠伤害被偷窃者，以方便自己逃跑。", "actors.mobs.senior.name=武僧大师", "actors.mobs.senior.desc=相较普通武僧而言，武僧大师变秃了，也变强了!"},
				{"actors_zh-hant.properties", "actors.mobs.bandit.name=紫衣大盜", "actors.mobs.bandit.desc=較普通綠衣小賊而言，紫衣大盜在偷竊同時，會狠狠傷害被偷竊者，以方便自己逃跑。", "actors.mobs.senior.name=武僧大師", "actors.mobs.senior.desc=相較普通武僧而言，武僧大師變禿了，也變強了!"},
				{"actors_ru.properties", "actors.mobs.bandit.name=Безумный бандит", "actors.mobs.bandit.desc=По сравнению с обычным вором в зеленой мантии, вор в фиолетовой мантии серьезно ранит во время кражи, чтобы облегчить свой побег.", "actors.mobs.senior.name=Монах-наставник", "actors.mobs.senior.desc=Эти монахи - фанатики, посвятившие себя защите своего короля физической силой. Их преданность настолько велика, что они полностью предали свое сознание своему королю, и теперь они бродят по городу дворфов как безмозглые зомби.\\n\\nПо сравнению с обычными монахами, монахи-наставники стали лысее и сильнее!"}
		};
		try {
			for (String[] locale : expected) {
				String text = java.nio.file.Files.readString(base.resolve(locale[0]), java.nio.charset.StandardCharsets.UTF_8);
				if (!text.contains(locale[1]) || !text.contains(locale[2])
						|| !text.contains(locale[3]) || !text.contains(locale[4]) || text.contains("\uFFFD")) {
					throw new AssertionError("SPS bandit text differs from 0.9.8 or contains damaged UTF-8: " + locale[0]);
				}
			}
		} catch (java.io.IOException error) {
			throw new AssertionError("Unable to read SPS bandit locale files as UTF-8", error);
		}
	}

	private static void checkPiranhaAndWraith() {
		Dungeon.depth = 3;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 6;
		Actor.clear();

		Piranha piranha = new Piranha();
		if (piranha.HT != 55 || piranha.defenseSkill != 16 || piranha.EXP != 5
				|| Math.abs(piranha.speed() - 1.5f) > 0.0001f
				|| piranha.loot != pd.items.food.meatfood.Meat.class
				|| Math.abs(piranha.lootChance - 1f) > 0.0001f
				|| !(piranha.SupercreateLoot() instanceof pd.items.weapon.missiles.meleethrow.HugeShuriken)
				|| !(Piranha.random().getClass() == Piranha.class)) {
			throw new AssertionError("SPS piranha stats, speed, or rewards differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int damage = piranha.damageRoll();
			int armor = piranha.drRoll();
			if (damage < 3 || damage > 10 || armor < 0 || armor > 3) {
				throw new AssertionError("SPS piranha damage or armor is out of range");
			}
		}
		if (piranha.attackSkill(Dungeon.hero) != 26
				|| !piranha.isImmune(Burning.class)
				|| !piranha.isImmune(pd.actors.buffs.Paralysis.class)
				|| !piranha.isImmune(pd.actors.blobs.ToxicGas.class)
				|| !piranha.isImmune(pd.actors.buffs.Roots.class)
				|| !piranha.isImmune(pd.actors.buffs.Frost.class)) {
			throw new AssertionError("SPS piranha combat stats or immunities differ from the source");
		}

		WraithProbe wraith = new WraithProbe();
		wraith.adjustStats(3);
		DamageProbe target = new DamageProbe();
		wraith.pos = 6;
		target.pos = 18;
		if (wraith.HT != 4 || wraith.EXP != 1 || wraith.defenseSkill != 65
				|| wraith.properties().contains(Char.Property.INORGANIC)
				|| !wraith.properties().contains(Char.Property.UNDEAD)
				|| !wraith.canReach(target)
				|| !Arrays.equals(Wraith.legacyLootTypes(), new Class[]{
				pd.items.scrolls.ScrollOfMagicalInfusion.class,
				pd.items.scrolls.ScrollOfUpgrade.class})
				|| !Arrays.equals(Wraith.legacyLootChances(), new float[]{0.06f, 0.09f})) {
			throw new AssertionError("SPS wraith stats, faction, range, or rewards differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int damage = wraith.damageRoll();
			if (damage < 1 || damage > 6) throw new AssertionError("SPS wraith damage is out of range");
		}
		for (int i = 0; i < 100 && target.buff(pd.actors.buffs.Vertigo.class) == null; i++) {
			wraith.attackProc(target, 1);
		}
		if (target.buff(pd.actors.buffs.Vertigo.class) == null
				|| target.buff(Terror.class) == null) {
			throw new AssertionError("SPS wraith did not apply vertigo and terror together");
		}
		if (!wraith.isImmune(Terror.class) || !wraith.isImmune(Amok.class)
				|| !wraith.isImmune(pd.actors.buffs.Charm.class)
				|| !wraith.isImmune(Sleep.class)
				|| !wraith.isImmune(pd.actors.blobs.ToxicGas.class)
				|| !wraith.isImmune(pd.items.scrolls.ScrollOfPsionicBlast.class)
				|| !wraith.isImmune(pd.actors.buffs.Vertigo.class)
				|| !wraith.isImmune(Burning.class)
				|| !wraith.isImmune(pd.actors.buffs.Paralysis.class)
				|| !wraith.isImmune(pd.actors.buffs.Roots.class)
				|| !wraith.isImmune(pd.actors.buffs.Frost.class)) {
			throw new AssertionError("SPS wraith immunities differ from the source");
		}

		com.watabou.utils.Bundle saved = new com.watabou.utils.Bundle();
		wraith.adjustStats(7);
		wraith.storeInBundle(saved);
		WraithProbe restored = new WraithProbe();
		restored.restoreFromBundle(saved);
		if (restored.level != 7 || restored.defenseSkill != 85) {
			throw new AssertionError("SPS wraith level was not restored from its save data");
		}
		if (Wraith.spawnAt(0) != null || Wraith.spawnAt(-1) != null) {
			throw new AssertionError("SPS wraith spawned outside the playable map");
		}
		Wraith.spawnAround(0);
		Actor.clear();
	}

	private static void checkMimicAndStatues() {
		Dungeon.depth = 13;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 6;

		Item reward = new pd.items.Gold(10);
		Mimic mimic = Mimic.spawnAt(12, reward);
		if (mimic.HT != 172 || mimic.HP != 172 || mimic.EXP != 6
				|| mimic.defenseSkill != 11 || mimic.attackSkill(Dungeon.hero) != 22
				|| mimic.alignment != Char.Alignment.ENEMY || mimic.state != mimic.HUNTING
				|| !mimic.properties().contains(Char.Property.UNKNOW)
				|| mimic.properties().contains(Char.Property.DEMONIC)
				|| !mimic.isImmune(pd.items.scrolls.ScrollOfPsionicBlast.class)
				|| mimic.items == null || mimic.items.size() != 1 || mimic.items.get(0) != reward) {
			throw new AssertionError("SPS mimic stats, faction, immunity, or stored reward differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int damage = mimic.damageRoll();
			if (damage < 8 || damage > 17 || mimic.drRoll() != 0) {
				throw new AssertionError("SPS mimic damage or armor is out of range");
			}
		}
		Mimic awakened = Mimic.spawnAt(12, Arrays.asList(
				new pd.items.Gold(7)));
		if (awakened == null || !Dungeon.level.mobs.contains(awakened) || awakened.items == null
				|| awakened.items.size() != 1
				|| !(awakened.items.get(0) instanceof pd.items.Gold)) {
			throw new AssertionError("SPS mimic did not awaken and retain its heap reward");
		}

		Statue statue = new Statue();
		statue.createWeapon(false);
		if (statue.HT != 80 || statue.defenseSkill != 30 || statue.EXP != 76
				|| !statue.properties().contains(Char.Property.ELEMENT)
				|| statue.properties().contains(Char.Property.INORGANIC)
				|| statue.resist(pd.actors.blobs.ToxicGas.class) >= 1f
				|| statue.resist(pd.actors.buffs.Poison.class) >= 1f
				|| statue.weapon() == null || statue.weapon().trueLevel() < 0
				|| !statue.weapon().isIdentified() || statue.weapon().enchantment == null
				|| Statue.random(false).getClass() != Statue.class) {
			throw new AssertionError("SPS weapon statue stats, faction, defenses, or weapon differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int armor = statue.drRoll();
			if (armor < 0 || armor > 13) throw new AssertionError("SPS weapon statue armor is out of range");
		}

		ArmorStatue armorStatue = new ArmorStatue();
		if (armorStatue.HT != 80 || armorStatue.EXP != 76
				|| armorStatue.attackSkill(Dungeon.hero) != 48
				|| !armorStatue.properties().contains(Char.Property.ELEMENT)
				|| armorStatue.armor() == null || armorStatue.armor().trueLevel() < 0
				|| !armorStatue.armor().isIdentified() || armorStatue.armor().glyph == null) {
			throw new AssertionError("SPS armor statue stats, faction, or armor differ from the source");
		}
		for (int i = 0; i < 100; i++) {
			int damage = armorStatue.damageRoll();
			if (damage < 13 || damage > 26) throw new AssertionError("SPS armor statue damage is out of range");
		}
	}

	private static void checkLegacySewerAndPrisonMobs() {
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 0;
		Actor.clear();

		Dungeon.depth = 3;
		Rat rat = new Rat();
		if (rat.drRoll() != 1 || !(rat.SupercreateLoot()
				instanceof pd.items.weapon.missiles.meleethrow.Brick)) {
			throw new AssertionError("SPS rat armor or special brick reward differs from the source");
		}

		Dungeon.depth = 8;
		Guard guard = new Guard();
		if (guard.properties().contains(Char.Property.UNDEAD)
				|| !guard.properties().contains(Char.Property.HUMAN)
				|| guard.attackDelay() != 1.2f
				|| !guard.isImmune(pd.items.weapon.enchantments.EnchantmentDark.class)
				|| !guard.isImmune(pd.items.weapon.enchantments.EnchantmentDark2.class)) {
			throw new AssertionError("SPS guard faction, speed, or dark enchantment immunity differs from the source");
		}
		DamageProbe chainTarget = new DamageProbe();
		guard.pos = 11;
		chainTarget.pos = 13;
		guard.enemy = chainTarget;
		if (!guard.legacyChainAllowed(true)) throw new AssertionError("SPS guard lost its source chain range");
		Buff.affect(guard, Silent.class, 10f);
		if (guard.legacyChainAllowed(true)) throw new AssertionError("silenced SPS guard could still use its chain");

		int guardDrops = Dungeon.LimitedDrops.GUARD_ARM.count;
		if (!(guard.createLoot() instanceof pd.items.armor.Armor)
				|| Dungeon.LimitedDrops.GUARD_ARM.count != guardDrops) {
			throw new AssertionError("SPS guard retained Shattered's diminishing armor-drop counter");
		}
		Actor.clear();
		DamageProbe blastTarget = new DamageProbe();
		blastTarget.pos = 12;
		Actor.add(blastTarget);
		guard.pos = 11;
		guard.legacyDeathBurst();
		if (blastTarget.lastSource != guard || blastTarget.lastDamage < 0 || blastTarget.lastDamage > 8
				|| blastTarget.buff(Silent.class) != null) {
			throw new AssertionError("SPS guard death burst damage differs from the source");
		}
		Actor.clear();
	}

	private static void checkLegacyCityMobs() {
		Dungeon.depth = 18;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new DamageProbe();
		Dungeon.hero.pos = 13;
		Actor.clear();
		Actor.add(Dungeon.hero);

		WarlockProbe warlock = new WarlockProbe();
		warlock.pos = 11;
		if (warlock.properties().contains(Char.Property.UNDEAD)
				|| !warlock.properties().contains(Char.Property.DWARF)
				|| warlock.loot != pd.items.Generator.Category.POTION
				|| Math.abs(warlock.lootChance - 0.83f) > 0.0001f
				|| !(warlock.SupercreateLoot() instanceof pd.items.eggs.Egg)) {
			throw new AssertionError("SPS warlock faction or loot differs from the source");
		}
		if (!warlock.canReach(Dungeon.hero)) throw new AssertionError("SPS warlock lost its clear-line shadow bolt");
		Buff.affect(warlock, Silent.class, 10f);
		if (warlock.canReach(Dungeon.hero)) throw new AssertionError("silenced SPS warlock still attacked at range");
		Dungeon.hero.pos = 12;
		if (!warlock.canReach(Dungeon.hero)) throw new AssertionError("silenced SPS warlock lost melee attacks");
		DamageProbe darkTarget = (DamageProbe) Dungeon.hero;
		if (warlock.attackProc(darkTarget, 20) != 10
				|| darkTarget.lastSource != pd.actors.damagetype.DamageType.DARK_DAMAGE) {
			throw new AssertionError("SPS warlock did not split melee damage into physical and dark halves");
		}

		Monk monk = new Monk();
		if (monk.properties().contains(Char.Property.UNDEAD)
				|| !monk.properties().contains(Char.Property.DWARF)
				|| monk.attackDelay() != 0.5f
				|| !monk.isImmune(Amok.class) || !monk.isImmune(Terror.class)
				|| monk.loot != pd.items.food.staplefood.NormalRation.class
				|| Math.abs(monk.lootChance - 0.1f) > 0.0001f
				|| !(monk.SupercreateLoot() instanceof pd.items.weapon.melee.normalweapon.FightGloves)) {
			throw new AssertionError("SPS monk speed, faction, immunity, or loot differs from the source");
		}
		pd.items.KindOfWeapon weapon =
				new pd.items.weapon.melee.normalweapon.Dagger();
		Dungeon.hero.belongings.weapon = weapon;
		monk.legacyDisarm(Dungeon.hero);
		if (Dungeon.hero.belongings.weapon != null || Dungeon.level.heaps.get(Dungeon.hero.pos) == null
				|| !Dungeon.level.heaps.get(Dungeon.hero.pos).items.contains(weapon)) {
			throw new AssertionError("SPS monk did not disarm an eligible weapon onto the hero's cell");
		}

		Golem golem = new Golem();
		if (!golem.properties().contains(Char.Property.MECH)
				|| golem.properties().contains(Char.Property.INORGANIC)
				|| golem.properties().contains(Char.Property.LARGE)
				|| golem.attackDelay() != 1.5f
				|| !golem.isImmune(Amok.class) || !golem.isImmune(Terror.class)
				|| !golem.isImmune(Sleep.class) || !golem.isImmune(Tar.class)
				|| !golem.isImmune(pd.actors.blobs.TarGas.class)
				|| golem.loot != pd.items.StoneOre.class
				|| Math.abs(golem.lootChance - 0.5f) > 0.0001f
				|| !(golem.SupercreateLoot() instanceof pd.items.weapon.guns.GunWeapon)) {
			throw new AssertionError("SPS golem speed, faction, immunity, or loot differs from the source");
		}
		if (Golem.legacyReleasesTar(22, 180) || !Golem.legacyReleasesTar(23, 180)) {
			throw new AssertionError("SPS golem tar release threshold is wrong");
		}
		Actor.clear();
	}

	private static void checkLegacyHallsMobs() {
		Dungeon.depth = 23;
		Dungeon.level = new TestLevel();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 0;
		Actor.clear();

		EyeProbe eye = new EyeProbe();
		eye.pos = 11;
		DamageProbe near = new DamageProbe();
		near.pos = 12;
		DamageProbe far = new DamageProbe();
		far.pos = 13;
		Actor.add(near);
		Actor.add(far);
		if (!eye.canReach(far) || eye.attackDelay() != 1.6f
				|| !eye.isImmune(Terror.class)
				|| eye.resist(pd.items.weapon.enchantments.EnchantmentDark.class) >= 1f
				|| eye.loot != pd.items.potions.PotionOfHealing.class
				|| Math.abs(eye.lootChance - 0.1f) > 0.0001f) {
			throw new AssertionError("SPS eye beam, speed, defense, or loot differs from the source");
		}
		DamageProbe meleeTarget = new DamageProbe();
		if (eye.attackProc(meleeTarget, 20) != 5
				|| meleeTarget.lastSource != pd.actors.damagetype.DamageType.LIGHT_DAMAGE) {
			throw new AssertionError("SPS eye did not split melee damage into physical and light portions");
		}

		Spinner spinner = new Spinner();
		spinner.state = spinner.FLEEING;
		spinner.pos = 12;
		spinner.leaveLegacyWeb(12);
		if (Math.abs(spinner.lootChance - 0.15f) > 0.0001f
				|| !spinner.isImmune(pd.actors.buffs.Roots.class)
				|| spinner.isImmune(pd.actors.blobs.Web.class)
				|| !(spinner.SupercreateLoot() instanceof pd.items.weapon.melee.normalweapon.Whip)
				|| pd.actors.blobs.Blob.volumeAt(12,
						pd.actors.blobs.Web.class) < 5) {
			throw new AssertionError("SPS spinner web trail, defense, or loot differs from the source");
		}

		ScorpioProbe scorpio = new ScorpioProbe();
		scorpio.pos = 12;
		far.pos = 14;
		if (!scorpio.canReach(far)
				|| scorpio.properties().contains(Char.Property.DEMONIC)
				|| !scorpio.properties().contains(Char.Property.BEAST)
				|| scorpio.loot != pd.items.potions.PotionOfHealing.class
				|| Math.abs(scorpio.lootChance - 0.2f) > 0.0001f
				|| !(scorpio.SupercreateLoot() instanceof pd.items.weapon.melee.normalweapon.Dagger)) {
			throw new AssertionError("SPS scorpio range, faction, or loot differs from the source");
		}
		Buff.affect(scorpio, Locked.class, 10f);
		if (scorpio.canReach(far)) throw new AssertionError("locked SPS scorpio still attacked at range");
		far.pos = 13;
		if (!scorpio.canReach(far)) throw new AssertionError("locked SPS scorpio lost melee attacks");

		Dungeon.level = new TestLevel();
		Actor.clear();
		far.pos = 13;
		Actor.add(far);
		FiendProbe fiend = new FiendProbe();
		fiend.pos = 11;
		if (!fiend.canReach(far)
				|| !fiend.isImmune(pd.actors.damagetype.DamageType.Dark.class)
				|| fiend.resist(pd.items.wands.fusion.WandOfBlood.class) >= 1f) {
			throw new AssertionError("SPS fiend range or dark defenses differ from the source: range="
					+ fiend.canReach(far) + ", collision=" + fiend.collision(far) + ", target=" + far.pos + ", immune="
					+ fiend.isImmune(pd.actors.damagetype.DamageType.Dark.class)
					+ ", resist=" + fiend.resist(pd.items.wands.fusion.WandOfBlood.class));
		}
		Buff.affect(fiend, Silent.class, 10f);
		if (fiend.canReach(far)) throw new AssertionError("silenced SPS fiend still attacked at range");
		far.pos = 12;
		if (!fiend.canReach(far)) throw new AssertionError("silenced SPS fiend lost melee attacks");
		fiend.HP = fiend.HT - 20;
		if (fiend.add(new pd.actors.buffs.ShadowCurse())
				|| fiend.HP != Math.min(fiend.HT, fiend.HT - 20 + fiend.HT / 10)) {
			throw new AssertionError("SPS fiend did not absorb shadow curse as healing");
		}
		if (fiend.add(new Terror()) || fiend.lastDamageTaken < 1
				|| fiend.lastDamageTaken > fiend.HT * 2 / 3) {
			throw new AssertionError("SPS fiend did not turn terror into direct damage");
		}
		for (int i = 0; i < 100; i++) {
			int damage = fiend.legacyZapDamage();
			if (damage < 20 || damage > 44) throw new AssertionError("SPS fiend shadow bolt damage is out of range");
		}
		Actor.clear();
	}

	private static void checkLegacySpeedUp() {
		SpeedProbe probe = new SpeedProbe();
		Buff.affect(probe, SpeedUp.class, 5f);
		probe.spendForTest(3f);
		if (Math.abs(probe.cooldown() - 2f) > 0.001f) {
			throw new AssertionError("SPS speed-up must accelerate all actions by 1.5x");
		}
	}

	private static void checkLegacyLootRules() {
		Dungeon.hero = new Hero();
		LootProbe probe = new LootProbe();
		probe.maxLvl = 5;
		Dungeon.hero.lvl = 805;
		if (!probe.levelEligible()) {
			throw new AssertionError("SPS loot was suppressed before the source max-level plus 800 boundary");
		}
		Dungeon.hero.lvl = 806;
		if (probe.levelEligible()) {
			throw new AssertionError("SPS loot remained eligible beyond the source max-level plus 800 boundary");
		}

		Dungeon.hero.heroClass = pd.actors.hero.HeroClass.SOLDIER;
		if (Math.abs(probe.secondaryChance(0.1f) - 0.2f) > 0.0001f) {
			throw new AssertionError("SPS secondary loot lost the Soldier five-level luck bonus");
		}
		float baseCombined = SpsSewerMobs.combinedLegacyLootChance(0.5f, 0.05f, 0);
		float luckyCombined = SpsSewerMobs.combinedLegacyLootChance(0.5f, 0.05f, 5);
		float luckyPrimaryShare = SpsSewerMobs.primaryLegacyLootShare(0.5f, 0.05f, 5);
		if (Math.abs(baseCombined - 0.525f) > 0.0001f
				|| Math.abs(luckyCombined - 0.66f) > 0.0001f
				|| Math.abs(luckyPrimaryShare - 0.6f / 0.66f) > 0.0001f) {
			throw new AssertionError("SPS primary/secondary else-if loot probabilities differ from 0.9.8");
		}

		Mob[] dualLootMobs = {
				new Vagrant(), new GnollShaman(), new Musketeer(),
				new DwarfLich(), new DemonRabbit(), new Wraith(),
				new MossySkeleton(), new PlagueDoctor(), new SewerHeart(), new RedWraith()
		};
		float[] expectedLuckyChances = {
				0.32f, 0.34f, 0.44f, 0.64f, 0.44f, 0.3196f, 0.68f, 1f, 1f, 0.68f
		};
		for (int i = 0; i < dualLootMobs.length; i++) {
			if (!(dualLootMobs[i] instanceof LegacyDualLootMob)
					|| Math.abs(dualLootMobs[i].lootChance() - expectedLuckyChances[i]) > 0.0001f) {
				throw new AssertionError("SPS dual loot lost luck scaling: "
						+ dualLootMobs[i].getClass().getSimpleName());
			}
		}

		if (Math.abs(LegacyDualLootMob.primaryShare(0.2f, 1f, 5) - 0.3f) > 0.0001f) {
			throw new AssertionError("SPS boss rare loot lost the source luck bonus");
		}
		checkLegacyBossLootTypes();
	}

	private static void checkLegacyBossLootTypes() {
		Class<?>[] gunPool = pd.items.Generator.Category.GUNWEAPON.classes;
		Class<?>[] expectedGuns = {
				pd.items.weapon.guns.GunA.class,
				pd.items.weapon.guns.GunB.class,
				pd.items.weapon.guns.GunC.class,
				pd.items.weapon.guns.GunD.class,
				pd.items.weapon.guns.GunE.class
		};
		if (!Arrays.equals(gunPool, expectedGuns)) throw new AssertionError("SPS five-gun boss pool differs from 0.9.8");
		if (!(ElderAvatar.rareLoot() instanceof pd.items.artifacts.AlienBag)) throw new AssertionError("SPS Elder Avatar rare loot differs from 0.9.8");
		if (!Arrays.asList(gunPool).contains(ElderAvatar.commonLoot().getClass())) throw new AssertionError("SPS Elder Avatar common loot differs from 0.9.8");
		if (!Arrays.asList(pd.items.Generator.Category.EGGS.classes)
				.contains(Hybrid.rareLoot().getClass())) throw new AssertionError("SPS Hybrid egg loot differs from 0.9.8");
		if (!(King.rareLoot() instanceof pd.items.artifacts.ChaliceOfBlood)) throw new AssertionError("SPS King rare loot differs from 0.9.8");
		pd.items.Item kingCommon = King.commonLoot();
		if (!(kingCommon instanceof pd.items.weapon.missiles.throwing.Skull)
				|| kingCommon.quantity() != 5) throw new AssertionError("SPS King five-skull loot differs from 0.9.8");
		if (!(LichDancer.rareLoot() instanceof pd.items.artifacts.GlassTotem)) throw new AssertionError("SPS Lich Dancer rare loot differs from 0.9.8");
		if (!Arrays.asList(pd.items.Generator.Category.MUSICWEAPON.classes)
				.contains(LichDancer.commonLoot().getClass())) throw new AssertionError("SPS Lich Dancer music loot differs from 0.9.8");
		if (!(PrisonWander.rareLoot() instanceof pd.items.artifacts.EtherealChains)) throw new AssertionError("SPS Prison Wander rare loot differs from 0.9.8");
		if (!(PrisonWander.commonLoot() instanceof pd.items.bombs.DungeonBomb)) throw new AssertionError("SPS Prison Wander common loot differs from 0.9.8");
	}

	private static void checkPrisonStatsAndSafety() {
		Dungeon.depth = 8;
		GhostPhoto photo = new GhostPhoto();
		if (photo.HT < 124 || photo.HT > 140 || photo.defenseSkill != 13
				|| photo.attackSkill(null) != 25 || !photo.flying
				|| !photo.properties().contains(pd.actors.Char.Property.UNKNOW)
				|| photo.spriteClass != pd.sprites.LivePhotoSprite.class
				|| GhostPhoto.specialLootCategory() != pd.items.Generator.Category.SEED
				|| !SpsPrisonMobs.GhostPhoto.class.isAssignableFrom(GhostPhoto.class)) {
			throw new AssertionError("mouldy painting stats, identity, faction, or special loot differ from SPS");
		}

		Dungeon.gold = 1000;
		GoldCollector collector = new GoldCollector();
		if (collector.HT != 175 || collector.speed() != 2f
				|| !collector.properties().contains(pd.actors.Char.Property.GOBLIN)
				|| collector.spriteClass != pd.sprites.GoldCollectorSprite.class
				|| !specialLootMatches(GoldCollector.specialLootTypes(), new Class[]{
				pd.items.Gold.class,
				pd.items.sellitem.VIPcard.class,
				pd.items.artifacts.MasterThievesArmband.class})
				|| !SpsPrisonMobs.GoldCollector.class.isAssignableFrom(GoldCollector.class)) {
			throw new AssertionError("gold collector identity, faction, scaling, or special loot differs from SPS");
		}

		FireRabbit rabbit = new FireRabbit();
		if (rabbit.HT < 104 || rabbit.HT > 120 || rabbit.defenseSkill != 16
				|| !rabbit.properties().contains(pd.actors.Char.Property.ORC)
				|| rabbit.properties().contains(pd.actors.Char.Property.FIERY)
				|| rabbit.spriteClass != pd.sprites.FireRabbitSprite.class
				|| FireRabbit.specialLootCategory() != pd.items.Generator.Category.FOOD
				|| !rabbit.isImmune(pd.actors.buffs.Locked.class)
				|| !rabbit.isImmune(Burning.class)
				|| !rabbit.isImmune(pd.actors.damagetype.DamageType.Fire.class)
				|| !rabbit.isImmune(pd.actors.blobs.effectblobs.Fire.class)
				|| !rabbit.isImmune(pd.items.wands.WandOfFirebolt.class)
				|| !SpsPrisonMobs.FireRabbit.class.isAssignableFrom(FireRabbit.class)) {
			throw new AssertionError("fire rabbit identity, faction, defenses, or special loot differ from SPS");
		}

		TrollWarrior troll = new TrollWarrior();
		if (troll.HT < 90 || troll.HT > 105 || troll.defenseSkill != 15
				|| !troll.properties().contains(pd.actors.Char.Property.TROLL)
				|| troll.spriteClass != pd.sprites.TrollWarriorSprite.class
				|| TrollWarrior.specialLootCategory() != pd.items.Generator.Category.MUSICWEAPON
				|| troll.resist(pd.items.weapon.enchantments.EnchantmentDark.class) >= 1f
				|| !SpsPrisonMobs.TrollWarrior.class.isAssignableFrom(TrollWarrior.class)) {
			throw new AssertionError("troll warrior identity, faction, defense, or special loot differs from SPS");
		}
		if (!specialLootMatches(pd.items.Generator.Category.MUSICWEAPON.classes,
				new Class[]{
						pd.items.weapon.melee.fusion.Triangolo.class,
						pd.items.weapon.melee.fusion.Flute.class,
						pd.items.weapon.melee.fusion.WarDrum.class,
						pd.items.weapon.melee.fusion.Trumpet.class,
						pd.items.weapon.melee.fusion.Harp.class})) {
			throw new AssertionError("music weapon reward pool differs from SPS");
		}

		Zombie zombie = new Zombie();
		if (zombie.HT < 94 || zombie.HT > 126 || zombie.defenseSkill != 13
				|| !zombie.properties().contains(pd.actors.Char.Property.UNDEAD)
				|| zombie.spriteClass != pd.sprites.ZombieSprite.class
				|| Zombie.specialLootType() != pd.items.UnBlessAnkh.class
				|| !zombie.isWeak(Burning.class)
				|| !zombie.isWeak(pd.items.wands.WandOfFirebolt.class)
				|| zombie.resist(pd.actors.blobs.ToxicGas.class) >= 1f
				|| !SpsPrisonMobs.Zombie.class.isAssignableFrom(Zombie.class)) {
			throw new AssertionError("zombie identity, faction, defenses, or special loot differs from SPS");
		}
	}

	private static void checkSewerStatsAndSafety() {
		Dungeon.depth = 3;
		DustElement dust = new DustElement();
		if (dust.HT < 38 || dust.HT > 44 || dust.defenseSkill != 5 || dust.attackSkill(null) != 14
				|| !dust.properties().contains(pd.actors.Char.Property.ELEMENT)
				|| dust.properties().contains(pd.actors.Char.Property.INORGANIC)
				|| dust.spriteClass != pd.sprites.DustElementSprite.class
				|| DustElement.specialLootCategory() != pd.items.Generator.Category.NORNSTONE
				|| !SpsSewerMobs.DustElement.class.isAssignableFrom(DustElement.class)) {
			throw new AssertionError("dust elemental stats, identity, faction, or special loot differ from SPS");
		}
		DamageProbe probe = new DamageProbe();
		dust.attackProc(probe, 4);
		if (probe.lastSource != pd.actors.damagetype.DamageType.EARTH_DAMAGE) {
			throw new AssertionError("dust elemental did not use the SPS earth damage source");
		}

		RatBoss ratBoss = new RatBoss();
		if (!ratBoss.properties().contains(pd.actors.Char.Property.BEAST)
				|| !ratBoss.properties().contains(pd.actors.Char.Property.BOSS)
				|| ratBoss.spriteClass != pd.sprites.RatBossSprite.class
				|| RatBoss.specialLootType() != pd.items.SaveYourLife.class
				|| !SpsSewerMobs.RatBoss.class.isAssignableFrom(RatBoss.class)) {
			throw new AssertionError("leader rat identity, factions, or special loot differ from SPS");
		}

		Shit toiletElf = new Shit();
		if (toiletElf.attackSkill(null) != 13
				|| !toiletElf.properties().contains(pd.actors.Char.Property.ELF)
				|| toiletElf.spriteClass != pd.sprites.ShitSprite.class
				|| Shit.specialLootType() != pd.items.potions.PotionOfToxicGas.class
				|| !SpsSewerMobs.Shit.class.isAssignableFrom(Shit.class)) {
			throw new AssertionError("toilet elf identity, faction, scaling, or special loot differ from SPS");
		}

		LiveMoss moss = new LiveMoss();
		if (!moss.properties().contains(pd.actors.Char.Property.PLANT)
				|| moss.spriteClass != pd.sprites.LiveMossSprite.class
				|| !specialLootCategoriesMatch(LiveMoss.specialLootCategories(), new pd.items.Generator.Category[]{
				pd.items.Generator.Category.POTION,
				pd.items.Generator.Category.SUMMONED})
				|| !SpsSewerMobs.LiveMoss.class.isAssignableFrom(LiveMoss.class)) {
			throw new AssertionError("living moss identity, faction, or special loot differ from SPS");
		}

		PatrolUAV drone = new PatrolUAV();
		if (!drone.properties().contains(pd.actors.Char.Property.MECH)
				|| drone.properties().contains(pd.actors.Char.Property.INORGANIC)
				|| drone.spriteClass != pd.sprites.PatrolUAVSprite.class
				|| !drone.isImmune(pd.actors.blobs.effectblobs.ElectriShock.class)
				|| !drone.isImmune(pd.items.wands.WandOfLightning.class)
				|| !specialLootMatches(PatrolUAV.specialLootTypes(), new Class[]{
				pd.items.scrolls.ScrollOfRecharging.class,
				pd.items.wands.WandOfTCloud.class})
				|| !SpsSewerMobs.PatrolUAV.class.isAssignableFrom(PatrolUAV.class)) {
			throw new AssertionError("patrol drone identity, faction, defenses, or special loot differ from SPS");
		}

		Vagrant vagrant = new Vagrant();
		ExVagrant infected = new ExVagrant();
		if (!vagrant.properties().contains(pd.actors.Char.Property.HUMAN)
				|| vagrant.spriteClass != pd.sprites.VagrantSprite.class
				|| Vagrant.specialLootType() != pd.items.weapon.melee.special.SJRBMusic.class
				|| !infected.properties().contains(pd.actors.Char.Property.HUMAN)
				|| infected.spriteClass != pd.sprites.ExVagrantSprite.class
				|| ExVagrant.specialLootType() != pd.items.weapon.melee.special.SJRBMusic.class
				|| !SpsSewerMobs.Vagrant.class.isAssignableFrom(Vagrant.class)
				|| !SpsSewerMobs.ExVagrant.class.isAssignableFrom(ExVagrant.class)) {
			throw new AssertionError("vagrant identities, factions, or special loot differ from SPS");
		}
		ExVagrantProbe regeneration = new ExVagrantProbe();
		regeneration.HP = 50;
		regeneration.takeTurn();
		if (regeneration.HP != 80) {
			throw new AssertionError("infected vagrant lost its inherited 20 plus 10 regeneration order");
		}
		regeneration.HP = 95;
		regeneration.takeTurn();
		if (regeneration.HP != 115) {
			throw new AssertionError("infected vagrant no longer preserves the source over-heal boundary");
		}
		Buff.affect(regeneration, BeOld.class).set(5f);
		regeneration.HP = 50;
		regeneration.takeTurn();
		if (regeneration.HP != 50) {
			throw new AssertionError("aged infected vagrant still regenerated");
		}
	}

	private static void checkCaveStatsAndSafety() {
		Dungeon.depth = 13;
		GnollShaman shaman = new GnollShaman();
		if (shaman.HT < 106 || shaman.HT > 145 || shaman.defenseSkill != 28
				|| shaman.attackSkill(null) != 29
				|| !shaman.properties().contains(pd.actors.Char.Property.ORC)
				|| !shaman.properties().contains(pd.actors.Char.Property.MAGICER)
				|| shaman.spriteClass != pd.sprites.GnollShamanSprite.class
				|| !specialLootMatches(GnollShaman.specialLootTypes(), new Class[]{
				pd.items.scrolls.ScrollOfRegrowth.class,
				pd.items.potions.PotionOfLevitation.class,
				pd.items.artifacts.SandalsOfNature.class})
				|| !SpsCaveMobs.GnollShaman.class.isAssignableFrom(GnollShaman.class)) {
			throw new AssertionError("cave shaman stats, identity, factions, or special loot differ from SPS");
		}
		DamageProbe damageProbe = new DamageProbe();
		shaman.attackProc(damageProbe, 4);
		if (damageProbe.lastSource != pd.actors.damagetype.DamageType.SHOCK_DAMAGE) {
			throw new AssertionError("cave shaman did not use the SPS shock damage source");
		}

		SandMob sand = new SandMob();
		if (!sand.properties().contains(pd.actors.Char.Property.ELEMENT)
				|| sand.spriteClass != pd.sprites.SandmobSprite.class
				|| SandMob.specialLootCategory() != pd.items.Generator.Category.GOLD
				|| sand.newMiniSand().getClass() != SandMob.MiniSand.class
				|| !SpsCaveMobs.SandMob.class.isAssignableFrom(SandMob.class)) {
			throw new AssertionError("sand creature identity, faction, split, or special loot differs from SPS");
		}

		TimeKeeper keeper = new TimeKeeper();
		if (!keeper.properties().contains(pd.actors.Char.Property.UNKNOW)
				|| !keeper.properties().contains(pd.actors.Char.Property.MAGICER)
				|| keeper.spriteClass != pd.sprites.TimeKeeperSprite.class
				|| !specialLootMatches(TimeKeeper.specialLootTypes(), new Class[]{
				pd.items.medicine.Timepill2.class,
				pd.items.potions.PotionOfMindVision.class,
				pd.items.artifacts.TimekeepersHourglass.class})
				|| !SpsCaveMobs.TimeKeeper.class.isAssignableFrom(TimeKeeper.class)) {
			throw new AssertionError("timekeeper identity, factions, or special loot differs from SPS");
		}
		damageProbe.lastSource = null;
		keeper.attackProc(damageProbe, 4);
		if (damageProbe.lastSource != pd.actors.damagetype.DamageType.ENERGY_DAMAGE) {
			throw new AssertionError("timekeeper did not use the SPS energy damage source");
		}

		IceBug ice = new IceBug();
		if (!ice.properties().contains(pd.actors.Char.Property.BEAST)
				|| ice.properties().contains(pd.actors.Char.Property.ICY)
				|| ice.spriteClass != pd.sprites.IceBugSprite.class
				|| IceBug.specialLootType() != pd.plants.Icecap.Seed.class
				|| !ice.isImmune(pd.actors.buffs.FrostIce.class)
				|| !ice.isImmune(pd.items.weapon.enchantments.EnchantmentIce.class)
				|| !ice.isImmune(pd.items.weapon.enchantments.EnchantmentIce2.class)
				|| !SpsCaveMobs.IceBug.class.isAssignableFrom(IceBug.class)) {
			throw new AssertionError("ice climber identity, faction, defenses, or special loot differs from SPS");
		}
	}

	private static void checkBlueCat() {
		Dungeon.depth = 6;
		BlueCat cat = new BlueCat();
		if (cat.HT < 38 || cat.HT > 50 || cat.defenseSkill != 14 || cat.attackSkill(null) != 120
				|| cat.attackDelay() != 0.5f || cat.drRoll() != 3) {
			throw new AssertionError("blue cat stats differ from SPS");
		}
		if (!cat.properties().contains(pd.actors.Char.Property.ELF)
				|| cat.spriteClass != pd.sprites.BanditKingSprite.class) {
			throw new AssertionError("blue cat faction or shared source sprite is missing");
		}
		if (Math.abs(BlueCat.armbandChance(0) - 0.01f) > 0.0001f
				|| Math.abs(BlueCat.armbandChance(10) - 0.21f) > 0.0001f) {
			throw new AssertionError("blue cat armband drop chance is wrong");
		}
		pd.actors.hero.Hero hero =
				new pd.actors.hero.Hero();
		pd.items.Amulet amulet =
				new pd.items.Amulet();
		hero.belongings.backpack.items.add(amulet);
		if (!cat.steal(hero) || cat.item != amulet || hero.belongings.getItem(
				pd.items.Amulet.class) != null) {
			throw new AssertionError("blue cat did not steal and retain the amulet");
		}
		com.watabou.utils.Bundle saved = new com.watabou.utils.Bundle();
		cat.storeInBundle(saved);
		BlueCat restored = new BlueCat();
		restored.restoreFromBundle(saved);
		if (!(restored.item instanceof pd.items.Amulet)) {
			throw new AssertionError("blue cat stolen amulet did not survive save restore");
		}
	}

	private static void checkBanditKing() {
		Dungeon.branch = 0;
		Dungeon.depth = 9;
		BanditKing king = new BanditKing();
		if (king.HT != 300 || king.defenseSkill != 20 || king.speed() != 2f || !king.flying
				|| king.damageRoll() != 1 || king.attackSkill(null) != 0 || king.maxLvl != 25) {
			throw new AssertionError("life bandit stats differ from SPS");
		}
		if (!king.properties().contains(pd.actors.Char.Property.ELF)
				|| !king.properties().contains(pd.actors.Char.Property.MINIBOSS)
				|| king.spriteClass != pd.sprites.BanditKingSprite.class) {
			throw new AssertionError("life bandit factions or original sprite identity are missing");
		}
		if (!SpsPrisonMobs.BanditKing.class.isAssignableFrom(BanditKing.class)) {
			throw new AssertionError("life bandit compatibility implementation is disconnected");
		}
	}

	private static void checkLegacyBranchScaling() {
		Dungeon.depth = 25;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(22);
		if (Dungeon.legacyDepth() != 85) {
			throw new AssertionError("混沌领域没有使用旧版85层数值深度");
		}

		GreyRat rat = new GreyRat();
		if (rat.defenseSkill != 93 || rat.attackSkill(null) != 91
				|| rat.HT < 335 || rat.HT > 505) {
			throw new AssertionError("灰鼠没有按异界旧版有效深度成长");
		}
		BlueCat cat = new BlueCat();
		Shit toiletElf = new Shit();
		SpsDM300.Tower tower = new SpsDM300.Tower();
		if (cat.defenseSkill != 93 || cat.HT < 275 || cat.HT > 445
				|| toiletElf.attackSkill(null) != 95
				|| tower.HT < 670 || tower.HT > 925) {
			throw new AssertionError("旧版特殊怪物仍按异界入口锚点成长");
		}

		com.watabou.utils.Random.pushGenerator(0x5350534445505448L);
		try {
			for (int i = 0; i < 4096; i++) {
				int amount = new pd.items.Gold().random().quantity();
				if (amount < 880 || amount >= 1760) {
					throw new AssertionError("混沌领域金币数量越过旧版半开区间：" + amount);
				}
			}
		} finally {
			com.watabou.utils.Random.popGenerator();
		}

		SommonSkeleton skeleton = new SommonSkeleton();
		skeleton.adjustStats(Dungeon.legacyDepth());
		skeleton.HP = skeleton.HT - 17;
		int savedHp = skeleton.HP;
		int savedHt = skeleton.HT;
		com.watabou.utils.Bundle bundle = new com.watabou.utils.Bundle();
		skeleton.storeInBundle(bundle);
		SommonSkeleton restored = new SommonSkeleton();
		restored.restoreFromBundle(bundle);
		if (restored.HP != savedHp || restored.HT != savedHt || restored.HP == restored.HT) {
			throw new AssertionError("召唤骷髅读档后生命被重掷或回满");
		}

		Dungeon.depth = pd.items.quest.AdventureJournal.anchorDepth(18);
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(18);
		Dungeon.sporkAvailable = true;
		new BanditKing();
		if (Dungeon.legacyDepth() != 41 || SpsPrisonMobs.BanditKing.grantsSpork()
				|| !Dungeon.sporkAvailable) {
			throw new AssertionError("盗贼追捕关被误判为主线早期叉勺奖励区域");
		}

		Dungeon.depth = 9;
		Dungeon.branch = 0;
		if (!SpsPrisonMobs.BanditKing.grantsSpork()) {
			throw new AssertionError("主线早期盗贼王不再提供旧版叉勺奖励");
		}
	}

	private static void checkBrownBat() {
		Dungeon.depth = 3;
		BrownBat bat = new BrownBat();
		if (bat.HT != 20 || bat.defenseSkill != 1 || bat.speed() != 2f
				|| !bat.flying || bat.attackSkill(null) != 8 || bat.drRoll() != 1) {
			throw new AssertionError("brown bat stats differ from SPS");
		}
		if (!bat.properties().contains(pd.actors.Char.Property.BEAST)
				|| bat.spriteClass != pd.sprites.BrownBatSprite.class) {
			throw new AssertionError("brown bat faction or original sprite identity is missing");
		}
		if (BrownBat.specialLootCategory() != pd.items.Generator.Category.SEED4
				|| !SpsSewerMobs.BrownBat.class.isAssignableFrom(BrownBat.class)) {
			throw new AssertionError("brown bat special loot or compatibility base is wrong");
		}
	}

	private static void checkBrokenRobot() {
		Dungeon.depth = 13;
		BrokenRobot robot = new BrokenRobot();
		if (robot.HT < 172 || robot.HT > 211 || robot.defenseSkill != 26
				|| robot.attackSkill(null) != 33 || robot.viewDistance != 6) {
			throw new AssertionError("broken robot depth scaling or sight differs from SPS");
		}
		if (!robot.properties().contains(pd.actors.Char.Property.MAGICER)
				|| !robot.properties().contains(pd.actors.Char.Property.MECH)
				|| robot.properties().contains(pd.actors.Char.Property.INORGANIC)) {
			throw new AssertionError("broken robot faction tags differ from SPS");
		}
		if (Math.abs(BrokenRobot.legacyDropChance(0) - 0.25f) > 0.0001f
				|| Math.abs(BrokenRobot.legacyDropChance(10) - 0.45f) > 0.0001f) {
			throw new AssertionError("broken robot legacy dual-drop chance is wrong");
		}
		Class<?>[] loot = BrokenRobot.specialLootTypes();
		if (loot.length != 3
				|| loot[0] != pd.items.potions.PotionOfLiquidFlame.class
				|| loot[1] != pd.items.weapon.melee.normalweapon.ShortSword.class
				|| loot[2] != pd.items.artifacts.RobotDMT.class) {
			throw new AssertionError("broken robot special loot does not match its three-item source pool");
		}
		if (!SpsDM300.BrokenRobot.class.isAssignableFrom(BrokenRobot.class)) {
			throw new AssertionError("broken robot compatibility implementation is disconnected");
		}
	}

	private static void checkHallsStatsAndSafety() {
		Dungeon.depth = 23;
		DemonGoo goo = new DemonGoo();
		if (goo.HT < 392 || goo.HT > 461 || goo.attackSkill(null) != 46) {
			throw new AssertionError("demon goo depth scaling is not the SPS formula");
		}
		if (!goo.properties().contains(pd.actors.Char.Property.ELEMENT)
				|| !goo.properties().contains(pd.actors.Char.Property.DEMONIC)
				|| goo.spriteClass != pd.sprites.DemonGooSprite.class
				|| goo.newSplit().getClass() != DemonGoo.class
				|| !(goo.SupercreateLoot() instanceof pd.items.weapon.missiles.throwing.Skull)
				|| !SpsHallsMobs.DemonGoo.class.isAssignableFrom(DemonGoo.class)) {
			throw new AssertionError("demon goo identity, faction, split, or special loot differs from SPS");
		}
		DemonFlower flower = new DemonFlower();
		if (!flower.properties().contains(pd.actors.Char.Property.PLANT)
				|| !flower.properties().contains(pd.actors.Char.Property.DEMONIC)
				|| !flower.isWeak(Burning.class)
				|| flower.spriteClass != pd.sprites.DemonflowerSprite.class
				|| DemonFlower.specialLootCategory() != pd.items.Generator.Category.PILL
				|| !SpsHallsMobs.DemonFlower.class.isAssignableFrom(DemonFlower.class)) {
			throw new AssertionError("demon flower identity, faction, weakness, or special loot differs from SPS");
		}
		ThiefImp imp = new ThiefImp();
		if (imp.HT < 292 || imp.HT > 361 || imp.attackSkill(null) != 53) {
			throw new AssertionError("thief imp depth scaling is not the SPS formula");
		}
		if (!imp.properties().contains(pd.actors.Char.Property.DEMONIC)
				|| imp.properties().contains(pd.actors.Char.Property.PLANT)
				|| imp.spriteClass != pd.sprites.ThiefImpSprite.class
				|| !specialLootMatches(ThiefImp.specialLootTypes(), new Class[]{
				pd.items.potions.PotionOfInvisibility.class,
				pd.items.scrolls.ScrollOfRage.class,
				pd.items.artifacts.ChaliceOfBlood.class})
				|| !SpsHallsMobs.ThiefImp.class.isAssignableFrom(ThiefImp.class)) {
			throw new AssertionError("thief imp identity, faction, or special loot differs from SPS");
		}
		Sufferer sufferer = new Sufferer();
		if (sufferer.HT < 272 || sufferer.HT > 341 || sufferer.attackSkill(null) != 45) {
			throw new AssertionError("sufferer depth scaling is not the SPS formula");
		}
		if (!sufferer.properties().contains(pd.actors.Char.Property.DEMONIC)
				|| !sufferer.properties().contains(pd.actors.Char.Property.MAGICER)
				|| !sufferer.properties().contains(pd.actors.Char.Property.HUMAN)
				|| sufferer.properties().contains(pd.actors.Char.Property.ORC)
				|| sufferer.spriteClass != pd.sprites.SuffererSprite.class
				|| !specialLootMatches(Sufferer.specialLootTypes(), new Class[]{
				pd.items.scrolls.ScrollOfUpgrade.class,
				pd.items.RedDewdrop.class,
				pd.items.artifacts.UnstableSpellbook.class})
				|| !SpsHallsMobs.Sufferer.class.isAssignableFrom(Sufferer.class)) {
			throw new AssertionError("sufferer identity, factions, or special loot differs from SPS");
		}
		DamageProbe damageProbe = new DamageProbe();
		sufferer.attackProc(damageProbe, 1);
		if (damageProbe.lastSource != pd.actors.damagetype.DamageType.DARK_DAMAGE) {
			throw new AssertionError("sufferer did not use the SPS dark damage source");
		}
		for (int i = 0; i < 200; i++) {
			int damage = sufferer.damageRoll();
			if (damage < 21 || damage > 30) throw new AssertionError("sufferer used a reversed random interval");
		}
		DemonRabbit rabbit = new DemonRabbit();
		if (rabbit.HT < 269 || rabbit.HT > 315 || rabbit.attackSkill(null) != 36) {
			throw new AssertionError("demon rabbit depth scaling is not the SPS formula");
		}
		if (!rabbit.properties().contains(pd.actors.Char.Property.ORC)
				|| !rabbit.properties().contains(pd.actors.Char.Property.DEMONIC)
				|| rabbit.spriteClass != pd.sprites.DemonRabbitSprite.class
				|| !(rabbit.SupercreateLoot() instanceof pd.items.weapon.guns.GunD)
				|| !SpsHallsMobs.DemonRabbit.class.isAssignableFrom(DemonRabbit.class)) {
			throw new AssertionError("demon rabbit identity, faction, or special loot differs from SPS");
		}
	}

	private static void checkCityStatsAndSafety() {
		Dungeon.depth = 18;
		FireElemental fire = new FireElemental();
		if (fire.HT < 192 || fire.HT > 246 || fire.defenseSkill != 38
				|| fire.attackSkill(null) != 34 || !fire.flying
				|| !fire.properties().contains(pd.actors.Char.Property.ELEMENT)
				|| !fire.properties().contains(pd.actors.Char.Property.MAGICER)
				|| fire.properties().contains(pd.actors.Char.Property.FIERY)
				|| fire.spriteClass != pd.sprites.FireElementalSprite.class
				|| FireElemental.specialLootType() != pd.plants.Firebloom.Seed.class
				|| !fire.isImmune(pd.actors.blobs.effectblobs.Fire.class)
				|| !fire.isImmune(pd.actors.damagetype.DamageType.Fire.class)
				|| fire.resist(pd.items.wands.WandOfFirebolt.class) >= 1f
				|| fire.resist(pd.items.weapon.enchantments.EnchantmentFire.class) >= 1f
				|| fire.resist(pd.items.weapon.enchantments.EnchantmentFire2.class) >= 1f) {
			throw new AssertionError("fire elemental stats, identity, factions, defenses, or special loot differ from SPS");
		}
		DamageProbe fireProbe = new DamageProbe();
		fire.attackProc(fireProbe, 1);
		if (fireProbe.lastSource != pd.actors.damagetype.DamageType.FIRE_DAMAGE) {
			throw new AssertionError("fire elemental did not use the SPS fire damage source");
		}
		int wounded = fire.HT - 20;
		fire.HP = wounded;
		if (fire.add(new Burning()) || fire.HP <= wounded) {
			throw new AssertionError("fire elemental did not absorb burning as healing");
		}

		GreyRat greyRat = new GreyRat();
		if (greyRat.HT < 134 || greyRat.HT > 170 || greyRat.defenseSkill != 26
				|| greyRat.attackSkill(null) != 24 || greyRat.attackDelay() != 0.8f
				|| !greyRat.properties().contains(pd.actors.Char.Property.BEAST)
				|| greyRat.spriteClass != pd.sprites.GreyRatSprite.class
				|| !greyRat.isImmune(Burning.class)
				|| !greyRat.isImmune(pd.items.scrolls.ScrollOfPsionicBlast.class)
				|| greyRat.resist(pd.actors.blobs.ToxicGas.class) >= 1f) {
			throw new AssertionError("grey rat stats, identity, faction, or defenses differ from SPS");
		}
		if (new DemonSummoner().spriteClass != pd.sprites.ErrorSprite.class
				|| new LotusSummoner().spriteClass != pd.sprites.ErrorSprite.class) {
			throw new AssertionError("dormant SPS summoner save types are not constructible");
		}

		DragonRider dragon = new DragonRider();
		if (dragon.HT < 230 || dragon.HT > 266 || dragon.attackSkill(null) != 45) {
			throw new AssertionError("dragon rider depth scaling is not the SPS formula");
		}
		if (!dragon.properties().contains(pd.actors.Char.Property.DRAGON)
				|| dragon.spriteClass != pd.sprites.DragonRiderSprite.class
				|| DragonRider.specialLootType() != pd.items.eggs.randomone.RandomMonthEgg.class
				|| !SpsCityMobs.DragonRider.class.isAssignableFrom(DragonRider.class)) {
			throw new AssertionError("dragon rider identity, faction, or special loot differs from SPS");
		}
		SpiderBot spider = new SpiderBot();
		if (spider.HT < 222 || spider.HT > 276 || spider.attackSkill(null) != 48) {
			throw new AssertionError("spider bot depth scaling is not the SPS formula");
		}
		if (!spider.properties().contains(pd.actors.Char.Property.BEAST)
				|| spider.spriteClass != pd.sprites.SpiderBotSprite.class
				|| !(spider.createLoot() instanceof pd.items.food.meatfood.Meat)
				|| SpiderBot.specialLootType() != pd.items.food.BugMeat.class
				|| !SpsCityMobs.SpiderBot.class.isAssignableFrom(SpiderBot.class)) {
			throw new AssertionError("scavenger identity, faction, normal loot, or special loot differs from SPS");
		}
		Musketeer musketeer = new Musketeer();
		if (musketeer.HT < 214 || musketeer.HT > 250 || musketeer.attackSkill(null) != 44) {
			throw new AssertionError("musketeer depth scaling is not the SPS formula");
		}
		if (!musketeer.properties().contains(pd.actors.Char.Property.DWARF)
				|| musketeer.spriteClass != pd.sprites.MusketeerSprite.class
				|| Musketeer.specialLootType() != pd.items.weapon.guns.ToyGun.class
				|| !SpsCityMobs.Musketeer.class.isAssignableFrom(Musketeer.class)) {
			throw new AssertionError("musketeer identity, faction, or special loot differs from SPS");
		}
		if (SpsCityMobs.riderPool().length != 32) {
			throw new AssertionError("dragon rider death pool must contain all 32 legacy candidates");
		}
		for (Class<?> type : SpsCityMobs.riderPool()) {
			if (type == SpsCityMobs.DragonRider.class || type == SpsCityMobs.SpiderBot.class
					|| type == SpsCityMobs.Musketeer.class) {
				throw new AssertionError("dragon rider death pool still creates a migration-only city mob type");
			}
		}
		ManySkeleton skeletons = new ManySkeleton();
		if (skeletons.HT != 100 || skeletons.attackSkill(null) != 39 || !skeletons.flying
				|| !skeletons.properties().contains(pd.actors.Char.Property.UNDEAD)
				|| skeletons.spriteClass != pd.sprites.ManySkeletonSprite.class
				|| !SpsCityMobs.ManySkeleton.class.isAssignableFrom(ManySkeleton.class)) {
			throw new AssertionError("huge skull stats, identity, or undead faction differs from SPS");
		}
		LevelChecker checker = new LevelChecker();
		if (!checker.properties().contains(pd.actors.Char.Property.MECH)
				|| checker.properties().contains(pd.actors.Char.Property.INORGANIC)
				|| !checker.isWeak(pd.items.wands.Wand.class)
				|| checker.spriteClass != pd.sprites.LevelCheckerSprite.class
				|| LevelChecker.specialLootType() != pd.items.ExpOre.class
				|| !SpsCityMobs.LevelChecker.class.isAssignableFrom(LevelChecker.class)) {
			throw new AssertionError("adjudicator identity, machine faction, weakness, or special loot differs from SPS");
		}
		Greatmoss moss = new Greatmoss();
		if (!moss.properties().contains(pd.actors.Char.Property.PLANT)
				|| moss.loot != pd.items.Generator.Category.MUSHROOM
				|| moss.spriteClass != pd.sprites.GreatMossSprite.class
				|| !SpsCityMobs.GreatMoss.class.isAssignableFrom(Greatmoss.class)) {
			throw new AssertionError("great moss identity, faction, or mushroom loot differs from SPS");
		}
		RedWraith wraith = new RedWraith();
		if (!wraith.properties().contains(pd.actors.Char.Property.UNDEAD)
				|| !wraith.isImmune(pd.items.scrolls.ScrollOfPsionicBlast.class)
				|| wraith.spriteClass != pd.sprites.RedWraithSprite.class
				|| !SpsCityMobs.RedWraith.class.isAssignableFrom(RedWraith.class)) {
			throw new AssertionError("chaos wraith identity, faction, or psionic immunity differs from SPS");
		}
		Hero hero = new Hero();
		hero.lvl = 1;
		if (LevelDown.reduceLevel(hero) || hero.lvl != 1) {
			throw new AssertionError("growth core reduced the hero below level one");
		}
	}

	private static void checkDeck(int depth, Class<?>[] classes, int[] expected) {
		ArrayList<Class<? extends Mob>> deck = MobSpawner.standardMobRotation(depth);
		Map<Class<?>, Integer> counts = new LinkedHashMap<>();
		for (Class<?> type : deck) counts.put(type, counts.getOrDefault(type, 0) + 1);
		if (counts.size() != classes.length) throw new AssertionError("depth " + depth + " extra mob type: " + counts);
		for (int i = 0; i < classes.length; i++) {
			if (counts.getOrDefault(classes[i], 0) != expected[i]) {
				throw new AssertionError("depth " + depth + " wrong count for " + classes[i].getSimpleName());
			}
		}
	}

	private static boolean specialLootMatches(Class<?>[] actual, Class<?>[] expected) {
		if (actual.length != expected.length) return false;
		for (int i = 0; i < actual.length; i++) if (actual[i] != expected[i]) return false;
		return true;
	}

	private static boolean specialLootCategoriesMatch(
			pd.items.Generator.Category[] actual,
			pd.items.Generator.Category[] expected) {
		if (actual.length != expected.length) return false;
		for (int i = 0; i < actual.length; i++) if (actual[i] != expected[i]) return false;
		return true;
	}

	private static final class DamageProbe extends Hero {
		private Object lastSource;
		private int lastDamage;

		@Override
		public void damage(int damage, Object src) {
			lastSource = src;
			lastDamage = damage;
		}
	}

	private static final class GnollProbe extends Gnoll {
		boolean canReach(Char target) { return canAttack(target); }
	}

	private static final class WarlockProbe extends Warlock {
		boolean canReach(Char target) { return canAttack(target); }
	}

	private static final class EyeProbe extends Eye {
		boolean canReach(Char target) { return canAttack(target); }
	}

	private static final class ScorpioProbe extends Scorpio {
		boolean canReach(Char target) { return canAttack(target); }
	}

	private static final class FiendProbe extends Fiend {
		private int lastDamageTaken;
		boolean canReach(Char target) { return canAttack(target); }
		int collision(Char target) { return new pd.mechanics.Ballistica(
				pos, target.pos, pd.mechanics.Ballistica.MAGIC_BOLT).collisionPos; }
		@Override public void damage(int damage, Object src) { lastDamageTaken = damage; }
	}

	private static final class WraithProbe extends Wraith {
		boolean canReach(Char target) { return canAttack(target); }
	}

	private static final class BanditProbe extends Bandit {
		boolean canReach(Char target) { return canAttack(target); }
		boolean takeTurn() { return act(); }
		void emitDarkGas() { seedDarkGas(); }
	}

	private static final class SeniorProbe extends Senior {
		boolean takeTurn() { return act(); }
	}

	private static final class ExVagrantProbe extends ExVagrant {
		@Override protected boolean actAfterRegeneration() { return true; }
		boolean takeTurn() { return act(); }
	}

	private static final class BambooInheritanceProbe extends ExBambooMob {
		private int buffRolls;
		private int retaliationRolls;
		@Override protected void tryDefenseBuff() { buffRolls++; }
		@Override protected void retaliate(Char enemy, int damage) { retaliationRolls++; }
	}

	private static final class ExplosionProbe extends Mob {
		{ HP = HT = 100; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(5, 5);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private static final class SpeedProbe extends Mob {
		private void spendForTest(float time) {
			spend(time);
		}
	}

	private static final class LootProbe extends Mob {
		boolean levelEligible() { return legacyLootLevelEligible(); }
		float secondaryChance(float base) { return legacySecondaryLootChance(base); }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
	}
}
