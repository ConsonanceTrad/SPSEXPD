package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.DarkEffectDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentLight;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsPrisonBossTimelineTest {

	private static final Class<?>[] PRISON_SEEDS = {
			Firebloom.Seed.class, Icecap.Seed.class, Sorrowmoss.Seed.class,
			Blindweed.Seed.class, Stormvine.Seed.class, Starflower.Seed.class
	};

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053505249534FL);
		try {
			testLegacyDefences();
			testTenguJumpTiming();
			testWanderSeedsAndTeleport();
			testSeekingBomb();
			testTankTrail();
			System.out.println("SPS监狱首领时序通过：天狗跳跃、典狱长种子/传送、追踪炸弹和坦克暗属性尾迹均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testLegacyDefences() {
		SpsTengu tengu = new SpsTengu();
		PrisonWander wander = new PrisonWander();
		Tank tank = new Tank();
		for (Mob boss : new Mob[]{tengu, wander, tank}) {
			check(boss.isWeak(WandOfLight.class) && boss.isWeak(EnchantmentLight.class),
					boss.getClass().getSimpleName() + "缺少旧版光系弱点");
		}
		check(tengu.resist(EnchantmentDark.class) == 0.5f
				&& tank.resist(EnchantmentDark.class) == 0.5f, "天狗或坦克缺少暗属性附魔抗性");
		for (Class<?> plant : new Class<?>[]{Icecap.class, Firebloom.class, Blindweed.class,
				Stormvine.class, Sorrowmoss.class}) {
			check(wander.isImmune(plant), "典狱长缺少植物免疫：" + plant.getSimpleName());
		}
		check(tank.isImmune(DarkEffectDamage.class), "坦克缺少自身暗属性场免疫");
	}

	private static void testTenguJumpTiming() {
		TestLevel level = level();
		Hero hero = hero(45);
		TestTengu tengu = new TestTengu();
		tengu.pos = 9;
		tengu.fieldOfView = new boolean[level.length()];
		Arrays.fill(tengu.fieldOfView, true);
		Actor.add(tengu);
		int oldPos = tengu.pos;
		float cost = tengu.approachAndPay(hero.pos);
		check(tengu.pos != oldPos, "天狗在可见目标前没有跳跃");
		check(Math.abs(cost - 2f) < 0.001f, "天狗追踪跳跃未支付原版两份动作时间：" + cost);
	}

	private static void testWanderSeedsAndTeleport() {
		int[] counts = new int[PRISON_SEEDS.length];
		for (int i = 0; i < 26_000; i++) {
			Plant.Seed seed = PrisonWander.randomBossSeed();
			int index = Arrays.asList(PRISON_SEEDS).indexOf(seed.getClass());
			check(index >= 0, "典狱长生成了原版六种之外的种子：" + seed.getClass().getSimpleName());
			counts[index]++;
		}
		int[] weights = {8, 4, 6, 4, 3, 1};
		for (int i = 0; i < counts.length; i++) {
			float actual = counts[i] / 26_000f;
			float expected = weights[i] / 26f;
			check(Math.abs(actual - expected) < 0.025f,
					"典狱长种子权重异常：" + PRISON_SEEDS[i].getSimpleName() + "=" + counts[i]);
		}

		TestLevel level = level();
		Hero hero = hero(27);
		PrisonWander wander = new PrisonWander();
		wander.pos = 18;
		wander.fieldOfView = new boolean[level.length()];
		Arrays.fill(wander.fieldOfView, true);
		Actor.add(wander);
		for (int i = 0; i < 2_000 && hero.buff(STRDown.class) == null; i++) {
			hero.pos = 27;
			wander.attackProc(hero, 0);
		}
		check(hero.buff(STRDown.class) != null, "典狱长没有触发传送与力量衰减");
		check(Math.abs(wander.cooldown() - 1f) < 0.001f, "典狱长传送后漏付1回合：" + wander.cooldown());
	}

	private static void testSeekingBomb() {
		TestLevel level = level();
		Hero hero = hero(28);
		hero.HP = hero.HT = 100_000;
		TestBomb bomb = new TestBomb();
		bomb.pos = 27;
		bomb.state = bomb.SLEEPING;
		Actor.add(bomb);
		bomb.tick();
		check(bomb.timer() == 8, "追踪炸弹在未移动时错误倒计时");
		bomb.move(19, false);
		check(bomb.timer() == 7, "追踪炸弹移动后没有减少倒计时");
		Level.set(18, Terrain.WALL, level);
		bomb.attackProc(hero, 0);
		check(level.map[18] == Terrain.EMPTY, "追踪炸弹没有使用可破墙的地牢炸弹");
	}

	private static void testTankTrail() {
		level();
		hero(45);
		Tank tank = new Tank();
		tank.pos = 9;
		Actor.add(tank);
		tank.move(10, false);
		check(Blob.volumeAt(10, DarkEffectDamage.class) >= 5,
				"坦克移动没有留下旧版暗属性伤害区域");
	}

	private static TestLevel level() {
		Actor.clear();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		Arrays.fill(level.heroFOV, true);
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

	private static final class TestTengu extends SpsTengu {
		float approachAndPay(int target) {
			float before = cooldown();
			if (getCloser(target)) spend(1f / speed());
			return cooldown() - before;
		}
	}

	private static final class TestBomb extends PrisonWander.SeekBombP {
		boolean tick() { return act(); }
		int timer() {
			Bundle bundle = new Bundle();
			storeInBundle(bundle);
			return bundle.getInt("timer");
		}
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<>();
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

	private SpsPrisonBossTimelineTest() { }
}
