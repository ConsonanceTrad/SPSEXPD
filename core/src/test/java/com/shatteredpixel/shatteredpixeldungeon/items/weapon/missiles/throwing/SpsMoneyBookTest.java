package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HolyStun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TownNpc;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsMoneyBookTest {

	private static final String ICON_HASH =
			"B605077A6B6EDDA6A74B91AD06EA9737DE67F6CDE416AFA22D2699034FE50B16";

	public static void main(String[] args) throws Exception {
		Game.version = "test";
		try {
			testItemAndSource();
			testCastAndHit();
			testIcon();
			System.out.println("SPS空白账本测试通过：5本掉落、半径2群控、隐身、命中眩晕、消耗和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testItemAndSource() {
		MoneyBook reward = (MoneyBook)new TownNpc().configure(TownNpc.Spec.FRUIT_CAT).SupercreateLoot();
		TownNpc npc = new TownNpc().configure(TownNpc.Spec.FRUIT_CAT);
		check(reward.quantity() == 5 && npc.properties().contains(Char.Property.BEAST),
				"FruitCat没有掉落5本账本或缺少野兽属性");
		check(reward.min() == 3 && reward.max() == 6 && reward.STRReq() == 10
				&& !reward.isUpgradable() && reward.isIdentified() && reward.value() == 0
				&& reward.image == ItemSpriteSheet.MONEY_BOOK, "账本基础数值、售价或图标错误");
	}

	private static void testCastAndHit() {
		Actor.clear();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = 27;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		PlainMob near = new PlainMob(); near.pos = 29; Actor.add(near);
		PlainMob far = new PlainMob(); far.pos = 31; Actor.add(far);
		MoneyBook book = new MoneyBook(2);
		check(book.collect(hero.belongings.backpack), "账本无法放入背包");
		book.execute(hero, MoneyBook.AC_CAST);
		check(book.quantity() == 1 && hero.buff(Invisibility.class) != null
				&& hero.buff(HolyStun.class) != null && near.buff(HolyStun.class) != null
				&& far.buff(HolyStun.class) == null, "账本撕碎的消耗、隐身或半径2群控错误");
		PlainMob hit = new PlainMob();
		book.proc(hero, hit, 4);
		check(hit.buff(HolyStun.class) != null && hit.buff(HolyStun.class).cooldown() == 10f,
				"账本投掷命中没有造成10回合神圣眩晕");
	}

	private static void testIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 848; y < 864; y++) for (int x = 240; x < 256; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(ICON_HASH.equals(actual.toString()), "账本图标与旧版像素不一致：" + actual);
	}

	private static final class PlainMob extends Mob { }

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsMoneyBookTest() { }
}
