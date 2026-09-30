package pd.actors.mobs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Heap;
import pd.items.Item;
import pd.items.artifacts.DriedRose;
import pd.items.weapon.melee.special.Goei;
import pd.items.weapon.melee.special.TekkoKagi;
import pd.items.weapon.melee.special.WraithBreath;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

/** Runtime checks for SPS-PD's three special kill and NPC reward weapons. */
public final class SpsSpecialRewardWeaponsTest {

	private static final int[][] ICON_CELLS = {{48, 944}, {64, 944}, {80, 944}};
	private static final String[] ICON_HASHES = {
			"241AD9FAF0473953AD5AA50EDEE3C520E56E580BE7F85612A8A86E1AE3220404",
			"F2DF64AE9ACBD419A22C572AA0A34B74D94EB6284C1DC225868A136D310416C6",
			"7A3EB5DBDC6812C311DD31EBE7C1AC75843016701602F61E2D1500CE5033F2EF"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350535350454349L);
		try {
			testStatsAndEffects();
			testSourcesAndPersistence();
			testIconsAndMessages();
			System.out.println("SPS特殊奖励武器测试通过：驱魔御币、攻击之爪、幽灵之息的数值、特效、来源、存档、双语文本和原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.LimitedDrops.reset();
			Statistics.reset();
		}
	}

	private static void testStatsAndEffects() {
		level();
		Hero hero = hero(27);

		Goei goei = new Goei();
		hero.belongings.weapon = goei;
		check(goei.tier == 3 && goei.min(0) == 4 && goei.max(0) == 15
				&& goei.reachFactor(hero) == 2, "驱魔御币基础数值错误");
		PlainMob plain = mob(new PlainMob(), 28, 10_000);
		for (int i = 0; i < 5; i++) goei.proc(hero, plain, 40);
		check(goei.charge() == 5 && plain.HP == 10_000, "驱魔御币前五次攻击错误触发蓄力伤害");
		goei.proc(hero, plain, 40);
		check(goei.charge() == 1 && plain.HP == 9_960, "驱魔御币第六次攻击未触发满额外伤害");
		DemonicMob demonic = mob(new DemonicMob(), 29, 10_000);
		goei.proc(hero, demonic, 40);
		check(demonic.HP == 9_986, "驱魔御币没有对恶魔造成35%额外伤害");

		TekkoKagi tekko = new TekkoKagi();
		hero.belongings.weapon = tekko;
		check(tekko.tier == 1 && tekko.min(0) == 6 && tekko.max(0) == 12
				&& tekko.reachFactor(hero) == 1, "攻击之爪基础数值错误");
		int triggers = 0;
		for (int i = 0; i < 20_000; i++) {
			plain.HP = plain.HT = 100;
			tekko.proc(hero, plain, 0);
			int loss = 100 - plain.HP;
			if (loss > 0) {
				triggers++;
				check(loss >= 25 && loss < 50, "攻击之爪额外伤害越界：" + loss);
			}
		}
		check(triggers > 3_600 && triggers < 4_400, "攻击之爪20%触发率异常：" + triggers);

		WraithBreath breath = new WraithBreath();
		hero.belongings.weapon = breath;
		check(breath.tier == 2 && breath.min(0) == 7 && breath.max(0) == 11
				&& breath.min(3) == 13 && breath.max(3) == 20
				&& breath.reachFactor(hero) == 4, "幽灵之息基础数值或强化成长错误");
		for (int i = 0; i < 200 && plain.buff(Terror.class) == null; i++) breath.proc(hero, plain, 0);
		check(plain.buff(Vertigo.class) != null && plain.buff(Terror.class) != null
				&& plain.buff(Terror.class).object == hero.id(), "幽灵之息没有同时施加眩晕和恐惧");
	}

	private static void testSourcesAndPersistence() throws Exception {
		Goei original = new Goei();
		PlainMob target = new PlainMob();
		for (int i = 0; i < 3; i++) original.proc(new Hero(), target, 0);
		Bundle weaponBundle = new Bundle();
		original.storeInBundle(weaponBundle);
		Goei restored = new Goei();
		restored.restoreFromBundle(weaponBundle);
		check(restored.charge() == 3 && new Goei().charge() == 0, "驱魔御币实例充能存档或隔离失败");

		Statistics.assassinsKilled = 37;
		Bundle stats = new Bundle();
		Statistics.storeInBundle(stats);
		Statistics.assassinsKilled = 0;
		Statistics.restoreFromBundle(stats);
		check(Statistics.assassinsKilled == 37, "刺客击杀数没有随游戏存档保存");
		check(new Assassin().SupercreateLoot() instanceof TekkoKagi,
				"刺客的超级掉落不是攻击之爪");

		RecordingLevel level = level();
		Statistics.assassinsKilled = 99;
		SpsPrisonMobs.Assassin.recordKill(12);
		check(Statistics.assassinsKilled == 100 && count(level, TekkoKagi.class) == 1,
				"第100只刺客没有掉落攻击之爪");
		SpsPrisonMobs.Assassin.recordKill(12);
		check(Statistics.assassinsKilled == 101 && count(level, TekkoKagi.class) == 1,
				"刺客里程碑奖励被重复发放");

		Method ready = TownNpc.class.getDeclaredMethod("renRewardReady");
		ready.setAccessible(true);
		TownNpc ren = new TownNpc().configure(TownNpc.Spec.RENNPC);
		Dungeon.LimitedDrops.reset();
		Statistics.gnollArchersKilled = Statistics.mossySkeletonsKilled = 51;
		Statistics.albinoPiranhasKilled = 51;
		Statistics.goldThievesKilled = 50;
		check(!(Boolean)ready.invoke(ren), "REN在四类击杀未全部超过50时错误发放御币");
		Statistics.goldThievesKilled = 51;
		check((Boolean)ready.invoke(ren), "REN在四类击杀全部超过50时没有开放御币奖励");
		Dungeon.LimitedDrops.SPS_GOEI.drop();
		check(!(Boolean)ready.invoke(ren), "REN会重复发放驱魔御币");
		Bundle limited = new Bundle();
		Dungeon.LimitedDrops.store(limited);
		Dungeon.LimitedDrops.reset();
		Dungeon.LimitedDrops.restore(limited);
		check(Dungeon.LimitedDrops.SPS_GOEI.dropped(), "御币唯一掉落标记没有随游戏存档保存");

		DriedRose.GhostHero ghost = new DriedRose.GhostHero();
		check(field(ghost, "loot") == WraithBreath.class
				&& Math.abs((Float)field(ghost, "lootChance") - .2f) < .0001f,
				"干枯玫瑰幽灵没有配置20%幽灵之息掉落");
	}

	private static void testIconsAndMessages() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		for (int i = 0; i < ICON_CELLS.length; i++) {
			ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
			for (int y = ICON_CELLS[i][1]; y < ICON_CELLS[i][1] + 16; y++) {
				for (int x = ICON_CELLS[i][0]; x < ICON_CELLS[i][0] + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[i].equals(hex(MessageDigest.getInstance("SHA-256").digest(pixels.array()))),
					"第" + (i + 1) + "件特殊奖励武器图标错误");
		}
		check(new Goei().image == ItemSpriteSheet.SPS_GOEI
				&& new TekkoKagi().image == ItemSpriteSheet.SPS_TEKKO_KAGI
				&& new WraithBreath().image == ItemSpriteSheet.SPS_WRAITH_BREATH,
				"特殊奖励武器图标槽绑定错误");

		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(!zh.contains("\uFFFD") && !en.contains("\uFFFD"), "特殊奖励武器文本含UTF-8替换字符");
		for (String key : Arrays.asList("goei.name", "tekkokagi.name", "wraithbreath.name")) {
			check(zh.contains("items.weapon.melee.special." + key + "=")
					&& en.contains("items.weapon.melee.special." + key + "="), "特殊奖励武器缺少双语键：" + key);
		}
		check(zh.contains("items.weapon.melee.special.goei.name=驱魔御币")
				&& zh.contains("items.weapon.melee.special.tekkokagi.name=攻击之爪")
				&& zh.contains("items.weapon.melee.special.wraithbreath.name=幽灵之息"),
				"特殊奖励武器中文文本乱码或名称错误");
	}

	private static Object field(Object object, String name) throws Exception {
		for (Class<?> type = object.getClass(); type != null; type = type.getSuperclass()) {
			try {
				Field field = type.getDeclaredField(name);
				field.setAccessible(true);
				return field.get(object);
			} catch (NoSuchFieldException ignored) { }
		}
		throw new NoSuchFieldException(name);
	}

	private static RecordingLevel level() {
		Actor.clear();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		level.buildFlagMaps();
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

	private static <T extends PlainMob> T mob(T mob, int pos, int health) {
		mob.pos = pos;
		mob.HP = mob.HT = health;
		Actor.add(mob);
		return mob;
	}

	private static int count(RecordingLevel level, Class<? extends Item> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) for (Item item : heap.items) {
			if (type.isInstance(item)) result += item.quantity();
		}
		return result;
	}

	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static class PlainMob extends Mob {
		@Override public int damageRoll() { return 20; }
		@Override public int drRoll() { return 0; }
	}

	private static final class DemonicMob extends PlainMob {
		{ properties.add(Char.Property.DEMONIC); }
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
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
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
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

	private SpsSpecialRewardWeaponsTest() { }
}
