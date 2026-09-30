package pd.items.weapon.missiles.arrows;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Drowsy;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.weapon.missiles.ShitBall;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.items.weapon.missiles.throwing.EmpBola;
import pd.items.weapon.missiles.throwing.EscapeKnive;
import pd.items.weapon.missiles.throwing.Skull;
import pd.items.weapon.missiles.throwing.Wave;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.features.LevelTransition;
import pd.levels.rooms.special.ShopRoom;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;
import com.watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

/** Headless verification for SPS-PD's special arrow pools and effects. */
public final class SpsSpecialArrowsTest {

	private static final String MAGIC_HAND_HASH = "C92C20EB9EEA9F39C5F6B4C6D9903EE190DB266F92940B3FD4815E4BCA13568C";
	private static final String RICE_BALL_HASH = "98A27F038F114784D8761A05C8C061376ED6D5D2CC6447BD1EE7AC1324A01B92";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-special-arrows" + File.separator);
		Game.version = "test";
		try {
			testBaseStatsAndRandomQuantity();
			testMagicHandSteal();
			testRiceBallEffects();
			testGeneratorPools();
			testHuntressAndShopSources();
			testResourcesAndIcons();
			System.out.println("SPS特殊箭矢测试通过：魔术手偷取、糯米团睡眠传送、专用生成池、猎人技能、商店来源、双语文本和原始图标均正常。");
		} finally {
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.depth = 1;
			app.exit();
		}
	}

	private static void testBaseStatsAndRandomQuantity() {
		Arrows base = new Arrows(2, 7);
		check(base.min(0) == 2 && base.max(99) == 7 && base.STRReq(0) == 10,
				"特殊箭矢基类伤害或力量需求错误");
		check(base.isIdentified() && !base.isUpgradable() && base.value() == 2 && base.glowing() != null,
				"特殊箭矢基类鉴定、售价或灰色发光错误");

		MagicHand hand = new MagicHand(5);
		check(hand.image == ItemSpriteSheet.MAGIC_HAND && hand.min(0) == 1 && hand.max(0) == 5
				&& hand.quantity() == 5 && hand.value() == 100, "魔术手基础属性错误");
		for (int i = 0; i < 50; i++) {
			int quantity = new MagicHand().random().quantity();
			check(quantity == 3 || quantity == 4, "魔术手随机数量超出3至4个");
		}

		RiceBall rice = new RiceBall(4);
		check(rice.image == ItemSpriteSheet.RICE_BALL && rice.min(0) == 1 && rice.max(0) == 1
				&& rice.value() == 40, "糯米团基础属性错误");
	}

	private static void testMagicHandSteal() {
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Hero hero = heroAt(34);
		Dungeon.hero = hero;
		LootMob target = new LootMob(false);
		target.pos = 50;

		MagicHand hand = new MagicHand();
		hand.proc(hero, target, 1);
		hand.proc(hero, target, 1);
		check(!target.firstItem && target.lootCalls == 1, "魔术手能从同一怪物重复偷取");
		check(goldAt(level, hero.pos) == 7, "魔术手没有在攻击者位置掉落特殊奖励");

		Hero nonMob = heroAt(35);
		hand.proc(hero, nonMob, 1);
		check(goldAt(level, hero.pos) == 7, "魔术手命中非怪物时错误地产生了奖励");
	}

	private static void testRiceBallEffects() {
		RecordingLevel level = new RecordingLevel();
		level.respawnCell = 90;
		Dungeon.level = level;
		Hero hero = heroAt(34);
		Dungeon.hero = hero;

		LootMob living = new LootMob(false);
		living.pos = 50;
		new RiceBall().proc(hero, living, 1);
		check(living.pos == 90 && living.buff(Drowsy.class) != null,
				"糯米团没有让普通生命目标困倦并传送");

		LootMob undead = new LootMob(true);
		undead.pos = 51;
		new RiceBall().proc(hero, undead, 1);
		check(undead.pos == 51 && undead.buff(Drowsy.class) == null,
				"糯米团错误地影响了亡灵目标");

		level.respawnCell = -1;
		LootMob trapped = new LootMob(false);
		trapped.pos = 52;
		new RiceBall().proc(hero, trapped, 1);
		check(trapped.pos == 52 && trapped.buff(Drowsy.class) != null,
				"糯米团在没有合法传送格时没有安全保留目标位置");
	}

	private static void testGeneratorPools() {
		Class<?>[] arrows = {
				BlindFruit.class, CharmFruit.class, CharmFruit.class,
				FireFruit.class, GlassFruit.class, HealFruit.class,
				IceFruit.class, MagicHand.class, NutFruit.class,
				RocketMissile.class, RootFruit.class, ShockFruit.class,
				SmokeFruit.class, ToxicFruit.class, RiceBall.class
		};
		Class<?>[] ranged = {
				EmpBola.class, EscapeKnive.class, RocketMissile.class, Skull.class,
				Wave.class, ShitBall.class, MagicHand.class
		};
		check(Arrays.equals(Generator.Category.ARROWS.classes, arrows), "ARROWS生成池与旧版15项顺序不一致");
		check(Arrays.equals(Generator.Category.RANGEWEAPON.classes, ranged), "RANGEWEAPON生成池与旧版7项顺序不一致");
		check(Generator.Category.ARROWS.ordinal() > Generator.Category.GOLD.ordinal()
				&& Generator.Category.RANGEWEAPON.ordinal() > Generator.Category.GOLD.ordinal(),
				"新增生成池没有追加在旧类别之后，可能破坏存档序号");
	}

	private static void testHuntressAndShopSources() throws Exception {
		Path root = Path.of("..", "java", "pd");
		String huntress = java.nio.file.Files.readString(root.resolve(Path.of("items", "skills", "HuntressSkill.java")), StandardCharsets.UTF_8);
		check(huntress.contains("dropAtHero(Generator.random(Generator.Category.ARROWS))"),
				"猎人第三技能没有使用旧版专用箭池");

		Dungeon.depth = 6;
		SpsShopRoom shop = new SpsShopRoom();
		Method ensure = SpsShopRoom.class.getDeclaredMethod("ensureItems");
		ensure.setAccessible(true);
		ensure.invoke(shop);
		Field items = ShopRoom.class.getDeclaredField("itemsToSpawn");
		items.setAccessible(true);
		int hands = 0;
		for (Item item : (ArrayList<Item>)items.get(shop)) {
			if (item instanceof MagicHand) hands += item.quantity();
		}
		check(hands >= 5, "章节商店没有固定出售5个魔术手");
	}

	private static void testResourcesAndIcons() throws Exception {
		String en = java.nio.file.Files.readString(Path.of("messages", "items", "items.properties"), StandardCharsets.UTF_8);
		String zh = java.nio.file.Files.readString(Path.of("messages", "items", "items_zh.properties"), StandardCharsets.UTF_8);
		for (String key : new String[]{"items.weapon.missiles.arrows.magichand.name=",
				"items.weapon.missiles.arrows.magichand.desc=",
				"items.weapon.missiles.arrows.riceball.name=",
				"items.weapon.missiles.arrows.riceball.desc="}) {
			check(en.contains(key) && zh.contains(key), "特殊箭矢缺少中英文资源键：" + key);
		}
		check(zh.contains("魔术手") && zh.contains("糯米团") && !zh.contains("�"), "特殊箭矢中文资源乱码");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		check(MAGIC_HAND_HASH.equals(hash(sheet, 16, 960)), "魔术手图标与SPS-PD 0.9.8原图不一致");
		check(RICE_BALL_HASH.equals(hash(sheet, 32, 960)), "糯米团图标与SPS-PD 0.9.8原图不一致");
	}

	private static Hero heroAt(int pos) {
		Hero hero = new Hero();
		hero.HT = hero.HP = 20;
		hero.pos = pos;
		return hero;
	}

	private static int goldAt(Level level, int cell) {
		Heap heap = level.heaps.get(cell);
		if (heap == null) return 0;
		int result = 0;
		for (Item item : heap.items) if (item instanceof Gold) result += item.quantity();
		return result;
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
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

	private static final class LootMob extends Mob {
		int lootCalls;

		LootMob(boolean undead) {
			HT = HP = 20;
			if (undead) properties.add(Char.Property.UNDEAD);
		}

		@Override public Item SupercreateLoot() {
			lootCalls++;
			return new Gold(7);
		}
	}

	private static final class RecordingLevel extends Level {
		int respawnCell = 90;

		RecordingLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			Arrays.fill(passable, true);
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
		@Override public int randomRespawnCell(Char ch) { return respawnCell; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.items.add(item);
			return heap;
		}
	}
}
