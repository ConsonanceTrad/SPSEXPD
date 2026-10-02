package pd.actors.mobs.npcs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Light;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.actors.mobs.Rat;
import pd.items.Generator;
import pd.items.Item;
import pd.items.specific.journalpages.NewHome;
import pd.items.quest.AdventureJournal;
import pd.items.equipment.weapon.melee.special.Pumpkin;
import pd.plants.BlandfruitBush;
import pd.plants.Dreamfoil;
import pd.plants.Freshberry;
import pd.plants.NutPlant;
import pd.plants.Plant;
import pd.plants.ReNepenth;
import pd.plants.Seedpod;
import pd.plants.SiOtwoFlower;
import pd.plants.StarEater;
import pd.plants.Starflower;
import pd.plants.Sungrass;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.imageio.ImageIO;

public final class SpsAshWolfTest {

	private static final Class<?>[] SEED4_CLASSES = {
			Sungrass.Seed.class, StarEater.Seed.class, Dreamfoil.Seed.class,
			Starflower.Seed.class, ReNepenth.Seed.class, NutPlant.Seed.class,
			BlandfruitBush.Seed.class, Seedpod.Seed.class,
			Freshberry.Seed.class, SiOtwoFlower.Seed.class
	};
	private static final float[] SEED4_WEIGHTS = {4, 1, 4, 2, 1, 3, 1, 1, 2, 1};

	public static void main(String[] args) throws Exception {
		Generator.fullReset();
		try {
			testSeedAndSaveState();
			testPumpkinExchange();
			testNewHomeRouteAndChallenge();
			testPumpkinWeapon();
			testHolidaySources();
			testAssetsAndUtf8();
			System.out.println("SPS城镇阿萨测试通过：种植、南瓜换蜂蜜、新居路线、测试挑战、战斗效果、存档和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.challenges = 0;
			Statistics.deepestFloor = 0;
		}
	}

	private static void testSeedAndSaveState() {
		check(Arrays.equals(Generator.Category.SEED4.classes, SEED4_CLASSES), "阿萨SEED4种子种类或顺序错误");
		check(Arrays.equals(Generator.Category.SEED4.defaultProbs, SEED4_WEIGHTS), "阿萨SEED4权重错误");
		TownNpc ash = new TownNpc().configure(TownNpc.Spec.ASH_WOLF);
		Plant.Seed seed = ash.takeFirstAshWolfSeed();
		check(seed != null && Arrays.asList(SEED4_CLASSES).contains(seed.getClass()), "阿萨首次交谈没有生成SEED4种子");

		Bundle bundle = new Bundle();
		ash.storeInBundle(bundle);
		TownNpc restored = new TownNpc();
		restored.restoreFromBundle(bundle);
		check(restored.spec() == TownNpc.Spec.ASH_WOLF && restored.takeFirstAshWolfSeed() == null,
				"阿萨首次种植状态没有随存档恢复");
	}

	private static void testPumpkinExchange() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Pumpkin primary = new Pumpkin();
		Pumpkin secondary = new Pumpkin();
		Pumpkin packed = new Pumpkin();
		hero.belongings.weapon = primary;
		hero.belongings.secondWep = secondary;
		hero.belongings.backpack.items.add(packed);
		TownNpc ash = new TownNpc().configure(TownNpc.Spec.ASH_WOLF);
		check(ash.consumeAshWolfPumpkins(hero) == 3, "阿萨没有销毁全部南瓜灯");
		check(hero.belongings.weapon == null && hero.belongings.secondWep == null
				&& hero.belongings.getItem(Pumpkin.class) == null, "装备或背包中仍残留南瓜灯");
		check(ash.consumeAshWolfPumpkins(hero) == 0, "没有南瓜灯时仍能重复兑换蜂蜜");
	}

	private static void testNewHomeRouteAndChallenge() {
		NewHome page = new NewHome();
		AdventureJournal journal = new AdventureJournal();
		check(page.destination() == 8 && page.image == SpecificPlaceHolderDict.SOMETHING_0,
				"样板房纸片的路线或图标错误");
		check(journal.addPage(page) && journal.isUnlocked(8), "样板房纸片没有解锁新居路线");

		Dungeon.challenges = 0;
		Statistics.deepestFloor = 24;
		TownNpc ash = new TownNpc().configure(TownNpc.Spec.ASH_WOLF);
		check(!ash.takeAshWolfNewHomeOffer(), "未通关24层时提前掉落样板房坐标");
		Statistics.deepestFloor = 25;
		check(ash.takeAshWolfNewHomeOffer() && !ash.takeAshWolfNewHomeOffer(), "样板房坐标不是一次性奖励");
		Bundle bundle = new Bundle();
		ash.storeInBundle(bundle);
		TownNpc restored = new TownNpc();
		restored.restoreFromBundle(bundle);
		check(!restored.takeAshWolfNewHomeOffer(), "样板房奖励状态没有随存档恢复");

		Statistics.deepestFloor = 0;
		Dungeon.challenges = Challenges.TEST_TIME;
		check(new TownNpc().configure(TownNpc.Spec.ASH_WOLF).takeAshWolfNewHomeOffer(),
				"测试时间挑战没有提前开放样板房坐标");
		check(Challenges.MAX_VALUE == 262143 && Challenges.MAX_CHALS == 18
				&& Challenges.MASKS[14] == Challenges.SPS_DARKNESS
				&& Challenges.MASKS[15] == Challenges.ABRASION
				&& Challenges.MASKS[16] == Challenges.ELE_STOME
				&& Challenges.MASKS[17] == Challenges.TEST_TIME, "测试时间挑战未接入挑战选择范围");
	}

	private static void testPumpkinWeapon() {
		Pumpkin pumpkin = new Pumpkin();
		check(pumpkin.image == SpecificPlaceHolderDict.SOMETHING_0 && pumpkin.min() == 1 && pumpkin.max() == 5,
				"南瓜灯基础图标或伤害错误");
		pumpkin.upgrade(3);
		check(pumpkin.min() == 4 && pumpkin.max() == 8 && Pumpkin.EFFECT_CHANCE == 20
				&& Pumpkin.HEALING == 10 && Pumpkin.LIGHT_DURATION == 50f, "南瓜灯升级数值或旧版效果常量错误");

		Actor.clear();
		Dungeon.level = null;
		Hero attacker = new Hero();
		attacker.HP = 1;
		attacker.HT = 1000;
		Rat defender = new Rat();
		defender.HP = defender.HT = 100000;
		Random.pushGenerator(0x50554D504B494E4CL);
		try {
			for (int i = 0; i < 200; i++) pumpkin.proc(attacker, defender, 1);
		} finally {
			Random.popGenerator();
		}
		check(defender.buff(Burning.class) != null, "南瓜灯没有触发引燃");
		check(defender.buff(Terror.class) != null, "南瓜灯没有触发恐吓");
		check(attacker.buff(Light.class) != null, "南瓜灯没有持续照明");
		check(attacker.HP > 1 && attacker.HP <= attacker.HT, "南瓜灯没有治疗或治疗溢出生命上限");
	}

	private static void testHolidaySources() {
		check(Arrays.asList(Generator.Category.EASTERWEAPON.classes).contains(Pumpkin.class),
				"节日武器池没有接入南瓜灯");
		Item loot = new TownNpc().configure(TownNpc.Spec.OLD_NEW_STWIST).SupercreateLoot();
		check(Arrays.asList(Generator.Category.EASTERWEAPON.classes).contains(loot.getClass()),
				"OldNewStwist没有使用节日武器池");
	}

	private static void testAssetsAndUtf8() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		check("7206B92337C4A2D762B9252A5E085F565EE73D9F72D1AD6D8B36A80BCCC9B24C".equals(hash(sheet, 96, 896)),
				"南瓜灯不是SPS-PD 0.9.8原始图标");
		check("09E3B2489FC6BB4C7A3460D412E8EB6525830446841D719C203A574AB81B2F2B".equals(hash(sheet, 240, 848)),
				"样板房地点纸片不是SPS-PD 0.9.8原始图标");
		String itemsZh = Files.readString(new File("messages/items/zh/items.properties").toPath(), StandardCharsets.UTF_8);
		String miscZh = Files.readString(new File("messages/misc/zh/misc.properties").toPath(), StandardCharsets.UTF_8);
		check(itemsZh.contains("items.equipment.weapon.melee.special.pumpkin.name=南瓜灯")
				&& itemsZh.contains("items.specific.journalpages.newhome.name=样板房坐标")
				&& miscZh.contains("challenges.test_time=测试时间"), "阿萨相关中文资源缺失");
		check(itemsZh.indexOf('\uFFFD') < 0 && miscZh.indexOf('\uFFFD') < 0, "阿萨相关中文资源出现乱码替代字符");
	}

	private static String hash(BufferedImage sheet, int x0, int y0) throws Exception {
		ByteBuffer data = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = y0; y < y0 + 16; y++) for (int x = x0; x < x0 + 16; x++) data.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(data.array());
		StringBuilder result = new StringBuilder(digest.length * 2);
		for (byte value : digest) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsAshWolfTest() { }
}
