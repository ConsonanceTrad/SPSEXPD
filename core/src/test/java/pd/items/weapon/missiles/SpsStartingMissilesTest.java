package pd.items.weapon.missiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Shocked;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.weapon.missiles.arrows.BlindFruit;
import pd.items.weapon.missiles.throwing.EmpBola;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsStartingMissilesTest {

	private static final String BLIND_HASH = "CC4F4C091578EAD5972CB0900A9DB369EA43E0A9EB0B01E2549494C3DEF529D5";
	private static final String BOLA_HASH = "02A33221A70CDF809CAD030FBBB13D9C2E0E405692C719145B721B7CFF05BF4E";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534D4953534CL);
		try {
			testDefinitions();
			testHitEffects();
			testLegacyIcons();
			System.out.println("SPS开局投掷物测试通过：闪耀果与电磁套索的数值、状态、机械伤害和原始图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testDefinitions() {
		BlindFruit fruit = new BlindFruit(3);
		EmpBola bola = new EmpBola(3);
		check(fruit.quantity() == 3 && fruit.min(0) == 10 && fruit.max(0) == 10 && fruit.STRReq(0) == 10,
				"闪耀果数量或数值错误");
		check(fruit.image == ItemSpriteSheet.LEGACY_BLIND_FRUIT && fruit.value() == 30, "闪耀果图标或价格错误");
		check(bola.quantity() == 3 && bola.min(0) == 5 && bola.max(0) == 10 && bola.STRReq(0) == 10,
				"电磁套索数量或数值错误");
		check(bola.image == ItemSpriteSheet.LEGACY_EMP_BOLA && bola.value() == 30, "电磁套索图标或价格错误");
	}

	private static void testHitEffects() {
		Hero attacker = new Hero();
		attacker.HP = attacker.HT = 100;
		Dungeon.hero = attacker;
		TestMob target = new TestMob(false);
		new BlindFruit().proc(attacker, target, 10);
		check(target.buff(Vertigo.class) != null && target.buff(Silent.class) != null
				&& target.buff(Locked.class) != null && target.buff(Disarm.class) != null,
				"闪耀果没有施加全部四种状态");

		target = new TestMob(true);
		Buff.affect(target, EnergyArmor.class).level(30);
		int before = target.HP;
		new EmpBola().proc(attacker, target, 7);
		check(target.buff(Cripple.class) != null && target.buff(Shocked.class) != null,
				"电磁套索没有施加致残和电击");
		check(target.buff(EnergyArmor.class) == null, "电磁套索没有移除能量护盾");
		check(target.HP == before - target.HT / 3, "电磁套索对机械生物的额外伤害错误");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(BLIND_HASH.equals(hash(sheet, 192, 736)), "闪耀果原始图标错误");
		check(BOLA_HASH.equals(hash(sheet, 208, 736)), "电磁套索原始图标错误");
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(64);
		for (byte value : hash) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static final class TestMob extends Mob {
		TestMob(boolean inorganic) {
			HP = HT =  ninety();
			if (inorganic) properties.add(Property.INORGANIC);
		}
		private static int ninety() { return 90; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
