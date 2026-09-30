package pd.actors.mobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Fire;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.items.Elevator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.PuddingCup;
import pd.items.keys.SpsSkeletonKey;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Runtime parity checks for SPS-PD 0.9.8's Yog encounter and four fists. */
public final class SpsHallsBossTimelineTest {

	private static final int WIDTH = 48;
	private static final int CENTER = 24 + 24 * WIDTH;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-halls-boss-timeline" + File.separator);
		Game.version = "test";
		Badges.loadGlobal();
		Random.pushGenerator(0x53505348414C4C53L);
		try {
			testPhasesAndYearBeast();
			testDefensesAndProperties();
			testFistCombat();
			testDamageRespawnAndRewards();
			System.out.println("SPS恶魔大厅首领时序通过：Yog四次传送、年兽、四拳战斗、重召代价、奖励与存档均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Dungeon.challenges = 0;
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testPhasesAndYearBeast() throws Exception {
		RecordingLevel level = setup();
		Arrays.fill(level.heroFOV, true);
		for (int y = 2; y < 12; y++) for (int x = 2; x < 12; x++) {
			level.heroFOV[x + y * WIDTH] = false;
		}
		TestYog yog = add(level, new TestYog(), CENTER);
		yog.state = yog.PASSIVE;
		yog.spawnFists();
		check(countOwnedFists(level, yog) == 4 && yog.drRoll() == 120,
				"Yog没有在开战时生成四拳并获得每拳30点减伤");
		for (Mob mob : level.mobs) if (mob instanceof SpsYog.Fist) {
			check(!level.heroFOV[mob.pos], "Yog拳头没有优先生成在英雄视野外");
		}

		int[] hp = {1599, 1199, 799, 399};
		for (int phase = 0; phase < hp.length; phase++) {
			yog.HP = hp[phase];
			int oldPos = yog.pos;
			float before = yog.cooldown();
			yog.takeTurn();
			check(getInt(yog, SpsYog.class, "breaks") == phase + 1,
					"Yog第" + (phase + 1) + "个生命阈值没有推进");
			check(close(yog.cooldown(), before), "Yog阶段传送错误消耗了额外回合");
			check(yog.pos != oldPos && !level.heroFOV[yog.pos], "Yog阶段传送没有落在视野外空地");
		}
		check(count(level, YearBeast.class) == 1 && count(level, YearBeast2.class) == 0,
				"Yog最终阶段没有召唤旧版YearBeast，或错误召唤了YearBeast2");
		YearBeast beast = first(level, YearBeast.class);
		check(beast != null && !level.adjacent(beast.pos, Dungeon.hero.pos), "Yog把年兽生成在英雄邻格");

		Bundle bundle = new Bundle();
		yog.storeInBundle(bundle);
		Yog restored = new Yog();
		restored.restoreFromBundle(bundle);
		check(getInt(restored, SpsYog.class, "breaks") == 4
				&& getBoolean(restored, SpsYog.class, "fistsSpawned"), "Yog阶段或四拳状态未随存档恢复");
	}

	private static void testDefensesAndProperties() {
		Yog yog = new Yog();
		check(yog.resist(Amok.class) == 0.5f && yog.resist(Terror.class) == 0.5f
				&& yog.resist(Charm.class) == 0.5f && yog.resist(Sleep.class) == 0.5f
				&& yog.resist(Vertigo.class) == 0.5f,
				"Yog旧版五项控制抗性没有按减半效果恢复");
		check(!yog.isImmune(Amok.class) && !yog.isImmune(Terror.class)
				&& !yog.isImmune(Charm.class) && !yog.isImmune(Sleep.class)
				&& !yog.isImmune(Vertigo.class), "Yog控制抗性被错误提升为完全免疫");
		check(yog.resist(ToxicGas.class) == 0.5f && yog.resist(EnchantmentDark.class) == 0.5f
				&& yog.resist(ScrollOfPsionicBlast.class) < 1f, "Yog元素、暗属性或灵能抗性不完整");

		SpsYog.Fist[] fists = {new Yog.RottingFist(), new Yog.BurningFist(),
				new Yog.InfectingFist(), new Yog.PinningFist()};
		for (SpsYog.Fist fist : fists) {
			check(!fist.properties().contains(Char.Property.BOSS_MINION),
					fist.getClass().getSimpleName() + "带有旧版不存在的首领随从属性");
			check(fist.resist(Amok.class) == 0.5f && fist.resist(Sleep.class) == 0.5f
					&& fist.resist(Terror.class) == 0.5f && fist.resist(Vertigo.class) == 0.5f,
					fist.getClass().getSimpleName() + "缺少旧版控制抗性");
			check(!fist.isImmune(Amok.class) && !fist.isImmune(Sleep.class)
					&& !fist.isImmune(Terror.class) && !fist.isImmune(Vertigo.class),
					fist.getClass().getSimpleName() + "控制抗性被错误提升为免疫");
			check(fist.resist(Charm.class) == 1f && !fist.isImmune(Charm.class),
					fist.getClass().getSimpleName() + "错误获得了魅惑防御");
		}
		Yog.BurningFist burning = new Yog.BurningFist();
		check(!burning.properties().contains(Char.Property.FIERY)
				&& burning.resist(Burning.class) == 0.5f && !burning.isImmune(Burning.class)
				&& burning.resist(Fire.class) == 0.5f, "火焰之拳属性或火焰防御偏离旧版");
		check(new Yog.RottingFist().resist(Poison.class) == 0.5f
				&& new Yog.InfectingFist().resist(Poison.class) == 0.5f
				&& new Yog.PinningFist().resist(Burning.class) == 0.5f,
				"大地、酸蚀或束缚之拳的专属防御不完整");
		check(!new Yog.Larva().properties().contains(Char.Property.BOSS_MINION),
				"古神幼虫带有旧版不存在的首领随从属性");
	}

	private static void testFistCombat() {
		RecordingLevel level = setup();
		TestRotting rotting = add(level, new TestRotting(), CENTER);
		level.map[rotting.pos] = Terrain.WATER;
		level.rebuildFlags();
		rotting.state = rotting.PASSIVE;
		rotting.HP = rotting.HT - 25;
		rotting.takeTurn();
		check(rotting.HP == rotting.HT + 25, "大地之拳水中回复被错误截断到生命上限");

		Target procTarget = add(level, new Target(0), CENTER + WIDTH);
		Dungeon.challenges = Challenges.ELE_STOME;
		Statistics.deepestFloor = 25;
		int before = procTarget.HP;
		rotting.proc(procTarget, 20);
		check(procTarget.HP == before, "大地之拳错误触发了被旧版覆写的元素风暴父类伤害");
		TestInfecting infecting = add(level, new TestInfecting(), CENTER + 2 * WIDTH);
		before = procTarget.HP;
		for (int i = 0; i < 100 && procTarget.buff(Poison.class) == null; i++) infecting.proc(procTarget, 20);
		check(procTarget.HP == before, "酸蚀之拳错误触发了被旧版覆写的元素风暴父类伤害");
		Bundle poisonBundle = new Bundle();
		procTarget.buff(Poison.class).storeInBundle(poisonBundle);
		float poisonDuration = poisonBundle.getFloat("left");
		check(poisonDuration >= 7f && poisonDuration <= 8f,
				"酸蚀之拳中毒时长没有使用旧版7至8回合排他上界");

		Target rangedTarget = add(level, new Target(1000), CENTER + 5);
		TestBurning burning = add(level, new TestBurning(), CENTER);
		check(burning.canShoot(rangedTarget), "火焰之拳无法锁定直线远程目标");
		before = rangedTarget.HP;
		check(burning.strike(rangedTarget), "火焰之拳远射没有命中零闪避目标");
		int dealt = before - rangedTarget.HP;
		check(dealt >= 40 && dealt <= 52, "火焰之拳远射没有无视护甲或错误追加父类伤害：" + dealt);
		Buff.affect(burning, Silent.class, 10f);
		check(!burning.canShoot(rangedTarget), "火焰之拳被沉默后仍可远射");
		rangedTarget.pos = burning.pos + 1;
		check(burning.canShoot(rangedTarget), "火焰之拳被沉默后无法攻击邻格目标");

		Target pinTarget = add(level, new Target(1000), CENTER + 10);
		TestPinning pinning = add(level, new TestPinning(), CENTER + 5);
		check(pinning.canShoot(pinTarget), "束缚之拳无法锁定直线远程目标");
		before = pinTarget.HP;
		check(pinning.strike(pinTarget), "束缚之拳远射没有命中零闪避目标");
		dealt = before - pinTarget.HP;
		check(dealt >= 30 && dealt <= 42, "束缚之拳远射没有无视护甲或错误追加父类伤害：" + dealt);
		Buff.affect(pinning, Silent.class, 10f);
		check(!pinning.canShoot(pinTarget), "束缚之拳被沉默后仍可远射");
		Dungeon.challenges = 0;
	}

	private static void testDamageRespawnAndRewards() {
		RecordingLevel level = setup();
		TestYog yog = add(level, new TestYog(), CENTER);
		yog.spawnFists();
		yog.HP = 100;
		yog.damage(101, Dungeon.hero);
		check(yog.HP == 99, "Yog没有把英雄过量伤害压为旧版固定1点");

		removeOwnedFists(level, yog);
		yog.HP = 500;
		yog.defend(Dungeon.hero, 1);
		check(countOwnedFists(level, yog) == 4 && yog.HP == 450,
				"Yog四拳清空后没有重召四拳并支付50点生命");
		check(countOwnedLarvae(level, yog) == 1, "Yog受击时没有推出古神幼虫");

		level = setup();
		yog = add(level, new TestYog(), CENTER);
		yog.spawnFists();
		check(yog.SupercreateLoot() instanceof PuddingCup, "Yog特殊战利品接口没有返回布丁杯");
		prepareDeaths(level);
		Dungeon.hero.HP = 0;
		yog.HP = 100;
		yog.damage(100, Dungeon.hero);
		check(!yog.isAlive(), "英雄造成恰好等于生命的伤害没有击杀Yog");
		check(!level.locked, "Yog死亡后没有解锁恶魔大厅");
		check(countItems(level, Elevator.class) == 1 && countItems(level, SpsSkeletonKey.class) == 1,
				"Yog没有掉落旧版升降器和骷髅钥匙");
		check(countItems(level, PuddingCup.class) == 0 && level.itemCount() == 2,
				"Yog击杀奖励混入了旧版没有的固定布丁杯或其他物品");
	}

	private static RecordingLevel setup() {
		Actor.clear();
		Dungeon.depth = 25;
		Dungeon.branch = 0;
		Dungeon.challenges = 0;
		Dungeon.quickslot = new QuickSlot();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = CENTER - WIDTH;
		hero.lvl = Hero.MAX_LEVEL;
		hero.fieldOfView = level.heroFOV;
		hero.sprite = new CharSprite() {
			@Override public void showStatusWithIcon(int color, String text, int icon, Object... args) { }
		};
		Dungeon.hero = hero;
		Actor.add(hero);
		return level;
	}

	private static <T extends Mob> T add(RecordingLevel level, T mob, int pos) {
		mob.pos = pos;
		level.mobs.add(mob);
		Actor.add(mob);
		return mob;
	}

	private static <T extends Mob> T first(RecordingLevel level, Class<T> type) {
		for (Mob mob : level.mobs) if (type.isInstance(mob) && mob.isAlive()) return type.cast(mob);
		return null;
	}

	private static int count(RecordingLevel level, Class<? extends Mob> type) {
		int count = 0;
		for (Mob mob : level.mobs) if (type.isInstance(mob) && mob.isAlive()) count++;
		return count;
	}

	private static int countOwnedFists(RecordingLevel level, SpsYog yog) {
		int count = 0;
		for (Mob mob : level.mobs) if (mob instanceof SpsYog.Fist
				&& ((SpsYog.Fist) mob).ownerId == yog.id() && mob.isAlive()) count++;
		return count;
	}

	private static int countOwnedLarvae(RecordingLevel level, SpsYog yog) {
		int count = 0;
		for (Mob mob : level.mobs) if (mob instanceof SpsYog.Larva
				&& ((SpsYog.Larva) mob).ownerId == yog.id() && mob.isAlive()) count++;
		return count;
	}

	private static int countItems(RecordingLevel level, Class<? extends Item> type) {
		int count = 0;
		for (Heap heap : level.heaps.valueList()) for (Item item : heap.items) if (type.isInstance(item)) count++;
		return count;
	}

	private static void removeOwnedFists(RecordingLevel level, SpsYog yog) {
		for (Mob mob : new ArrayList<>(level.mobs)) if (mob instanceof SpsYog.Fist
				&& ((SpsYog.Fist) mob).ownerId == yog.id()) {
			attachDeathSprite(mob);
			mob.die(SpsHallsBossTimelineTest.class);
		}
	}

	private static void prepareDeaths(RecordingLevel level) {
		for (Mob mob : level.mobs) attachDeathSprite(mob);
	}

	private static void attachDeathSprite(Mob mob) {
		if (mob.sprite == null) mob.sprite = new CharSprite() {
			@Override public void die() { }
		};
	}

	private static int getInt(Object target, Class<?> owner, String name) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(target);
	}

	private static boolean getBoolean(Object target, Class<?> owner, String name) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		return field.getBoolean(target);
	}

	private static boolean close(float a, float b) { return Math.abs(a - b) < 0.0001f; }

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestYog extends Yog {
		boolean takeTurn() { return act(); }
		int defend(Char enemy, int damage) { return defenseProc(enemy, damage); }
	}

	private static final class TestRotting extends Yog.RottingFist {
		boolean takeTurn() { return act(); }
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
	}

	private static final class TestInfecting extends Yog.InfectingFist {
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
	}

	private static final class TestBurning extends Yog.BurningFist {
		boolean canShoot(Char enemy) { return canAttack(enemy); }
		boolean strike(Char enemy) { return attack(enemy, 1f, 0f, 1f); }
	}

	private static final class TestPinning extends Yog.PinningFist {
		boolean canShoot(Char enemy) { return canAttack(enemy); }
		boolean strike(Char enemy) { return attack(enemy, 1f, 0f, 1f); }
	}

	private static final class Target extends Mob {
		private final int armor;
		Target(int armor) { this.armor = armor; HP = HT = 1000; defenseSkill = 0; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return armor; }
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
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
			heroFOV = new boolean[length()];
			Arrays.fill(heroFOV, true);
		}
		void rebuildFlags() { buildFlagMaps(); }
		int itemCount() {
			int count = 0;
			for (Heap heap : heaps.valueList()) count += heap.items.size();
			return count;
		}
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heap.sprite = new ItemSprite() {
					@Override public void place(int cell) { }
					@Override public void drop() { }
					@Override public void drop(int from) { }
				};
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

	private SpsHallsBossTimelineTest() { }
}
