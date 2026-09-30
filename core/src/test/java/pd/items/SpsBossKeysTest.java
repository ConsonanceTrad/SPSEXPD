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
import pd.actors.hero.Hero;
import pd.actors.mobs.pets.BlueDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsBossKeysTest {

	private static final int CENTER = 8 + 8 * 16;

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testIdentityAndDestinations();
			testLegacySaveFields();
			testPetCaptureOrder();
			testOtherPortalSaveFields();
			testOtherPortalPetCapture();
			System.out.println("SPS特殊传送测试通过：挑战信物、日志与特殊入口的旧存档回程点和传送前宠物收回均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.depth = 1;
			Dungeon.branch = 0;
			app.exit();
		}
	}

	private static void testIdentityAndDestinations() {
		Hero hero = state();
		SpsBossKey[] keys = {new Bone(), new ConchShell(), new AncientCoin()};
		int[] destinations = {11, 12, 13};
		int[] images = {ItemSpriteSheet.KING_BONE, ItemSpriteSheet.CAVE_SHELL, ItemSpriteSheet.ANCIENT_COIN};
		for (int i = 0; i < keys.length; i++) {
			SpsBossKey key = keys[i];
			check(key.destination() == destinations[i] && key.image == images[i],
					key.getClass().getSimpleName() + "目的地或图标错误");
			check(key.unique && !key.stackable && key.isIdentified() && !key.isUpgradable(),
					key.getClass().getSimpleName() + "基础属性错误");
			check(SpsBossKey.AC_PORT.equals(key.defaultAction) && key.actions(hero).contains(SpsBossKey.AC_PORT),
					key.getClass().getSimpleName() + "缺少默认传送动作");
			check(key.glowing() != null, key.getClass().getSimpleName() + "缺少黑色发光");
		}
	}

	private static void testLegacySaveFields() {
		for (SpsBossKey key : new SpsBossKey[]{new Bone(), new ConchShell(), new AncientCoin()}) {
			Bundle legacy = new Bundle();
			legacy.put("depth", 17);
			legacy.put("pos", 137);
			key.restoreFromBundle(legacy);
			Bundle migrated = new Bundle();
			key.storeInBundle(migrated);
			check(migrated.getInt("return_depth") == 17 && migrated.getInt("return_branch") == 0
					&& migrated.getInt("return_pos") == 137,
					key.getClass().getSimpleName() + "没有读取0.9.8回程字段");
			check(migrated.getInt("depth") == 17 && migrated.getInt("pos") == 137,
					key.getClass().getSimpleName() + "没有双写0.9.8回程字段");

			Bundle mixed = new Bundle();
			mixed.put("depth", 6);
			mixed.put("pos", 66);
			mixed.put("return_depth", 18);
			mixed.put("return_branch", 2);
			mixed.put("return_pos", 188);
			key.restoreFromBundle(mixed);
			Bundle preferred = new Bundle();
			key.storeInBundle(preferred);
			check(preferred.getInt("return_depth") == 18 && preferred.getInt("return_branch") == 2
					&& preferred.getInt("return_pos") == 188,
					key.getClass().getSimpleName() + "没有优先读取新回程字段");

			key.reset();
			Bundle reset = new Bundle();
			key.storeInBundle(reset);
			check(reset.getInt("return_depth") == -1 && reset.getInt("return_pos") == -1
					&& reset.getInt("depth") == -1,
					key.getClass().getSimpleName() + "重置后仍保留回程点");
		}
	}

	private static void testPetCaptureOrder() {
		for (SpsBossKey key : new SpsBossKey[]{new Bone(), new ConchShell(), new AncientCoin()}) {
			Hero hero = state();
			BlueDragon pet = new BlueDragon();
			pet.HP = 77;
			pet.pos = CENTER + 1;
			Dungeon.level.mobs.add(pet);
			Actor.add(pet);
			hero.belongings.backpack.items.add(key);

			key.execute(hero, SpsBossKey.AC_PORT);
			check(LegacyPet.active() == null, key.getClass().getSimpleName() + "传送前没有收回在场宠物");
			PocketBallFull ball = hero.belongings.getItem(PocketBallFull.class);
			check(ball != null && ball.pet_type == LegacyPet.Kind.BLUE_DRAGON.legacyType && ball.pet_hp == 77,
					key.getClass().getSimpleName() + "没有保留宠物类型和生命");
			check(hero.belongings.backpack.contains(key), key.getClass().getSimpleName() + "在非法地点错误消耗");
		}
	}

	private static void testOtherPortalSaveFields() {
		Item[] portals = {new BossRush(), new PotKey(), new Triforce(), new TreasureMap(),
				new DolyaSlate(), new ChallengeBook()};
		for (Item portal : portals) {
			Bundle legacy = new Bundle();
			legacy.put("depth", 19);
			legacy.put("pos", 191);
			portal.restoreFromBundle(legacy);
			Bundle migrated = new Bundle();
			portal.storeInBundle(migrated);
			check(migrated.getInt("return_depth") == 19 && migrated.getInt("return_pos") == 191,
					portal.getClass().getSimpleName() + "没有读取旧版回程字段");
			check(migrated.getInt("depth") == 19 && migrated.getInt("pos") == 191,
					portal.getClass().getSimpleName() + "没有双写旧版回程字段");
		}
	}

	private static void testOtherPortalPetCapture() {
		Item[] portals = {new BossRush(), new PotKey(), new Triforce(), new TreasureMap()};
		String[] actions = {BossRush.AC_READ, PotKey.AC_PORT, Triforce.AC_PORT, TreasureMap.AC_PORT};
		for (int i = 0; i < portals.length; i++) {
			Hero hero = state();
			Dungeon.depth = portals[i] instanceof Triforce ? 5 : 1;
			BlueDragon pet = new BlueDragon();
			pet.HP = 76;
			pet.pos = CENTER + 1;
			Dungeon.level.mobs.add(pet);
			Actor.add(pet);
			hero.belongings.backpack.items.add(portals[i]);

			portals[i].execute(hero, actions[i]);
			check(LegacyPet.active() == null,
					portals[i].getClass().getSimpleName() + "在检查传送地点前没有收回宠物");
			PocketBallFull ball = hero.belongings.getItem(PocketBallFull.class);
			check(ball != null && ball.pet_hp == 76,
					portals[i].getClass().getSimpleName() + "收回宠物时丢失状态");
			check(hero.belongings.backpack.contains(portals[i]),
					portals[i].getClass().getSimpleName() + "在非法地点错误消耗");
		}
	}

	private static Hero state() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = CENTER;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsBossKeysTest() { }
}
