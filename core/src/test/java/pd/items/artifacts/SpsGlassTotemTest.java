package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.GlassShield;
import pd.actors.hero.Hero;
import pd.actors.mobs.Gnoll;
import pd.actors.mobs.LichDancer;
import pd.items.Generator;
import pd.items.Item;
import pd.items.bags.ArrowCollecter;
import pd.items.bags.KeyRing;
import pd.items.rings.Ring;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.imageio.ImageIO;

public final class SpsGlassTotemTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Ring.initGems();
		try {
			testChargeAndSave();
			testBlessings();
			testSources();
			testResourcesAndExactSprites();
			System.out.println("SPS玻璃图腾通过：充能、升级、双祝福、诅咒、存档、掉落来源与原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testChargeAndSave() {
		GlassTotem totem = new GlassTotem();
		for (int i = 0; i < 499; i++) totem.advanceCharge();
		check(totem.charge() == 99, "玻璃图腾在500回合前提前充满");
		totem.advanceCharge();
		check(totem.charge() == GlassTotem.FULL_CHARGE, "玻璃图腾未在500回合充满");
		for (int i = 0; i < 20; i++) totem.advanceCharge();
		check(totem.charge() == GlassTotem.FULL_CHARGE, "玻璃图腾充能超过100");

		totem.level(7);
		Bundle bundle = new Bundle();
		totem.storeInBundle(bundle);
		GlassTotem restored = new GlassTotem();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 7 && restored.charge() == GlassTotem.FULL_CHARGE,
				"玻璃图腾等级或充能存档丢失");
	}

	private static void testBlessings() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		GlassTotem totem = new GlassTotem();
		totem.identify();
		hero.belongings.artifact = totem;
		totem.charge = GlassTotem.FULL_CHARGE;
		check(totem.actions(hero).contains(GlassTotem.AC_ATK), "满充能时缺少进攻祝福动作");
		totem.useAttackBlessing(hero);
		check(totem.level() == 1 && totem.charge() == 0, "进攻祝福没有升级或清空充能");
		check(hero.buff(AttackUp.class) != null && hero.buff(AttackUp.class).level() == 8,
				"进攻祝福攻击提升数值错误");
		check(hero.buff(ArmorBreak.class) != null && hero.buff(ArmorBreak.class).level() == 8,
				"进攻祝福护甲破坏数值错误");

		totem.level(3);
		check(totem.actions(hero).contains(GlassTotem.AC_DEF), "三级以上缺少保护祝福动作");
		totem.useDefenceBlessing(hero);
		check(totem.level() == 1 && hero.buff(AttackUp.class) == null,
				"保护祝福没有消耗两级或移除攻击提升");
		check(hero.buff(GlassShield.class) != null && hero.buff(GlassShield.class).turns() == 2,
				"保护祝福没有给予两层玻璃盾");

		clearTestBuffs(hero);
		totem.applyCursedBacklash(hero);
		check(hero.buff(ArmorBreak.class) != null && hero.buff(ArmorBreak.class).level() == 100,
				"诅咒反噬强度错误");
		clearTestBuffs(hero);
		totem.applyFullChargeBlessing(hero);
		check(hero.buff(AttackUp.class) != null && hero.buff(AttackUp.class).level() == 20
				&& hero.buff(DefenceUp.class) != null && hero.buff(DefenceUp.class).level() == 20,
				"满充能随机短祝福数值错误");
	}

	private static void testSources() {
		check(Arrays.asList(Generator.Category.ARTIFACT.classes).contains(GlassTotem.class),
				"玻璃图腾没有接入神器池");
		boolean gnollDropsTotem = false;
		for (int i = 0; i < 300 && !gnollDropsTotem; i++) {
			gnollDropsTotem = new Gnoll().SupercreateLoot() instanceof GlassTotem;
		}
		check(gnollDropsTotem, "豺狼特殊掉落没有玻璃图腾");
		check(LichDancer.rareLoot() instanceof GlassTotem,
				"死灵舞者的20%首领稀有奖励不是玻璃图腾");
	}

	private static void testResourcesAndExactSprites() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.artifacts.glasstotem.name=", "items.artifacts.glasstotem.ac_atk=",
				"items.artifacts.glasstotem.ac_def=", "items.artifacts.glasstotem.desc="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("玻璃图腾") && zh.contains("耗竭-保护祝福") && !zh.contains("�"),
				"玻璃图腾中文乱码或缺失");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check("D13328F8A559659DADE9365CE89EE6EFBB8F60F4FE42E3D1C715576E443761F2".equals(
				hash(sheet, ItemSpriteSheet.SPS_KEY_RING)), "钥匙环不是旧版原始图标");
		check("8F76D96952992282FD2844204B983ED96B8B532EDB36C32CA46B07FFAE3F2090".equals(
				hash(sheet, ItemSpriteSheet.SPS_ARROW_COLLECTER)), "暗器袋不是旧版原始图标");
		check("FF69EA0E65D0DC0FB2991E1CCC9F3E653382C40DE975BD4CAF95C58B3C8A4A77".equals(
				hash(sheet, ItemSpriteSheet.SPS_GLASS_TOTEM)), "玻璃图腾不是旧版原始图标");
		check(new KeyRing().image == ItemSpriteSheet.SPS_KEY_RING
				&& new ArrowCollecter().image == ItemSpriteSheet.SPS_ARROW_COLLECTER
				&& new GlassTotem().image == ItemSpriteSheet.SPS_GLASS_TOTEM,
				"新增物品没有使用各自的旧版图标");
	}

	private static String hash(BufferedImage sheet, int itemIndex) throws Exception {
		int left = (itemIndex % 16) * 16;
		int top = (itemIndex / 16) * 16;
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static void clearTestBuffs(Hero hero) {
		Buff.detach(hero, AttackUp.class);
		Buff.detach(hero, ArmorBreak.class);
		Buff.detach(hero, DefenceUp.class);
		Buff.detach(hero, GlassShield.class);
	}

	private SpsGlassTotemTest() { }
}
