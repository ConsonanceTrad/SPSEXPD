package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HolyStun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.VioletDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DewTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.LightningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsLegacyTrapTest {
	private SpsLegacyTrapTest() { }

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Dungeon.hero = new Hero();

		Rat target = new Rat();
		target.HP = target.HT = 90;
		LightningTrap trap = new LightningTrap();
		trap.pos = 20;
		Random.pushGenerator(0x5350534C49474854L);
		try {
			int before = target.HP;
			trap.activate(target);
			int damage = before - target.HP;
			check(damage >= 30 && damage < 60, "闪电陷阱生命比例伤害错误：" + damage);
		} finally {
			Random.popGenerator();
		}

		WandOfMagicMissile wand = new WandOfMagicMissile();
		wand.maxCharges = 7;
		wand.curCharges = 0;
		level.drop(wand, trap.pos);
		trap.activate(null);
		check(wand.curCharges == 4, "闪电陷阱未恢复一半缺失法杖充能：" + wand.curCharges);
		check(trap.color == LightningTrap.TEAL && trap.shape == LightningTrap.DIAMOND,
				"闪电陷阱图形标识错误");

		testStatusAndElementTraps(level);
		testSummonTraps(level);
		testGuardianTrap(level);
		testLegacyTeleportTraps(level);
		testLegacyEquipmentTraps(level);
		testLegacyDamageTraps(level);
		testLegacyHazardTraps(level);
		testLegacyBranchDepth(level);
		testLegacyChasm(level);
		testLegacyDewEdge(level);
		System.out.println("SPS旧版陷阱测试通过：伤害、状态、召唤、守卫、扭曲、传送与装备规则均正常。");
	}

	private static void testStatusAndElementTraps(RecordingLevel level) {
		Actor.clear();
		level.heaps.clear();
		Dungeon.depth = 12;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 40;
		Actor.add(Dungeon.hero);

		GrippingTrap gripping = new GrippingTrap();
		gripping.pos = Dungeon.hero.pos;
		Random.pushGenerator(0x5350534752495050L);
		try {
			gripping.activate();
		} finally {
			Random.popGenerator();
		}
		check(gripping.color == GrippingTrap.GREY && gripping.shape == GrippingTrap.DIAMOND,
				"夹伤陷阱图形标识错误");
		check(Dungeon.hero.buff(Cripple.class) != null
				&& Dungeon.hero.buff(Cripple.class).cooldown() == 15f
				&& Dungeon.hero.buff(Roots.class) != null
				&& Dungeon.hero.buff(Roots.class).cooldown() == 5f,
				"夹伤陷阱没有施加15回合残废和5回合缠绕");

		Dungeon.hero.pos = 41;
		Heap lightHeap = level.drop(new Sungrass.Seed(), Dungeon.hero.pos);
		FlashingTrap flashing = new FlashingTrap();
		flashing.pos = Dungeon.hero.pos;
		Random.pushGenerator(0x535053464C415348L);
		try {
			flashing.activate();
		} finally {
			Random.popGenerator();
		}
		check(flashing.color == FlashingTrap.GREY && flashing.shape == FlashingTrap.ONE_DOT,
				"闪光陷阱没有恢复灰色单点标识");
		check(level.heaps.get(lightHeap.pos) == null, "闪光陷阱没有执行光属性物品堆反应");
		check(Dungeon.hero.buff(Blindness.class) != null
				&& Dungeon.hero.buff(Blindness.class).cooldown() >= 17f
				&& Dungeon.hero.buff(Blindness.class).cooldown() <= 21f,
				"闪光陷阱失明时长没有按5至9加深度计算");

		Dungeon.hero.pos = 42;
		Heap earthHeap = level.drop(new StoneOre(), Dungeon.hero.pos);
		WeakeningTrap weakening = new WeakeningTrap();
		weakening.pos = Dungeon.hero.pos;
		weakening.activate();
		check(weakening.color == WeakeningTrap.WHITE && weakening.shape == WeakeningTrap.DIAMOND,
				"削弱陷阱图形标识错误");
		check(Dungeon.hero.buff(STRDown.class) != null
				&& Dungeon.hero.buff(STRDown.class).cooldown() == 10f,
				"削弱陷阱没有对英雄施加10回合力量削弱");
		check(level.heaps.get(earthHeap.pos) == null, "削弱陷阱没有执行地属性物品堆反应");
	}

	private static void testSummonTraps(RecordingLevel level) {
		Actor.clear();
		level.mobs.clear();
		level.heaps.clear();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 16;
		Actor.add(Dungeon.hero);

		Dungeon.depth = 9;
		FlockTrap flock = new FlockTrap();
		flock.pos = 60;
		level.drop(new Bomb(), flock.pos);
		flock.activate();
		check(flock.color == FlockTrap.WHITE && flock.shape == FlockTrap.GRILL,
				"羊群陷阱图形标识错误");
		check(level.heaps.get(flock.pos) == null, "羊群陷阱没有执行暗属性物品堆反应");

		SummoningTrap summoning = new SummoningTrap();
		summoning.pos = 80;
		level.drop(new Bomb(), summoning.pos);
		Random.pushGenerator(0x53505353554D4D4FL);
		try {
			summoning.activate();
		} finally {
			Random.popGenerator();
		}
		check(summoning.color == SummoningTrap.RED && summoning.shape == SummoningTrap.WAVES,
				"召唤陷阱图形标识错误");
		check(level.heaps.get(summoning.pos) == null, "召唤陷阱没有执行暗属性物品堆反应");

		Dungeon.depth = 10;
		SummoningTrap bossTrap = new SummoningTrap();
		bossTrap.pos = 81;
		level.drop(new Bomb(), bossTrap.pos);
		int before = level.mobs.size();
		bossTrap.activate();
		check(level.mobs.size() == before && level.heaps.get(bossTrap.pos) != null,
				"召唤陷阱在首领层没有按旧版完全停用");
	}

	private static void testGuardianTrap(RecordingLevel level) {
		Dungeon.depth = 20;
		Dungeon.hero.pos = 17;
		GuardianTrap trap = new GuardianTrap();
		trap.pos = 24;
		level.drop(new Bomb(), trap.pos);

		Random.pushGenerator(0x5350534755415244L);
		try {
			trap.activate();
		} finally {
			Random.popGenerator();
		}

		check(trap.color == GuardianTrap.GREEN && trap.shape == GuardianTrap.LARGE_DOT,
				"守卫陷阱没有恢复绿色大圆点标识");
		check(level.heaps.get(trap.pos) == null,
				"守卫陷阱没有对陷阱格炸弹执行暗属性反应");
		int guardians = 0;
		for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob : level.mobs) {
			if (mob instanceof GuardianTrap.Guardian) {
				GuardianTrap.Guardian guardian = (GuardianTrap.Guardian)mob;
				guardians++;
				check(guardian.EXP == 0 && guardian.weapon() != null
						&& guardian.weapon().level() == 0 && !guardian.weapon().cursed,
						"守卫陷阱生成的雕像经验或基础武器错误");
			}
		}
		check(guardians == 3, "20层守卫陷阱没有生成恰好3个守卫：" + guardians);
	}

	private static void testLegacyTeleportTraps(RecordingLevel level) {
		Actor.clear();
		level.heaps.clear();
		level.resetRespawnCell(120);
		Dungeon.depth = 12;
		Dungeon.branch = 3;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 20;
		Actor.add(Dungeon.hero);

		IronKey currentKey = new IronKey(Dungeon.depth);
		IronKey otherKey = new IronKey(Dungeon.depth - 1);
		check(currentKey.collect(Dungeon.hero.belongings.backpack)
				&& otherKey.collect(Dungeon.hero.belongings.backpack), "扭曲陷阱测试钥匙无法放入背包");
		TestDistortionTrap distortion = new TestDistortionTrap();
		distortion.pos = Dungeon.hero.pos;
		check(distortion.prepare(Dungeon.hero), "扭曲陷阱没有接受存活角色触发");
		check(!Dungeon.hero.belongings.backpack.items.contains(currentKey)
				&& Dungeon.hero.belongings.backpack.items.contains(otherKey),
				"扭曲陷阱没有只清除当前深度钥匙");
		check(InterlevelScene.mode == InterlevelScene.Mode.RESET
				&& InterlevelScene.returnDepth == Dungeon.depth
				&& InterlevelScene.returnBranch == Dungeon.branch,
				"扭曲陷阱重置目标层或分支错误");
		check(distortion.color == DistortionTrap.WHITE && distortion.shape == DistortionTrap.LARGE_DOT,
				"扭曲陷阱图形标识错误");

		Rat teleported = new Rat();
		teleported.pos = 40;
		Actor.add(teleported);
		Rat neighbour = new Rat();
		neighbour.pos = 41;
		Actor.add(neighbour);
		TeleportationTrap teleport = new TeleportationTrap();
		teleport.pos = teleported.pos;
		level.drop(new Bomb(), teleport.pos);
		level.drop(new WandOfMagicMissile(), teleport.pos);
		teleport.activate();
		check(teleport.color == TeleportationTrap.GREY && teleport.shape == TeleportationTrap.GRILL,
				"传送陷阱图形标识错误");
		check(teleported.pos == 120 && neighbour.pos == 41, "传送陷阱没有只移动触发格角色");
		Heap destination = level.heaps.get(120);
		check(destination != null && destination.items.stream().anyMatch(item -> item instanceof Fadeleaf.Seed),
				"传送陷阱没有在怪物目的地留下枯叶草种子");
		check(level.heaps.get(teleport.pos) != null && level.heaps.get(teleport.pos).size() == 1,
				"传送陷阱没有恰好搬走触发格一件物品");

		Actor.clear();
		level.heaps.clear();
		Dungeon.droppedItems = new SparseArray<>();
		Dungeon.depth = 8;
		Dungeon.branch = 0;
		WarpingTrap warping = new WarpingTrap();
		warping.pos = 70;
		level.drop(new Bomb(), warping.pos);
		Random.pushGenerator(0x535053574152504CL);
		try {
			warping.activate();
		} finally {
			Random.popGenerator();
		}
		check(warping.color == WarpingTrap.TEAL && warping.shape == WarpingTrap.LARGE_DOT,
				"回退陷阱图形标识错误");
		check(level.heaps.get(warping.pos) == null, "回退陷阱没有移除原地物品堆");
		int stored = 0;
		for (int depth = 1; depth < Dungeon.depth; depth++) {
			ArrayList<Item> items = Dungeon.droppedItems.get(depth);
			if (items != null) stored += items.size();
		}
		check(stored == 1, "回退陷阱没有把物品投递到更早楼层");

		Dungeon.depth = 10;
		WarpingTrap bossWarping = new WarpingTrap();
		bossWarping.pos = 71;
		level.drop(new Bomb(), bossWarping.pos);
		bossWarping.activate();
		check(level.heaps.get(bossWarping.pos) != null, "回退陷阱不应在首领层处理物品");
	}

	private static void testLegacyEquipmentTraps(RecordingLevel level) {
		Actor.clear();
		level.heaps.clear();
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 50;
		Actor.add(Dungeon.hero);

		Sword weapon = new Sword();
		LeatherArmor armor = new LeatherArmor();
		Dungeon.hero.belongings.weapon = weapon;
		Dungeon.hero.belongings.armor = armor;
		WandOfMagicMissile groundWand = new WandOfMagicMissile();
		level.drop(groundWand, Dungeon.hero.pos);
		CursingTrap cursing = new CursingTrap();
		cursing.pos = Dungeon.hero.pos;
		cursing.activate();
		check(cursing.color == CursingTrap.VIOLET && cursing.shape == CursingTrap.LARGE_DOT,
				"诅咒陷阱图形标识错误");
		check(weapon.cursed && weapon.cursedKnown && armor.cursed && armor.cursedKnown,
				"诅咒陷阱没有诅咒全部已装备物品");
		check(groundWand.cursed && groundWand.cursedKnown, "诅咒陷阱没有诅咒地面可强化物品");

		level.heaps.clear();
		level.resetRespawnCell(130);
		Dungeon.hero.pos = 51;
		Sword disarmedWeapon = new Sword();
		Dungeon.hero.belongings.weapon = disarmedWeapon;
		level.drop(new Bomb(), Dungeon.hero.pos);
		level.drop(new StoneOre(), Dungeon.hero.pos);
		DisarmingTrap disarming = new DisarmingTrap();
		disarming.pos = Dungeon.hero.pos;
		disarming.activate();
		check(disarming.color == DisarmingTrap.ORANGE && disarming.shape == DisarmingTrap.LARGE_DOT,
				"缴械陷阱图形标识错误");
		check(Dungeon.hero.belongings.weapon == null && level.heaps.get(131) != null,
				"缴械陷阱没有把英雄武器传送到随机位置");
		check(level.heaps.get(disarming.pos) != null && level.heaps.get(disarming.pos).size() == 1
				&& level.heaps.get(130) != null, "缴械陷阱没有恰好搬走一件地面物品");

		Dungeon.hero.pos = 52;
		Knuckles knuckles = new Knuckles();
		Dungeon.hero.belongings.weapon = knuckles;
		DisarmingTrap gloveSafe = new DisarmingTrap();
		gloveSafe.pos = Dungeon.hero.pos;
		gloveSafe.activate();
		check(Dungeon.hero.belongings.weapon == knuckles, "缴械陷阱不应传送旧版拳套武器");
	}

	private static void testLegacyDamageTraps(RecordingLevel level) {
		Actor.clear();
		level.heaps.clear();
		Dungeon.depth = 12;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 20;
		Actor.add(Dungeon.hero);

		Rat disintegrationTarget = new Rat();
		disintegrationTarget.pos = 80;
		disintegrationTarget.HP = disintegrationTarget.HT = 100;
		Actor.add(disintegrationTarget);
		level.drop(new StoneOre(), disintegrationTarget.pos);
		DisintegrationTrap disintegration = new DisintegrationTrap();
		disintegration.pos = disintegrationTarget.pos;
		Random.pushGenerator(0x535053444953494EL);
		try {
			disintegration.activate();
		} finally {
			Random.popGenerator();
		}
		int disintegrationDamage = 100 - disintegrationTarget.HP;
		check(disintegration.color == DisintegrationTrap.RED
				&& disintegration.shape == DisintegrationTrap.LARGE_DOT,
				"解离陷阱图形标识错误");
		check(disintegrationDamage >= 50 && disintegrationDamage < 67,
				"解离陷阱没有使用旧版当前生命比例伤害：" + disintegrationDamage);
		check(level.heaps.get(disintegration.pos) == null, "解离陷阱没有摧毁触发格普通物品");

		Actor.clear();
		Rat center = new Rat();
		center.pos = 100;
		center.HP = center.HT = 100;
		Actor.add(center);
		Rat neighbour = new Rat();
		neighbour.pos = 101;
		neighbour.HP = neighbour.HT = 100;
		Actor.add(neighbour);
		RockfallTrap rockfall = new RockfallTrap();
		rockfall.pos = center.pos;
		Random.pushGenerator(0x535053524F434B53L);
		try {
			rockfall.activate();
		} finally {
			Random.popGenerator();
		}
		check(rockfall.color == RockfallTrap.YELLOW && rockfall.shape == RockfallTrap.LARGE_DOT,
				"落石陷阱图形标识错误");
		check(center.HP < 100 && neighbour.HP < 100, "落石陷阱没有伤害九宫格内全部角色");
		check(center.buff(Paralysis.class) != null && center.buff(Paralysis.class).cooldown() == 5f
				&& neighbour.buff(Paralysis.class) != null && neighbour.buff(Paralysis.class).cooldown() == 5f,
				"落石陷阱没有施加旧版5回合麻痹");
	}

	private static void testLegacyHazardTraps(RecordingLevel level) {
		Actor.clear();
		level.heaps.clear();
		Arrays.fill(level.heroFOV, false);
		Dungeon.depth = 12;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 20;
		Actor.add(Dungeon.hero);

		Rat grimTarget = new Rat();
		grimTarget.pos = 110;
		grimTarget.HP = grimTarget.HT = 100;
		Actor.add(grimTarget);
		Rat safeNeighbour = new Rat();
		safeNeighbour.pos = 111;
		safeNeighbour.HP = safeNeighbour.HT = 100;
		Actor.add(safeNeighbour);
		GrimTrap grim = new GrimTrap();
		grim.pos = grimTarget.pos;
		Random.pushGenerator(0x5350534752494D54L);
		try {
			grim.activate();
		} finally {
			Random.popGenerator();
		}
		check(grimTarget.HP < 100 && safeNeighbour.HP == 100,
				"致命陷阱没有只攻击触发格角色");
		check(grimTarget.HP <= 50 && grimTarget.HP > 0,
				"致命陷阱没有使用旧版当前生命一半至全部伤害");
		check(!grim.canBeHidden && !grim.avoidsHallways,
				"致命陷阱的可见性或走廊生成规则错误");

		Actor.clear();
		level.heaps.clear();
		Dungeon.droppedItems = new SparseArray<>();
		PitfallTrap pitfall = new PitfallTrap();
		pitfall.pos = 140;
		level.traps.put(pitfall.pos, pitfall);
		Level.set(pitfall.pos, Terrain.TRAP, level);
		level.buildFlagMaps();
		level.drop(new StoneOre(), pitfall.pos);
		pitfall.disarm();
		check(level.map[pitfall.pos] == Terrain.CHASM, "陷坑陷阱解除时没有立即形成单格深渊");
		pitfall.activate();
		check(level.heaps.get(pitfall.pos) == null
				&& Dungeon.droppedItems.get(Dungeon.depth + 1) != null,
				"陷坑陷阱没有让触发格物品立即坠落");

		level.heaps.clear();
		level.drop(new StoneOre(), 150);
		ToxicTrap toxic = new ToxicTrap();
		toxic.pos = 150;
		toxic.activate();
		check(level.heaps.get(toxic.pos) == null, "毒气陷阱没有执行旧版地属性物品堆反应");

		int wall = 161;
		Level.set(wall, Terrain.WALL, level);
		level.buildFlagMaps();
		ExplosiveTrap explosive = new ExplosiveTrap();
		explosive.pos = 160;
		explosive.activate();
		check(level.map[wall] == Terrain.EMPTY, "爆炸陷阱没有使用可摧毁墙体的旧版地牢炸弹");
	}

	private static void testLegacyBranchDepth(RecordingLevel level) {
		Actor.clear();
		ScalingTrap trap = new ScalingTrap();
		Dungeon.depth = 25;
		Dungeon.branch = AdventureJournal.branchFor(22);
		check(Dungeon.legacyDepth() == 85, "混沌分支没有使用旧版85层数值");
		check(trap.depth() == 85, "混沌分支的公共陷阱缩放没有使用旧版85层数值");

		Dungeon.depth = ChallengeJournal.anchorDepth(0);
		Dungeon.branch = ChallengeJournal.branchFor(0);
		check(Dungeon.legacyDepth() == 90, "冰雪挑战没有使用旧版90层数值");
		check(trap.depth() == 90, "冰雪挑战的公共陷阱缩放没有使用旧版90层数值");
		Dungeon.depth = 12;
		Dungeon.branch = 0;
		check(Dungeon.legacyDepth() == 12, "主线陷阱深度被支线映射污染");
		check(trap.depth() == 12, "主线公共陷阱缩放被支线映射污染");
	}

	private static void testLegacyDewEdge(RecordingLevel level) {
		level.heaps.clear();
		DewTrap dew = new DewTrap();
		dew.pos = level.width() + 1;
		dew.activate();
		int drops = 0;
		for (Heap heap : level.heaps.valueList()) {
			for (Item item : heap.items) {
				if (item instanceof VioletDewdrop) drops += item.quantity();
			}
		}
		check(drops == 9, "露珠陷阱在地图边缘没有保持旧版九滴产量：" + drops);
	}

	private static void testLegacyChasm(RecordingLevel level) {
		Actor.clear();
		Dungeon.depth = 14;
		Dungeon.branch = AdventureJournal.branchFor(22);
		Hero hero = new Hero();
		hero.pos = 100;
		hero.HP = hero.HT = 1000;
		Dungeon.hero = hero;
		Actor.add(hero);
		Random.pushGenerator(0x434841534D535053L);
		try {
			Chasm.heroFall(hero.pos);
		} finally {
			Random.popGenerator();
		}
		check(hero.HP >= 667 && hero.HP <= 750,
				"英雄坠落没有按旧版最大生命四分之一至三分之一受伤");
		check(hero.buff(Bleeding.class) != null && hero.buff(HolyStun.class) != null
				&& hero.buff(Cripple.class) != null,
				"英雄坠落没有施加旧版流血、神圣眩晕和残废");

		Rat mob = new Rat();
		mob.pos = 120;
		mob.HP = mob.HT = 1000;
		Actor.add(mob);
		Random.pushGenerator(0x4D4F42434841534DL);
		try {
			Chasm.mobFall(mob);
		} finally {
			Random.popGenerator();
		}
		check(mob.HP == 800, "怪物坠落没有按旧版扣除五分之一最大生命");
		check(mob.buff(Bleeding.class) != null && mob.buff(HolyStun.class) != null
				&& mob.buff(Cripple.class) != null,
				"怪物坠落没有施加旧版状态");
		check(level.map[mob.pos] == Terrain.SECRET_TRAP
				&& level.traps.get(mob.pos) instanceof PitfallTrap,
				"怪物坠落没有在落点留下隐藏陷坑");
		Dungeon.branch = 0;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestDistortionTrap extends DistortionTrap {
		boolean prepare(com.shatteredpixel.shatteredpixeldungeon.actors.Char target) {
			return prepareLegacyReset(target);
		}
	}

	private static final class ScalingTrap extends Trap {
		int depth() { return scalingDepth(); }
		@Override public void activate() { }
	}

	private static final class RecordingLevel extends Level {
		private int nextRespawnCell = 32;

		RecordingLevel() {
			setSize(16, 16);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY);
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public int randomRespawnCell(com.shatteredpixel.shatteredpixeldungeon.actors.Char ch) {
			return nextRespawnCell++;
		}
		void resetRespawnCell(int cell) {
			nextRespawnCell = cell;
		}
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
}
