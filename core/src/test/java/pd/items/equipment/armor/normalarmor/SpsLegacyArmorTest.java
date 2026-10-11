package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.Generator;
import pd.QuickSlot;
import pd.items.BrokenSeal;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.glyphs.Crystalglyph;
import pd.items.skills.ClassSkill;
import pd.items.skills.WarriorSkill;
import render.noosa.Game;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;
import pd.atlas.IconEntry;

/** Headless regression checks for SPS-PD 0.9.8's complete normal armor deck. */
public final class SpsLegacyArmorTest {

	private static final Class<?>[] CLASSES = {
			ClothArmor.class, WoodenArmor.class, VestArmor.class,
			LeatherArmor.class, CeramicsArmor.class, RubberArmor.class,
			DiscArmor.class, StoneArmor.class, CDArmor.class,
			MailArmor.class, MultiplelayerArmor.class, StyrofoamArmor.class,
			ScaleArmor.class, BulletArmor.class, ProtectiveclothingArmor.class,
			PlateArmor.class, MachineArmor.class, PhantomArmor.class
	};
	private static final int[] TIERS = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6};
	private static final float[] DEX = {2f, 1f, 4f, 1.8f, .8f, 3.7f, 1.6f, .6f, 3.4f,
			1.4f, .4f, 3f, 1f, .2f, 2.8f, 1.2f, 0f, 2.4f};
	private static final float[] STE = {6f, 1f, 12f, 5f, .5f, 11f, 4f, 0f, 10f,
			3f, -.5f, 9f, 2f, -1f, 8f, 1f, -2f, 7f};
	private static final int[] ENG = {3, 2, 1, 3, 3, 2, 4, 4, 3, 4, 3, 4, 3, 2, 3, 3, 1, 2};
	private static final IconEntry[] images = {
			EquipmentEquipArmorBasicArmorDict.ARMOR_CLOTH_0, EquipmentEquipArmorBasicArmorDict.WOODEN_ARMOR, SpecificPlaceHolderDict.SOMETHING_0,
			EquipmentEquipArmorBasicArmorDict.ARMOR_LEATHER_0, EquipmentEquipArmorBasicArmorDict.CERAMICS_ARMOR, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, EquipmentEquipArmorBasicArmorDict.STONE_ARMOR, EquipmentEquipArmorBasicArmorDict.CD_ARMOR,
			EquipmentEquipArmorBasicArmorDict.ARMOR_MAIL_0, SpecificPlaceHolderDict.SOMETHING_0, EquipmentEquipArmorBasicArmorDict.STYROFOAM_ARMOR,
			EquipmentEquipArmorBasicArmorDict.ARMOR_SCALE_0, SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			EquipmentEquipArmorBasicArmorDict.ARMOR_PLATE_0, SpecificPlaceHolderDict.SOMETHING_0, EquipmentEquipArmorBasicArmorDict.PHANTOM_ARMOR
	};
	private static final String[] ICON_HASHES = {
			"7FD95F62DACDDB0A00FB78B03579E5F1630CFBC5A8AC8D508A033FDCECB95D6C",
			"A9F8009AC2AE1A9201D55417C7E43D0915C3612CAAC44485A0CD7D6CA7B49083",
			"8A800E5EE5FDF26F246C14762ECBA4187BA0B6591E687B21123DAC26EB50DA98",
			"52520880871D5C66084F36C02522CD6DE4FDFDCDD85053622BD4CCD74DD85E9C",
			"441269405E5E324D7380D8C3486961A766CCC2A0A6228955FADA5D0EB391065B",
			"6AEE951D6AFF8B08A8EEEFA9A81CC8158FBB031863AD75682FFA9B186C84B0D0",
			"3F6DEF45AEE8E0EBE4F814C90D5301AAD92DBAA788A59857D8A1B87D096BA109",
			"FDE6A5BDD6C69F3A0391EFABCA262EDD5072EA085D3E1CD08CFB3AC845926C4E",
			"EAE4E6F8621D8CD1D55DC1AA146F12064824C4FC1EA6E4617F9BA9FCE24247EA",
			"0CFD942557E24951E00E02EAEC14359A21F6AEFCC4F83D5B54C7A2943E7C819E",
			"987AFDF4986B256CDE44C2E336A3DF9CC31D0FA375A9A40892F1C7B4FDD6F584",
			"E08C1C4E82D5F40B3130800391368F6CB4294AA37998E06B58A05432ACFD40BC",
			"31919FD44B7286FF9E837D8356EC486C56A1AED48E7ECB071DACABE354075E2D",
			"453C7B7B868345258D74D01873601ECB381EF7FF153181B8D07AEECE977BA86F",
			"C241E57F40F836D500FDEC8EBBE2323A52CBAA7DB941644937931F622C4F6BDA",
			"17C72486321F36E0F41DD65937699B31C34021CC07A6A2C56926430E23D8500F",
			"219595BB2A2922C8D23EE866F704910E267E37AE0B1AEBC865B61120E24DBA5B",
			"AF2254D86CB24DFE1AF9262E6E461DE10EA75CCCE2F152453AC85A5E83DF8D0F"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		//SPSEXPD: 纹章用例会经 TengusMask 触达 GLog/Messages，而 Messages 的静态初始化要读
		//GameSettings（依赖 Gdx.app）——没有 HeadlessApplication 会 NPE。这里补上，避免误判为功能故障
		com.badlogic.gdx.backends.headless.HeadlessApplication app =
				new com.badlogic.gdx.backends.headless.HeadlessApplication(
						new com.badlogic.gdx.ApplicationAdapter() { },
						new com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration());
		//SPSEXPD: 纹章用例先跑 —— 后面的护甲图标槽断言是本仓既有的基线红（与本功能无关），
		//排在它前面会挡住纹章相关的校验
		try {
			testBrokenSealCarry();
			testDefinitionsAndGenerator();
			testHeroFactorsAndDamageReduction();
			testSkillEnergy();
			testUpgradeSaveRestore();
			testLegacyIcons();
			System.out.println("SPS旧版18套普通防具测试通过：牌组、数值、力量、防御、潜行、技能能量、存档和原始图标均正常。");
		} finally {
			//SPSEXPD: HeadlessApplication 的渲染线程会阻止 JVM 退出，断言失败时也必须关掉
			app.exit();
		}
	}

	private static void testDefinitionsAndGenerator() throws Exception {
		check(Generator.Category.ARMOR.classes.length == CLASSES.length, "普通防具牌组数量错误");
		for (int i = 0; i < CLASSES.length; i++) {
			check(Generator.Category.ARMOR.classes[i] == CLASSES[i], "普通防具生成顺序错误：" + i);
			check(Generator.Category.ARMOR.probs[i] == 1f, "普通防具生成权重错误：" + i);
			NormalArmor armor = (NormalArmor)CLASSES[i].getDeclaredConstructor().newInstance();
			check(armor.tier == TIERS[i], armor.getClass().getSimpleName() + "阶级错误");
			check(close(armor.DEX, DEX[i]) && close(armor.STE, STE[i]) && armor.ENG == ENG[i],
					armor.getClass().getSimpleName() + "闪避、潜行或技能能量错误");
			//SPSEXPD: DR 与力量需求统一到破碎式（Armor）：DRMin=lvl、DRMax=tier*(2+lvl)、STRReq 随等级下降
			check(armor.DRMin(0) == 0 && armor.DRMax(0) == 2 * TIERS[i],
					armor.getClass().getSimpleName() + "基础防御错误");
			check(armor.DRMin(4) == 4 && armor.DRMax(4) == 6 * TIERS[i],
					armor.getClass().getSimpleName() + "升级成长错误");
			check(armor.STRReq(0) == 8 + TIERS[i] * 2 && armor.STRReq(10) == 4 + TIERS[i] * 2,
					armor.getClass().getSimpleName() + "力量需求错误");
			check(armor.image == images[i], armor.getClass().getSimpleName() + "图标槽错误");
		}
	}

	private static void testHeroFactorsAndDamageReduction() {
		Hero hero = new Hero();
		hero.STR = 100;
		CeramicsArmor ceramics = new CeramicsArmor();
		//SPSEXPD: DEX 已收敛为有界加项（1.0 为中性），0.8 的 DEX 把 10 点闪避放大到 9
		check(close(ceramics.evasionFactor(hero, 10f), 9f), "防具DEX没有接入闪避结算");
		hero.belongings.armor = new VestArmor();
		check(close(hero.stealth(), 12f), "防具STE没有接入英雄潜行");
		hero.subClass = HeroSubClass.AGENT;
		check(close(hero.stealth(), 17f), "特工潜行加成错误");

		WoodenArmor wooden = new WoodenArmor();
		hero.STR = 20;
		check(wooden.damageReductionFactor(hero, 5) == 5, "超额力量不再增加减伤");
		hero.STR = 10;
		check(wooden.damageReductionFactor(hero, 5) == 5, "力量刚好时不应扣减伤");
		hero.STR = 8;
		check(wooden.damageReductionFactor(hero, 5) == 1, "力量不足应按每点扣两点减伤");
	}

	private static void testSkillEnergy() throws Exception {
		Hero hero = new Hero();
		hero.belongings.armor = new MailArmor();
		Dungeon.hero = hero;
		ClassSkill.resetCooldown();
		Field charge = ClassSkill.class.getDeclaredField("charge");
		charge.setAccessible(true);
		charge.setInt(null, 1);
		WarriorSkill skill = new WarriorSkill();
		skill.charge(hero);
		Buff charger = null;
		for (Buff buff : hero.buffs()) {
			if (buff.getClass().getSimpleName().equals("SkillCharger")) charger = buff;
		}
		check(charger != null, "职业技能充能器没有附加到英雄");
		for (int i = 0; i < 4; i++) charger.act();
		check(ClassSkill.remainingCooldown() == 1, "ENG=4时技能冷却过早完成");
		charger.act();
		check(ClassSkill.remainingCooldown() == 0, "ENG=4未在5回合完成20点技能冷却");
	}

	private static void testUpgradeSaveRestore() {
		MachineArmor source = new MachineArmor();
		source.upgrade(3);
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		MachineArmor restored = new MachineArmor();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 3, "防具升级等级没有随存档恢复");
		//SPSEXPD: +3 的 T6 护甲 => DRMin 3 / DRMax 6*(2+3)=30；STRReq(3) = (8+12)-2 = 18
		check(restored.DRMin() == 3 && restored.DRMax() == 30,
				"升级防具读档后防御数值错误");
		check(restored.STRReq(restored.level()) == 18, "升级防具读档后力量需求错误变化");
	}

	private static void testBrokenSealCarry() throws Exception {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Dungeon.quickslot = new QuickSlot();

		//SPSEXPD: 纹章随护甲升级记录等级，破损纹章上限 2（原版 1；纹章碎片为 1）
		final int cap = new BrokenSeal().maxCarriedLevel();
		Armor armor = new WoodenArmor();
		BrokenSeal seal = new BrokenSeal();
		armor.affixSeal(seal);
		for (int i = 0; i < cap; i++) armor.upgrade();
		check(seal.level() == cap, "纹章没有随护甲升级记录到 " + cap + " 级：" + seal.level());
		armor.upgrade();
		check(seal.level() == cap, "纹章记录超过 " + cap + " 级上限：" + seal.level());

		//SPSXPD: 摘下纹章时护甲扣回全部携带等级，纹章保留等级
		int before = armor.trueLevel();
		BrokenSeal detached = armor.detachSeal();
		check(detached == seal && detached.level() == cap,
				"摘下纹章后携带等级丢失：" + detached.level());
		check(armor.trueLevel() == before - cap,
				"摘下纹章没有从护甲扣回 " + cap + " 级：" + armor.trueLevel());

		//SPSXPD: 贴到新护甲时携带等级全部转移
		Armor second = new MailArmor();
		int secondBefore = second.trueLevel();
		second.affixSeal(detached);
		check(second.trueLevel() == secondBefore + cap,
				"纹章携带的 " + cap + " 级没有全部转移给新护甲：" + second.trueLevel());

		//SPSXPD: 取消诅咒限制后，携带刻印的纹章可贴附到被诅咒护甲
		Armor cursed = new WoodenArmor();
		cursed.cursed = true;
		cursed.cursedKnown = true;
		BrokenSeal glyphSeal = new BrokenSeal();
		glyphSeal.setGlyph(new Crystalglyph());
		cursed.affixSeal(glyphSeal);
		check(cursed.checkSeal() == glyphSeal && cursed.glyph instanceof Crystalglyph,
				"被诅咒护甲没有接受携带刻印的纹章");

		//SPSEXPD: 拆卸下来的纹章本身可被升级（上限 2）并被附魔道具选中
		BrokenSeal loose = new BrokenSeal();
		check(loose.isUpgradable(), "拆卸下来的纹章不能升级");
		for (int i = 0; i < cap; i++) loose.upgrade();
		check(loose.level() == cap && !loose.isUpgradable(),
				"纹章升到 " + cap + " 级后仍可继续升级：" + loose.level());
		check(pd.items.consum.scrolls.exotic.ScrollOfEnchantment.enchantable(loose),
				"拆卸下来的纹章不能被附魔道具选中");
		loose.inscribe(new Crystalglyph());
		check(loose.getGlyph() instanceof Crystalglyph, "纹章不能被刻上刻印");

		//SPSEXPD: 纹章碎片——同机制但上限 1 级、只携带普通刻印、不提供护盾
		pd.items.SealShard shard = new pd.items.SealShard();
		check(shard.maxCarriedLevel() == 1, "纹章碎片的携带上限不是 1 级：" + shard.maxCarriedLevel());
		check(shard.canCarryGlyph(new Crystalglyph()), "纹章碎片应能携带普通刻印");
		check(shard.maxShield(3, 5) == 0, "纹章碎片不应提供护盾：" + shard.maxShield(3, 5));
		Armor shardArmor = new WoodenArmor();
		shardArmor.affixSeal(shard);
		for (int i = 0; i < 4; i++) shardArmor.upgrade();
		check(shard.level() == 1, "纹章碎片记录超过 1 级上限：" + shard.level());
		check(!shard.isUpgradable(), "纹章碎片达到 1 级后仍可继续升级");

		//SPSEXPD: 完整纹章——天狗面具强化后的成品：上限 5 级、护盾上限比破损纹章多 4
		pd.items.SealComplete complete = new pd.items.SealComplete();
		check(complete.maxCarriedLevel() == 5, "完整纹章的携带上限不是 5 级：" + complete.maxCarriedLevel());
		check(complete.maxShield(3, 5) == new BrokenSeal().maxShield(3, 5) + 4,
				"完整纹章的护盾上限没有比破损纹章多 4：" + complete.maxShield(3, 5));
		check(complete.canCarryGlyph(new Crystalglyph()), "完整纹章应能携带普通刻印");

		//SPSEXPD: 天狗面具——佩戴时把破损纹章/碎片整体换成完整纹章，并保留等级与刻印
		Armor maskArmor = new WoodenArmor();
		BrokenSeal worn = new BrokenSeal();
		worn.level(2);
		worn.setGlyph(new Crystalglyph());
		maskArmor.affixSeal(worn);
		hero.belongings.armor = maskArmor;
		hero.belongings.backpack.items.clear();
		pd.items.SealShard looseShard = new pd.items.SealShard();
		looseShard.collect(hero.belongings.backpack);

		int upgraded = new pd.items.TengusMask().upgradeUniqueItems(hero);
		check(upgraded == 2, "面具没有强化到 2 件专属物品：" + upgraded);
		check(maskArmor.checkSeal() instanceof pd.items.SealComplete
				&& maskArmor.checkSeal().level() == 2
				&& maskArmor.checkSeal().getGlyph() instanceof Crystalglyph,
				"护甲上的破损纹章没有换成完整纹章，或等级/刻印丢失");
		check(hero.belongings.backpack.items.size() == 1
				&& hero.belongings.backpack.items.get(0) instanceof pd.items.SealComplete,
				"背包里的纹章碎片没有换成完整纹章");

		String sealSource = java.nio.file.Files.readString(
				java.nio.file.Paths.get("../java/pd/items/BrokenSeal.java"),
				java.nio.charset.StandardCharsets.UTF_8);
		check(!sealSource.contains("armor.cursed && (getGlyph() == null"),
				"纹章仍对被诅咒护甲保留了刻印类型限制");
		//SPSEXPD: 护甲上已挂另一枚纹章/碎片时必须拒绝贴附，否则会覆盖并把原来那枚弄丢
		check(sealSource.contains("armor.checkSeal() != null && armor.checkSeal() != this"),
				"纹章缺少「护甲已挂另一枚纹章则拒绝贴附」的检查");
		//SPSEXPD: 护盾改为「每 7 回合累积上限的 20%（至少 1 点）」
		check(sealSource.contains("SHIELD_TICK_TURNS = 7") && sealSource.contains("SHIELD_TICK_RATIO = 0.20f"),
				"护盾的累积节奏常量不是每 7 回合 20%");
		check(!sealSource.contains("public synchronized void activate()"),
				"护盾仍保留「补满」用的 activate()");
		String charSource = java.nio.file.Files.readString(
				java.nio.file.Paths.get("../java/pd/actors/Char.java"),
				java.nio.charset.StandardCharsets.UTF_8);
		check(!charSource.contains("shield.activate()"),
				"Char 仍保留「濒死时把护盾补满」的触发式逻辑");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992,
				"物品图集不是256x992像素");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			pixels.clear();
			int left = (slot % 16) * 16;
			int top = 688 + (slot / 16) * 16;
			for (int y = top; y < top + 16; y++) {
				for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))),
					"第" + (slot + 1) + "个普通防具图标与旧版像素不一致");
		}
	}

	private static boolean close(float first, float second) {
		return Math.abs(first - second) < 0.0001f;
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLegacyArmorTest() {
	}
}
