package com.shatteredpixel.shatteredpixeldungeon.items;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TownNpc;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsTestCloakTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		try {
			testUseAndSource();
			testIcon();
			System.out.println("SPS实验斗篷测试通过：三种100回合状态、单件消耗、售价、NPC掉落和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testUseAndSource() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		TestCloak cloak = new TestCloak();
		cloak.quantity(2);
		hero.belongings.backpack.items.add(cloak);
		cloak.execute(hero, TestCloak.AC_USE);
		check(cloak.quantity() == 1 && hero.belongings.backpack.items.contains(cloak),
				"实验斗篷没有按单件消耗");
		check(hero.buff(HasteBuff.class) != null && duration(hero.buff(HasteBuff.class)) == 100f,
				"实验斗篷没有给予100回合双倍移速");
		check(hero.buff(Levitation.class) != null && duration(hero.buff(Levitation.class)) == 100f,
				"实验斗篷没有给予100回合漂浮");
		check(hero.buff(Invisibility.class) != null && duration(hero.buff(Invisibility.class)) == 100f,
				"实验斗篷没有给予100回合隐身");
		check(cloak.value() == 50 && cloak.image == ItemSpriteSheet.TEST_CLOAK,
				"实验斗篷售价或图标槽位错误");
		TownNpc npc = new TownNpc().configure(TownNpc.Spec.SAID_BY_SUN);
		check(npc.SupercreateLoot() instanceof TestCloak
				&& npc.properties().contains(Char.Property.BEAST),
				"SaidbySun特殊掉落或野兽属性未恢复");
	}

	private static float duration(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff buff) {
		return buff.cooldown();
	}

	private static void testIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		String actual = hash(sheet, 96, 880);
		check("58A000776880B1D89209DACBE0783A4F3FB5C561B218F357A64BD00E4EEBF79B".equals(actual),
				"实验斗篷图标与旧版像素不一致：" + actual);
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTestCloakTest() { }
}
