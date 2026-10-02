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
import pd.actors.buffs.Arcane;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DamageUp;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm2;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.normalarmor.DiscArmor;
import pd.items.equipment.armor.normalarmor.VestArmor;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.equipment.artifacts.Pylon;
import pd.items.equipment.artifacts.UnstableSpellbook;
import pd.items.equipment.weapon.melee.start.LinkSword;
import pd.items.equipment.weapon.melee.start.Whisk;
import pd.items.equipment.weapon.missiles.TaurcenBow;
import pd.items.equipment.weapon.spammo.HeavyAmmo;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

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

public final class SpsSkinThreeTest {

	private static final int CENTER = 8 + 8 * 16;
	private static final String[] ICON_HASHES = {
			"236849F37EB694BC358F7FB1819D7D88C939383306601D572D761A2DFBC61451",
			"42B32435E7DC52A912621AC0F3C6EA132CB0A9167AD71E33BF4E2E45CF2C7569",
			"23344252A8F551F0922C48ABFC7AB9E3EDCE6E548E3B4EB3B21A52BA69DDC6F6",
			"8108F2568C9C1A8309E4835D3F1F003D88AD64A086CF5075D1BAB7B694FAB54E",
			"773B5B3BD5551C5A540EEF1818E7F87C1E822D1E3697DF62F05C12934330DF12",
			"A3B2A638E19F033196215881FE51483612C243DE5304010EB3AEBA8F5401A4A1"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-skin-three" + File.separator);
		Game.version = "test";
		try {
			pd.items.consum.scrolls.Scroll.initLabels();
			pd.items.consum.potions.Potion.initColors();
			pd.items.equipment.rings.Ring.initGems();
			Generator.fullReset();
			Badges.loadGlobal();
			testStarts();
			testSavageHelmet();
			testHorseTotem();
			testRangeBag();
			testDanceLion();
			testHealBag();
			testWhisk();
			testIcons();
			System.out.println("SPS皮肤3测试通过：八职业开局、六件专属物品、战斗、充能、掉落、存档及原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testStarts() {
		Hero warrior = start(HeroClass.WARRIOR);
		check(warrior.STR == Hero.STARTING_STR + 2, "皮肤3战士初始力量错误");
		check(warrior.belongings.weapon instanceof pd.items.equipment.weapon.melee.normalweapon.Spear
				&& warrior.belongings.armor instanceof DiscArmor, "皮肤3战士长矛或圆盘甲错误");
		check(has(warrior, MissileShield.class) && has(warrior, SavageHelmet.class), "皮肤3战士缺少反射盾或蛮族头盔");

		Hero mage = start(HeroClass.MAGE);
		check(mage.belongings.weapon instanceof pd.items.equipment.weapon.melee.normalweapon.WoodenStaff
				&& mage.belongings.armor instanceof VestArmor, "皮肤3法师木杖或背心错误");
		check(has(mage, pd.items.equipment.wands.WandOfFirebolt.class)
				&& has(mage, pd.items.equipment.wands.WandOfFreeze.class)
				&& has(mage, GnollMark.class) && has(mage, PotionOfMage.class), "皮肤3法师法杖或职业物品缺失");

		Hero rogue = start(HeroClass.ROGUE);
		check(rogue.STR == Hero.STARTING_STR + 4, "皮肤3盗贼初始力量错误");
		check(rogue.belongings.weapon instanceof pd.items.equipment.weapon.melee.normalweapon.Glaive
				&& rogue.belongings.armor instanceof DiscArmor && rogue.belongings.artifact instanceof CloakOfShadows,
				"皮肤3盗贼关刀、圆盘甲或暗影斗篷错误");
		check(has(rogue, HorseTotem.class), "皮肤3盗贼缺少赤兔图腾");

		Hero huntress = start(HeroClass.HUNTRESS);
		check(huntress.belongings.weapon instanceof pd.items.equipment.weapon.melee.normalweapon.Knuckles
				&& huntress.belongings.armor instanceof pd.items.equipment.armor.normalarmor.ClothArmor,
				"皮肤3女猎手指虎或布甲错误");
		check(has(huntress, TaurcenBow.class) && has(huntress, RangeBag.class), "皮肤3女猎手缺少马人长弓或飞镖袋");

		Hero performer = start(HeroClass.PERFORMER);
		check(performer.belongings.weapon instanceof pd.items.equipment.weapon.melee.fusion.Triangolo
				&& performer.belongings.armor instanceof pd.items.equipment.armor.normalarmor.ClothArmor
				&& has(performer, Shovel.class) && has(performer, DanceLion.class), "皮肤3演员武器、布甲、铁铲或舞狮手册错误");

		Hero soldier = start(HeroClass.SOLDIER);
		check(soldier.belongings.weapon instanceof pd.items.equipment.weapon.guns.GunA
				&& soldier.belongings.armor instanceof VestArmor, "皮肤3星兵枪械或背心错误");
		check(has(soldier, HeavyAmmo.class) && has(soldier, GunOfSoldier.class) && has(soldier, HealBag.class),
				"皮肤3星兵缺少重弹、制式手枪或医疗箱");

		Hero follower = start(HeroClass.FOLLOWER);
		check(follower.STR == Hero.STARTING_STR + 4, "皮肤3信徒初始力量错误");
		check(follower.belongings.weapon instanceof pd.items.equipment.weapon.melee.normalweapon.Rapier
				&& has(follower, FaithSign.class) && follower.belongings.artifact instanceof Pylon,
				"皮肤3信徒刺剑、信仰印记或已装备电塔错误");

		Hero ascetic = start(HeroClass.ASCETIC);
		check(ascetic.STR == Hero.STARTING_STR + 4 && ascetic.belongings.weapon instanceof Whisk,
				"皮肤3苦修者力量或拂尘木剑错误");
		check(has(ascetic, BigBattery.class) && ascetic.belongings.artifact instanceof UnstableSpellbook,
				"皮肤3苦修者缺少大型电池或已装备不稳定魔典");
	}

	private static void testSavageHelmet() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		SavageHelmet helmet = new SavageHelmet();
		helmetEquip(hero, helmet);
		Random.pushGenerator(0x5341564147454845L);
		int result;
		try { result = hero.defenseProc(mobAt(level, CENTER + 2), 20); }
		finally { Random.popGenerator(); }
		DamageUp damageUp = hero.buff(DamageUp.class);
		check(result >= 10 && result < 20 && damageUp != null && damageUp.level() == 20 - result,
				"装备蛮族头盔没有必定减伤并把减伤转为下一击增伤");

		hero = freshHero(freshLevel());
		helmet = new SavageHelmet();
		int triggers = probability(helmet, hero, true);
		check(triggers > 130 && triggers < 270, "携带蛮族头盔的20%触发率异常：" + triggers);
	}

	private static void testHorseTotem() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		HorseTotem totem = new HorseTotem();
		hero.belongings.artifact = totem;
		totem.activate(hero);
		Random.pushGenerator(0x484F525345544F54L);
		int result;
		try { result = hero.attackProc(mobAt(level, CENTER + 2), 30); }
		finally { Random.popGenerator(); }
		check(result > 30 && result < 40 && hero.buff(HasteBuff.class) != null,
				"装备赤兔图腾没有必定增伤并获得4回合加速");

		hero = freshHero(freshLevel());
		totem = new HorseTotem();
		int triggers = probability(totem, hero, false);
		check(triggers > 130 && triggers < 270, "携带赤兔图腾的20%触发率异常：" + triggers);
	}

	private static int probability(MiscEquippable item, Hero hero, boolean helmet) {
		item.collect(hero.belongings.backpack);
		int count = 0;
		Random.pushGenerator(0x50524F424142494CL);
		try {
			for (int i = 0; i < 1000; i++) {
				if (helmet ? ((SavageHelmet)item).shouldTrigger(hero) : ((HorseTotem)item).shouldTrigger(hero)) count++;
			}
		} finally { Random.popGenerator(); }
		return count;
	}

	private static void testRangeBag() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		RangeBag bag = new RangeBag();
		bag.collect(hero.belongings.backpack);
		Random.pushGenerator(0x52414E4745424147L);
		try {
			for (int i = 0; i < 100; i++) check(!bag.shouldDrop(hero), "未装备飞镖袋错误触发击杀掉落");
		} finally { Random.popGenerator(); }
		hero.belongings.artifact = bag;
		bag.activate(hero);
		int drops = 0;
		Random.pushGenerator(0x52414E474544524FL);
		try { for (int i = 0; i < 1200; i++) if (bag.shouldDrop(hero)) drops++; }
		finally { Random.popGenerator(); }
		check(drops > 150 && drops < 250, "装备飞镖袋的1/6击杀掉落率异常：" + drops);
		for (int i = 0; i < 100; i++) check(inLinkPool(bag.createDrop()), "飞镖袋生成了LINKDROP牌组以外的物品");

		Dungeon.gold = 499;
		check(!bag.buy(hero) && Dungeon.gold == 499 && level.heaps.get(hero.pos) == null,
				"飞镖袋在金币不足时仍完成购买");
		Dungeon.gold = RangeBag.PRICE;
		check(bag.buy(hero) && Dungeon.gold == 0 && level.heaps.get(hero.pos) != null
				&& inLinkPool(level.heaps.get(hero.pos).peek()), "飞镖袋没有在500金币边界购买LINKDROP物品");
	}

	private static boolean inLinkPool(Item item) {
		if (item == null) return false;
		for (Class<?> type : LinkSword.linkDropClasses()) if (type.isInstance(item)) return true;
		return false;
	}

	private static void testDanceLion() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		DanceLion lion = new DanceLion();
		lion.charge(39);
		check(!lion.actions(hero).contains(DanceLion.AC_SPIN), "舞狮手册在40点前错误开放舞步");
		lion.charge(40);
		check(lion.actions(hero).contains(DanceLion.AC_SPIN), "舞狮手册在40点时没有开放舞步");
		lion.execute(hero, DanceLion.AC_SPIN);
		check(lion.charge() == 0 && hero.buff(Rhythm.class) != null, "旋风舞的消耗或律动状态错误");

		hero = freshHero(freshLevel()); lion = chargedLion(); lion.execute(hero, DanceLion.AC_STAND);
		check(hero.buff(DefenceUp.class) != null && hero.buff(DefenceUp.class).level() == 30, "站立舞没有提供30级防御强化");
		hero = freshHero(freshLevel()); lion = chargedLion(); lion.execute(hero, DanceLion.AC_BACK);
		check(hero.buff(Recharging.class) != null, "后退舞没有提供充能");
		hero = freshHero(freshLevel()); lion = chargedLion(); lion.execute(hero, DanceLion.AC_RUSH);
		check(hero.buff(AttackUp.class) != null && hero.buff(AttackUp.class).level() == 30, "冲刺舞没有提供30级攻击强化");
		hero = freshHero(freshLevel()); lion = chargedLion(); lion.execute(hero, DanceLion.AC_JUMP);
		check(hero.buff(Levitation.class) != null, "跳跃舞没有提供漂浮");

		hero = freshHero(freshLevel()); hero.subClass = HeroSubClass.SUPERSTAR; lion = chargedLion(); lion.execute(hero, DanceLion.AC_SPIN);
		check(hero.buff(Rhythm.class) != null && hero.buff(Rhythm2.class) != null, "超级巨星旋风舞追加效果缺失");
		hero = freshHero(freshLevel()); hero.subClass = HeroSubClass.SUPERSTAR; lion = chargedLion(); lion.execute(hero, DanceLion.AC_STAND);
		check(hero.buff(EnergyArmor.class) != null, "超级巨星站立舞追加护盾缺失");
		hero = freshHero(freshLevel()); hero.subClass = HeroSubClass.SUPERSTAR; lion = chargedLion(); lion.execute(hero, DanceLion.AC_BACK);
		check(hero.buff(Arcane.class) != null, "超级巨星后退舞追加奥术状态缺失");
		hero = freshHero(freshLevel()); hero.subClass = HeroSubClass.SUPERSTAR; lion = chargedLion(); lion.execute(hero, DanceLion.AC_RUSH);
		check(hero.buff(Invisibility.class) != null, "超级巨星冲刺舞追加隐身缺失");
		hero = freshHero(freshLevel()); hero.subClass = HeroSubClass.SUPERSTAR; lion = chargedLion(); lion.execute(hero, DanceLion.AC_JUMP);
		check(hero.buff(GlassShield.class) != null, "超级巨星跳跃舞追加玻璃护盾缺失");

		lion.charge(73);
		Bundle saved = new Bundle(); lion.storeInBundle(saved);
		DanceLion restored = new DanceLion(); restored.restoreFromBundle(saved);
		check(restored.charge() == 73, "舞狮手册充能没有随存档恢复");
	}

	private static DanceLion chargedLion() {
		DanceLion lion = new DanceLion(); lion.charge(DanceLion.USE_COST); return lion;
	}

	private static void testHealBag() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		HealBag bag = new HealBag();
		bag.charge(14);
		check(!bag.actions(hero).contains(HealBag.AC_HEAL) && !bag.heal(hero), "医疗箱在15点前错误开放治疗");
		bag.charge(15);
		TestMob patient = mobAt(level, CENTER + 1); patient.HP = 60;
		hero.HP = 20;
		Buff.affect(hero, Poison.class); Buff.affect(hero, Cripple.class); Buff.affect(hero, STRDown.class);
		Buff.affect(hero, Bleeding.class); Buff.affect(hero, AttackDown.class); Buff.affect(hero, ArmorBreak.class);
		check(bag.heal(hero) && bag.charge() == 0 && hero.HP == 70 && patient.HP == patient.HT,
				"医疗箱15点治疗没有正确治疗九格目标并封顶生命");
		check(hero.buff(Poison.class) == null && hero.buff(Cripple.class) == null && hero.buff(STRDown.class) == null
				&& hero.buff(Bleeding.class) == null && hero.buff(AttackDown.class) == null && hero.buff(ArmorBreak.class) == null,
				"医疗箱没有清除完整的六类负面状态");

		level = freshLevel(); hero = freshHero(level); bag = new HealBag(); bag.charge(39);
		check(!bag.actions(hero).contains(HealBag.AC_COOK) && !bag.cook(hero), "医疗箱在40点前错误开放调制");
		bag.charge(40);
		check(bag.actions(hero).contains(HealBag.AC_COOK) && bag.cook(hero) && bag.charge() == 0
				&& level.heaps.get(hero.pos) != null && isCookedPool(level.heaps.get(hero.pos).peek()),
				"医疗箱40点调制没有生成旧版食药牌组物品");

		bag.charge(31); Bundle saved = new Bundle(); bag.storeInBundle(saved);
		HealBag restored = new HealBag(); restored.restoreFromBundle(saved);
		check(restored.charge() == 31, "医疗箱充能没有随存档恢复");
	}

	private static boolean isCookedPool(Item item) {
		return inCategory(item, Generator.Category.POTION) || inCategory(item, Generator.Category.HIGHFOOD)
				|| inCategory(item, Generator.Category.MUSHROOM) || inCategory(item, Generator.Category.PILL);
	}

	private static boolean inCategory(Item item, Generator.Category category) {
		if (item == null) return false;
		for (Class<?> type : category.classes) if (type.isInstance(item)) return true;
		return false;
	}

	private static void testWhisk() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		Whisk whisk = new Whisk();
		check(whisk.min() == 8 && whisk.max() == 15 && whisk.legacyAccuracy(0) == 1f
				&& whisk.legacyDelay(0) == 1f && whisk.legacyReach(0) == 2, "拂尘木剑旧版基础数值错误");
		TestMob target = mobAt(level, CENTER + 2);
		for (int i = 0; i < 10; i++) whisk.proc(hero, target, 10);
		check(whisk.charge() == 10 && target.buff(Vertigo.class) == null, "拂尘木剑在第11击前错误触发蓄风");
		whisk.proc(hero, target, 10);
		check(whisk.charge() == 0 && whisk.extraCharge() == 1 && target.buff(Vertigo.class) != null,
				"拂尘木剑第11击没有击退、眩晕并增加蓄风次数");

		level = freshLevel(); hero = freshHero(level);
		TestHero nonMob = new TestHero(); nonMob.HP = nonMob.HT = 100; nonMob.pos = CENTER + 2; Actor.add(nonMob);
		whisk = new Whisk(); whisk.charge(10, 4);
		whisk.proc(hero, nonMob, 10);
		check(whisk.extraCharge() == 5, "拂尘木剑对非Mob目标的第5次蓄风处理错误");

		level = freshLevel(); hero = freshHero(level); target = mobAt(level, CENTER + 2);
		whisk = new Whisk(); whisk.charge(10, 4);
		whisk.proc(hero, target, 10);
		check(whisk.charge() == 0 && whisk.extraCharge() == 0 && !target.firstItem && level.heaps.get(target.pos) != null,
				"拂尘木剑第5次蓄风没有生成敌人额外战利品");

		Whisk other = new Whisk();
		check(other.charge() == 0 && other.extraCharge() == 0, "不同拂尘木剑实例错误共享充能");
		whisk.charge(7, 3); Bundle saved = new Bundle(); whisk.storeInBundle(saved);
		Whisk restored = new Whisk(); restored.restoreFromBundle(saved);
		check(restored.charge() == 7 && restored.extraCharge() == 3, "拂尘木剑充能没有随存档恢复");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集不是256x992");
		for (int i = 0; i < ICON_HASHES.length; i++) {
			check(ICON_HASHES[i].equals(hash(sheet, i * 16, 784)), "皮肤3第" + (i + 1) + "个原始图标错误");
		}
	}

	private static Hero start(HeroClass heroClass) {
		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.gold = 0;
		Hero hero = new Hero();
		hero.skin = 3;
		Dungeon.hero = hero;
		heroClass.initHero(hero);
		return hero;
	}

	private static boolean has(Hero hero, Class<? extends Item> type) {
		return hero.belongings.getItem(type) != null;
	}

	private static void helmetEquip(Hero hero, SavageHelmet helmet) {
		hero.belongings.artifact = helmet;
		helmet.activate(hero);
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
		level.mobs().add(mob);
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
			mobs().clear();
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

	private SpsSkinThreeTest() { }
}
