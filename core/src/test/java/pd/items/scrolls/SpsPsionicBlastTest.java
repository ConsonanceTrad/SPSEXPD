package pd.items.scrolls;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.SuperArcane;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.scrolls.exotic.ExoticScroll;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import javax.imageio.ImageIO;

/** Runtime parity checks for the ordinary SPS-PD 0.9.8 psionic-draw scroll. */
public final class SpsPsionicBlastTest {

	private static final int WIDTH = 32;
	private static final int CENTER = 16 + 16 * WIDTH;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-psionic-blast" + File.separator);
		Game.version = "test";
		try {
			testIdentityPoolAndLabels();
			testOrdinaryRead();
			testEmpoweredRead();
			testSaveAndObsoleteLabelMigration();
			testLegacySpritesAndLocalizedResources();
			System.out.println("SPS灵能汲取卷轴通过：普通警觉与30回合灵能、强化视野击杀、14类权重池、识别存档、旧符文素材和四语文本均符合0.9.8。");
		} finally {
			Scroll.clearLabels();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testIdentityPoolAndLabels() {
		Class<?>[] expected = {
				ScrollOfIdentify.class, ScrollOfTeleportation.class, ScrollOfRemoveCurse.class,
				ScrollOfUpgrade.class, ScrollOfRecharging.class, ScrollOfMagicMapping.class,
				ScrollOfRage.class, ScrollOfTerror.class, ScrollOfLullaby.class,
				ScrollOfMagicalInfusion.class, ScrollOfPsionicBlast.class,
				ScrollOfMirrorImage.class, ScrollOfRegrowth.class, ScrollOfDummy.class
		};
		float[] probabilities = {30, 10, 15, 3, 10, 20, 10, 8, 8, 3, 3, 6, 6, 6};
		check(Arrays.equals(Generator.Category.SCROLL.classes, expected),
				"普通卷轴池的类型或顺序不符合0.9.8");
		check(Arrays.equals(Generator.Category.SCROLL.defaultProbs, probabilities)
				&& Arrays.equals(Generator.Category.SCROLL.defaultProbsTotal, probabilities)
				&& Generator.Category.SCROLL.defaultProbs2 == null,
				"普通卷轴池的权重或单牌组规则不符合0.9.8");
		check(!Arrays.asList(expected).contains(ScrollOfRetribution.class)
				&& !Arrays.asList(expected).contains(ScrollOfTransmutation.class)
				&& !Arrays.asList(expected).contains(
						pd.items.scrolls.exotic.ScrollOfPsionicBlast.class),
				"破碎版普通卷轴或灵爆秘卷仍进入SPS普通卷轴池");
		check(pd.items.scrolls.exotic.ScrollOfPsionicBlast.class
				.getSuperclass() == ExoticScroll.class, "破碎版灵爆秘卷源码未保留");

		resetLabels();
		HashSet<Integer> images = new HashSet<>();
		for (Class<?> type : expected) {
			Scroll scroll = (Scroll) render.utils.serialize.Reflection.newInstance(type);
			images.add(scroll.image);
		}
		check(images.size() == 14 && Scroll.getUnknown().size() == 14,
				"14类普通卷轴没有获得14个独立旧版符文标签");

		ScrollOfPsionicBlast scroll = new ScrollOfPsionicBlast();
		check(scroll.getClass().getSuperclass() == Scroll.class
				&& !ExoticScroll.class.isAssignableFrom(scroll.getClass()),
				"SPS灵能卷轴仍是破碎版秘卷");
		check(scroll.consumedValue == 10 && scroll.energyVal() == 10
				&& scroll.initials() == null, "灵能卷轴的炼金值或未识别缩写不符合源码");
		scroll.setKnown();
		check(scroll.value() == 80 && scroll.initials() == 7,
				"灵能卷轴的已知售价或缩写不符合源码");
	}

	private static void testOrdinaryRead() {
		TestLevel level = prepare();
		RecordingMob visible = mobAt(level, CENTER + 1, true);
		RecordingMob unseen = mobAt(level, CENTER + WIDTH * 3, false);
		TestNpc npc = new TestNpc();
		npc.pos = CENTER - 1;
		npc.HP = npc.HT = 20;
		level.mobs().add(npc);
		Actor.add(npc);

		TestScroll scroll = new TestScroll();
		check(scroll.collect(Dungeon.hero.belongings.backpack), "灵能卷轴无法放入背包");
		scroll.setCurrent(Dungeon.hero);
		int heroHp = Dungeon.hero.HP;
		scroll.doRead();

		SuperArcane arcane = Dungeon.hero.buff(SuperArcane.class);
		check(arcane != null && arcane.level() == 2 && arcane.cooldown() == 30f,
				"普通读取没有按非NPC怪物数给予30回合SuperArcane");
		check(visible.beckonedTarget() == CENTER && unseen.beckonedTarget() == CENTER,
				"普通读取没有警觉并召唤本层全部怪物");
		check(visible.damageCalls == 0 && unseen.damageCalls == 0 && Dungeon.hero.HP == heroHp
				&& Dungeon.hero.buff(Blindness.class) == null && Dungeon.hero.buff(Weakness.class) == null,
				"普通读取错误伤害、致盲或虚弱了英雄/怪物");
		check(scroll.readAnimated && Dungeon.hero.belongings.getItem(ScrollOfPsionicBlast.class) == null,
				"普通读取没有消耗卷轴或执行读取动画");
	}

	private static void testEmpoweredRead() {
		TestLevel level = prepare();
		RecordingMob visible = mobAt(level, CENTER + 1, true);
		RecordingMob unseen = mobAt(level, CENTER + WIDTH * 3, false);
		TestScroll scroll = new TestScroll();
		scroll.setCurrent(Dungeon.hero);
		scroll.empoweredRead();

		check(visible.damageCalls == 1 && visible.lastDamage == visible.HT && visible.HP == 0,
				"强化读取没有对视野内怪物造成其最大生命值伤害");
		check(unseen.damageCalls == 0 && unseen.HP == unseen.HT,
				"强化读取错误伤害了视野外怪物");
		check(((RecordingHero) Dungeon.hero).spentAndNext == 1f
				&& Dungeon.hero.buff(SuperArcane.class) == null,
				"强化读取没有耗时1回合或错误给予SuperArcane");
	}

	private static void testSaveAndObsoleteLabelMigration() {
		resetLabels();
		ScrollOfPsionicBlast scroll = new ScrollOfPsionicBlast();
		scroll.setKnown();
		Bundle savedLabels = new Bundle();
		Scroll.save(savedLabels);
		Scroll.clearLabels();
		Scroll.restore(savedLabels);
		check(new ScrollOfPsionicBlast().isKnown(), "灵能卷轴识别状态没有随存档恢复");

		Bundle old = new Bundle();
		old.put("ScrollOfIdentify_label", "ODAL");
		old.put("ScrollOfIdentify_known", true);
		Scroll.clearLabels();
		Scroll.restore(old);
		ScrollOfIdentify migrated = new ScrollOfIdentify();
		check(migrated.isKnown() && !"ODAL".equals(migrated.rune) && migrated.image != 0,
				"旧ODAL标签没有安全迁移，可能在载入物品图标时崩溃");

		Bundle itemBundle = new Bundle();
		new ScrollOfPsionicBlast().quantity(3).storeInBundle(itemBundle);
		ScrollOfPsionicBlast restored = new ScrollOfPsionicBlast();
		restored.restoreFromBundle(itemBundle);
		check(restored.quantity() == 3 && restored.consumedValue == 10,
				"灵能卷轴数量存档或固有炼金值恢复错误");
	}

	private static void testLegacySpritesAndLocalizedResources() throws Exception {
		BufferedImage current = ImageIO.read(Path.of("sprites/items", "items.png").toFile());
		//外部 0.9.8 基准缺失时回退到仓库内置参考源码
		java.nio.file.Path legacyPng = Path.of("..", "..", "..", "..",
				"SPS-PD-0.9.8", "SPS-PD-0.9.8", "assets", "items.png");
		if (!legacyPng.toFile().exists()) legacyPng = Path.of("..", "..", "..", "_ref", "ref", "SPS-PD", "assets", "items.png");
		BufferedImage legacy = ImageIO.read(legacyPng.toFile());
		for (int source = 0; source < 14; source++) {
			int destination = source == 13 ? 14 : source;
			for (int y = 0; y < 16; y++) {
				for (int x = 0; x < 16; x++) {
					check(current.getRGB(destination * 16 + x, 19 * 16 + y)
							== legacy.getRGB(source * 16 + x, 23 * 16 + y),
							"旧版卷轴符文素材不一致：" + source + "," + x + "," + y);
				}
			}
		}
		check(ItemSpriteSheet.SCROLL_NCOSRANE != ItemSpriteSheet.SCROLL_NENDIL
				&& ItemSpriteSheet.SCROLL_NENDIL != ItemSpriteSheet.SCROLL_LIBRA,
				"新增旧版符文图标槽发生冲突");

		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ondeath", "desc"}) {
				required(items, "items.scrolls.scrollofpsionicblast." + key, file);
			}
			for (String rune : new String[]{"ncosrane", "nendil", "libra"}) {
				required(items, "items.scrolls.scroll." + rune, file);
			}
		}
		try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				String text = java.nio.file.Files.readString(path, StandardCharsets.UTF_8);
				check(!text.contains("\uFFFD"), "资源含替换字符：" + path);
			}
		}
	}

	private static TestLevel prepare() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		RecordingHero hero = new RecordingHero();
		hero.pos = CENTER;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		resetLabels();
		return level;
	}

	private static RecordingMob mobAt(TestLevel level, int position, boolean visible) {
		RecordingMob mob = new RecordingMob();
		mob.pos = position;
		mob.HP = mob.HT = 20;
		level.heroFOV[position] = visible;
		level.mobs().add(mob);
		Actor.add(mob);
		return mob;
	}

	private static void resetLabels() {
		Scroll.clearLabels();
		Scroll.initLabels();
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(
				java.nio.file.Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static String required(Properties properties, String key, String file) {
		String value = properties.getProperty(key);
		check(value != null && !value.isEmpty(), file + "缺少文本：" + key);
		return value;
	}

	private static final class TestScroll extends ScrollOfPsionicBlast {
		boolean readAnimated;
		@Override public void readAnimation() { readAnimated = true; }
	}

	private static final class RecordingHero extends Hero {
		float spentAndNext = -1f;
		@Override public void spendAndNext(float time) { spentAndNext = time; }
	}

	private static final class RecordingMob extends Mob {
		int damageCalls;
		int lastDamage;
		int beckonedTarget() { return target; }
		@Override public void damage(int damage, Object source) {
			damageCalls++;
			lastDamage = damage;
			HP -= damage;
		}
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
	}

	private static final class TestNpc extends NPC {
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(WIDTH, WIDTH);
			mobs().clear(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
			Arrays.fill(heroFOV, false);
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

	private SpsPsionicBlastTest() { }
}
