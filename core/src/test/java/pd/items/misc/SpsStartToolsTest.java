package pd.items.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.bombs.IceBomb;
import pd.items.bombs.SpsFireBomb;
import pd.items.bombs.StormBomb;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Game;
import watabou.utils.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsStartToolsTest {
	private static final String[] HASHES = {
			"A4A2AF569231E20BAF424376A2AF984FD093424231E3F59CBB72A0FBEE97F4C2",
			"F4A1139F40201C4F9C1C57D87E1D38B91CCE92A565AC99C096D5591DB308BC65",
			"4704519FF83EC641A82A9930E06761774A269798CDA84494D0AA5BA84E9414DA",
			"AB5D200EA3797AB48C6B2DAA2149E84ADE787B0A8D53E0AC6C9E6BAF0634B60F",
			"B43D7C805ACDF21AAF915F99F7CE4D21803148295726E54FD3BD58D56B29AD8C",
			"25E9A0A5170A605D4DC6FBA63F7D625031F572EDAB2D748D579D57A236676C35"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testShovelChargeAndSave();
		testBombDefinitions();
		testPulsePistol();
		testLegacyIcons();
		System.out.println("SPS演员与星兵开局工具测试通过：铲子、元素炸弹、脉冲手枪、存档及原始图标均正常。");
	}

	private static void testShovelChargeAndSave() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Shovel shovel = new Shovel();
		check(shovel.charge() == 0 && !shovel.actions(hero).contains(Shovel.AC_USE), "铁铲初始充能错误");
		shovel.gainCharge(40);
		check(shovel.charge() == 40 && shovel.actions(hero).contains(Shovel.AC_USE), "铁铲40点时不能破墙");
		shovel.gainCharge(1000);
		check(shovel.charge() == Shovel.FULL_CHARGE && shovel.actions(hero).contains(Shovel.AC_BUILD), "铁铲充能上限或造墙入口错误");
		Bundle bundle = new Bundle();
		shovel.storeInBundle(bundle);
		Shovel restored = new Shovel();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == Shovel.FULL_CHARGE, "铁铲充能没有随存档恢复");
	}

	private static void testBombDefinitions() {
		check(new SpsFireBomb().image == ItemSpriteSheet.LEGACY_FIRE_BOMB && new SpsFireBomb().value() == 20,
				"旧版火焰炸弹图标或价格错误");
		check(new IceBomb().image == ItemSpriteSheet.LEGACY_ICE_BOMB && new IceBomb().value() == 20,
				"寒霜炸弹图标或价格错误");
		check(new StormBomb().image == ItemSpriteSheet.LEGACY_STORM_BOMB && new StormBomb().value() == 20,
				"风暴炸弹图标或价格错误");
	}

	private static void testPulsePistol() {
		GunOfSoldier gun = new GunOfSoldier();
		for (int i = 0; i < GunOfSoldier.FULL_CHARGE + 20; i++) gun.gainCharge();
		check(gun.charge() == GunOfSoldier.FULL_CHARGE, "脉冲手枪充能没有封顶");
		check(gun.consumeShot() && gun.charge() == 150, "脉冲手枪单发耗能错误");
		Bundle bundle = new Bundle();
		gun.storeInBundle(bundle);
		GunOfSoldier restored = new GunOfSoldier();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 150, "脉冲手枪充能没有随存档恢复");

		Hero attacker = new Hero();
		attacker.HP = attacker.HT = 100;
		Dungeon.hero = attacker;
		TestMob regular = new TestMob(false);
		regular.HP = 60;
		gun.ammo().proc(attacker, regular, 0);
		check(regular.HP == 30, "脉冲手枪普通目标失血伤害错误");
		TestMob boss = new TestMob(true);
		boss.HP = 60;
		gun.ammo().proc(attacker, boss, 0);
		check(boss.HP == 45, "脉冲手枪首领失血伤害错误");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		for (int i = 0; i < HASHES.length; i++) {
			check(HASHES[i].equals(hash(sheet, 128 + i * 16, 128)), "演员或星兵第" + (i + 1) + "个原始图标错误");
		}
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(64);
		for (byte value : hash) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static final class TestMob extends Mob {
		TestMob(boolean boss) {
			HP = HT = 90;
			if (boss) properties.add(Char.Property.BOSS);
		}
	}

	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
