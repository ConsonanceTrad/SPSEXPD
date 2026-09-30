package pd.items.weapon.melee.special;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Waterskin;
import pd.items.weapon.melee.Spork;
import pd.items.weapon.melee.normalweapon.ShortSword;
import pd.items.weapon.missiles.throwing.Boomerang;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Game;
import watabou.utils.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsLegacySpecialMeleeTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testRunicBlade();
			testSporkAndTenguSword();
			testLegacyGrowthAndHandcannon();
			testResources();
			System.out.println("SPS特殊近战测试通过：强化成长、负等级、奥能火炮、符文重铸、皇家叉勺、天狗剑、四语文本及原始图标均正常。");
		} finally {
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testRunicBlade() throws Exception {
		RunicBlade blade = new RunicBlade();
		check(blade.image == ItemSpriteSheet.SPS_RUNIC_BLADE, "符文之刃没有使用旧版图标");
		check(blade.min(0) == 0 && blade.max(0) == 35 && blade.STRReq(0) == 18,
				"符文之刃基础数值错误");
		check(!blade.actions(new Hero()).contains(RunicBlade.AC_REFORGE), "零级符文之刃错误开放重铸");
		blade.upgrade(2);
		check(blade.min(2) == 0 && blade.max(2) == 61 && blade.STRReq(2) == 16,
				"符文之刃强化成长错误");
		check(blade.actions(new Hero()).contains(RunicBlade.AC_REFORGE), "强化符文之刃没有开放重铸");

		ShortSword target = new ShortSword();
		check(blade.reforge(target) == 2 && target.level() == 2, "符文之刃前两次必定强化规则错误");
		Boomerang excluded = new Boomerang();
		check(blade.reforge(excluded) == 0 && excluded.level() == 0, "符文之刃错误强化回旋镖");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check("BE7D93D7BA2973F78372442E1A0063B74E1D8BC8989F61BB08BC6E5BB6A7DC06"
				.equals(iconHash(sheet, ItemSpriteSheet.SPS_RUNIC_BLADE)), "符文之刃原始图标错误");
	}

	private static void testSporkAndTenguSword() {
		Spork spork = new Spork();
		check(spork.min(0) == 8 && spork.max(0) == 14 && spork.STRReq(0) == 14,
				"皇家叉勺基础数值错误");
		check(spork.min(2) == 12 && spork.max(2) == 18, "皇家叉勺强化成长错误");

		TenguSword sword = new TenguSword();
		check(sword.min(0) == 7 && sword.max(0) == 9 && sword.STRReq(0) == 12,
				"天狗剑基础数值错误");
		check(sword.min(2) == 11 && sword.max(2) == 17, "天狗剑强化成长错误");
	}

	private static void testLegacyGrowthAndHandcannon() {
		check(new FireCracker().min(3) == 4 && new FireCracker().max(3) == 8,
				"爆竹漏掉旧版近战基类强化成长");
		check(new SJRBMusic().min(3) == 6 && new SJRBMusic().max(3) == 9,
				"鸡乐器漏掉旧版近战基类强化成长");
		check(new TenguSword().min(-2) == 7 && new TenguSword().max(-2) == 9,
				"负等级错误降低天狗剑的旧版固定伤害");
		check(new ShadowEater().min(-2) == 9 && new ShadowEater().max(-2) == 24,
				"负等级错误降低暗噬的旧版固定伤害");
		check(new RunicBlade().max(-2) == 35 && new RunicBlade().STRReq(-2) == 18,
				"负等级错误降低符文之刃伤害或提高力量需求");

		TestHero hero = new TestHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Waterskin skin = new Waterskin(100, 0);
		check(skin.collect(hero.belongings.backpack), "测试露水瓶无法放入背包");
		Handcannon cannon = new Handcannon();
		check(cannon.isReinforced() && cannon.min(-2) == 9 && cannon.max(-2) == 31,
				"奥能火炮强化标记或负等级数值错误");
		cannon.execute(hero, Handcannon.AC_ONOFF);
		check(cannon.turnedOn() && hero.nextCalls == 1, "奥能火炮启动没有结束当前动作");
		TestTarget target = new TestTarget();
		target.HP = target.HT = 1;
		cannon.proc(hero, target, 0);
		check(target.HP <= 0 && skin.checkVol() == 95,
				"奥能火炮没有在目标提前死亡后完成五发射击与露水消耗");
		check(cannon.desc().contains("95") && cannon.desc().contains("ON"),
				"奥能火炮说明没有显示能源或开关状态");

		Bundle saved = new Bundle();
		cannon.storeInBundle(saved);
		Handcannon restored = new Handcannon();
		restored.restoreFromBundle(saved);
		check(restored.turnedOn(), "奥能火炮开关状态没有随存档恢复");
		restored.execute(hero, Handcannon.AC_ONOFF);
		check(!restored.turnedOn() && hero.nextCalls == 2, "奥能火炮关闭没有结束当前动作");
		Dungeon.hero = null;
	}

	private static void testResources() throws Exception {
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String key = "items.weapon.melee.special.runicblade.";
		for (String suffix : new String[]{"name=", "ac_reforge=", "choose=", "reforged=", "desc="}) {
			check(en.contains(key + suffix) && zh.contains(key + suffix), "符文之刃缺少双语文本：" + suffix);
		}
		for (String path : new String[]{"messages/items/en/items.properties", "messages/items/zh/items.properties",
				"messages/items/zh-hant/items.properties", "messages/items/ru/items.properties"}) {
			String text = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
			String cannon = "items.weapon.melee.special.handcannon.";
			for (String suffix : new String[]{"name=", "ac_onoff=", "fuel=", "power_on=", "power_off=", "desc="}) {
				check(text.contains(cannon + suffix), "奥能火炮四语文本缺少键：" + path + " / " + suffix);
			}
			check(!text.contains("\uFFFD"), "特殊近战四语资源包含乱码替换字符：" + path);
		}
		check(!en.contains("\uFFFD") && !zh.contains("\uFFFD"), "符文之刃资源包含乱码替换字符");
	}

	private static final class TestHero extends Hero {
		int nextCalls;
		@Override public int damageRoll() { return 20; }
		@Override public void next() { nextCalls++; }
	}

	private static final class TestTarget extends Mob {
		@Override public void damage(int damage, Object source) { HP -= Math.max(0, damage); }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
	}

	private static String iconHash(BufferedImage image, int itemIndex) throws Exception {
		int left = itemIndex % 16 * 16;
		int top = itemIndex / 16 * 16;
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(image.getRGB(x, y));
		}
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(hash.length * 2);
		for (byte value : hash) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLegacySpecialMeleeTest() { }
}
