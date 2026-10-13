package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.Magazine;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import javax.imageio.ImageIO;
import pd.atlas.IconEntry;

/** Headless checks for SPS-PD's five firearms, sling, toy gun, and the general magazine. */
public final class SpsGunTest {

	private static final String[] ICON_HASHES = {
			"3A4167185D8CA958CF3D3CB14C6757436C3E39F74FCAE428A6AA567D4037659A",
			"A0A5B8429AE4478D05F808981DD0A2EA6519A07A55E31F639F1ABDD69629C36B",
			"B357E9C1A60B12197026105519F491F9E986FB7505E45BB450D2992232E629A9",
			"CB2AE214FB9388B0596F13FFD007664C07FDBA1A9359DD8745A77DB6F131039F",
			"E0D2DD27C4055D1A82F86AA8F28B974E1483EF74D7004DFCFB6FEE8493265BDA",
			"FC8E4AE53743B648EF6ED0FF0F1FA149F60A41AC5449B8A3F34B60E7BE9706A3",
			"671743419CC5A11C79D911FA556F62A0F5C4FC1AF0463D2905060522C9EA8F58",
			"9E9F0FA6CAFFCAE6AA4FBCEED298FBA965A48D27E0D431C10F7F56D78F90A628"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x53505347554E534CL);
		try {
			testDefinitions();
			testReloadAndShotUse();
			testAgentAndToyRules();
			testMagazineAndSave();
			testLegacyIcons();
			System.out.println("SPS枪械测试通过：五档枪械、投石索、玩具枪、弹匣、特工射击、通用弹匣装填、存档和8个旧版图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testDefinitions() {
		GunWeapon[] guns = {new GunA(), new GunB(), new GunC(), new GunD(), new GunE()};
		//SPSEXPD: 重铸后的五把枪——手枪 / 冲锋枪 / 霰弹枪 / 狙击枪 / 狙击枪
		//SPSEXPD: 枪械不再分阶位，直接断言类型基准数值（min(lvl)=minBase+lvl、max(lvl)=maxBase+growth*lvl）
		int[] capacities = {7, 20, 4, 5, 6};
		int[] min0 = {3, 4, 5, 6, 6};
		int[] max0 = {8, 10, 14, 20, 20};
		int[] min4 = {7, 8, 9, 10, 10};
		int[] max4 = {16, 22, 26, 36, 36};
		int[] str0 = {10, 12, 14, 16, 16};
		int[] str4 = {8, 10, 12, 14, 14};
		IconEntry[] images = {EquipmentEquipWeaponBasicWeaponDict.PISTOL, EquipmentEquipWeaponBasicWeaponDict.MACHINE_GUN,
				pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict.ELECTROMAGNETIC_SHOTGUN,
				EquipmentEquipWeaponBasicWeaponDict.GUN_4, EquipmentEquipWeaponBasicWeaponDict.GUN_4};
		for (int i = 0; i < guns.length; i++) {
			GunWeapon gun = guns[i];
			check(gun.min(0) == min0[i] && gun.max(0) == max0[i],
					gun.getClass().getSimpleName() + "基础伤害错误");
			check(gun.min(4) == min4[i] && gun.max(4) == max4[i],
					gun.getClass().getSimpleName() + "强化成长错误");
			check(gun.STRReq(0) == str0[i] && gun.fullCharge() == capacities[i],
					gun.getClass().getSimpleName() + "力量需求或弹匣容量错误");
			//SPSEXPD: 力量需求随等级下降（+1/+3/+6/+10 各降 1 点，+4 时降 2 点）
			check(gun.STRReq(4) == str4[i],
					gun.getClass().getSimpleName() + "强化后的力量需求错误");
			check(gun.image == images[i], gun.getClass().getSimpleName() + "图标槽错误");
		}

		Sling sling = new Sling();
		check(sling.min(0) == 3 && sling.max(0) == 7 && sling.STRReq(0) == 8,
				"投石索基础数值错误");
		check(sling.min(4) == 11 && sling.max(4) == 23 && sling.fullCharge() == 1,
				"投石索强化成长或容量错误");

		ToyGun toy = new ToyGun();
		check(toy.min(0) == 1 && toy.max(0) == 10 && toy.STRReq(0) == 10,
				"玩具枪基础数值错误");
		check(toy.min(4) == 5 && toy.max(4) == 22 && toy.fullCharge() == 10,
				"玩具枪强化成长或容量错误");
		check(toy.isReinforced(), "玩具枪强化规则错误");
	}

	private static void testReloadAndShotUse() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		GunA gun = new GunA();
		gun.charge = 3;
		//SPSEXPD: 装填消耗一个通用弹匣并把弹匣填满（这里弹匣 7，3 → 7）
		hero.belongings.backpack.items.add(new Magazine());
		check(gun.reloadMagazine(hero) && gun.charge() == 7, "通用弹匣装填没有填满弹匣");
		check(gun.reloadMagazine(hero) == false && gun.charge() == 7, "满弹匣仍可继续装填");
		//SPSEXPD: 装满后不再消耗弹匣
		check(hero.belongings.getItem(Magazine.class) == null, "满弹匣装填仍然消耗了通用弹匣");
		check(gun.reloadTime(hero, false) == 1f && gun.reloadTime(hero, true) == 1f,
				"手枪装填耗时错误");
		check(gun.consumeRound() && gun.charge() == 6, "射击没有消耗一发子弹");
		gun.charge = 0;
		check(!gun.consumeRound() && gun.charge() == 0, "空弹匣仍能射击或扣成负数");
		check(gun.status() == null, "未鉴定枪械泄露了弹匣状态");
		gun.identify(false);
		check("0/7".equals(gun.status()), "鉴定后没有显示弹匣状态");
	}

	private static void testAgentAndToyRules() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		GunA gun = new GunA();
		check(!gun.canShoot(hero) && gun.canReload(hero), "普通职业未装备枪械时的操作规则错误");
		hero.subClass = HeroSubClass.AGENT;
		check(!gun.canShoot(hero), "特工未装备枪械时仍能射击（枪械必须装备）");
		hero.justMoved = true;
		check(Math.abs(gun.new GunAmmo().delayFactor(hero) - 0.1f) < 0.00001f,
				"特工移动后的射击延迟不是0.1回合");
		hero.justMoved = false;
		check(gun.new GunAmmo().delayFactor(hero) > 0.1f, "特工普通射击错误保留了移动加速");

		ToyGun toy = new ToyGun();
		check(!toy.canShoot(hero) && !toy.canReload(hero), "玩具枪未装备时仍可射击或装填");
		check(toy.reloadTime(hero, true) == 3f, "玩具枪装填不是固定3回合");
		TestMob target = new TestMob(100);
		toy.new GunAmmo().proc(hero, target, 10);
		check(target.HP == 100, "玩具枪错误附带了普通枪的额外能量伤害");
	}

	private static void testMagazineAndSave() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TestGun gun = new TestGun();
		gun.charge = 2;

		//SPSEXPD: 一次命中只结算一次伤害——GunAmmo.proc 只转发伤害（法术拆分段已移除），
		//实际扣血由 Char.attack 统一结算，proc 自己不应扣血
		TestMob target = new TestMob(100);
		check(new TestGun().new GunAmmo().proc(hero, target, 20) == 20, "枪弹 proc 改动了伤害");
		check(target.HP == 100, "枪弹 proc 不应自行结算伤害（应由 Char.attack 统一结算一次）");

		Bundle bundle = new Bundle();
		gun.storeInBundle(bundle);
		TestGun restored = new TestGun();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 2, "枪械弹匣没有随存档恢复");

		//SPSEXPD: 通用弹匣——一个弹匣填满任意容量
		Hero owner = new Hero();
		Dungeon.hero = owner;
		owner.belongings.backpack.items.add(new Magazine());
		restored.charge = 0;
		check(restored.reloadMagazine(owner) && restored.charge() == restored.fullCharge(),
				"通用弹匣没有装满枪械");
		check(!owner.belongings.backpack.contains(new Magazine()), "装填后通用弹匣没有从背包消耗");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992,
				"物品图集不是256x992像素");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			pixels.clear();
			for (int y = 624; y < 640; y++) {
				for (int x = slot * 16; x < (slot + 1) * 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			String actual = toHex(digest.digest(pixels.array()));
			check(ICON_HASHES[slot].equals(actual), "第" + (slot + 1) + "个枪械图标与旧版像素不一致");
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static final class TestGun extends GunWeapon {
		TestGun() { super(GunArchetype.CUSTOM); fullCharge = 4; }
		@Override public int min(int lvl) { return 20; }
		@Override public int max(int lvl) { return 20; }
	}

	private static final class TestMob extends Mob {
		TestMob(int health) { HP = HT = health; }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsGunTest() { }
}
