package pd.actors.mobs;

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
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.OrbOfZot;
import pd.items.bags.ScrollHolder;
import pd.items.journalpages.EnergyCore;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.Bundle;
import render.utils.FileUtils;
import render.utils.Random;
import render.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

/** Runtime and source-contract checks for the complete SPS Orb of Zot route. */
public final class SpsOrbOfZotTest {

	private static final int WIDTH = 16;
	private static final int CENTER = 8 + 8 * WIDTH;
	private static final String ICON_HASH = "6DAE88194B457DF5DA2B963EA92800D710687CB40AC17C4B098E2D4B465D6EC7";
	private static final String SPRITE_HASH = "933B748E783715408B0415BA9B1085C0CE4A6BC5CB7AB2EDE158CA4880B4D4E0";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-orb-of-zot" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x5350534F52425A4FL);
		try {
			testChargeActionsAndPersistence();
			testBreakAndStorage();
			testDeploymentSafety();
			testTurretCombatAndReturn();
			testShadowYogDrop();
			testAssetsMessagesAndWiring();
			System.out.println("SPS储能装置测试通过：500回合充能、投掷保护、能源炮台、激光自损、关闭返还、ShadowYog掉落、日志路线和双语原始素材均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testChargeActionsAndPersistence() {
		freshLevel();
		TestHero hero = heroAt(CENTER, 1000);
		OrbOfZot orb = new OrbOfZot();
		check(orb.image == ItemSpriteSheet.ORB_OF_ZOT, "储能装置未绑定原始图标槽");
		check(!orb.actions(hero).contains(OrbOfZot.AC_ACTIVATETHROW)
				&& orb.actions(hero).contains(OrbOfZot.AC_BREAK), "未充满时动作列表错误");
		for (int i = 0; i < 600; i++) orb.gainCharge();
		check(orb.charge() == OrbOfZot.FULL_CHARGE, "储能装置没有在500点封顶");
		check(orb.actions(hero).contains(OrbOfZot.AC_ACTIVATETHROW), "满充后没有开放释放能量动作");

		Bundle bundle = new Bundle();
		orb.storeInBundle(bundle);
		OrbOfZot restored = new OrbOfZot();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == OrbOfZot.FULL_CHARGE, "储能装置充能没有存档恢复");
		OrbOfZot separate = new OrbOfZot();
		check(separate.charge() == 0, "不同储能装置错误共享充能状态");
	}

	private static void testBreakAndStorage() {
		TestLevel level = freshLevel();
		TestHero hero = heroAt(CENTER, 1000);
		OrbOfZot orb = new OrbOfZot();
		check(orb.collect(hero.belongings.backpack), "储能装置无法放入背包");
		check(new ScrollHolder().canHold(new OrbOfZot()), "卷轴筒没有恢复储能装置收纳分类");
		check(orb.breakOpen(hero), "提取能源核心动作失败");
		check(!hero.belongings.backpack.contains(orb), "提取后没有消耗储能装置");
		Heap heap = level.heaps.get(CENTER);
		check(heap != null && heap.peek() instanceof EnergyCore
				&& ((EnergyCore)heap.peek()).destination() == 7, "提取没有掉落目的地7的能源核心日志");
	}

	private static void testDeploymentSafety() throws Exception {
		TestLevel level = freshLevel();
		TestHero hero = heroAt(CENTER, 1000);
		TestOrb orb = chargedOrb();
		arm(orb);
		orb.throwAt(CENTER);
		OrbOfZotMob turret = null;
		for (Mob mob : level.mobs) if (mob instanceof OrbOfZotMob) turret = (OrbOfZotMob)mob;
		check(turret != null && turret.pos != CENTER && level.adjacent(turret.pos, CENTER)
				&& Actor.findChar(turret.pos) == null, "占用格投掷没有选择合法邻格");

		level = freshLevel();
		hero = heroAt(CENTER, 1000);
		for (int y = 1; y < WIDTH - 1; y++) {
			for (int x = 1; x < WIDTH - 1; x++) level.map[x + y * WIDTH] = Terrain.WALL;
		}
		level.map[CENTER] = Terrain.EMPTY;
		level.buildFlagMaps();
		orb = chargedOrb();
		arm(orb);
		orb.throwAt(CENTER);
		check(level.mobs.isEmpty(), "没有合法落点时错误生成重叠炮台");
		check(level.heaps.get(CENTER) != null && level.heaps.get(CENTER).peek() == orb,
				"没有合法落点时吞掉了储能装置");

		int edgeResult = OrbOfZot.summonCell(0);
		check(edgeResult == -1, "地图边界外投掷发生横向绕行");
	}

	private static void testTurretCombatAndReturn() {
		TestLevel level = freshLevel();
		TestHero hero = heroAt(CENTER - WIDTH, 1000);
		OrbOfZotMob turret = new OrbOfZotMob();
		turret.pos = CENTER;
		turret.fieldOfView = new boolean[level.length()];
		Arrays.fill(turret.fieldOfView, true);
		level.mobs.add(turret);
		Actor.add(turret);

		TestMob ally = mobAt(level, CENTER + 2, 500, Char.Alignment.ALLY);
		TestMob enemy = mobAt(level, CENTER + 4, 500, Char.Alignment.ENEMY);
		check(turret.chooseEnemy() == enemy, "能源炮台选择了英雄或盟友而非敌对怪物");
		check(turret.HT == 500 && turret.defenseSkill == 35
				&& turret.attackSkill(enemy) == 70 + Dungeon.depth, "能源炮台基础数值错误");
		check(Char.hasProp(turret, Char.Property.MECH) && Char.hasProp(turret, Char.Property.IMMOVABLE)
				&& !turret.getCloser(enemy.pos), "能源炮台机械或固定属性错误");
		check(turret.isImmune(Terror.class) && turret.isImmune(ToxicGas.class), "能源炮台缺少旧版免疫");

		int turretHp = turret.HP;
		turret.fireBeamAt(enemy);
		check(enemy.HP <= 400 && enemy.HP >= 300, "能源激光伤害不在100至200范围");
		check(turret.HP <= turretHp - 10 && turret.HP >= turretHp - 20, "能源炮台攻击自损不在10至20范围");
		check(ally.HP == ally.HT && hero.HP == hero.HT, "能源激光误伤英雄或盟友");

		int deathPos = turret.pos;
		turret.die(SpsOrbOfZotTest.class);
		Heap returned = level.heaps.get(deathPos);
		check(returned != null && returned.peek() instanceof OrbOfZot
				&& ((OrbOfZot)returned.peek()).charge() == 0, "能源炮台关闭后没有返还零充能装置");
	}

	private static void testShadowYogDrop() {
		TestLevel level = freshLevel();
		heroAt(CENTER - WIDTH, 1000);
		ShadowYog first = shadowAt(level, CENTER - 1);
		ShadowYog last = shadowAt(level, CENTER + 1);
		first.die(SpsOrbOfZotTest.class);
		check(level.heaps.get(first.pos) == null, "非最后一个ShadowYog错误掉落储能装置");
		last.die(SpsOrbOfZotTest.class);
		check(level.heaps.get(last.pos) != null && level.heaps.get(last.pos).peek() instanceof OrbOfZot,
				"最后一个ShadowYog没有掉落储能装置");
	}

	private static void testAssetsMessagesAndWiring() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(ICON_HASH.equals(tileHash(sheet, 224, 528)), "储能装置物品图标与旧版不一致");
		check(SPRITE_HASH.equals(fileHash("sprites/mobs/sps_orbofzot.png")), "能源炮台动画与旧版不一致");

		String zhItems = read("messages/items/zh/items.properties");
		String enItems = read("messages/items/en/items.properties");
		String zhActors = read("messages/actors/zh/actors.properties");
		String enActors = read("messages/actors/en/actors.properties");
		for (String text : Arrays.asList(zhItems, enItems, zhActors, enActors)) {
			check(!text.contains("\uFFFD"), "储能装置双语资源含UTF-8替换字符");
		}
		check(zhItems.contains("items.orbofzot.name=储能装置")
				&& zhItems.contains("items.journalpages.energycore.name=Otiluck的旅行日志之能源核心")
				&& enItems.contains("items.orbofzot.ac_activatethrow=USE")
				&& zhActors.contains("actors.mobs.orbofzotmob.name=zot牌能源球")
				&& enActors.contains("actors.mobs.orbofzotmob.die=Energy down, waiting for charge."),
				"储能装置、能源核心或炮台缺少双语文本");

		String hero = read("../java/pd/actors/hero/Hero.java");
		String holder = read("../java/pd/items/bags/ScrollHolder.java");
		String yog = read("../java/pd/actors/mobs/ShadowYog.java");
		check(hero.contains("if (orbOfZot != null) orbOfZot.gainCharge();"), "英雄回合没有接入储能装置充能");
		check(holder.contains("item instanceof OrbOfZot"), "卷轴筒源码没有接入储能装置");
		check(yog.contains("Dungeon.level.drop(new OrbOfZot(), deathPos)"), "ShadowYog源码没有接入储能装置掉落");
	}

	private static TestOrb chargedOrb() {
		TestOrb orb = new TestOrb();
		orb.gainCharge(OrbOfZot.FULL_CHARGE);
		return orb;
	}

	private static void arm(OrbOfZot orb) throws Exception {
		Field field = OrbOfZot.class.getDeclaredField("activatedThrow");
		field.setAccessible(true);
		field.setBoolean(orb, true);
	}

	private static ShadowYog shadowAt(TestLevel level, int pos) {
		ShadowYog yog = new ShadowYog();
		yog.pos = pos;
		level.mobs.add(yog);
		Actor.add(yog);
		return yog;
	}

	private static TestLevel freshLevel() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static TestHero heroAt(int pos, int health) {
		TestHero hero = new TestHero();
		hero.pos = pos;
		hero.HP = hero.HT = health;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static TestMob mobAt(TestLevel level, int pos, int health, Char.Alignment alignment) {
		TestMob mob = new TestMob();
		mob.pos = pos;
		mob.HP = mob.HT = health;
		mob.alignment = alignment;
		level.mobs.add(mob);
		Actor.add(mob);
		return mob;
	}

	private static String read(String path) throws Exception {
		return java.nio.file.Files.readString(Paths.get(path), StandardCharsets.UTF_8);
	}

	private static String fileHash(String path) throws Exception {
		return hex(MessageDigest.getInstance("SHA-256").digest(
				java.nio.file.Files.readAllBytes(Paths.get(path))));
	}

	private static String tileHash(BufferedImage image, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(image.getRGB(x, y));
		}
		return hex(MessageDigest.getInstance("SHA-256").digest(pixels.array()));
	}

	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestOrb extends OrbOfZot {
		void throwAt(int cell) { onThrow(cell); }
	}

	private static final class TestHero extends Hero {
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}

	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(WIDTH, WIDTH);
			Arrays.fill(map, Terrain.EMPTY);
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
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private SpsOrbOfZotTest() { }
}
