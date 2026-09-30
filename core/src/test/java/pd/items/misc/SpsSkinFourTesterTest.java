package pd.items.misc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.actbuff.NmImbue;
import pd.actors.buffs.BeCorrupt;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.ForeverShadow;
import pd.actors.buffs.mindbuff.LoseMind;
import pd.actors.buffs.mindbuff.WeakMind;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.armor.normalarmor.StyrofoamArmor;
import pd.items.armor.normalarmor.VestArmor;
import pd.items.armor.specialarmor.TestArmor;
import pd.items.food.completefood.FruitCandy;
import pd.items.reward.BoundReward;
import pd.items.wands.WandOfTest;
import pd.items.weapon.guns.GunA;
import pd.items.weapon.guns.GunB;
import pd.items.weapon.melee.Mace;
import pd.items.weapon.melee.normalweapon.ShortSword;
import pd.items.weapon.melee.start.DemonBlade;
import pd.items.weapon.melee.special.TestWeapon;
import pd.items.weapon.missiles.ElfBow;
import pd.items.weapon.missiles.buildblock.WaterBlock;
import pd.items.weapon.missiles.throwing.EscapeKnive;
import pd.items.weapon.missiles.throwing.MindArrow;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
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

public final class SpsSkinFourTesterTest {

	private static final int CENTER = 8 + 8 * 16;
	private static final String[] ICON_HASHES = {
			"5BD34C4A7F006FF353415B92971E7699957A2AA4C011D43F0004812A955D661A",
			"D758872318860A933C3663D935761674660E3F09D2EC51D9DAA4E1FC23652C71",
			"9F79876B105B95EDE7154AC1C86C56BAE53B911D52785A8AF51320BAD3007532",
			"C9612C388383D70224611B6E5783C289FCE35130DD22C2A12B2AF3F73D497C23",
			"6E615867CFB4CBC4DA3E9E8A0FE7EA5A47CDBBC6C975D3DFCB2A1B3CC36169F1",
			"60EFB3AD24EE328558041B525D5D1344BFCE5F93CA82501321463A7F95678E1A",
			"10C197D30098268813878BBD1D596D5288497E92187B8A664AB701984B53FAA2",
			"0C5177BDCE4FB660921EED992CCBA120DABC9CCB6D4F9E1854D953A489B2CF5F",
			"C4EFCAA0D0CB2F58F29AA5E38D3DACAA51F1BAD8F3C33F490AE5D90EB07D03EA",
			"15879D6622FAD9E2FBCF383C87C96319882D9C92C34B32D1A4AF67B5124D3258",
			"236849F37EB694BC358F7FB1819D7D88C939383306601D572D761A2DFBC61451",
			"B261E05FBA43D3BC4AD5379D9141465DC5880099770D8169BF6532F0A78F1A59",
			"506FBFC92A0A8FA2D1E6B30C13558ABDE064846C894CB4C69F1B6FD540E43C9E"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-skin-four-tester" + File.separator);
		Game.version = "test";
		try {
			pd.items.scrolls.Scroll.initLabels();
			pd.items.potions.Potion.initColors();
			pd.items.rings.Ring.initGems();
			Generator.fullReset();
			Badges.loadGlobal();
			testStart();
			testWeaponAndArmor();
			testWand();
			testRewardPaper();
			testOtherStarts();
			testElfBowAndNeedPaper();
			testPpcAndMindEffects();
			testLeaderFlag();
			testNanoBagAndDiceTower();
			testIcons();
			System.out.println("SPS皮肤4路线通过：八职业开局、专属装备、精神状态、充能、地形回收、存档和13个原始图标均正常。");
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
		Hero hero = new Hero();
		hero.skin = 4;
		Dungeon.hero = hero;
		HeroClass.WARRIOR.initHero(hero);
		check(hero.STR == Hero.STARTING_STR + 3, "皮肤4战士初始力量错误");
		check(hero.belongings.weapon instanceof GunB && hero.belongings.armor instanceof StyrofoamArmor,
				"皮肤4战士测试枪或泡沫甲错误");
		check(has(hero, TestWeapon.class) && has(hero, WandOfTest.class)
				&& has(hero, TestArmor.class) && has(hero, RewardPaper.class) && has(hero, JumpW.class),
				"皮肤4战士测试装备、报酬清单或战士之鞋缺失");
	}

	private static void testWeaponAndArmor() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		TestMob target = mobAt(level, CENTER + 2);
		TestWeapon weapon = new TestWeapon();
		check(weapon.min() == 10 && weapon.max() == 10 && weapon.STRReq() == 10,
				"测试武器基础数值错误");
		weapon.proc(hero, target, 8);
		check(hero.spp == 0, "测试武器低伤害错误增加试验点");
		weapon.proc(hero, target, 20);
		check(hero.spp == 1, "测试武器高伤害没有增加试验点");

		TestArmor armor = new TestArmor();
		check(armor.DRMin() == 0 && armor.DRMax() == 0, "测试护甲不应提供基础格挡");
		armor.proc(target, hero, 7);
		check(hero.spp == 2, "测试护甲受击没有增加试验点");
		armor.type(2);
		Bundle saved = new Bundle();
		armor.storeInBundle(saved);
		TestArmor restored = new TestArmor();
		restored.restoreFromBundle(saved);
		check(restored.type() == 2, "测试护甲类型没有随存档恢复");
	}

	private static void testWand() {
		WandOfTest wand = new WandOfTest();
		check(wand.min(0) == 10 && wand.max(0) == 10 && wand.maxCharges == 99 && wand.curCharges == 99,
				"测试法杖基础伤害或99发充能错误");
		DamageType[] types = {
				DamageType.ENERGY_DAMAGE, DamageType.FIRE_DAMAGE, DamageType.ICE_DAMAGE,
				DamageType.SHOCK_DAMAGE, DamageType.EARTH_DAMAGE, DamageType.LIGHT_DAMAGE,
				DamageType.DARK_DAMAGE
		};
		String[] actions = {
				WandOfTest.AC_ENERGY, WandOfTest.AC_FIRE, WandOfTest.AC_ICE,
				WandOfTest.AC_SHOCK, WandOfTest.AC_EARTH, WandOfTest.AC_LIGHT, WandOfTest.AC_DARK
		};
		for (int i = 0; i < actions.length; i++) {
			wand.execute(null, actions[i]);
			check(wand.type() == i && wand.damageType() == types[i], "测试法杖第" + i + "种元素切换错误");
		}
		Bundle saved = new Bundle();
		wand.storeInBundle(saved);
		WandOfTest restored = new WandOfTest();
		restored.restoreFromBundle(saved);
		check(restored.type() == 6 && restored.maxCharges == 99, "测试法杖元素或充能上限没有随存档恢复");
	}

	private static void testRewardPaper() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		RewardPaper paper = new RewardPaper();
		ShortSword uniqueTarget = new ShortSword();
		hero.spp = 49;
		check(!paper.improveItem(hero, uniqueTarget, RewardPaper.AC_DOSP), "报酬不足时仍锁定了物品");
		hero.spp = 50;
		check(paper.improveItem(hero, uniqueTarget, RewardPaper.AC_DOSP)
				&& uniqueTarget.unique && hero.spp == 0, "50点边界没有锁定物品");

		ShortSword reinforced = new ShortSword();
		hero.spp = 100;
		check(paper.improveItem(hero, reinforced, RewardPaper.AC_DOUP)
				&& reinforced.reinforced && hero.spp == 0, "100点边界没有强化物品");
		ShortSword upgraded = new ShortSword();
		hero.spp = 100;
		check(paper.improveItem(hero, upgraded, RewardPaper.AC_DORE)
				&& upgraded.level() == 10 && hero.spp == 0, "100点边界没有使物品破阶10级");

		Dungeon.gold = 999;
		check(!paper.buyReward(hero) && Dungeon.gold == 999, "金币不足时错误购买报酬");
		Dungeon.gold = 1000;
		check(paper.buyReward(hero) && Dungeon.gold == 0
				&& level.heaps.get(hero.pos).peek() instanceof BoundReward, "1000金币边界没有购买报酬");

		Bundle before = stored(hero);
		int oldPermanentHT = hero.permanentHT();
		hero.spp = 50;
		check(paper.rankUp(hero, 49) && hero.spp == 0, "50点边界没有执行全属性提升");
		Bundle after = stored(hero);
		check(hero.permanentHT() == oldPermanentHT + 1
				&& after.getInt("attackSkill") == before.getInt("attackSkill") + 1
				&& after.getInt("defenseSkill") == before.getInt("defenseSkill") + 1
				&& after.getInt("magicSkill") == before.getInt("magicSkill") + 1,
				"报酬清单全属性提升结果错误");
	}

	private static void testOtherStarts() {
		Hero mage = start(HeroClass.MAGE);
		check(mage.belongings.weapon instanceof ElfBow && mage.belongings.armor instanceof VestArmor
				&& has(mage, JumpM.class)
				&& has(mage, pd.items.scrolls.ScrollOfRegrowth.class),
				"皮肤4法师的精灵弓、背心、法师之鞋或再生卷轴错误");

		Hero rogue = start(HeroClass.ROGUE);
		check(rogue.belongings.weapon instanceof pd.items.weapon.melee.normalweapon.Dagger
				&& rogue.belongings.armor instanceof pd.items.armor.normalarmor.ClothArmor
				&& has(rogue, JumpR.class) && has(rogue, NeedPaper.class),
				"皮肤4盗贼的匕首、布甲、盗贼之鞋或通缉令错误");

		Hero huntress = start(HeroClass.HUNTRESS);
		check(huntress.belongings.weapon instanceof pd.items.weapon.melee.normalweapon.WoodenStaff
				&& huntress.belongings.armor instanceof pd.items.armor.normalarmor.ClothArmor
				&& has(huntress, PPC.class) && has(huntress, JumpH.class),
				"皮肤4女猎手的木杖、布甲、电子放大镜或女猎手之鞋错误");

		Hero performer = start(HeroClass.PERFORMER);
		check(performer.STR == Hero.STARTING_STR + 2 && performer.belongings.weapon instanceof Mace
				&& performer.belongings.armor instanceof pd.items.armor.normalarmor.LeatherArmor
				&& has(performer, LeaderFlag.class) && has(performer, JumpP.class) && Dungeon.gold == 1000,
				"皮肤4演员的力量、钉头锤、皮甲、领主之旗、鞋或金币错误");

		Hero soldier = start(HeroClass.SOLDIER);
		EscapeKnive knives = soldier.belongings.getItem(EscapeKnive.class);
		check(soldier.belongings.weapon instanceof GunA && soldier.belongings.armor instanceof VestArmor
				&& has(soldier, NmHealBag.class) && has(soldier, JumpS.class)
				&& knives != null && knives.quantity() == 10 && soldier.buff(NmImbue.class) != null,
				"皮肤4星兵的测试枪、背心、纳米维生包、鞋或10把逃脱飞刀错误");

		Hero follower = start(HeroClass.FOLLOWER);
		check(follower.belongings.weapon instanceof pd.items.weapon.melee.normalweapon.Knuckles
				&& follower.belongings.armor instanceof VestArmor && has(follower, DiceTower.class)
				&& has(follower, JumpF.class) && Dungeon.gold == 1000,
				"皮肤4信徒的指虎、背心、骰子塔、鞋或金币错误");

		Hero ascetic = start(HeroClass.ASCETIC);
		FruitCandy candy = ascetic.belongings.getItem(FruitCandy.class);
		check(candy != null && candy.quantity() == 3
				&& new pd.items.scrolls.ScrollOfMirrorImage().isKnown()
				&& new pd.items.potions.PotionOfShield().isKnown(),
				"皮肤4苦修者没有保持空专属分支和公共糖果、镜像卷轴、护盾药剂");
	}

	private static void testElfBowAndNeedPaper() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		ElfBow bow = new ElfBow();
		hero.belongings.weapon = bow;
		for (int i = 0; i < 10; i++) check(bow.drink(hero), "精灵弓第" + (i + 1) + "次饮用失败");
		check(hero.belongings.weapon == bow && bow.charge() == 10 && hero.spp == 100,
				"精灵弓在10次饮用前错误变形或点数错误");
		check(bow.drink(hero) && hero.belongings.weapon == null && bow.charge() == 0
				&& level.heaps.get(hero.pos).peek() instanceof DemonBlade,
				"精灵弓第11次饮用没有变为+3恶魔之刃");
		check(level.heaps.get(hero.pos).peek().level() == 3, "精灵弓生成的恶魔之刃不是+3");

		hero = freshHero(freshLevel());
		NeedPaper paper = new NeedPaper();
		hero.spp = NeedPaper.HELP_COST - 1;
		check(!paper.hide(hero), "通缉令在500点前错误提供庇护");
		hero.spp = NeedPaper.HELP_COST;
		hero.HP = 5;
		Buff.prolong(hero, Cripple.class, 10f);
		check(paper.hide(hero) && hero.spp == 0 && hero.HP == hero.HT
				&& hero.buff(Cripple.class) == null && hero.buff(ForeverShadow.class) != null,
				"通缉令500点边界的治疗、净化或永久暗影错误");

		level = freshLevel();
		hero = freshHero(level);
		hero.spp = NeedPaper.SHOP_COST - 1;
		check(!paper.shop(hero), "通缉令在3000点前错误生成装备");
		hero.spp = NeedPaper.SHOP_COST;
		Random.pushGenerator(0x4E45454450415045L);
		try { check(paper.shop(hero), "通缉令在3000点边界没有生成装备"); }
		finally { Random.popGenerator(); }
		check(hero.spp == 0 && level.heaps.get(hero.pos) != null,
				"通缉令黑市没有扣除点数或投放装备");
	}

	private static void testPpcAndMindEffects() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		PPC ppc = new PPC();
		ppc.charge(1);
		Random.pushGenerator(0x505043494E505554L);
		try { check(ppc.input(hero), "电子放大镜有1条记录时试探失败"); }
		finally { Random.popGenerator(); }
		check(ppc.charge() == 0 && hero.spp >= 0 && hero.spp < 10 && hero.buff(Bless.class) != null,
				"电子放大镜试探的扣费、点数或祝福错误");

		ppc.charge(PPC.HEAL_COST);
		hero.HP = 20;
		hero.spp = 99;
		Buff.affect(hero, WeakMind.class);
		check(ppc.recover(hero) && ppc.charge() == 0 && hero.HP == 40 && hero.spp == 0
				&& hero.buff(WeakMind.class) == null,
				"电子放大镜疗养的20条记录、恢复量或精神异常清除错误");

		ppc.charge(PPC.MIND_COST);
		check(ppc.remember(hero) && ppc.charge() == 0
				&& level.heaps.get(hero.pos).peek() instanceof MindArrow
				&& level.heaps.get(hero.pos).peek().quantity() == 5,
				"电子放大镜追忆没有用2条记录制造5支意识之矢");
		ppc.charge(37);
		Bundle saved = new Bundle();
		ppc.storeInBundle(saved);
		PPC restored = new PPC();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 37, "电子放大镜记录数没有随存档恢复");

		hero = freshHero(freshLevel());
		int baseMagic = hero.magicSkill();
		Buff.affect(hero, LoseMind.class);
		check(hero.magicSkill() == baseMagic - 5 && hero.attackProc(mobAt((TestLevel)Dungeon.level, CENTER + 2), 10) == 12,
				"失智状态没有降低5点法强并提高20%输出");
		TestMob target = mobAt((TestLevel)Dungeon.level, CENTER + 3);
		hero.spp = 7;
		new MindArrow().proc(hero, target, 0);
		BeCorrupt corrupt = target.buff(BeCorrupt.class);
		check(target.HP == 93 && corrupt != null && corrupt.level() == 7,
				"意识之矢没有附加当前点数伤害和同级腐化");
	}

	private static void testLeaderFlag() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		int wall = CENTER - 1, water = CENTER + 1, statue = CENTER - 16, entrance = CENTER + 16;
		Level.set(wall, Terrain.WALL, level);
		Level.set(water, Terrain.WATER, level);
		Level.set(statue, Terrain.STATUE, level);
		Level.set(entrance, Terrain.ENTRANCE, level);
		hero.spp = hero.lvl + 5;
		LeaderFlag flag = new LeaderFlag();
		check(flag.removeAround(hero) && flag.charge() == 900 && level.map[wall] == Terrain.EMPTY
				&& level.map[water] == Terrain.EMPTY && level.map[statue] == Terrain.EMPTY
				&& level.map[entrance] == Terrain.ENTRANCE,
				"领主之旗拆卸没有正确回收普通地形或保护入口");
		check(level.heaps.get(hero.pos).items.stream().anyMatch(item -> item instanceof WaterBlock),
				"领主之旗拆卸水面后没有返还水块");

		flag = new LeaderFlag();
		hero.spp = 0;
		check(flag.recruit(hero) && flag.charge() == 400 && hero.spp == hero.lvl,
				"领主之旗招募的600内政值或人气收益错误");
		flag = new LeaderFlag();
		hero.spp = hero.lvl + 10;
		Dungeon.gold = 7;
		check(flag.exile(hero) && flag.charge() == 400 && hero.spp == hero.lvl && Dungeon.gold == 107,
				"领主之旗流放没有把超额人气追加折算为金币");

		flag = new LeaderFlag();
		hero.spp = 100;
		Random.pushGenerator(0x4C4541444552464CL);
		try { check(flag.levy(hero), "领主之旗征收失败"); }
		finally { Random.popGenerator(); }
		check(flag.charge() == 0 && level.heaps.get(hero.pos).size() >= 2,
				"领主之旗征收没有按每50人气产生一件物品");
		flag.resetDaily();
		check(flag.charge() == LeaderFlag.FULL_CHARGE, "领主之旗每日内政值没有恢复到1440");

		hero = freshHero(freshLevel());
		flag = new LeaderFlag();
		flag.charge(0);
		hero.spp = 37;
		Dungeon.gold = 100;
		flag.advanceTime(hero, LeaderFlag.FULL_CHARGE - 1);
		check(flag.charge() == 0 && Dungeon.gold == 100 && flag.collect(hero.belongings.backpack),
				"领主之旗在一天结束前提前结算");
		hero.spend(1f);
		check(flag.charge() == LeaderFlag.FULL_CHARGE && Dungeon.gold == 63 && flag.dayProgress() == 0,
				"领主之旗没有在第1440行动恢复并扣除等于人气的金币");
		flag.advanceTime(hero, LeaderFlag.FULL_CHARGE * 2f);
		check(Dungeon.gold == 0 && flag.charge() == LeaderFlag.FULL_CHARGE && flag.dayProgress() == 0,
				"领主之旗跨越多个周期时结算错误");

		Bundle saved = new Bundle();
		flag.charge(731);
		flag.advanceTime(hero, 412.5f);
		flag.storeInBundle(saved);
		LeaderFlag restored = new LeaderFlag();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 731 && restored.dayProgress() == 412.5f,
				"领主之旗内政值或每日进度没有随存档恢复");
	}

	private static void testNanoBagAndDiceTower() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		NmHealBag bag = new NmHealBag();
		hero.HP = 20;
		hero.spp = 30;
		Buff.prolong(hero, Cripple.class, 10f);
		check(bag.heal(hero) && hero.HP == 50 && hero.spp == 0 && hero.buff(Cripple.class) == null,
				"纳米医疗没有把全部点数转为生命并清除有害状态");
		hero.HP = 40;
		check(bag.improve(hero) && hero.HP == 1 && hero.spp == 10,
				"纳米增值没有按四分之一生命生成点数");
		Random.pushGenerator(0x4E414E4F434F4F4BL);
		try { check(bag.rebuild(hero), "纳米重组在10点边界失败"); }
		finally { Random.popGenerator(); }
		check(hero.spp == 0 && level.heaps.get(hero.pos) != null,
				"纳米重组没有扣除10点或生成物品");

		hero = freshHero(freshLevel());
		DiceTower dice = new DiceTower();
		dice.charge(DiceTower.CHEAT_COST - 1);
		check(!dice.cheat(hero), "骰子塔在60充能前错误出千");
		dice.charge(DiceTower.CHEAT_COST);
		check(dice.cheat(hero) && dice.charge() == 0 && hero.spp == 100,
				"骰子塔60充能边界没有把天命点数设为100");
		Dungeon.gold = 10_500;
		hero.spp = 5;
		check(dice.allIn(hero) && Dungeon.gold == 0 && hero.spp == 7,
				"骰子塔乾坤一掷的5000金币折算错误");
		dice.charge(99);
		dice.gainCharge();
		dice.gainCharge();
		check(dice.charge() == DiceTower.FULL_CHARGE, "骰子塔充能没有在100封顶");
		Bundle saved = new Bundle();
		dice.storeInBundle(saved);
		DiceTower restored = new DiceTower();
		restored.restoreFromBundle(saved);
		check(restored.charge() == DiceTower.FULL_CHARGE, "骰子塔充能没有随存档恢复");

		hero = freshHero(freshLevel());
		dice = new DiceTower();
		check(dice.collect(hero.belongings.backpack), "骰子塔无法放入背包");
		hero.attackProc(mobAt((TestLevel)Dungeon.level, CENTER + 2), 5);
		check(dice.charge() == 1, "英雄命中后骰子塔没有获得1点充能");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集不是256x992");
		for (int i = 0; i < ICON_HASHES.length; i++) {
			check(ICON_HASHES[i].equals(hash(sheet, i * 16, 800)), "皮肤4测试员第" + (i + 1) + "个原始图标错误");
		}
	}

	private static boolean has(Hero hero, Class<? extends Item> type) {
		return hero.belongings.getItem(type) != null;
	}

	private static Hero start(HeroClass heroClass) {
		Actor.clear();
		Dungeon.level = null;
		Dungeon.gold = 0;
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = 4;
		Dungeon.hero = hero;
		heroClass.initHero(hero);
		return hero;
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

	private SpsSkinFourTesterTest() { }
}
