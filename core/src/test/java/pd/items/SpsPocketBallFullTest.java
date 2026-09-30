package pd.items;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.WatchOut;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.pets.LegacyPet;
import pd.items.eggs.DogpetEgg;
import pd.items.eggs.Egg;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
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

public final class SpsPocketBallFullTest {
	private static final int[] TYPES = {
			101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206,
			301, 302, 303, 304, 305, 306, 401, 402, 403, 404, 405,
			501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 601, 666
	};
	private static final String ICON_HASH = "95EDACCED59B673ADCDFDAA0651087F0BA7F5F2AFBEAA0070B2E73EAED205652";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testAllPetMappings();
			testSaveAndRelease();
			testRecallAndCommands();
			testOrdinaryLevelTransition();
			testEmptyBallAndFailureSafety();
			testLegacyDepthRules();
			testResourcesAndIcon();
			System.out.println("SPS容魂灯通过：35种宠物映射、生命与冷却存档、释放、召回、指令、普通换层携带、精灵球还原和失败保护均正常。");
		} finally {
			Mob.clearHeldAllies();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testAllPetMappings() {
		check(TYPES.length == 35, "旧版宠物编号数量错误");
		for (int type : TYPES) {
			LegacyPet pet = PocketBallFull.createPet(type);
			Egg egg = PocketBallFull.petEgg(type);
			LegacyPet compatibility = new CapturedPetEgg(type).hatchling();
			check(pet != null && pet.legacyType() == type, "容魂灯缺少宠物编号：" + type);
			check(egg != null, "空精灵球缺少对应宠物蛋：" + type);
			check(compatibility != null && compatibility.legacyType() == type,
					"旧版捕获蛋存档缺少宠物编号：" + type);
		}
		check(PocketBallFull.createPet(1) == null && PocketBallFull.petEgg(1) == null,
				"无效宠物编号错误生成实体");
	}

	private static void testSaveAndRelease() {
		Actor.clear();
		level();
		Hero hero = hero(27);
		Dungeon.depth = 1;
		PocketBallFull lantern = new PocketBallFull(101, 37, 12);
		lantern.collect(hero.belongings.backpack);

		Bundle saved = new Bundle();
		lantern.storeInBundle(saved);
		PocketBallFull restored = new PocketBallFull();
		restored.restoreFromBundle(saved);
		check(restored.pet_type == 101 && restored.pet_hp == 37 && restored.pet_cooldown == 12,
				"容魂灯没有保存宠物编号、生命或奖励冷却");
		check(lantern.actions(hero).contains(PocketBallFull.AC_USE), "无宠物时容魂灯没有开放使用动作");
		check(lantern.release(hero), "容魂灯无法释放有效宠物");
		LegacyPet pet = LegacyPet.active();
		check(pet != null && pet.legacyType() == 101 && pet.HP == 37 && pet.rewardCooldown() == 12,
				"容魂灯没有按保存状态恢复宠物");
		check(hero.belongings.getItem(PocketBallFull.class) == null,
				"成功释放后没有消耗容魂灯");
	}

	private static void testRecallAndCommands() {
		LegacyPet pet = LegacyPet.active();
		Hero hero = Dungeon.hero;
		int oldPos = pet.pos;
		int oldHP = pet.HP;
		int oldCooldown = pet.rewardCooldown();
		check(PocketBallFull.teleportPet(hero), "宠物无法召回英雄身边");
		check(Dungeon.level.adjacent(hero.pos, pet.pos)
				&& pet.HP == oldHP && pet.rewardCooldown() == oldCooldown,
				"宠物召回没有保留状态或落在英雄相邻格：" + oldPos + " -> " + pet.pos
						+ ", hp=" + oldHP + "/" + pet.HP + ", cd=" + oldCooldown + "/" + pet.rewardCooldown());

		PocketBallFull.target(hero);
		check(pet.buff(WatchOut.class) != null && pet.buff(HiddenShadow.class) == null,
				"宠物目标指令没有切换到警戒状态");
		PocketBallFull.distarget(hero);
		check(pet.buff(WatchOut.class) == null && pet.buff(HiddenShadow.class) != null,
				"宠物取消目标指令没有切换到潜行状态");

		PocketBallFull captured = PocketBallFull.removePet(hero);
		check(captured != null && captured.pet_type == 101 && captured.pet_hp == oldHP
				&& captured.pet_cooldown == oldCooldown && LegacyPet.active() == null,
				"移除宠物时没有完整保存状态");
	}

	private static void testEmptyBallAndFailureSafety() {
		Actor.clear();
		TestLevel level = level();
		Hero hero = hero(27);
		LegacyPet dog = PocketBallFull.createPet(201);
		dog.pos = 28;
		level.mobs.add(dog);
		Actor.add(dog);
		new ExposedPocketBall().throwAt(28);
		check(level.heaps.get(28) != null && level.heaps.get(28).peek() instanceof DogpetEgg,
				"空精灵球没有还原宠物对应的原始蛋");
		check(LegacyPet.active() == null, "空精灵球捕获后宠物实体仍然存在");

		PocketBallFull invalid = new PocketBallFull(1, 5);
		invalid.collect(hero.belongings.backpack);
		check(!invalid.release(hero) && hero.belongings.contains(invalid),
				"无效宠物编号导致容魂灯被吞掉");
		Arrays.fill(level.passable, false);
		PocketBallFull blocked = new PocketBallFull(101, 20);
		blocked.collect(hero.belongings.backpack);
		check(PocketBallFull.spawnCell(0) == -1 && !blocked.release(hero)
				&& hero.belongings.contains(blocked), "无合法落点时容魂灯被吞掉或发生越界");
	}

	private static void testOrdinaryLevelTransition() {
		Actor.clear();
		Mob.clearHeldAllies();
		TestLevel source = level();
		Hero hero = hero(27);
		LegacyPet pet = PocketBallFull.createPet(101);
		pet.restoreRuntimeState(41, 13);
		pet.pos = 62;
		pet.stayHere();
		source.mobs.add(pet);
		Actor.add(pet);

		Mob.holdAllies(source);
		check(!source.mobs.contains(pet), "普通换层没有从旧层暂存宠物");
		check(!pet.staying(), "普通换层后宠物仍保留旧层驻守位置");

		TestLevel destination = level();
		Dungeon.hero = hero;
		Mob.restoreAllies(destination, hero.pos);
		check(destination.mobs.contains(pet) && LegacyPet.active() == pet,
				"普通换层没有在新层恢复原宠物实体");
		check(destination.adjacent(hero.pos, pet.pos), "换层后宠物没有落在英雄相邻格");
		check(pet.HP == 41 && pet.rewardCooldown() == 13,
				"普通换层丢失宠物生命或奖励冷却");
	}

	private static void testLegacyDepthRules() {
		Actor.clear();
		TestLevel level = level();
		Hero hero = hero(27);
		LegacyPet pet = PocketBallFull.createPet(201);
		pet.pos = 28;
		level.mobs.add(pet);
		Actor.add(pet);
		PocketBallFull lantern = new PocketBallFull(101, 20);

		Dungeon.depth = AdventureJournal.anchorDepth(0);
		Dungeon.branch = AdventureJournal.branchFor(0);
		check(Dungeon.legacyDepth() == 50 && PocketBallFull.petHomeDepth()
				&& PocketBallFull.canReleaseHere() && lantern.actions(hero).contains(PocketBallFull.AC_USE),
				"旧版50层宠物之家没有允许在已有宠物时继续释放容魂灯");
		check(!PocketBallFull.teleportPet(hero), "旧版50层宠物之家错误允许召回宠物");

		Dungeon.depth = AdventureJournal.anchorDepth(1);
		Dungeon.branch = AdventureJournal.branchFor(1);
		check(Dungeon.legacyDepth() == 51 && !PocketBallFull.canReleaseHere()
				&& !lantern.actions(hero).contains(PocketBallFull.AC_USE),
				"旧版51层错误允许释放容魂灯");

		Dungeon.depth = 25;
		Dungeon.branch = 0;
		check(PocketBallFull.canReleaseHere(), "主线25层错误禁止释放容魂灯");
		Dungeon.depth = 26;
		check(!PocketBallFull.canReleaseHere(), "主线26层错误允许释放容魂灯");
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Actor.clear();
	}

	private static void testResourcesAndIcon() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.pocketballfull.name=", "items.pocketballfull.ac_use=",
				"items.pocketballfull.no_place=", "items.pocketballfull.no_pet="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("容魂灯") && !zh.contains("�"), "容魂灯中文乱码或缺失");
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(ICON_HASH.equals(hash(sheet, ItemSpriteSheet.SPS_POCKET_BALL_FULL)), "容魂灯不是旧版原始图标");
	}

	private static TestLevel level() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Dungeon.quickslot = new QuickSlot();
		PathFinder.setMapSize(level.width(), level.height());
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		Arrays.fill(level.solid, false);
		Arrays.fill(level.avoid, false);
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

	private static String hash(BufferedImage sheet, int itemIndex) throws Exception {
		int left = itemIndex % 16 * 16, top = itemIndex / 16 * 16;
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

	private static final class ExposedPocketBall extends PocketBall {
		void throwAt(int cell) { onThrow(cell); }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			Arrays.fill(heroFOV, true);
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private SpsPocketBallFullTest() { }
}
