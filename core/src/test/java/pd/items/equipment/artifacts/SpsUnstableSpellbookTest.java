package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.items.DewVial;
import pd.items.Heap;
import pd.items.Item;
import pd.items.quest.AdventureJournal;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfLullaby;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfMirrorImage;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.items.consum.scrolls.ScrollOfRegrowth;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.consum.scrolls.ScrollOfTerror;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's unstable spellbook. */
public final class SpsUnstableSpellbookTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-unstable-spellbook" + File.separator);
		Game.version = "test";
		try {
			Badges.loadGlobal();
			testActionsCapacityAndDew();
			testRandomScrollDispatch();
			testSongTiers();
			testRechargeAndSaveCompatibility();
			testEmpoweredScrollCoverage();
			testMirrorDepthRestriction();
			testLocalizedResourcesAndSourceGuards();
			System.out.println("SPS不稳定魔典测试通过：露珠升级、14类随机卷轴、圣歌、旧版充能、存档和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testMirrorDepthRestriction() {
		Dungeon.depth = 12;
		Dungeon.branch = 0;
		check(!ScrollOfMirrorImage.legacyMagicBlocked(),
				"主线镜像卷轴被错误阻止");
		Dungeon.depth = 14;
		Dungeon.branch = AdventureJournal.branchFor(22);
		check(ScrollOfMirrorImage.legacyMagicBlocked(),
				"混沌85层没有阻止旧版镜像魔法");
		Dungeon.branch = 0;
	}

	private static void testActionsCapacityAndDew() {
		RecordingHero hero = prepareHero(false);
		TestBook book = equip(hero);
		check(book.chargeValue() == 2 && book.capValue() == 2, "零级魔典初始容量或充能不是2");
		check(book.actions(hero).contains(UnstableSpellbook.AC_READ)
				&& book.actions(hero).contains(UnstableSpellbook.AC_ADD)
				&& !book.actions(hero).contains(UnstableSpellbook.AC_SONG), "装备动作条件不符合旧版");
		MagicImmune immunity = new MagicImmune();
		check(immunity.attachTo(hero) && book.actions(hero).contains(UnstableSpellbook.AC_READ),
				"魔法免疫错误隐藏魔典阅读动作");

		int[] capacities = {3, 4, 4, 5, 5, 6, 6, 7, 7, 8};
		for (int expected : capacities) {
			book.upgrade();
			check(book.capValue() == expected, "升级后的旧版容量公式错误：" + book.level());
		}

		hero = prepareHero(false);
		book = equip(hero);
		DewVial vial = new DewVial(0, 100);
		hero.belongings.backpack.items.add(vial);
		check(!book.upgradeNow(hero) && book.level() == 0 && vial.checkVolEx() == 100,
				"露珠恰好等于需求时不应允许升级");
		vial.setVol(0, 101);
		check(book.upgradeNow(hero) && book.level() == 1 && book.capValue() == 3
				&& vial.checkVolEx() == 1 && close(hero.spent, 2f),
				"露珠严格大于需求时没有扣除100点并升级魔典");
	}

	private static void testRandomScrollDispatch() {
		RecordingHero hero = prepareHero(false);
		RecordingScroll regular = new RecordingScroll();
		TestBook book = equip(hero);
		book.setCharge(2);
		book.nextScroll = regular;
		book.regularEffect = true;
		book.readNow(hero);
		check(regular.normalReads == 1 && regular.empoweredReads == 0 && book.chargeValue() == 1,
				"普通随机分支没有消耗一格并调用普通卷轴效果");
		check(regular.ownedByBook && regular.talentChance == 0 && regular.isAnonymous(),
				"书中卷轴仍会消耗实体、鉴定符文或触发卷轴天赋");

		RecordingScroll empowered = new RecordingScroll();
		book.nextScroll = empowered;
		book.regularEffect = false;
		book.readNow(hero);
		check(empowered.normalReads == 0 && empowered.empoweredReads == 1 && book.chargeValue() == 0,
				"强化随机分支没有消耗一格并调用强化卷轴效果");
	}

	private static void testSongTiers() throws Exception {
		RecordingHero level4 = prepareHero(true);
		TestBook book4 = carry(level4, 4);
		check(book4.actions(level4).contains(UnstableSpellbook.AC_SONG), "四级未装备魔典没有显示圣歌");
		check(book4.singNow(level4), "四级圣歌没有执行");
		check(level4.buff(AttackUp.class) != null && level4.buff(AttackUp.class).level() == 25
				&& level4.buff(DefenceUp.class) != null && level4.buff(DefenceUp.class).level() == 25
				&& level4.buff(Arcane.class) != null && level4.buff(TargetShoot.class) != null,
				"四级圣歌没有施加攻击、防御、奥术和瞄准强化");
		checkSongConsumed(level4, book4);

		RecordingHero level8 = prepareHero(true);
		int attack8 = rawSkill(level8, "attackSkill");
		int defense8 = rawSkill(level8, "defenseSkill");
		TestBook book8 = carry(level8, 8);
		check(book8.singNow(level8), "八级圣歌没有执行");
		check(rawSkill(level8, "attackSkill") == attack8 + 1
				&& rawSkill(level8, "defenseSkill") == defense8 + 1,
				"八级圣歌没有永久增加一点命中和闪避");

		RecordingHero level10 = prepareHero(true);
		int magic10 = rawSkill(level10, "magicSkill");
		TestBook book10 = carry(level10, 10);
		check(book10.singNow(level10), "十级圣歌没有执行");
		check(rawSkill(level10, "magicSkill") == magic10 + 1
				&& level10.buff(Invisibility.class) != null && level10.buff(HasteBuff.class) != null,
				"十级圣歌没有永久法强、隐形或加速效果");
	}

	private static void testRechargeAndSaveCompatibility() {
		RecordingHero hero = prepareHero(false);
		TestBook book = equip(hero);
		for (int i = 0; i < 10; i++) book.upgrade();
		book.setCharge(0);
		UnstableSpellbook.bookRecharge recharge = book.new bookRecharge();
		check(recharge.attachTo(hero), "魔典被动充能状态无法附加");
		for (int i = 0; i < 31; i++) recharge.act();
		check(book.chargeValue() == 1 && book.partialValue() < 0.05f,
				"空的十级魔典没有按约30回合一格的旧版公式充能");
		int beforeExternal = book.chargeValue();
		book.charge(hero, 100f);
		check(book.chargeValue() == beforeExternal, "外部神器供能仍会增加魔典充能");
		check(new MagicImmune().attachTo(hero), "测试魔法免疫状态无法附加");
		for (int i = 0; i < 46; i++) recharge.act();
		check(book.chargeValue() == 2, "魔法免疫错误阻止魔典被动充能");

		book.setCharge(7);
		book.setPartial(0.625f);
		Bundle saved = new Bundle();
		book.storeInBundle(saved);
		TestBook restored = new TestBook();
		restored.restoreFromBundle(saved);
		check(restored.level() == 10 && restored.capValue() == 8 && restored.chargeValue() == 7
				&& close(restored.partialValue(), 0.625f), "魔典等级、容量、充能或部分充能读档错误");
	}

	private static void testEmpoweredScrollCoverage() throws Exception {
		Class<?>[] empowered = {
				ScrollOfIdentify.class, ScrollOfLullaby.class, ScrollOfMagicMapping.class,
				ScrollOfRage.class, ScrollOfRemoveCurse.class, ScrollOfTerror.class,
				ScrollOfTeleportation.class, ScrollOfUpgrade.class, ScrollOfRecharging.class,
				ScrollOfMirrorImage.class, ScrollOfPsionicBlast.class, ScrollOfRegrowth.class,
				ScrollOfMagicalInfusion.class
		};
		check(empowered.length == 13, "旧版随机卷轴池数量不是13");
		for (Class<?> type : empowered) {
			check(type.getDeclaredMethod("empoweredRead").getDeclaringClass() == type,
					type.getSimpleName() + "没有独立强化入口");
		}
		new ScrollOfUpgrade().empoweredRead();
		new ScrollOfMagicalInfusion().empoweredRead();

		RecordingHero hero = prepareHero(false);
		TestItem cursed = new TestItem();
		cursed.level(-2);
		cursed.setCursed();
		check(ScrollOfRemoveCurse.uncurse(hero, cursed) && !cursed.cursed && cursed.level() == 2,
				"旧版祛邪没有解除诅咒并反转负等级");
	}

	private static void testLocalizedResourcesAndSourceGuards() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_read", "ac_add", "ac_song", "blinded",
					"no_charge", "cursed", "update", "dew_empty", "desc", "desc_cursed", "desc_index"}) {
				required(items, "items.equipment.artifacts.unstablespellbook." + key, file);
			}
			check(items.getProperty("items.equipment.artifacts.unstablespellbook.desc_index").contains("%s"),
					file + "的露珠升级说明缺少数量占位符");
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("耗竭-圣歌".equals(zh.getProperty("items.equipment.artifacts.unstablespellbook.ac_song"))
				&& zh.getProperty("items.equipment.artifacts.unstablespellbook.desc_index").contains("露珠"),
				"不稳定魔典简体中文不是旧版文案或出现乱码");

		//SPSEXPD: 修正既有测试的相对路径（工作目录为 core/src/test）
		String book = Files.readString(Path.of("../java/pd/items/equipment/artifacts/UnstableSpellbook.java"));
		check(book.contains("Random.Int(15) < level()")
				&& book.contains("attempts < 100")
				&& book.contains("vial.checkVol() <= cost")
				&& book.contains("1 / (150f - (chargeCap - charge)*15f)")
				&& !book.contains("RingOfEnergy.artifactChargeMultiplier"),
				"魔典随机率、死循环保护、露珠边界或旧版充能公式发生偏移");
		String teleport = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfTeleportation.java"));
		check(teleport.contains("if (target != null) teleportToLocation(curUser, target)"),
				"强化传送取消选择时仍可能错误执行");
		String mirror = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfMirrorImage.java"));
		check(mirror.contains("new DelayedImageSpawner(6 - spawnImages(curUser, 2), 2, 3f)")
				&& mirror.contains("private static final int NIMAGES\t= 3")
				&& mirror.contains("totalImages <= 0 || spawned == 0"),
				"强化镜像没有保持立即2个、每3回合2个、最多6个或无位置终止保护");
		String lullaby = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfLullaby.java"));
		check(lullaby.contains("AttackDown.class, 10f).level(50)")
				&& lullaby.contains("ArmorBreak.class, 10f).level(20)"),
				"催眠卷轴缺少0.9.8的敌我攻防削弱");
		String rage = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfRage.java"));
		check(rage.contains("Silent.class, 20f") && rage.contains("Heap.Type.MIMIC"),
				"盛怒卷轴缺少0.9.8的沉默或宝箱怪唤醒");
		String recharge = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfRecharging.java"));
		check(recharge.contains("Arcane.class, 3f") && recharge.contains("Shocked.class).level(6)"),
				"充能卷轴缺少0.9.8的奥术或可见敌人电击");
		String terror = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfTerror.java"));
		check(terror.contains("ShadowCurse.class") && terror.contains("CountDown.class")
				&& terror.contains("Paralysis.class") && terror.contains("HasteBuff.class"),
				"恐惧卷轴缺少0.9.8的暗影诅咒或强化附加状态");
		String mapping = Files.readString(Path.of("../java/pd/items/consum/scrolls/ScrollOfMagicMapping.java"));
		check(mapping.contains("readMap(false)") && mapping.contains("readMap(true)"),
				"地图卷轴没有区分普通映射与强化秘密发现");

		try (java.util.stream.Stream<Path> paths = Files.walk(Path.of("../assets/messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				String text = Files.readString(path, StandardCharsets.UTF_8);
				check(!text.contains("\uFFFD"), "资源含UTF-8替换字符：" + path);
			}
		}
	}

	private static RecordingHero prepareHero(boolean withLevel) {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.level = withLevel ? new TestLevel() : null;
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		hero.pos = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static TestBook equip(RecordingHero hero) {
		TestBook book = new TestBook();
		hero.belongings.artifact = book;
		return book;
	}

	private static TestBook carry(RecordingHero hero, int level) {
		TestBook book = new TestBook();
		book.level(level);
		hero.belongings.backpack.items.add(book);
		return book;
	}

	private static void checkSongConsumed(Hero hero, TestBook oldBook) {
		check(!hero.belongings.backpack.items.contains(oldBook), "圣歌后原魔典没有消耗");
		Heap heap = Dungeon.level.heaps.get(hero.pos);
		check(heap != null && heap.peek() instanceof UnstableSpellbook
				&& heap.peek().level() == 0, "圣歌后没有在脚下生成新的零级魔典");
	}

	private static int rawSkill(Hero hero, String name) throws Exception {
		Field field = Hero.class.getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(hero);
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(),
				file + "缺少文本：" + key);
	}

	private static boolean close(float actual, float expected) { return Math.abs(actual - expected) < 0.0001f; }
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class RecordingScroll extends Scroll {
		int normalReads;
		int empoweredReads;
		@Override public void doRead() { normalReads++; }
		@Override public void empoweredRead() { empoweredReads++; }
		boolean isAnonymous() { return anonymous; }
	}

	private static final class TestItem extends Item {
		void setCursed() { cursed = true; }
	}

	private static final class TestBook extends UnstableSpellbook {
		Scroll nextScroll;
		boolean regularEffect;
		int chargeValue() { return charge; }
		int capValue() { return chargeCap; }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
		boolean upgradeNow(Hero hero) { return upgradeWithDew(hero); }
		boolean singNow(Hero hero) { return sing(hero); }
		void readNow(Hero hero) { doReadEffect(hero); }
		@Override protected Scroll randomScroll() { return nextScroll; }
		@Override protected boolean useRegularScrollEffect() { return regularEffect; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs().clear(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
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
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsUnstableSpellbookTest() { }
}
