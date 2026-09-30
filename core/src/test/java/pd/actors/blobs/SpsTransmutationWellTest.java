package pd.actors.blobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.MitBottle;
import pd.items.StrBottle;
import pd.items.armor.Armor;
import pd.items.armor.LeatherArmor;
import pd.items.armor.glyphs.Stone;
import pd.items.scrolls.ScrollOfMagicalInfusion;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.weapon.Weapon;
import pd.items.weapon.enchantments.Blazing;
import pd.items.weapon.melee.Shortsword;
import pd.journal.Notes;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.features.LevelTransition;
import pd.levels.rooms.secret.SecretWellRoom;
import pd.levels.rooms.special.MagicWellRoom;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import watabou.noosa.Game;
import watabou.utils.FileUtils;
import watabou.utils.SparseArray;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Headless verification for SPS-PD's transmutation well and safe well interactions. */
public final class SpsTransmutationWellTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-transmutation-well" + File.separator);
		Game.version = "test";
		try {
			testTransmutations();
			testWellConsumption();
			testUnsupportedItemSafety();
			testRoomPoolsAndResources();
			System.out.println("SPS嬗变之泉测试通过：旧版转化规则、属性继承、三种井水来源、耗尽逻辑、边界保护和双语文本均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.depth = 1;
			app.exit();
		}
	}

	private static void testTransmutations() {
		Dungeon.depth = 1;
		Dungeon.hero = heroAt(20);
		Dungeon.level = new RecordingLevel(8, 8);
		WaterOfTransmutation water = new WaterOfTransmutation();

		check(water.affectItem(new StrBottle(), 20) instanceof MitBottle,
				"力量瓶没有转化为根骨之瓶");
		check(water.affectItem(new ScrollOfUpgrade(), 20) instanceof ScrollOfMagicalInfusion,
				"升级卷轴没有转化为注魔卷轴");
		check(water.affectItem(new ScrollOfMagicalInfusion(), 20) instanceof ScrollOfUpgrade,
				"注魔卷轴没有转化为升级卷轴");

		Shortsword sword = new Shortsword();
		Blazing enchantment = new Blazing();
		sword.level(3);
		sword.enchantment = enchantment;
		sword.reinforced = true;
		sword.levelKnown = false;
		sword.cursedKnown = true;
		sword.cursed = true;
		Item changedWeapon = water.affectItem(sword, 20);
		check(changedWeapon instanceof Weapon && changedWeapon.getClass() != sword.getClass(),
				"武器没有重抽为不同类型");
		Weapon weapon = (Weapon)changedWeapon;
		check(weapon.trueLevel() == 3 && weapon.enchantment == enchantment && weapon.reinforced,
				"武器等级、附魔或强化状态没有继承");
		check(!weapon.levelKnown && weapon.cursedKnown && weapon.cursed,
				"武器鉴定或诅咒状态没有继承");

		LeatherArmor leather = new LeatherArmor();
		Stone glyph = new Stone();
		leather.level(-2);
		leather.glyph = glyph;
		leather.reinforced = true;
		leather.levelKnown = true;
		leather.cursedKnown = false;
		leather.cursed = true;
		Item changedArmor = water.affectItem(leather, 20);
		check(changedArmor instanceof Armor && changedArmor.getClass() != leather.getClass(),
				"护甲没有重抽为不同类型");
		Armor armor = (Armor)changedArmor;
		check(armor.trueLevel() == -2 && armor.glyph == glyph && armor.reinforced,
				"护甲等级、刻印或强化状态没有继承");
		check(armor.levelKnown && !armor.cursedKnown && armor.cursed,
				"护甲鉴定或诅咒状态没有继承");
	}

	private static void testWellConsumption() {
		Actor.clear();
		Notes.reset();
		RecordingLevel level = new RecordingLevel(8, 8);
		Dungeon.level = level;
		Dungeon.hero = heroAt(10);
		int wellCell = 27;
		level.map[wellCell] = Terrain.WELL;
		Heap heap = heapAt(level, wellCell, new StrBottle());
		WaterOfTransmutation water = new WaterOfTransmutation();
		water.seed(level, wellCell, 1);
		level.blobs.put(WaterOfTransmutation.class, water);

		WellWater.affectCell(wellCell);
		check(heap.peek() instanceof MitBottle, "投入井中的力量瓶没有被替换");
		check(water.volume == 0 && level.map[wellCell] == Terrain.EMPTY_WELL,
				"成功转化后井水没有耗尽或地形没有变为空井");
		check(water.landmark() == Notes.Landmark.WELL_OF_TRANSMUTATION,
				"嬗变之泉没有独立日志地标");
	}

	private static void testUnsupportedItemSafety() {
		Actor.clear();
		RecordingLevel trapped = new RecordingLevel(5, 5);
		Dungeon.level = trapped;
		Dungeon.hero = heroAt(18);
		int edgeWell = 6;
		Heap original = heapAt(trapped, edgeWell, new Gold(3));
		WaterOfTransmutation water = new WaterOfTransmutation();
		water.seed(trapped, edgeWell, 1);
		check(!water.affect(edgeWell), "不支持的物品错误地消耗了井水");
		check(original.peek() instanceof Gold && water.volume == 1,
				"没有合法相邻格时物品未安全保留在原处");

		trapped.passable[7] = true;
		check(!water.affect(edgeWell), "移开不支持的物品时错误地消耗了井水");
		check(trapped.heaps.get(edgeWell) == null && trapped.heaps.get(7) != null
				&& trapped.heaps.get(7).peek() instanceof Gold,
				"不支持的物品没有安全移到唯一合法相邻格");
		check(water.volume == 1, "移开不支持的物品后井水被错误耗尽");
	}

	private static void testRoomPoolsAndResources() throws Exception {
		Class<?>[] expected = {WaterOfAwareness.class, WaterOfHealth.class, WaterOfTransmutation.class};
		check(Arrays.equals(expected, roomWaters(MagicWellRoom.class)), "魔法井房不是旧版三种等权井水");
		check(Arrays.equals(expected, roomWaters(SecretWellRoom.class)), "隐藏井房不是三种等权井水");

		Path actors = Path.of("messages", "actors");
		Path journal = Path.of("messages", "journal");
		String en = strictUtf8(actors.resolve("en/actors.properties"));
		String zh = strictUtf8(actors.resolve("zh/actors.properties"));
		String journalEn = strictUtf8(journal.resolve("en/journal.properties"));
		String journalZh = strictUtf8(journal.resolve("zh/journal.properties"));
		check(en.contains("actors.blobs.wateroftransmutation.name=")
				&& en.contains("actors.blobs.wateroftransmutation.desc="), "嬗变之泉缺少英文文本");
		check(zh.contains("actors.blobs.wateroftransmutation.name=嬗变之泉")
				&& zh.contains("变化的力量正在从这口井的水里涌出。\\n扔进一个物品以将其转化为其他物品。"),
				"嬗变之泉中文文本不完整或与旧版不一致");
		check(journalEn.contains("journal.notes$landmark.well_of_transmutation=")
				&& journalZh.contains("journal.notes$landmark.well_of_transmutation=嬗变之泉"),
				"嬗变之泉缺少双语日志标题");
		check(!zh.contains("�") && !journalZh.contains("�"), "嬗变之泉中文资源出现乱码");
	}

	private static Class<?>[] roomWaters(Class<?> roomClass) throws Exception {
		Field field = roomClass.getDeclaredField("WATERS");
		field.setAccessible(true);
		return (Class<?>[])field.get(null);
	}

	private static String strictUtf8(Path path) throws Exception {
		byte[] bytes = java.nio.file.Files.readAllBytes(path);
		try {
			return StandardCharsets.UTF_8.newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.decode(ByteBuffer.wrap(bytes)).toString();
		} catch (CharacterCodingException error) {
			throw new AssertionError(path + " 不是有效UTF-8", error);
		}
	}

	private static Hero heroAt(int pos) {
		Hero hero = new Hero();
		hero.HT = hero.HP = 20;
		hero.pos = pos;
		return hero;
	}

	private static Heap heapAt(RecordingLevel level, int cell, Item item) {
		Heap heap = new Heap();
		heap.pos = cell;
		heap.items.add(item);
		level.heaps.put(cell, heap);
		return heap;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel(int width, int height) {
			setSize(width, height);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<LevelTransition>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }

		@Override
		public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}
}
