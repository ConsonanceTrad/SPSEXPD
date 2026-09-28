package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dewcharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GlassShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HighLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.InfJump;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Muscle;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm2;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TargetShoot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wet;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.StrBottle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.BaseArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.VestArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MoonCake;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FruitCandy;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor.LifeArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Pill;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.FairyCard;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HolyWater;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Skull;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.TaurcenBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunC;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsSkinTwoWarriorTest {
	private static final int CENTER = 8 + 8 * 16;
	private static final String[] ICON_HASHES = {
			"E37BA665C00E82C96DADB3DD9E79335BA29C583E0AF46043DF20CDEE8416F8AE",
			"19EA5ABA1EC96950CA54320BAA1EC4BB2FF5010FEA7A9C632AAEF7D68620864E",
			"61A3FE51A21CAF9F86E9BDBB6EBA1EC02DF708D182C55A2472D426161183BF55"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-skin-two-warrior" + File.separator);
		Game.version = "test";
		try {
			Scroll.initLabels();
			Potion.initColors();
			Ring.initGems();
			Generator.fullReset();
			Badges.loadGlobal();
			Badges.unlock(Badges.Badge.ITEM_LEVEL_1);
			Badges.unlock(Badges.Badge.ITEM_LEVEL_2);
			testStart();
			testMageStart();
			testStrengthBottle();
			testDemonContract();
			testWarriorShoes();
			testGnollMark();
			testMageShoes();
			testRogueStart();
			testUndeadBook();
			testRogueShoes();
			testHuntressStart();
			testTaurcenBow();
			testHuntressShoes();
			testPerformerStart();
			testHolyWater();
			testCopyBall();
			testPerformerShoes();
			testSoldierStart();
			testMechPocket();
			testSoldierShoes();
			testFollowerStart();
			testPylon();
			testFollowerShoes();
			testAsceticStart();
			testLifeArmor();
			testGrassBook();
			testAsceticShoes();
			testIcons();
			System.out.println("SPS皮肤2八职业测试通过：全部开局、职业物品、八双职业鞋、状态、地图效果、存档及原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.gold = 0;
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.WARRIOR.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 66 && hero.HT == 66 && hero.HP == 66, "皮肤2战士初始生命错误");
		check(hero.STR == Hero.STARTING_STR - 4, "皮肤2战士初始力量错误");
		check(stats.getInt("attackSkill") == 6 && stats.getInt("defenseSkill") == 6
				&& stats.getInt("magicSkill") == 6, "皮肤2战士三项技能错误");
		check(Dungeon.gold == 666, "皮肤2战士初始金币错误");
		check(hero.belongings.weapon == null, "皮肤2战士错误装备了近战武器");
		check(hero.belongings.armor instanceof BaseArmor && hero.belongings.armor.level() == 6
				&& hero.belongings.armor.DRMin() == 0 && hero.belongings.armor.DRMax() == 0,
				"皮肤2战士基础护甲等级或零防御错误");
		WandOfFirebolt firebolt = hero.belongings.getItem(WandOfFirebolt.class);
		check(firebolt != null && firebolt.level() == 6, "皮肤2战士火球法杖缺失或等级错误");
		check(hero.belongings.getItem(DemoScroll.class) != null && hero.belongings.getItem(JumpW.class) != null,
				"皮肤2战士恶魔契约或战士之鞋缺失");
		StrBottle bottle = hero.belongings.getItem(StrBottle.class);
		check(bottle != null && bottle.quantity() == 4, "皮肤2战士力量之瓶数量错误");
	}

	private static void testStrengthBottle() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		hero.STR = 6;
		hero.HP = 1;
		StrBottle bottle = new StrBottle();
		bottle.quantity(4).collect(hero.belongings.backpack);
		check(bottle.use(hero), "力量之瓶无法使用");
		check(hero.STR == 7 && hero.HP == hero.HT && bottle.quantity() == 3,
				"力量之瓶没有加力量、回满生命或逐瓶消耗");
	}

	private static void testMageStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.MAGE.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 20 && hero.HT == 20 && hero.HP == 20, "皮肤2法师初始生命错误");
		check(stats.getInt("attackSkill") == 5 && stats.getInt("defenseSkill") == 7
				&& stats.getInt("magicSkill") == 6, "皮肤2法师三项技能错误");
		check(hero.belongings.weapon instanceof Dagger && hero.belongings.armor instanceof VestArmor,
				"皮肤2法师匕首或背心错误");
		check(levelOf(hero, WandOfFirebolt.class) == 1 && levelOf(hero, WandOfFreeze.class) == 1
				&& levelOf(hero, WandOfLightning.class) == 1, "皮肤2法师三根元素法杖缺失或等级错误");
		check(hero.belongings.getItem(GnollMark.class) != null && hero.belongings.getItem(JumpM.class) != null,
				"皮肤2法师仪式面具或法师之鞋缺失");
	}

	private static void testDemonContract() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		hero.HTBoost = 36;
		hero.updateHT(true);
		hero.spp = 2;
		DemoScroll contract = new DemoScroll();
		contract.collect(hero.belongings.backpack);
		DemoScroll other = new DemoScroll();

		Bundle before = stored(hero);
		int oldPermanentHT = hero.permanentHT();
		int oldHP = hero.HP;
		Random.pushGenerator(0x44454D4F5343524CL);
		int improved;
		try {
			improved = contract.bloodTrade(hero);
		} finally {
			Random.popGenerator();
		}
		Bundle after = stored(hero);
		check(improved >= 0 && contract.trades() == 1 && !contract.canBloodTrade(hero),
				"恶魔契约没有执行每级一次的鲜血交易");
		check(hero.permanentHT() == oldPermanentHT - 3 && hero.HP == oldHP - 3,
				"鲜血交易没有按灵能+1同时扣除当前生命与永久生命");
		int increases = (after.getInt("attackSkill") - before.getInt("attackSkill"))
				+ (after.getInt("defenseSkill") - before.getInt("defenseSkill"))
				+ (after.getInt("magicSkill") - before.getInt("magicSkill"));
		check(increases == 1, "鲜血交易没有永久提升且仅提升一项技能");
		check((improved == 0 && hero.buff(Muscle.class) != null)
				|| (improved == 1 && hero.buff(Rhythm.class) != null)
				|| (improved == 2 && hero.buff(Recharging.class) != null), "鲜血交易缺少对应临时状态");
		check(other.souls() == 0 && other.trades() == 0, "不同恶魔契约实例错误共享状态");

		for (int i = 0; i < 11; i++) contract.gainSoul();
		int beforeImbue = hero.permanentHT();
		check(contract.soulImbue(hero) && contract.souls() == 1 && hero.permanentHT() == beforeImbue + 1,
				"11个灵魂没有正确转换为1点永久生命");
		DemoScroll restored = new DemoScroll();
		Bundle saved = new Bundle();
		contract.storeInBundle(saved);
		restored.restoreFromBundle(saved);
		check(restored.souls() == 1 && restored.trades() == 1, "恶魔契约状态没有随存档恢复");

		TestMob dead = mobAt(level, hero.pos + 2);
		dead.EXP = 0;
		dead.destroy();
		check(contract.souls() == 2, "敌人死亡没有给恶魔契约增加灵魂");
	}

	private static void testWarriorShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		JumpW shoes = new JumpW();
		shoes.collect(hero.belongings.backpack);
		shoes.gainCharge(1000);
		check(shoes.charge() == JumpW.FULL_CHARGE, "战士之鞋充能没有封顶");
		ArrayList<TestMob> landingMobs = new ArrayList<>();
		for (int offset : new int[]{level.width(), -level.width(), level.width() + 1,
				level.width() - 1, -level.width() + 1, -level.width() - 1, 1}) {
			landingMobs.add(mobAt(level, CENTER + 5 + offset));
		}
		Random.pushGenerator(0x4A554D5057415252L);
		try {
			check(shoes.jumpTo(hero, CENTER + 6), "战士之鞋无法发动五格跳跃");
		} finally {
			Random.popGenerator();
		}
		check(hero.pos == CENTER + 5, "战士之鞋落点不是路径上的第五格：" + hero.pos);
		check(shoes.charge() == 30, "战士之鞋单次跳跃耗能错误");
		int paralyzed = 0;
		for (TestMob mob : landingMobs) if (mob.buff(Paralysis.class) != null) paralyzed++;
		check(paralyzed > 0 && paralyzed < landingMobs.size(), "战士之鞋没有逐目标执行70%落地麻痹：" + paralyzed);

		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpW restored = new JumpW();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 30, "战士之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpW();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 3) && shoes.charge() == 0,
				"无限跳跃状态没有免除充能门槛和消耗");
		hero.rooted = true;
		check(!shoes.jumpTo(hero, hero.pos + 2), "缠绕状态下仍可使用战士之鞋");
	}

	private static void testGnollMark() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		GnollMark mark = new GnollMark();
		mark.gainCharge(29);
		check(!mark.actions(hero).contains(GnollMark.AC_LIGHT), "仪式面具在30点前错误开放仪式");
		mark.gainCharge();
		check(mark.actions(hero).contains(GnollMark.AC_LIGHT), "仪式面具30点时没有开放仪式");
		check(mark.sacrifice(hero) && hero.HP == 80 && mark.charge() == GnollMark.FULL_CHARGE,
				"生命献祭没有消耗20%生命并充满面具");
		check(mark.lightRite(hero) && mark.charge() == 30, "光明仪式没有消耗30点准备度");
		check(hero.buff(HighLight.class) != null && hero.buff(AttackUp.class).level() == 80
				&& hero.buff(DefenceUp.class).level() == 80 && hero.buff(Silent.class) != null
				&& hero.buff(Locked.class) != null, "光明仪式状态不完整");
		new WandOfFirebolt().execute(hero, com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand.AC_ZAP);

		level = freshLevel();
		hero = freshHero(level);
		mark = new GnollMark();
		mark.gainCharge(60);
		check(mark.darkRite(hero), "黑暗仪式无法发动");
		check(hero.buff(Recharging.class) != null && hero.buff(Arcane.class) != null
				&& hero.buff(STRDown.class) != null && hero.buff(Disarm.class) != null,
				"黑暗仪式状态不完整");
		check(DamageWand.applyArcaneBonus(hero, 17) == 34, "奥术专注没有使伤害法杖伤害翻倍");

		level = freshLevel();
		hero = freshHero(level);
		mark = new GnollMark();
		mark.gainCharge(60);
		check(mark.earthRite(hero), "自然仪式无法发动");
		check(hero.buff(EnergyArmor.class) != null && hero.buff(EnergyArmor.class).shielding() == 20
				&& hero.buff(BerryRegeneration.class) != null && hero.buff(Rhythm.class) != null,
				"自然仪式护盾、再生或律动不完整");
		Bundle saved = new Bundle();
		mark.storeInBundle(saved);
		GnollMark restored = new GnollMark();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 30, "仪式面具准备度没有随存档恢复");
	}

	private static void testMageShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		JumpM shoes = new JumpM();
		shoes.gainCharge(1000);
		Random.pushGenerator(0x4A554D504D414745L);
		try {
			check(shoes.jumpTo(hero, CENTER + 6), "法师之鞋无法执行三格闪烁");
		} finally {
			Random.popGenerator();
		}
		check(hero.pos == CENTER + 3 && shoes.charge() == 35, "法师之鞋闪烁距离或耗能错误");
		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpM restored = new JumpM();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 35, "法师之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpM();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 2) && shoes.charge() == 0,
				"无限跳跃状态没有免除法师之鞋充能门槛和消耗");
	}

	private static void testRogueStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.ROGUE.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 10 && hero.HT == 10 && hero.HP == 10, "皮肤2盗贼初始生命错误");
		check(stats.getInt("attackSkill") == 10 && stats.getInt("defenseSkill") == 8
				&& stats.getInt("magicSkill") == 3, "皮肤2盗贼三项技能错误");
		check(hero.stealth() == 14f, "皮肤2盗贼潜行加成错误：" + hero.stealth());
		check(hero.belongings.weapon instanceof com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger
				&& hero.belongings.armor instanceof com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor,
				"皮肤2盗贼没有装备旧版匕首与布甲");
		check(hero.belongings.getItem(UndeadBook.class) != null
				&& hero.belongings.getItem(JumpR.class) != null, "皮肤2盗贼亡灵圣经或盗贼之鞋缺失");
		Skull skull = hero.belongings.getItem(Skull.class);
		check(skull != null && skull.quantity() == 5, "皮肤2盗贼骷髅弹药数量错误");
	}

	private static void testUndeadBook() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		UndeadBook book = new UndeadBook();
		book.collect(hero.belongings.backpack);
		book.gainCharge(11);
		check(book.actions(hero).contains(UndeadBook.AC_READ), "亡灵圣经11点能量时未开放神圣庇佑");
		hero.defenseProc(mobAt(level, CENTER + 3), 1);
		check(book.charge() == 12, "亡灵圣经没有在英雄受击时充能");
		check(book.holyBless(hero) && book.charge() == 2, "神圣庇佑没有消耗10点能量");

		level = freshLevel();
		hero = freshHero(level);
		book.applyHolyBless(hero, 0);
		check(hero.buff(Levitation.class) != null && hero.buff(HighLight.class) != null,
				"神圣庇佑的漂浮与强光结果不完整");
		hero.lvl = 4;
		book.applyHolyBless(hero, 1);
		check(hero.buff(GlassShield.class) != null && hero.buff(GlassShield.class).turns() == 2
				&& hero.buff(EnergyArmor.class) != null && hero.buff(EnergyArmor.class).shielding() == 8,
				"神圣庇佑的玻璃与能量护盾结果不完整");
		TestMob first = mobAt(level, CENTER + 4);
		TestMob second = mobAt(level, CENTER + 5);
		book.applyHolyBless(hero, 2);
		check(first.HP == 50 && second.HP == 50, "神圣庇佑没有伤害视野内所有敌人");
		FairyCard.Fairy fairy = book.summonFairy(hero);
		check(fairy != null && fairy.HT == 12 && fairy.HP == 12 && level.mobs.contains(fairy),
				"神圣庇佑召唤的仙女位置或生命错误");

		level = freshLevel();
		hero = freshHero(level);
		hero.HTBoost = 40;
		hero.updateHT(true);
		book = new UndeadBook();
		book.gainCharge(51);
		int oldHT = hero.permanentHT();
		check(book.soulSacrifice(hero) && book.charge() == 1 && hero.permanentHT() == oldHT
				&& hero.buff(Dewcharge.class) != null, "高能量灵魂献祭没有只消耗50点能量");
		UndeadBook lowCharge = new UndeadBook();
		oldHT = hero.permanentHT();
		check(lowCharge.soulSacrifice(hero) && hero.permanentHT() == oldHT - 5,
				"低能量灵魂献祭没有永久扣除5点生命");

		hero.lvl = 2;
		check(lowCharge.pray(hero) && lowCharge.pray(hero) && !lowCharge.pray(hero)
				&& lowCharge.prayers() == 2, "亡灵圣经没有严格限制每英雄等级一次祈祷");
		int ankhs = 0;
		for (Item item : level.heaps.get(hero.pos).items) if (item instanceof Ankh) ankhs += item.quantity();
		check(ankhs == 2, "亡灵圣经祈祷没有生成对应数量的复活十字架");

		Bundle saved = new Bundle();
		lowCharge.gainCharge(7);
		lowCharge.storeInBundle(saved);
		UndeadBook restored = new UndeadBook();
		restored.restoreFromBundle(saved);
		UndeadBook separate = new UndeadBook();
		check(restored.charge() == 7 && restored.prayers() == 2
				&& separate.charge() == 0 && separate.prayers() == 0, "亡灵圣经存档或实例隔离错误");
	}

	private static void testRogueShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		JumpR shoes = new JumpR();
		shoes.gainCharge(1000);
		Random.pushGenerator(0x4A554D50524F4755L);
		try {
			check(shoes.jumpTo(hero, CENTER + 6), "盗贼之鞋无法执行两格跳跃");
		} finally {
			Random.popGenerator();
		}
		check(hero.pos == CENTER + 2 && shoes.charge() == 30, "盗贼之鞋跳跃距离或耗能错误");
		check(hero.buff(Levitation.class) != null, "盗贼之鞋没有赋予漂浮");

		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpR restored = new JumpR();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 30, "盗贼之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpR();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 2) && shoes.charge() == 0,
				"无限跳跃状态没有免除盗贼之鞋充能门槛和消耗");
	}

	private static void testHuntressStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.HUNTRESS.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 20 && hero.HT == 20 && hero.HP == 20, "皮肤2女猎手初始生命错误");
		check(stats.getInt("attackSkill") == 15 && stats.getInt("defenseSkill") == 8
				&& stats.getInt("magicSkill") == 0, "皮肤2女猎手三项技能错误");
		check(hero.belongings.weapon instanceof com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles
				&& hero.belongings.armor instanceof com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor,
				"皮肤2女猎手没有装备旧版拳套与布甲");
		check(hero.belongings.getItem(TaurcenBow.class) != null
				&& hero.belongings.getItem(JumpH.class) != null, "皮肤2女猎手马人长弓或猎手之鞋缺失");
	}

	private static void testTaurcenBow() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		TaurcenBow bow = new TaurcenBow();
		check(bow.min() == 4 && bow.max() == 8 && bow.STRReq() == 10, "马人长弓基础数值错误");
		bow.upgrade();
		check(bow.min() == 7 && bow.max() == 13, "马人长弓强化成长错误");

		TestMob breakTarget = mobAt(level, CENTER + 2);
		breakTarget.HP = breakTarget.HT = 1000;
		bow.setArrow(TaurcenBow.Arrow.NONE);
		bow.applySpecialArrow(hero, breakTarget, 40);
		check(breakTarget.HP == 944 && breakTarget.buff(ArmorBreak.class) != null
				&& breakTarget.buff(ArmorBreak.class).level() == 40, "马人长弓破甲箭错误");
		TestMob fireTarget = mobAt(level, CENTER + 3);
		fireTarget.HP = fireTarget.HT = 1000;
		bow.setArrow(TaurcenBow.Arrow.FIRE);
		bow.applySpecialArrow(hero, fireTarget, 40);
		check(fireTarget.HP == 980 && fireTarget.buff(Burning.class) != null, "马人长弓燃烧箭错误");
		TestMob iceTarget = mobAt(level, CENTER + 4);
		iceTarget.HP = iceTarget.HT = 1000;
		bow.setArrow(TaurcenBow.Arrow.ICE);
		bow.applySpecialArrow(hero, iceTarget, 40);
		check(iceTarget.HP == 980 && iceTarget.buff(Wet.class) != null && iceTarget.buff(Slow.class) != null,
				"马人长弓霜冻箭错误");
		TestMob poisonTarget = mobAt(level, CENTER + 5);
		poisonTarget.HP = poisonTarget.HT = 1000;
		bow.setArrow(TaurcenBow.Arrow.POISON);
		bow.applySpecialArrow(hero, poisonTarget, 40);
		check(poisonTarget.HP == 990 && poisonTarget.buff(Ooze.class) != null, "马人长弓腐蚀箭错误");
		TestMob eleTarget = mobAt(level, CENTER + 6);
		eleTarget.HP = eleTarget.HT = 1000;
		bow.setArrow(TaurcenBow.Arrow.ELE);
		bow.applySpecialArrow(hero, eleTarget, 42);
		check(eleTarget.HP == 986 && eleTarget.buff(Shocked.class) != null
				&& hero.buff(AttackUp.class) != null && hero.buff(AttackUp.class).level() == 30,
				"马人长弓电磁箭错误");

		bow = new TaurcenBow();
		bow.setArrow(TaurcenBow.Arrow.POISON);
		TaurcenBow.TaurcenBowArrow arrow = bow.new TaurcenBowArrow();
		TestMob chargedTarget = mobAt(level, CENTER + 7);
		chargedTarget.HP = chargedTarget.HT = 1000;
		for (int i = 0; i < 8; i++) arrow.proc(hero, chargedTarget, 40);
		check(bow.charge() == 8 && chargedTarget.HP == 1000, "马人长弓前8次命中充能错误");
		arrow.proc(hero, chargedTarget, 40);
		check(bow.charge() == 1 && chargedTarget.HP == 990 && chargedTarget.buff(Ooze.class) != null,
				"马人长弓满充特殊箭触发次数或重置错误");

		Bundle saved = new Bundle();
		bow.storeInBundle(saved);
		TaurcenBow restored = new TaurcenBow();
		restored.restoreFromBundle(saved);
		TaurcenBow separate = new TaurcenBow();
		check(restored.charge() == 1 && restored.arrow() == TaurcenBow.Arrow.POISON
				&& separate.charge() == 0 && separate.arrow() == TaurcenBow.Arrow.NONE,
				"马人长弓存档或实例隔离错误");
	}

	private static void testHuntressShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		hero.improveAttackSkill(1000);
		hero.HP = 99;
		TestMob nearOne = mobAt(level, CENTER + 4);
		TestMob nearTwo = mobAt(level, CENTER + 3 + level.width());
		TestMob far = mobAt(level, 8);
		nearOne.HP = nearOne.HT = nearTwo.HP = nearTwo.HT = far.HP = far.HT = 1000;
		JumpH shoes = new JumpH();
		shoes.gainCharge(1000);
		check(shoes.jumpTo(hero, CENTER + 6), "猎手之鞋无法执行三格跳跃");
		check(hero.pos == CENTER + 3 && shoes.charge() == 35, "猎手之鞋跳跃距离或耗能错误");
		check(hero.HP == 100 && far.HP == 1000 && (nearOne.HP < 1000 || nearTwo.HP < 1000),
				"猎手之鞋没有群攻七格视野目标并恢复1点生命");

		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpH restored = new JumpH();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 35, "猎手之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpH();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 2) && shoes.charge() == 0,
				"无限跳跃状态没有免除猎手之鞋充能门槛和消耗");
	}

	private static void testPerformerStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.PERFORMER.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 20 && hero.HT == 20 && hero.HP == 20, "皮肤2演员初始生命错误");
		check(hero.STR == Hero.STARTING_STR + 4 && stats.getInt("attackSkill") == 10
				&& stats.getInt("defenseSkill") == 10 && stats.getInt("magicSkill") == 3,
				"皮肤2演员属性错误");
		check(Dungeon.LimitedDrops.STRENGTH_POTIONS.count == 4, "皮肤2演员力量药剂补偿错误");
		check(hero.belongings.weapon instanceof HolyWater && hero.belongings.armor instanceof BaseArmor,
				"皮肤2演员圣水或基础护甲错误");
		check(hero.belongings.getItem(CopyBall.class) != null && hero.belongings.getItem(JumpP.class) != null,
				"皮肤2演员侵蚀核心或演员之鞋缺失");
		check(hero.belongings.getItem(com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMending.class) != null
				&& hero.belongings.getItem(com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing.class) != null,
				"皮肤2演员两瓶治疗药剂缺失");
	}

	private static void testHolyWater() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		HolyWater plain = new HolyWater();
		HolyWater charged = new HolyWater();
		charged.gainCharge(HolyWater.FULL_CHARGE);
		int plainDamage;
		int chargedDamage;
		Random.pushGenerator(0x484F4C5957415445L);
		try { plainDamage = plain.damageRoll(hero); } finally { Random.popGenerator(); }
		Random.pushGenerator(0x484F4C5957415445L);
		try { chargedDamage = charged.damageRoll(hero); } finally { Random.popGenerator(); }
		check(plain.min() == 22 && plain.max() == 34 && plain.STRReq() == 14
				&& chargedDamage == plainDamage * 5, "圣水基础数值或满充五倍伤害错误");
		plain.upgrade();
		check(plain.min() == 24 && plain.max() == 36, "圣水强化成长错误");
		hero.HP = 10;
		charged.proc(hero, mobAt(level, CENTER + 2), 100);
		check(hero.HP == 30 && charged.charge() == 1, "圣水满充命中没有正确回血并从1点重新积蓄");

		Bundle saved = new Bundle();
		charged.storeInBundle(saved);
		HolyWater restored = new HolyWater();
		restored.restoreFromBundle(saved);
		HolyWater separate = new HolyWater();
		check(restored.charge() == 1 && separate.charge() == 0, "圣水存档或实例隔离错误");
	}

	private static void testCopyBall() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		CopyBall ball = new CopyBall();
		ball.gainCharge(1000);
		check(ball.charge() == CopyBall.FULL_CHARGE && ball.actions(hero).contains(CopyBall.AC_USE),
				"侵蚀核心充能上限或施放条件错误");
		CopyBall.SlimeS slime = ball.spawnClone(CENTER + 2);
		check(slime != null && slime.pos == CENTER + 2 && slime.HP == 33 && slime.HT == 33
				&& slime.alignment == Char.Alignment.ALLY && slime.flying && level.mobs.contains(slime),
				"侵蚀核心分身位置、生命或阵营错误");
		Buff.affect(slime, Slow.class, 3f);
		check(slime.buff(Slow.class) == null,
				"侵蚀核心分身错误接受了负面状态");
		Buff.affect(slime, AttackUp.class, 3f);
		check(slime.buff(AttackUp.class) != null,
				"侵蚀核心分身没有接受允许的增益状态");

		Bundle saved = new Bundle();
		ball.storeInBundle(saved);
		CopyBall restored = new CopyBall();
		restored.restoreFromBundle(saved);
		check(restored.charge() == CopyBall.FULL_CHARGE, "侵蚀核心充能没有随存档恢复");
	}

	private static void testPerformerShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		JumpP shoes = new JumpP();
		shoes.gainCharge(1000);
		check(shoes.jumpTo(hero, CENTER + 6), "演员之鞋无法执行三格跳跃");
		check(hero.pos == CENTER + 3 && shoes.charge() == 10 && hero.buff(GlassShield.class) != null
				&& hero.buff(GlassShield.class).turns() == 1 && hero.buff(Rhythm.class) != null,
				"演员之鞋距离、耗能或基础增益错误");

		level = freshLevel();
		hero = freshHero(level);
		hero.subClass = HeroSubClass.SUPERSTAR;
		shoes = new JumpP();
		shoes.gainCharge(10);
		check(shoes.jumpTo(hero, CENTER + 2) && hero.buff(Rhythm2.class) != null,
				"超级明星使用演员之鞋没有获得超级律动");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpP();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 2) && shoes.charge() == 0,
				"无限跳跃状态没有免除演员之鞋充能门槛和消耗");
	}

	private static void testSoldierStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.SOLDIER.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 35 && hero.HT == 35 && hero.HP == 35, "皮肤2星兵初始生命错误");
		check(hero.STR == Hero.STARTING_STR + 6 && stats.getInt("attackSkill") == 4
				&& stats.getInt("defenseSkill") == -28 && stats.getInt("magicSkill") == 5,
				"皮肤2星兵属性错误");
		check(Dungeon.LimitedDrops.STRENGTH_POTIONS.count == 6, "皮肤2星兵力量药剂补偿错误");
		check(hero.belongings.weapon instanceof GunC && hero.belongings.armor instanceof BaseArmor,
				"皮肤2星兵三级枪或基础护甲错误");
		check(hero.belongings.getItem(MechPocket.class) != null && hero.belongings.getItem(JumpS.class) != null,
				"皮肤2星兵机械口袋或星兵之鞋缺失");
	}

	private static void testMechPocket() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		MechPocket pocket = new MechPocket();
		pocket.collect(hero.belongings.backpack);
		Random.pushGenerator(0x4D454348504F434BL);
		int generated;
		try { generated = pocket.use(hero); } finally { Random.popGenerator(); }
		check(generated == MechPocket.ITEM_COUNT && !hero.belongings.backpack.contains(pocket)
				&& level.heaps.get(hero.pos) != null && !level.heaps.get(hero.pos).items.isEmpty(),
				"机械口袋没有生成20件随机物品后自毁");
	}

	private static void testSoldierShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		GunC gun = new GunC();
		hero.belongings.weapon = gun;
		gun.activate(hero);
		JumpS shoes = new JumpS();
		shoes.gainCharge(1000);
		Random.pushGenerator(0x4A554D50534F4C44L);
		try { check(shoes.jumpTo(hero, CENTER + 6), "星兵之鞋无法执行三格跳跃"); }
		finally { Random.popGenerator(); }
		check(hero.pos == CENTER + 3 && shoes.charge() == 20, "星兵之鞋跳跃距离或耗能错误");
		check(gun.charge() >= 1 && gun.charge() <= 2
				&& ((hero.buff(TargetShoot.class) == null && gun.charge() == 1)
				|| (hero.buff(TargetShoot.class) != null && gun.charge() == 2)),
				"星兵之鞋逐枪装填或60%瞄准联动错误");

		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpS restored = new JumpS();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 20, "星兵之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpS();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 2) && shoes.charge() == 0,
				"无限跳跃状态没有免除星兵之鞋充能门槛和消耗");
	}

	private static void testFollowerStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.FOLLOWER.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 30 && hero.HT == 30 && hero.HP == 30, "皮肤2信徒初始生命错误");
		check(stats.getInt("attackSkill") == 10 && stats.getInt("defenseSkill") == 5
				&& stats.getInt("magicSkill") == 0, "皮肤2信徒三项技能错误");
		check(hero.belongings.weapon instanceof com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger
				&& hero.belongings.weapon.level() == 2 && hero.belongings.armor instanceof VestArmor,
				"皮肤2信徒的强化匕首或背心甲错误");
		Pylon pylon = hero.belongings.getItem(Pylon.class);
		check(pylon != null && hero.belongings.artifact == pylon && pylon.level() == 2
				&& pylon.charge() == 2 && hero.buff(Pylon.BeaconRecharge.class) != null,
				"皮肤2信徒的水晶塔没有强化、装备或激活");
		check(hero.belongings.getItem(JumpF.class) != null
				&& hero.belongings.getItem(MoonCake.class) != null,
				"皮肤2信徒缺少信徒之鞋或月饼");
	}

	private static void testPylon() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		Pylon pylon = new Pylon();
		pylon.upgrade(2);
		hero.belongings.artifact = pylon;
		pylon.activate(hero);
		check(pylon.level() == 2 && pylon.charge() == 2, "水晶塔升级没有逐级补充能量");

		pylon.setReturnPoint(hero);
		check(pylon.returnDepth() == 1 && pylon.returnBranch() == 0 && pylon.returnPos() == CENTER,
				"水晶塔没有记录楼层、支线和坐标");
		Bundle saved = new Bundle();
		pylon.storeInBundle(saved);
		Pylon restored = new Pylon();
		restored.restoreFromBundle(saved);
		check(restored.level() == 2 && restored.charge() == 2 && restored.returnDepth() == 1
				&& restored.returnBranch() == 0 && restored.returnPos() == CENTER,
				"水晶塔等级、充能或返回点没有随存档恢复");
		Bundle legacy = new Bundle();
		legacy.put("depth", 12);
		legacy.put("pos", CENTER + 1);
		Pylon legacyRestored = new Pylon();
		legacyRestored.restoreFromBundle(legacy);
		check(legacyRestored.returnDepth() == 12 && legacyRestored.returnBranch() == 0
				&& legacyRestored.returnPos() == CENTER + 1,
				"水晶塔无法读取0.9.8的depth/pos返回点字段");
		hero.pos = CENTER + 1;
		float beforeReturn = hero.cooldown();
		check(pylon.returnToPoint(hero) && hero.pos == CENTER,
				"水晶塔无法返回同层锚点");
		check(hero.cooldown() == beforeReturn, "水晶塔同层返回错误消耗了回合");

		Pylon.BeaconRecharge recharge = hero.buff(Pylon.BeaconRecharge.class);
		recharge.gainExp(3.01f);
		check(pylon.level() == 3 && pylon.charge() == 3 && pylon.experience() == 1,
				"水晶塔没有在301点神器经验时自动升级");
		Bundle before = stored(hero);
		int oldHT = hero.permanentHT();
		check(pylon.rankUp(hero) && pylon.level() == 0 && hero.permanentHT() == oldHT + 5,
				"满级水晶塔无法耗竭升阶或没有增加5点永久生命");
		Bundle after = stored(hero);
		check(after.getInt("attackSkill") == before.getInt("attackSkill") + 1
				&& after.getInt("defenseSkill") == before.getInt("defenseSkill") + 1
				&& after.getInt("magicSkill") == before.getInt("magicSkill") + 1,
				"水晶塔升阶没有永久提高命中、闪避和魔法能力");

		TestMob target = mobAt(level, CENTER + 2);
		int oldPos = target.pos;
		Random.pushGenerator(0x50594C4F4E5A4150L);
		try { check(pylon.teleportTarget(target), "水晶塔无法随机传送普通目标"); }
		finally { Random.popGenerator(); }
		check(target.pos != oldPos && level.insideMap(target.pos), "水晶塔没有把目标移到有效随机位置");
		pylon.reset();
		check(pylon.returnDepth() == -1 && pylon.returnPos() == -1, "水晶塔重置后仍保留返回点");
	}

	private static void testFollowerShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		JumpF shoes = new JumpF();
		shoes.gainCharge(1000);
		check(shoes.jumpTo(hero, CENTER + 6), "信徒之鞋无法执行三格跳跃");
		check(hero.pos == CENTER + 3 && shoes.charge() == 17
				&& level.map[CENTER] == Terrain.HIGH_GRASS,
				"信徒之鞋距离、耗能或起跳格高草效果错误");

		Plant planted = shoes.plantSpecialSeed(CENTER + 5);
		check(planted != null && level.plants.get(CENTER + 5) == planted,
				"信徒之鞋没有从旧版特殊种子池种植植物");
		HashSet<Class<?>> specialPlants = new HashSet<>(Arrays.asList(
				com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.Starflower.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod.class,
				com.shatteredpixel.shatteredpixeldungeon.plants.SiOtwoFlower.class));
		check(specialPlants.contains(planted.getClass()), "信徒之鞋种植了特殊种子池以外的植物");

		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpF restored = new JumpF();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 17, "信徒之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		level.map[CENTER] = Terrain.ENTRANCE;
		level.updateCellFlags(CENTER);
		shoes = new JumpF();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 2) && shoes.charge() == 0
				&& level.map[CENTER] == Terrain.ENTRANCE,
				"无限跳跃未免耗能，或信徒之鞋错误覆盖了入口");
	}

	private static void testAsceticStart() {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 2;
		Dungeon.hero = hero;
		HeroClass.ASCETIC.initHero(hero);
		Bundle stats = stored(hero);
		check(hero.permanentHT() == 40 && hero.HT == 40 && hero.HP == 30,
				"皮肤2修士应增加10点生命上限但不恢复当前生命");
		check(stats.getInt("attackSkill") == 14 && stats.getInt("defenseSkill") == 7
				&& stats.getInt("magicSkill") == 0, "皮肤2修士三项技能错误");
		check(hero.belongings.weapon instanceof com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.WoodenStaff
				&& hero.belongings.armor instanceof LifeArmor, "皮肤2修士的木杖或活性护甲错误");
		check(hero.buff(LifeArmor.LifeCharge.class) != null, "皮肤2修士的活性护甲没有激活");
		check(hero.belongings.getItem(GrassBook.class) != null
				&& hero.belongings.getItem(JumpA.class) != null, "皮肤2修士缺少自然之书或修士之鞋");
		FruitCandy candy = hero.belongings.getItem(FruitCandy.class);
		check(candy != null && candy.quantity() == 3, "皮肤2修士缺少3份水果糖");
	}

	private static void testLifeArmor() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		hero.HP = 50;
		LifeArmor armor = new LifeArmor();
		hero.belongings.armor = armor;
		armor.activate(hero);
		check(armor.DRMin() == 0 && armor.DRMax() == 0, "活性护甲初始格挡不为0");
		armor.proc(mobAt(level, CENTER + 2), hero, 7);
		check(armor.charge() == 7 && armor.recoveryTime() == 20, "活性护甲没有累计实际伤害并重置20回合计时");
		LifeArmor.LifeCharge life = hero.buff(LifeArmor.LifeCharge.class);
		life.tickNow();
		check(armor.adaptiveMax() == 7 && armor.DRMax() == 7 && armor.recoveryTime() == 19,
				"活性护甲没有把累计伤害转化为动态最大格挡");

		Bundle saved = new Bundle();
		armor.storeInBundle(saved);
		LifeArmor restored = new LifeArmor();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 7 && restored.recoveryTime() == 19 && restored.adaptiveMax() == 7,
				"活性护甲的伤害、计时或动态格挡没有随存档恢复");

		for (int i = 0; i < 19; i++) life.tickNow();
		check(hero.HP == 57 && armor.charge() == 0 && armor.recoveryTime() == 0
				&& armor.adaptiveMax() == 0, "活性护甲没有在20回合后治疗累计伤害并清空格挡");
	}

	private static void testGrassBook() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		GrassBook book = new GrassBook();
		Dungeon.gold = 500;
		check(!book.actions(hero).contains(GrassBook.AC_READ)
				&& !book.actions(hero).contains(GrassBook.AC_READ2), "自然之书在仅有500金币时错误开放动作");
		Dungeon.gold = 501;
		check(book.actions(hero).contains(GrassBook.AC_READ)
				&& book.actions(hero).contains(GrassBook.AC_READ2), "自然之书在金币大于500时没有开放动作");
		check(book.growGrass(hero) && Dungeon.gold == 1, "自然之书枯枝护佑没有严格消耗500金币");
		check(hero.buff(Levitation.class) != null && hero.buff(ShieldArmor.class) != null
				&& hero.buff(ShieldArmor.class).level() == hero.lvl + 10, "自然之书缺少30回合漂浮或等级+10护盾");
		for (int offset : com.watabou.utils.PathFinder.NEIGHBOURS8) {
			check(level.map[CENTER + offset] == Terrain.OLD_HIGH_GRASS, "自然之书没有把周围可用地面变成旧式高草");
		}

		check(Generator.Category.MUSHROOM.classes.length == 8
				&& Generator.Category.SPS_BERRY.classes.length == 4
				&& Generator.Category.SPS_SEED.classes.length == 19,
				"自然之书使用的旧版药材、浆果或种子池不完整");
		boolean mushroom = false, berry = false, regrowth = false, seed = false;
		Random.pushGenerator(0x4752415353424F4FL);
		try {
			for (int i = 0; i < 400 && !(mushroom && berry && regrowth && seed); i++) {
				Item item = book.randomNaturalItem();
				mushroom |= item instanceof Pill;
				berry |= item instanceof Fruit;
				regrowth |= item instanceof ScrollOfRegrowth;
				seed |= item instanceof Plant.Seed;
			}
		} finally { Random.popGenerator(); }
		check(mushroom && berry && regrowth && seed, "自然之书的四类随机产物没有全部出现");
	}

	private static void testAsceticShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		JumpA shoes = new JumpA();
		shoes.gainCharge(1000);
		Random.pushGenerator(0x4A554D5041534345L);
		try { check(shoes.jumpTo(hero, CENTER + 6), "修士之鞋无法执行四格闪烁"); }
		finally { Random.popGenerator(); }
		check(hero.pos == CENTER + 4 && shoes.charge() == 20, "修士之鞋闪烁距离或耗能错误");
		boolean haste = hero.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff.class) != null;
		Random.pushGenerator(0x4841535445425546L);
		try {
			for (int i = 0; i < 20 && !haste; i++) haste = shoes.rollHaste(hero);
		} finally { Random.popGenerator(); }
		check(haste && hero.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff.class) != null,
				"修士之鞋没有施加40%概率的4回合双倍移速");

		Bundle saved = new Bundle();
		shoes.storeInBundle(saved);
		JumpA restored = new JumpA();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 20, "修士之鞋充能没有随存档恢复");

		level = freshLevel();
		hero = freshHero(level);
		shoes = new JumpA();
		Buff.affect(hero, InfJump.class, 10f);
		check(shoes.jumpTo(hero, CENTER + 3) && shoes.charge() == 0,
				"无限跳跃状态没有免除修士之鞋充能门槛和消耗");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		for (int i = 0; i < ICON_HASHES.length; i++) {
			check(ICON_HASHES[i].equals(hash(sheet, 80 + i * 16, 768)), "皮肤2战士第" + (i + 1) + "个原始图标错误");
		}
		check("8475D734F07C1BBFF812A2E7EA3ECAF80D731BCC631E42D33103C1D0F6B8133F".equals(hash(sheet, 128, 768)),
				"皮肤2法师仪式面具原始图标错误");
		check("8B033D952AE0C3CD6A4E89AFDE1BFEE17B09DEBCC694D504AA6BA6B28F1E50FF".equals(hash(sheet, 144, 768)),
				"皮肤2盗贼亡灵圣经原始图标错误");
		check("F500930F34684FE21931C98F4632E7414DB79EE787014CF3FD948F00AC75445A".equals(hash(sheet, 160, 768)),
				"皮肤2女猎手马人长弓原始图标错误");
		check("B665CE8B3B440178D9FCAB13956C733304A3952479FE07ADCA0C1FA5B7C5E26B".equals(hash(sheet, 176, 768)),
				"皮肤2演员圣水原始图标错误");
		check("DCDCFF2F82E6B978407FFC81CDD8EF3CFCB6AB79F25D5A0CBD731BFCA535BE12".equals(hash(sheet, 192, 768)),
				"皮肤2星兵机械口袋原始图标错误");
		check("0F9EC46DE0E201281028F3EDE276283D86DBDFD3A1D5191227BF221553C4849B".equals(hash(sheet, 208, 768)),
				"皮肤2修士自然之书原始图标错误");
		check("21BE50D34F69078E94AF5E40E232C7E0E2A08D994DAFB64FD5BF871D935E1A90".equals(hash(sheet, 224, 768)),
				"皮肤2修士活性护甲原始图标错误");
	}

	private static int levelOf(Hero hero, Class<? extends Item> type) {
		Item item = hero.belongings.getItem(type);
		return item == null ? Integer.MIN_VALUE : item.level();
	}

	private static Bundle stored(Hero hero) {
		Bundle bundle = new Bundle();
		hero.storeInBundle(bundle);
		return bundle;
	}

	private static TestLevel freshLevel() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static Hero freshHero(TestLevel level) {
		Hero hero = new TestHero();
		hero.heroClass = HeroClass.WARRIOR;
		Talent.initClassTalents(hero);
		hero.HP = hero.HT = 100;
		hero.pos = CENTER;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static TestMob mobAt(TestLevel level, int pos) {
		TestMob mob = new TestMob();
		mob.HP = mob.HT = 100;
		mob.pos = pos;
		level.mobs.add(mob);
		Actor.add(mob);
		return mob;
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
	}

	private static final class TestHero extends Hero {
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
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
			Arrays.fill(heroFOV, true);
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private SpsSkinTwoWarriorTest() { }
}
