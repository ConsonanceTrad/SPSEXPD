package pd.items.armor.normalarmor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.Generator;
import pd.items.skills.ClassSkill;
import pd.items.skills.WarriorSkill;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

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
	private static final int[] BASE_MIN = {0, 2, 0, 0, 4, 0, 0, 6, 0, 0, 8, 0, 0, 10, 0, 0, 15, 0};
	private static final int[] BASE_MAX = {4, 6, 2, 12, 18, 8, 20, 26, 15, 28, 36, 22, 36, 46, 30, 44, 60, 35};
	private static final int[] MIN_GROWTH = {1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 3, 0};
	private static final int[] MAX_GROWTH = {3, 3, 1, 3, 3, 1, 3, 4, 2, 3, 4, 2, 3, 5, 3, 3, 5, 3};
	private static final int[] STR_OFFSET = {0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1};
	private static final int[] IMAGES = {
			ItemSpriteSheet.SPS_ARMOR_CLOTH, ItemSpriteSheet.SPS_WOODEN_ARMOR, ItemSpriteSheet.SPS_VEST_ARMOR,
			ItemSpriteSheet.SPS_ARMOR_LEATHER, ItemSpriteSheet.SPS_CERAMICS_ARMOR, ItemSpriteSheet.SPS_RUBBER_ARMOR,
			ItemSpriteSheet.SPS_ARMOR_DISC, ItemSpriteSheet.SPS_STONE_ARMOR, ItemSpriteSheet.SPS_CD_ARMOR,
			ItemSpriteSheet.SPS_ARMOR_MAIL, ItemSpriteSheet.SPS_MULTIPLE_ARMOR, ItemSpriteSheet.SPS_STYROFOAM_ARMOR,
			ItemSpriteSheet.SPS_ARMOR_SCALE, ItemSpriteSheet.SPS_BULLET_ARMOR, ItemSpriteSheet.SPS_PROTECTIVE_ARMOR,
			ItemSpriteSheet.SPS_ARMOR_PLATE, ItemSpriteSheet.SPS_MACHINE_ARMOR, ItemSpriteSheet.SPS_PHANTOM_ARMOR
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
		testDefinitionsAndGenerator();
		testHeroFactorsAndDamageReduction();
		testSkillEnergy();
		testUpgradeSaveRestore();
		testLegacyIcons();
		System.out.println("SPS旧版18套普通防具测试通过：牌组、数值、力量、防御、潜行、技能能量、存档和原始图标均正常。");
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
			check(armor.DRMin(0) == BASE_MIN[i] && armor.DRMax(0) == BASE_MAX[i],
					armor.getClass().getSimpleName() + "基础防御错误");
			check(armor.DRMin(4) == BASE_MIN[i] + MIN_GROWTH[i] * 4
					&& armor.DRMax(4) == BASE_MAX[i] + MAX_GROWTH[i] * 4,
					armor.getClass().getSimpleName() + "升级成长错误");
			int expectedStrength = 8 + TIERS[i] * 2 + STR_OFFSET[i];
			check(armor.STRReq(0) == expectedStrength && armor.STRReq(10) == expectedStrength,
					armor.getClass().getSimpleName() + "固定力量需求错误");
			check(armor.image == IMAGES[i], armor.getClass().getSimpleName() + "图标槽错误");
		}
	}

	private static void testHeroFactorsAndDamageReduction() {
		Hero hero = new Hero();
		hero.STR = 100;
		CeramicsArmor ceramics = new CeramicsArmor();
		check(close(ceramics.evasionFactor(hero, 10f), 8f), "防具DEX没有接入闪避结算");
		hero.belongings.armor = new VestArmor();
		check(close(hero.stealth(), 12f), "防具STE没有接入英雄潜行");
		hero.subClass = HeroSubClass.AGENT;
		check(close(hero.stealth(), 17f), "特工潜行加成错误");

		WoodenArmor wooden = new WoodenArmor();
		hero.STR = 20;
		check(wooden.damageReductionFactor(hero, 5) == 14, "超额力量没有增加旧版防御");
		hero.STR = 10;
		check(wooden.damageReductionFactor(hero, 5) == 5, "轻微力量不足处罚不符合旧版");
		hero.STR = 8;
		check(wooden.damageReductionFactor(hero, 5) == 0, "严重力量不足处罚不符合旧版");
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
		check(restored.DRMin() == 24 && restored.DRMax() == 75,
				"升级防具读档后丢失旧版防御成长");
		check(restored.STRReq() == 21, "升级防具读档后力量需求错误变化");
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
