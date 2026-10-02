package pd.items.equipment.weapon.melee.block;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.equipment.weapon.melee.block.GoblinShield;
import pd.items.equipment.weapon.melee.block.SpKnuckles;
import pd.windows.WndGoblin;
import pd.windows.WndShower;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

/** Runtime checks for the two legacy town weapon shops and their weapons. */
public final class SpsTownBlockWeaponsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053424C4F434BL);
		try {
			testStatsAndCombat();
			testChargeAndSave();
			testPurchasesAndNpcRoutes();
			testIconsAndMessages();
			System.out.println("SPS城镇格挡武器测试通过：哥布林神盾、特制指虎、战斗效果、充能存档、购买路径、双语文本和原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testStatsAndCombat() {
		GoblinShield shield = new GoblinShield();
		SpKnuckles knuckles = new SpKnuckles();
		check(shield.tier == 3 && shield.min(0) == 8 && shield.max(0) == 18
				&& shield.min(3) == 14 && shield.max(3) == 27 && shield.STRReq(0) == 14,
				"哥布林神盾数值或成长错误");
		check(knuckles.tier == 1 && knuckles.min(0) == 1 && knuckles.max(0) == 10
				&& knuckles.min(3) == 7 && knuckles.max(3) == 19 && knuckles.STRReq(0) == 10,
				"特制指虎数值或成长错误");

		Hero attacker = hero(100);
		PlainMob defender = mob(100);
		shield.proc(attacker, defender, 10);
		check(attacker.buff(EnergyArmor.class) != null
				&& attacker.buff(EnergyArmor.class).shielding() == 12, "哥布林神盾没有给予八分之一能量护盾");
		for (int i = 0; i < 1000 && defender.buff(Paralysis.class) == null; i++) {
			knuckles.proc(attacker, defender, 10);
		}
		check(attacker.buff(ShieldArmor.class) != null
				&& attacker.buff(ShieldArmor.class).level() == 10, "特制指虎没有给予十分之一物理护盾");
		check(defender.buff(Paralysis.class) != null, "特制指虎没有触发50%麻痹");
	}

	private static void testChargeAndSave() {
		GoblinShield shield = new GoblinShield();
		Hero attacker = hero(80);
		PlainMob defender = mob(80);
		for (int i = 0; i < 7; i++) shield.proc(attacker, defender, 1);
		Bundle bundle = new Bundle();
		shield.storeInBundle(bundle);
		GoblinShield restored = new GoblinShield();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 7 && new GoblinShield().charge() == 0, "神盾充能存档或实例隔离错误");
		attacker.HP = 20;
		restored.triggerEffect(13, attacker, defender, 25);
		check(attacker.HP == 45, "神盾治疗效果错误");
		attacker.HP = 75;
		restored.triggerEffect(13, attacker, defender, 25);
		check(attacker.HP == attacker.HT, "神盾治疗越过生命上限");
		restored.triggerEffect(15, attacker, defender, 1);
		check(restored.level() == 1, "神盾自我升级效果错误");
	}

	private static void testPurchasesAndNpcRoutes() throws Exception {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Dungeon.gold = 3000;
		check(!WndGoblin.purchase() && Dungeon.gold == 3000, "旧版严格金币边界被改变");
		Dungeon.gold = 3001;
		check(WndGoblin.purchase() && Dungeon.gold == 1
				&& hero.belongings.getItem(GoblinShield.class) != null, "哥布林商店购买或扣款错误");
		Dungeon.gold = 3001;
		check(WndShower.purchase() && Dungeon.gold == 1
				&& hero.belongings.getItem(SpKnuckles.class) != null, "Shower商店购买或扣款错误");
		String npc = Files.readString(Paths.get("../java/pd/actors/mobs/npcs/TownNpc.java"), StandardCharsets.UTF_8);
		check(npc.contains("new WndGoblin()") && npc.contains("new WndShower()")
				&& npc.contains("Badges.checkOtilukeRescued()"), "城镇NPC没有接回营救后的购买窗口");
	}

	private static void testIconsAndMessages() throws Exception {
		check(new GoblinShield().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new SpKnuckles().image == SpecificPlaceHolderDict.SOMETHING_0, "城镇格挡武器图标槽错误");
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		check("42C20F707B5351E9C295CBCACA6B89B179D709FDCD14F78CDC19397EC0D9E733".equals(iconHash(sheet, 208, 944)), "哥布林神盾原始图标错误");
		check("4A51C6CF834F7BD014AE6EC0D3E1C12A0144FE82A316A737973604649D0A4834".equals(iconHash(sheet, 224, 944)), "特制指虎原始图标错误");
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8)
				+ Files.readString(Paths.get("messages/windows/zh/windows.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8)
				+ Files.readString(Paths.get("messages/windows/en/windows.properties"), StandardCharsets.UTF_8);
		for (String key : new String[]{"goblinshield.name=", "spknuckles.name="}) {
			check(zh.contains(key) && en.contains(key), "城镇格挡武器缺少双语文本：" + key);
		}
		check(zh.contains("windows.wndgoblin.message=") && zh.contains("windows.wndshower.message=")
				&& en.contains("windows.wndgoblin.message=") && en.contains("windows.wndshower.message=")
				&& !zh.contains("\uFFFD") && !en.contains("\uFFFD"), "购买窗口双语文本缺失或乱码");
	}

	private static String iconHash(BufferedImage image, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(image.getRGB(x, y));
		}
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(hash.length * 2);
		for (byte value : hash) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static PlainMob mob(int health) {
		PlainMob mob = new PlainMob();
		mob.HP = mob.HT = health;
		return mob;
	}

	private static Hero hero(int health) {
		Hero hero = new Hero();
		hero.HP = hero.HT = health;
		Dungeon.hero = hero;
		return hero;
	}

	private static final class PlainMob extends Mob {
		@Override public int damageRoll() { return 10; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTownBlockWeaponsTest() { }
}
