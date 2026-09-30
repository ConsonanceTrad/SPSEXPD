package pd.items.food.staplefood;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Light;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Calendar;
import javax.imageio.ImageIO;

public final class SpsPastyTest {

	private static final String[] ICON_HASHES = {
			"16022B064B408E8C82D1D8D1CCEE17BF8CEC40A3FA11E6C451A068F93964E861",
			"AED0892A978052F8DB029EBA7751612FD4351E5816C85880322D7E2DC57D1607",
			"FCCB90478C39D8D1065C0DB542B1AF24FD97B63C892A3EB3DED24B60897DEBE9",
			"946DB444141D1BA3E2A9C850183DDE4F202DE3F934BF27F79100D30452833C7E",
			"9121E94ABC2C6116316BEB72951850C6DCC69C3178A071432912ED945C2C8DA6",
			"E4BFAC76F7F03A72F8DF52204B3EBAD1B3924FF5D08B44BCE2FB1084FEF99343",
			"82EBC5AB57F76D01F51C68809F210B86C98D7BC3ED61183993881B6FB400008B",
			"2191D033DE7214F86D9813EDB9A16457A206A9D37E27AF8F26340B4FF01EE2C5",
			"B80D5EFB99C10A0B70EC5E94881DBA142950BDACFFCB29931F443AD2C5BA5936"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testDefinition();
			testCalendar();
			testImages();
			testPixelIcons();
			testEffects();
			System.out.println("SPS节日肉馅饼测试通过：九种日期规则、原始图标、状态效果、永久生命和金币奖励均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testPixelIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
			for (int y = 848; y < 864; y++) {
				for (int x = slot * 16; x < slot * 16 + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))),
					"第" + (slot + 1) + "个节日食物原始图标错误");
		}
	}

	private static void testDefinition() {
		Pasty pasty = new Pasty();
		check(pasty instanceof StapleFood && pasty.energy == 400f && pasty.value() == 100,
				"肉馅饼主食类型、饱食值或价格错误");
		check(pasty.image == Pasty.imageFor(Pasty.holidayFor(Calendar.getInstance())), "肉馅饼当前日期图标错误");
	}

	private static void testCalendar() {
		checkHoliday(2024, Calendar.JANUARY, 1, Pasty.Holiday.XMAS);
		checkHoliday(2024, Calendar.JANUARY, 18, Pasty.Holiday.SPRING);
		checkHoliday(2024, Calendar.FEBRUARY, 28, Pasty.Holiday.SPRING);
		checkHoliday(2024, Calendar.FEBRUARY, 29, Pasty.Holiday.NONE);
		checkHoliday(2024, Calendar.APRIL, 15, Pasty.Holiday.EASTER);
		checkHoliday(2024, Calendar.MAY, 7, Pasty.Holiday.WORKER);
		checkHoliday(2024, Calendar.MAY, 8, Pasty.Holiday.NONE);
		checkHoliday(2024, Calendar.JUNE, 3, Pasty.Holiday.CHILD);
		checkHoliday(2024, Calendar.JUNE, 4, Pasty.Holiday.NONE);
		checkHoliday(2024, Calendar.JULY, 15, Pasty.Holiday.STUDENT);
		checkHoliday(2024, Calendar.AUGUST, 15, Pasty.Holiday.STUDENT);
		checkHoliday(2024, Calendar.OCTOBER, 20, Pasty.Holiday.HWEEN);
		checkHoliday(2024, Calendar.NOVEMBER, 1, Pasty.Holiday.HWEEN);
		checkHoliday(2024, Calendar.NOVEMBER, 20, Pasty.Holiday.THANK);
		checkHoliday(2024, Calendar.DECEMBER, 1, Pasty.Holiday.THANK);
		checkHoliday(2024, Calendar.DECEMBER, 20, Pasty.Holiday.XMAS);
		checkHoliday(2024, Calendar.MARCH, 15, Pasty.Holiday.NONE);
	}

	private static void testImages() {
		int[] expected = {
				ItemSpriteSheet.SPS_PASTY, ItemSpriteSheet.SPS_SPRING_ASSORTED,
				ItemSpriteSheet.SPS_KNOWLEDGE_FOOD, ItemSpriteSheet.SPS_PASTY_EASTER_EGG,
				ItemSpriteSheet.SPS_PUMPKIN_PIE, ItemSpriteSheet.SPS_TURKEY_MEAT,
				ItemSpriteSheet.SPS_CANDY_CANE, ItemSpriteSheet.SPS_JELLY_SWORD,
				ItemSpriteSheet.SPS_BRICK_FOOD
		};
		Pasty.Holiday[] holidays = {
				Pasty.Holiday.NONE, Pasty.Holiday.SPRING, Pasty.Holiday.STUDENT,
				Pasty.Holiday.EASTER, Pasty.Holiday.HWEEN, Pasty.Holiday.THANK,
				Pasty.Holiday.XMAS, Pasty.Holiday.CHILD, Pasty.Holiday.WORKER
		};
		for (int i = 0; i < holidays.length; i++) {
			check(Pasty.imageFor(holidays[i]) == expected[i], holidays[i] + "节日图标映射错误");
		}
	}

	private static void testEffects() {
		Hero hero = hero(100);
		Pasty.applyHoliday(hero, Pasty.Holiday.SPRING);
		check(hero.buff(BerryRegeneration.class) != null, "春节食物没有提供浆果恢复");

		hero = hero(100);
		Pasty.applyHoliday(hero, Pasty.Holiday.EASTER);
		check(hero.buff(Bless.class) != null && hero.buff(Bless.class).cooldown() >= 5f, "复活节食物祝福错误");

		hero = hero(100);
		Pasty.applyHoliday(hero, Pasty.Holiday.STUDENT);
		check(hero.buff(Light.class) != null && hero.buff(MindVision.class) != null,
				"暑假食物照明或灵视错误");

		hero = hero(100);
		hero.HP = 20;
		Pasty.applyHoliday(hero, Pasty.Holiday.HWEEN);
		check(hero.HP == 30, "万圣节食物治疗错误");

		hero = hero(100);
		Pasty.applyHoliday(hero, Pasty.Holiday.THANK);
		check(hero.buff(HasteBuff.class) != null && hero.buff(Levitation.class) != null,
				"感恩节食物加速或漂浮错误");

		hero = hero(100);
		Pasty.applyHoliday(hero, Pasty.Holiday.XMAS);
		check(hero.buff(Recharging.class) != null, "圣诞食物充能错误");

		hero = hero(100);
		hero.HP = 10;
		int oldHT = hero.HT;
		Pasty.applyHoliday(hero, Pasty.Holiday.CHILD);
		check(hero.HT == oldHT + 3 && hero.HP == hero.HT, "儿童节食物永久生命错误");
		check(hero.buff(Blindness.class) != null && hero.buff(Vertigo.class) != null,
				"儿童节食物负面状态错误");

		hero = hero(100);
		Dungeon.gold = 10;
		Pasty.applyHoliday(hero, Pasty.Holiday.WORKER);
		check(Dungeon.gold == 510, "劳动节食物金币奖励错误");
	}

	private static void checkHoliday(int year, int month, int day, Pasty.Holiday expected) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		check(Pasty.holidayFor(calendar) == expected,
				year + "-" + (month + 1) + "-" + day + "日期规则错误");
	}

	private static Hero hero(int health) {
		Actor.clear();
		Hero hero = new Hero();
		hero.HTBoost = health - Hero.STARTING_HT;
		hero.updateHT(false);
		hero.HP = hero.HT;
		Dungeon.hero = hero;
		return hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private SpsPastyTest() { }
}
