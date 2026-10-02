package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.blobs.SteamWarn;
import pd.actors.buffs.Burning;
import pd.actors.buffs.FireFollower;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.plants.Plant;
import render.utils.data.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsBottleFireTest {

	private static final String ICON_HASH =
			"9EE618749CDB77A119FD845C3C3C7F3E318DE5DC64513F8278B6D443B6C1CF05";

	public static void main(String[] args) throws Exception {
		try {
			testItemAndSource();
			testThrowAndHit();
			testFollowerAndWarning();
			testIcon();
			System.out.println("SPS瓶装火焰测试通过：NPC来源、九宫格点火、命中燃烧、30回合跟随、三回合预警和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testItemAndSource() {
		TownNpc npc = new TownNpc().configure(TownNpc.Spec.LERY);
		BottleFire bottle = (BottleFire)npc.SupercreateLoot();
		check(npc.properties().contains(Char.Property.ELEMENT), "Lery缺少元素属性");
		check(bottle.min() == 1 && bottle.max() == 1 && bottle.STRReq() == 10
				&& !bottle.isUpgradable() && bottle.isIdentified() && bottle.value() == 0
				&& bottle.image == SpecificPlaceHolderDict.SOMETHING_0, "瓶装火焰基础数值、售价或图标错误");
	}

	private static void testThrowAndHit() {
		Actor.clear();
		TestLevel level = level();
		Hero hero = hero(27);
		PlainMob nearby = new PlainMob(); nearby.pos = 28; Actor.add(nearby);
		level.flamable[35] = true;
		BottleFire bottle = new BottleFire();
		bottle.igniteArea(hero, 27);
		check(Blob.volumeAt(28, Fire.class) == 5 && Blob.volumeAt(35, Fire.class) == 5
				&& hero.buff(FireFollower.class) != null && hero.buff(FireFollower.class).left() == 30f,
				"瓶装火焰空地九宫格点火或跟随状态错误");
		PlainMob target = new PlainMob();
		bottle.proc(hero, target, 1);
		check(target.buff(Burning.class) != null && hero.buff(FireFollower.class) != null,
				"瓶装火焰命中没有燃烧目标或刷新跟随状态");
	}

	private static void testFollowerAndWarning() {
		Actor.clear();
		level();
		Hero hero = hero(27);
		FireFollower follower = pd.actors.buffs.Buff
				.affect(hero, FireFollower.class).set(30f);
		follower.act();
		check(follower.left() == 29f && Blob.volumeAt(27, SteamWarn.class) == 4,
				"火焰跟随没有每回合留下4单位蒸汽或递减持续时间");
		SteamWarn warning = (SteamWarn)Dungeon.level.blobs.get(SteamWarn.class);
		warning.act();
		check(Blob.volumeAt(27, Fire.class) == 0, "蒸汽预警在第一回合提前点火");
		warning.act();
		check(Blob.volumeAt(27, Fire.class) == 0, "蒸汽预警在第二回合提前点火");
		warning.act();
		check(Blob.volumeAt(27, Fire.class) == 5, "蒸汽预警没有在第三回合生成火焰");
	}

	private static TestLevel level() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static Hero hero(int pos) {
		Hero hero = new Hero();
		hero.pos = pos;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static void testIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 800; y < 816; y++) for (int x = 208; x < 224; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(ICON_HASH.equals(actual.toString()), "瓶装火焰图标与旧版像素不一致：" + actual);
	}

	private static final class PlainMob extends Mob { }

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs().clear();
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

	private SpsBottleFireTest() { }
}
