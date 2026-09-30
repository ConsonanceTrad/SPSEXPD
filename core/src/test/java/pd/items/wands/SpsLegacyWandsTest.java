package pd.items.wands;

import pd.Dungeon;
import pd.actors.buffs.AcidOoze;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.SpsAcidOoze;
import pd.actors.buffs.SpeedSlow;
import pd.actors.blobs.SwampGas;
import pd.items.Generator;
import pd.items.wands.fusion.WandOfBlood;
import pd.items.wands.fusion.WandOfFlow;
import pd.sprites.ItemSpriteSheet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

/** Headless regression checks for the original SPS-PD wand deck. */
public final class SpsLegacyWandsTest {

	private static final String ACID_ICON_HASH =
			"DC021A77B302B8E3FB8347F80504CF0A343D4007AFCE22574252D89E2BD3B212";
	private static final String FREEZE_ICON_HASH =
			"D2E52B3B0DACCA1F9C8E3B3BB5C10BBC7854C28FC52FC51E55758B43F0E0E6D4";
	private static final String FIREBOLT_ICON_HASH =
			"72573F162A0E1EA0603DB677DCA88AB8D9BDB8CB18DECD5073371474F65F76BA";
	private static final String LIGHT_ICON_HASH =
			"04576BC7388EC3300C72B49302DEF373E01A7C254CDA1041841A6297416FC682";
	private static final String SWAMP_ICON_HASH =
			"FB38C1BC165FF3ADA4EFD15ABA3AE370581C13221C8CC41064EDC8C4D3A6A34C";
	private static final String CHARM_ICON_HASH =
			"B4FC5E48F897034C365FE97BC2C5D6161EFBE4B4BB6A17EB7C8296979F4155E4";
	private static final String FLOCK_ICON_HASH =
			"0C0ACE356127CA85EE82E5C7A12A179290E6F6A654F5E61BB1577C9AD91EA91D";
	private static final String METEORITE_ICON_HASH =
			"97F04B1184479B4A9EA35A9CD07C43E65CA856A6787F1A1939E8D074EB644C64";
	private static final String ERROR_ICON_HASH =
			"98122F7C355B51AC00B36275F358C5F0AE129F0FAFD448DAD248BFCEEBF93163";
	private static final String TCLOUD_ICON_HASH =
			"C49768450BA26A453223AA60C9DC57A7E80B8B6DB4B4977C7CA250FA50B15CE2";
	private static final String TCLOUD_SHEET_HASH =
			"F652CDDE5DB892A59A7550323513BE3A9223EAEFADB6637C13598E86672BD8E3";
	private static final String FLOW_ICON_HASH =
			"0F9EC46DE0E201281028F3EDE276283D86DBDFD3A1D5191227BF221553C4849B";
	private static final String BLOOD_ICON_HASH =
			"4AB894760B4D2518A56D7F03E98F1C91DDB01B915A22EF9E09AB3071E1D5CB79";
	private static final String WATER_RAY_HASH =
			"BC251CE13610DDB734ABEE9E6372202B72A41FE679B45C5234E7C5756E49ACCE";
	private static final String MAGIC_MISSILE_ICON_HASH =
			"738D34968273D8459C75FBCDF751DAEBA33E94468755315AD29081C021F358AF";
	private static final String DISINTEGRATION_ICON_HASH =
			"4AB894760B4D2518A56D7F03E98F1C91DDB01B915A22EF9E09AB3071E1D5CB79";
	private static final String LIGHTNING_ICON_HASH =
			"C4EFCAA0D0CB2F58F29AA5E38D3DACAA51F1BAD8F3C33F490AE5D90EB07D03EA";

	private SpsLegacyWandsTest() { }

	public static void main(String[] args) throws Exception {
		testAcidWand();
		testFreezeWand();
		testFireboltWand();
		testLightWand();
		testSwampWand();
		testCharmWand();
		testFlockWand();
		testMeteoriteWand();
		testErrorWand();
		testTCloudWand();
		testBloodWand();
		testFlowWand();
		testLightningWand();
		testMagicMissileWand();
		testDisintegrationWand();
		testPortedGeneratorOrder();
		testAcidIcon();
		System.out.println("SPS旧版15项法杖测试通过：数值、状态、充能、生成权重和原始图标均正常。");
	}

	private static void testLightningWand() {
		WandOfLightning wand = new WandOfLightning();
		check(wand.min(0) == 5 && wand.max(0) == 10, "雷霆法杖+0伤害错误");
		check(wand.min(5) == 10 && wand.max(5) == 16, "雷霆法杖平方成长错误");
		check(Math.abs(WandOfLightning.chainMultiplier(1, false) - 1f) < 0.0001f
				&& Math.abs(WandOfLightning.chainMultiplier(2, false) - 0.7f) < 0.0001f,
				"雷霆法杖多目标伤害分摊错误");
		check(Math.abs(WandOfLightning.chainMultiplier(2, true) - 1.05f) < 0.0001f,
				"雷霆法杖水中倍率错误");
		check(wand.image == ItemSpriteSheet.WAND_SPS_LIGHTNING, "雷霆法杖没有使用旧版图标槽位");
	}

	private static void testMagicMissileWand() {
		WandOfMagicMissile wand = new WandOfMagicMissile();
		check(wand.min(0) == 2 && wand.max(0) == 6, "魔弹法杖+0伤害错误");
		check(wand.min(5) == 7 && wand.max(5) == 31, "魔弹法杖升级成长错误");
		check(wand.initialCharges() == 3, "魔弹法杖初始充能错误");
		check(WandOfMagicMissile.magicSkillMultiplier(7) == 1.7f, "魔弹法杖魔力倍率错误");
		check(wand.image == ItemSpriteSheet.WAND_SPS_MAGIC_MISSILE, "魔弹法杖没有使用旧版图标槽位");
	}

	private static void testDisintegrationWand() {
		WandOfDisintegration wand = new WandOfDisintegration();
		check(wand.min(0) == 2 && wand.max(0) == 8, "解离法杖+0伤害错误");
		check(wand.min(5) == 7 && wand.max(5) == 28, "解离法杖升级成长错误");
		check(WandOfDisintegration.maxDistance(0) == 2
				&& WandOfDisintegration.maxDistance(6) == 8
				&& WandOfDisintegration.maxDistance(20) == 8, "解离法杖射程错误");
		check(WandOfDisintegration.damageLevel(5, 3, 2) == 9,
				"解离法杖没有应用穿墙和多目标加成");
		check(wand.image == ItemSpriteSheet.WAND_SPS_DISINTEGRATION,
				"解离法杖没有使用旧版图标槽位");
	}

	private static void testBloodWand() {
		WandOfBlood wand = new WandOfBlood();
		check(wand.min(0) == 0 && wand.max(0) == 6, "鲜血法杖+0伤害错误");
		check(wand.min(5) == 5 && wand.max(5) == 16, "鲜血法杖升级成长错误");
		check(WandOfBlood.magicSkillMultiplier(0) == 1f
				&& WandOfBlood.magicSkillMultiplier(7) == 1.7f, "鲜血法杖魔力倍率错误");
		check(wand.image == ItemSpriteSheet.WAND_BLOOD, "鲜血法杖没有使用旧版图标槽位");
	}

	private static void testFlowWand() {
		WandOfFlow wand = new WandOfFlow();
		check(wand.min(0) == 1 && wand.max(0) == 5, "涌流法杖+0伤害错误");
		check(wand.min(5) == 6 && wand.max(5) == 20, "涌流法杖升级成长错误");
		check(WandOfFlow.pushStrength(0) == 3 && WandOfFlow.pushStrength(2) == 5
				&& WandOfFlow.pushStrength(9) == 5, "涌流法杖击退成长错误");
		check(WandOfFlow.magicSkillMultiplier(7) == 1.7f, "涌流法杖魔力倍率错误");
		check(wand.image == ItemSpriteSheet.WAND_FLOW, "涌流法杖没有使用旧版图标槽位");
	}

	private static void testTCloudWand() {
		WandOfTCloud wand = new WandOfTCloud();
		check(wand.initialCharges() == 1, "雷云法杖初始充能错误");
		check(!WandOfTCloud.canSummon(9) && WandOfTCloud.canSummon(10),
				"雷云法杖召唤阈值错误");
		wand.curCharges = 9;
		check(wand.chargesPerCast() == 9, "雷云法杖没有消耗全部低充能");
		wand.curCharges = 10;
		check(wand.chargesPerCast() == 10, "雷云法杖没有消耗全部召唤充能");
		WandOfTCloud.TCloud cloud = new WandOfTCloud.TCloud();
		WandOfTCloud.STCloud leaderCloud = new WandOfTCloud.STCloud();
		check(cloud.HT == 200 && cloud.remainingLife() == WandOfTCloud.TCLOUD_LIFETIME,
				"普通雷云生命或寿命错误");
		check(leaderCloud.HT == 100 && leaderCloud.remainingLife() == WandOfTCloud.STCLOUD_LIFETIME,
				"领袖雷云生命或寿命错误");
		check(wand.image == ItemSpriteSheet.WAND_TCLOUD, "雷云法杖没有使用旧版图标槽位");
	}

	private static void testErrorWand() {
		WandOfError wand = new WandOfError();
		check(WandOfError.EFFECT_COUNT == 10, "错误法杖随机结果数量错误");
		check(WandOfError.quarterLifeDamage(100, false) == 25, "错误法杖生命比例伤害错误");
		check(WandOfError.quarterLifeDamage(100, true) == 100, "错误法杖月怒伤害错误");
		check(wand.image == ItemSpriteSheet.WAND_ERROR, "错误法杖没有使用旧版图标槽位");
	}

	private static void testMeteoriteWand() {
		WandOfMeteorite wand = new WandOfMeteorite();
		check(wand.min(0) == 0 && wand.max(0) == 12, "陨星法杖+0伤害错误");
		check(wand.min(5) == 5 && wand.max(5) == 42, "陨星法杖升级成长错误");
		check(WandOfMeteorite.splashDamage(90, 0, 1) == 10, "陨星法杖范围伤害比例错误");
		check(WandOfMeteorite.splashDamage(90, 5, 1) == 15, "陨星法杖范围伤害魔力倍率错误");
		check(WandOfMeteorite.paralysisDurationMax(0) == 5
				&& WandOfMeteorite.paralysisDurationMax(8) == 8, "陨星法杖麻痹时长错误");
		check(wand.initialCharges() == 2, "陨星法杖初始充能错误");
		check(wand.image == ItemSpriteSheet.WAND_METEORITE, "陨星法杖没有使用旧版图标槽位");
	}

	private static void testFlockWand() {
		WandOfFlock wand = new WandOfFlock();
		check(wand.image == ItemSpriteSheet.WAND_FLOCK, "招羊法杖没有使用旧版图标槽位");
		check(!WandOfFlock.protectedPuzzleDestination(0), "安全层被错误视为防作弊推箱地图");
		check(WandOfFlock.protectedPuzzleDestination(1) && WandOfFlock.protectedPuzzleDestination(4),
				"招羊法杖没有识别旧版四张推箱地图");
		check(!WandOfFlock.protectedPuzzleDestination(5), "城镇被错误视为防作弊推箱地图");
		Dungeon.depth = pd.items.quest.AdventureJournal.anchorDepth(22);
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(22);
		check(WandOfFlock.legacyDepth() == 85, "招羊法杖没有使用混沌领域旧版85层深度");
		Dungeon.depth = 1;
		Dungeon.branch = 0;
	}

	private static void testCharmWand() {
		WandOfCharm wand = new WandOfCharm();
		check(wand.initialCharges() == 2, "魅惑法杖初始充能错误");
		check(WandOfCharm.chargesToSpend(1) == 1, "魅惑法杖低充能消耗错误");
		check(WandOfCharm.chargesToSpend(3) == 2 && WandOfCharm.chargesToSpend(10) == 5,
				"魅惑法杖没有向上取整消耗一半充能");
		check(WandOfCharm.charmDurationMax(0, 1) == 1, "魅惑法杖+0时长上界错误");
		check(WandOfCharm.charmDurationMax(5, 3) == 15, "魅惑法杖时长成长错误");
		check(wand.image == ItemSpriteSheet.WAND_CHARM, "魅惑法杖没有使用旧版图标槽位");
	}

	private static void testSwampWand() {
		WandOfSwamp wand = new WandOfSwamp();
		check(wand.min(0) == 0 && wand.max(0) == 8, "沼泽法杖+0伤害错误");
		check(wand.min(5) == 5 && wand.max(5) == 28, "沼泽法杖升级成长错误");
		check(WandOfSwamp.magicSkillMultiplier(7) == 1.7f, "沼泽法杖魔力倍率错误");
		check(SpeedSlow.speedFactor(3f) == 0.7f, "衰减缓速的速度倍率错误");
		check(SpeedSlow.speedFactor(10f) == 0.5f, "衰减缓速没有在50%封顶");
		check(!SwampGas.shouldStandDown(9.99f) && SwampGas.shouldStandDown(10f),
				"沼泽气体的僵直阈值错误");
		check(!SwampGas.ignites(2, 2) && SwampGas.ignites(1, 2), "沼泽气体点火概率错误");
		check(wand.image == ItemSpriteSheet.WAND_POISON, "沼泽法杖没有使用旧版图标槽位");
	}

	private static void testLightWand() {
		WandOfLight wand = new WandOfLight();
		check(wand.min(0) == 3 && wand.max(0) == 6, "强光法杖+0伤害错误");
		check(wand.min(5) == 8 && wand.max(5) == 26, "强光法杖升级成长错误");
		check(!WandOfLight.blindRollSucceeds(2, 0) && WandOfLight.blindRollSucceeds(3, 0),
				"强光法杖基础致盲阈值错误");
		check(WandOfLight.blindRollSucceeds(8, 5), "强光法杖致盲概率没有随等级成长");
		check(wand.image == ItemSpriteSheet.WAND_LIGHT, "强光法杖没有使用旧版图标槽位");
	}

	private static void testFireboltWand() {
		WandOfFirebolt wand = new WandOfFirebolt();
		check(wand.min(0) == 0 && wand.max(0) == 10, "火球法杖+0伤害错误");
		check(wand.min(5) == 5 && wand.max(5) == 40, "火球法杖升级成长错误");
		check(WandOfFirebolt.magicSkillMultiplier(0) == 1f, "火球法杖零魔力倍率错误");
		check(WandOfFirebolt.magicSkillMultiplier(7) == 1.7f, "火球法杖魔力倍率错误");
		check(wand.image == ItemSpriteSheet.WAND_SPS_FIREBOLT, "火球法杖没有使用旧版图标槽位");
	}

	private static void testFreezeWand() {
		WandOfFreeze wand = new WandOfFreeze();
		check(wand.min(0) == 5 && wand.max(0) == 10, "霜冻法杖+0伤害错误");
		check(wand.min(5) == 15 && wand.max(5) == 30, "霜冻法杖升级成长错误");
		check(WandOfFreeze.freezeThreshold(0) == 8, "霜冻法杖+0水中冻结概率错误");
		check(WandOfFreeze.freezeThreshold(5) == 3, "霜冻法杖冻结概率成长错误");
		check(pd.actors.buffs.FrostIce.initialDamage(60_000) == 1000,
				"冻伤首次伤害没有封顶1000");
		check(pd.actors.buffs.FrostIce.movementDamage(30_000) == 500,
				"冻伤移动伤害没有封顶500");
		check(wand.image == ItemSpriteSheet.WAND_FREEZE, "霜冻法杖没有使用旧版图标槽位");
	}

	private static void testAcidWand() {
		WandOfAcid wand = new WandOfAcid();
		check(wand.min(0) == 2 && wand.max(0) == 6, "酸蚀法杖+0伤害错误");
		check(wand.min(5) == 7 && wand.max(5) == 26, "酸蚀法杖升级成长错误");
		check(WandOfAcid.magicSkillMultiplier(0) == 1f, "零魔力倍率错误");
		check(WandOfAcid.magicSkillMultiplier(7) == 1.7f, "魔力倍率没有按每点10%成长");
		check(AcidOoze.tickDamage(30_000, 0) == 1, "腐酸1/6的固定1点伤害错误");
		check(AcidOoze.tickDamage(30_000, 1) == 500, "腐酸伤害没有封顶500");
		check(AcidOoze.tickDamage(300, 5) == 20, "腐酸生命比例伤害错误");
		check(WandOfAcid.oozeEffectClass() == Ooze.class, "酸蚀法杖没有施加旧版普通污泥");
		check(AcidOoze.class.getSuperclass() == Buff.class, "永久腐酸错误继承了会自然消退的污泥");
		check(AcidOoze.class.isAssignableFrom(SpsAcidOoze.class), "迁移期腐酸存档别名失效");
		check(wand.image == ItemSpriteSheet.WAND_ACID, "酸蚀法杖没有使用旧版图标槽位");
	}

	private static void testPortedGeneratorOrder() {
		Class<?>[] expected = { WandOfAcid.class, WandOfFreeze.class, WandOfFirebolt.class,
				WandOfLight.class, WandOfSwamp.class, WandOfBlood.class, WandOfLightning.class,
				WandOfCharm.class, WandOfFlow.class, WandOfFlock.class,
				WandOfMagicMissile.class, WandOfDisintegration.class, WandOfMeteorite.class,
				WandOfError.class, WandOfTCloud.class };
		check(Generator.Category.WAND.classes.length == expected.length, "当前已迁移法杖牌组数量错误");
		for (int i = 0; i < expected.length; i++) {
			check(Generator.Category.WAND.classes[i] == expected[i], "当前已迁移法杖顺序错误：" + i);
			check(Generator.Category.WAND.defaultProbs[i] == (expected[i] == WandOfError.class ? 0f : 5f),
					"旧版法杖生成权重错误：" + i);
		}
	}

	private static void testAcidIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992,
				"物品图集不是256x992像素");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 672; y < 688; y++) {
			for (int x = 160; x < 176; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(ACID_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "酸蚀法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 128; x < 144; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(FREEZE_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "霜冻法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 96; x < 112; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(FIREBOLT_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "火球法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 176; x < 192; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(LIGHT_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "强光法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 16; x < 32; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(SWAMP_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "沼泽法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 64; x < 80; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(CHARM_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "魅惑法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 192; x < 208; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(FLOCK_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "招羊法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 32; x < 48; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(METEORITE_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "陨星法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 224; x < 240; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(ERROR_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "错误法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 208; x < 224; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(TCLOUD_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "雷云法杖图标与旧版像素不一致");
		check(TCLOUD_SHEET_HASH.equals(toHex(digest.digest(
				Files.readAllBytes(new File("sprites/mobs/tcloud.png").toPath())))), "雷云召唤物图集与旧版文件不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 48; x < 64; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(FLOW_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "涌流法杖图标与旧版像素不一致");
		pixels.clear();
		for (int y = 672; y < 688; y++) {
			for (int x = 112; x < 128; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		check(BLOOD_ICON_HASH.equals(toHex(digest.digest(pixels.array()))), "鲜血法杖图标与旧版像素不一致");

		BufferedImage effects = ImageIO.read(new File("effects/effects.png"));
		ByteBuffer rayPixels = ByteBuffer.allocate(16 * 8 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 45; y < 53; y++) {
			for (int x = 16; x < 32; x++) rayPixels.putInt(effects.getRGB(x, y));
		}
		check(WATER_RAY_HASH.equals(toHex(digest.digest(rayPixels.array()))), "涌流法杖水束与旧版像素不一致");

		int[] iconX = { 0, 80, 144 };
		String[] iconHashes = { MAGIC_MISSILE_ICON_HASH, DISINTEGRATION_ICON_HASH, LIGHTNING_ICON_HASH };
		String[] iconNames = { "魔弹", "解离", "雷霆" };
		for (int i = 0; i < iconX.length; i++) {
			pixels.clear();
			for (int y = 672; y < 688; y++) {
				for (int x = iconX[i]; x < iconX[i] + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(iconHashes[i].equals(toHex(digest.digest(pixels.array()))),
					iconNames[i] + "法杖图标与旧版像素不一致");
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
