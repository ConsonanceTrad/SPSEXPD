package pd.actors.mobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Hot;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.items.misc.CopyBall;
import pd.items.misc.MissileShield;
import pd.items.misc.PotionOfMage;
import pd.items.wands.WandOfFirebolt;
import pd.items.wands.WandOfLight;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.items.weapon.enchantments.EnchantmentDark2;
import pd.items.weapon.enchantments.EnchantmentFire;
import pd.items.weapon.enchantments.EnchantmentFire2;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.CharSprite;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.FileUtils;
import watabou.utils.Random;
import watabou.utils.SparseArray;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Runtime parity checks for all three SPS-PD 0.9.8 sewer bosses. */
public final class SpsSewerBossTimelineTest {

	private static final int WIDTH = 48;
	private static final int CENTER = 24 + 24 * WIDTH;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-sewer-boss-timeline" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x5350535345574552L);
		try {
			testGooAndPoisonGoo();
			testSewerHeart();
			testPlagueDoctor();
			testSummonedMinions();
			System.out.println("SPS下水道首领时序通过：Goo蓄力/分裂、下水道之心阶段/光束、瘟疫医生三阶段及召唤物均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Dungeon.challenges = 0;
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testGooAndPoisonGoo() throws Exception {
		RecordingLevel level = setup();
		TestGoo goo = add(level, new TestGoo(), CENTER);
		check(goo.resist(ToxicGas.class) == 0.5f
				&& goo.resist(EnchantmentDark.class) == 0.5f
				&& goo.resist(EnchantmentDark2.class) == 0.5f,
				"Goo缺少旧版毒气或暗属性抗性");
		check(goo.isWeak(pd.actors.buffs.Burning.class)
				&& goo.isWeak(WandOfFirebolt.class) && goo.isWeak(EnchantmentFire.class)
				&& goo.isWeak(EnchantmentFire2.class) && goo.isWeak(DamageType.Fire.class),
				"Goo缺少旧版五项火系弱点");
		check(goo.isImmune(Roots.class), "Goo缺少扎根免疫");
		check(goo.SupercreateLoot() instanceof CopyBall, "Goo特殊战利品没有恢复侵蚀核心");

		level.map[goo.pos] = Terrain.WATER;
		level.rebuildFlags();
		goo.state = goo.PASSIVE;
		goo.HP = goo.HT - 1;
		goo.takeTurn();
		check(goo.HP == goo.HT + 2, "Goo水中三点回复被错误截断到生命上限");

		Target target = add(level, new Target(), CENTER + WIDTH);
		Dungeon.challenges = Challenges.ELE_STOME;
		Statistics.deepestFloor = 5;
		int hp = target.HP;
		goo.proc(target, 10);
		check(target.HP == hp, "Goo攻击错误触发了旧版覆写掉的父类元素风暴");
		setInt(goo, SpsGoo.class, "pumpedUp", 2);
		goo.forcedMiss(target);
		check(getInt(goo, SpsGoo.class, "pumpedUp") == 0, "Goo蓄力攻击落空后没有清除蓄力");
		Dungeon.challenges = 0;

		level = setup();
		goo = add(level, new TestGoo(), CENTER);
		goo.notice();
		check(level.locked && count(level, SpsGoo.PoisonGoo.class) == 4,
				"Goo开战时没有封锁楼层并生成四只毒液史莱姆");
		goo.notice();
		check(count(level, SpsGoo.PoisonGoo.class) == 4, "Goo重复察觉后再次生成毒液史莱姆");
		Bundle gooSave = new Bundle();
		goo.storeInBundle(gooSave);
		SpsGoo restoredGoo = new SpsGoo();
		restoredGoo.restoreFromBundle(gooSave);
		check(getBoolean(restoredGoo, SpsGoo.class, "spawnedMini"), "Goo随从生成状态没有随存档恢复");

		level = setup();
		add(level, new SpsGoo(), CENTER - WIDTH);
		TestPoisonGoo poison = add(level, new TestPoisonGoo(), CENTER);
		check(poison.resist(ToxicGas.class) == 0.5f
				&& poison.resist(EnchantmentDark.class) == 0.5f && poison.isImmune(Roots.class),
				"毒液史莱姆旧版抗性或免疫不完整");
		poison.HT = poison.HP = 95;
		level.map[poison.pos] = Terrain.WATER;
		level.rebuildFlags();
		poison.state = poison.PASSIVE;
		poison.takeTurn();
		check(poison.HT == 100 && poison.HP == 100, "毒液史莱姆水中没有恢复旧版生命上限成长");
		poison.HP = 99;
		poison.add(new Roots());
		check(poison.HP == 109, "毒液史莱姆扎根治疗被错误截断到生命上限");
		poison.HP = 1000;
		int beforeHot = poison.HP;
		poison.add(new Hot());
		check(poison.HP < beforeHot && poison.buff(Hot.class) == null,
				"毒液史莱姆没有把热浪立即转为旧版伤害");

		Target poisonTarget = add(level, new Target(), CENTER + WIDTH);
		Dungeon.challenges = Challenges.ELE_STOME;
		hp = poisonTarget.HP;
		poison.proc(poisonTarget, 10);
		check(poisonTarget.HP == hp && poisonTarget.buff(Poison.class) != null,
				"毒液史莱姆攻击遗漏中毒或错误追加父类元素风暴");
		Dungeon.challenges = 0;
		int oldCount = count(level, SpsGoo.PoisonGoo.class);
		poison.HP = 100;
		poison.defend(poisonTarget, 20);
		check(count(level, SpsGoo.PoisonGoo.class) == oldCount + 1 && poison.HP == 60,
				"毒液史莱姆受击时没有按旧版对半分裂");
	}

	private static void testSewerHeart() throws Exception {
		RecordingLevel level = setup();
		TestSewerHeart heart = add(level, new TestSewerHeart(), CENTER);
		heart.state = heart.PASSIVE;
		check(heart.SupercreateLoot() instanceof MissileShield, "下水道之心特殊战利品不是飞弹护盾");
		check(heart.properties().contains(Char.Property.PLANT)
				&& heart.properties().contains(Char.Property.BOSS)
				&& heart.isImmune(ToxicGas.class), "下水道之心属性或毒气免疫不完整");
		int[] phaseHp = {400, 300, 200, 100, 1};
		for (int i = 0; i < phaseHp.length; i++) {
			heart.HP = phaseHp[i];
			float before = heart.cooldown();
			heart.takeTurn();
			check(getInt(heart, SewerHeart.class, "breaks") == i + 1,
					"下水道之心第" + (i + 1) + "个阶段阈值没有推进");
			check(close(heart.cooldown(), before), "下水道之心阶段推进错误消耗额外回合");
		}

		Bundle save = new Bundle();
		heart.storeInBundle(save);
		SewerHeart restored = new SewerHeart();
		restored.restoreFromBundle(save);
		check(getInt(restored, SewerHeart.class, "breaks") == 5,
				"下水道之心阶段状态没有随存档恢复");

		level = setup();
		heart = add(level, new TestSewerHeart(), CENTER);
		Target beamTarget = add(level, new Target(), CENTER + 5);
		heart.fieldOfView = new boolean[level.length()];
		Arrays.fill(heart.fieldOfView, true);
		check(heart.canFire(beamTarget), "下水道之心无法锁定直线目标");
		check(heart.fire(beamTarget) && heart.beamCharged(), "下水道之心第一回合没有完成蓄力");
		int hp = beamTarget.HP;
		check(heart.fire(beamTarget) && !heart.beamCharged(), "无画面时下水道之心光束没有同步结算");
		check(beamTarget.HP < hp, "下水道之心光束没有造成伤害");
	}

	private static void testPlagueDoctor() throws Exception {
		RecordingLevel level = setup();
		TestPlagueDoctor doctor = add(level, new TestPlagueDoctor(), CENTER);
		doctor.state = doctor.PASSIVE;
		check(doctor.SupercreateLoot() instanceof PotionOfMage
				&& doctor.SupercreateLoot().isIdentified(), "瘟疫医生特殊战利品不是已鉴定魔力药瓶");
		for (Class<?> gas : new Class<?>[]{ToxicGas.class, ParalyticGas.class,
				DarkGas.class, ConfusionGas.class}) {
			check(doctor.isImmune(gas), "瘟疫医生缺少气体免疫：" + gas.getSimpleName());
		}
		int[] phaseHp = {300, 125, 1};
		for (int i = 0; i < phaseHp.length; i++) {
			doctor.HP = phaseHp[i];
			float before = doctor.cooldown();
			doctor.takeTurn();
			check(getInt(doctor, PlagueDoctor.class, "breaks") == i + 1,
					"瘟疫医生第" + (i + 1) + "个阶段阈值没有推进");
			check(close(doctor.cooldown(), before), "瘟疫医生阶段推进错误消耗额外回合");
		}
		Bundle save = new Bundle();
		doctor.storeInBundle(save);
		PlagueDoctor restored = new PlagueDoctor();
		restored.restoreFromBundle(save);
		check(getInt(restored, PlagueDoctor.class, "breaks") == 3,
				"瘟疫医生阶段状态没有随存档恢复");

		Target target = add(level, new Target(), CENTER + WIDTH);
		target.HT = 0;
		setInt(doctor, PlagueDoctor.class, "breaks", 0);
		Dungeon.challenges = Challenges.ELE_STOME;
		int hp = target.HP;
		check(doctor.proc(target, 50) == 0 && target.HP == hp,
				"瘟疫医生第一阶段没有归零伤害或错误触发父类元素风暴");
		Dungeon.challenges = 0;
		setInt(doctor, PlagueDoctor.class, "breaks", 2);
		for (int i = 0; i < 100 && target.buff(Bleeding.class) == null; i++) doctor.proc(target, 10);
		check(target.buff(Bleeding.class) != null, "瘟疫医生第二阶段没有施加五回合流血");
		setInt(doctor, PlagueDoctor.class, "breaks", 3);
		doctor.proc(target, 10);
		check(doctor.buff(AttackUp.class) != null && doctor.buff(DefenceUp.class) != null,
				"瘟疫医生第三阶段没有获得攻防强化");

		setInt(doctor, PlagueDoctor.class, "breaks", 1);
		for (int i = 0; i < 100 && totalGasAt(doctor.pos) == 0; i++) doctor.defend(target, 10);
		check(totalGasAt(doctor.pos) > 0, "瘟疫医生逃亡阶段受击没有播种四类气体");
	}

	private static void testSummonedMinions() {
		RecordingLevel level = setup();
		Target target = add(level, new Target(), CENTER + WIDTH);
		Dungeon.challenges = Challenges.ELE_STOME;
		Statistics.deepestFloor = 5;
		TestSewerLasher lasher = add(level, new TestSewerLasher(), CENTER);
		int hp = target.HP;
		lasher.proc(target, 0);
		check(target.HP == hp - 2, "下水道触手没有保留旧版两次父类元素风暴回调");
		check(lasher.properties().contains(Char.Property.PLANT)
				&& lasher.properties().contains(Char.Property.MINIBOSS)
				&& lasher.isImmune(ToxicGas.class), "下水道触手属性或毒气免疫不完整");

		TestShadowRat rat = add(level, new TestShadowRat(), CENTER + 2);
		hp = target.HP;
		rat.proc(target, 0);
		check(target.HP == hp - 2, "暗影鼠没有保留旧版两次父类元素风暴回调");
		check(rat.isImmune(ToxicGas.class) && rat.isImmune(Poison.class)
				&& rat.isImmune(pd.actors.buffs.Burning.class),
				"暗影鼠旧版免疫不完整");
		rat.damage(1, new WandOfLight());
		check(!rat.isAlive(), "SPS光明法杖没有直接消灭暗影鼠");
		Dungeon.challenges = 0;

		level = setup();
		SewerHeart.LasherSpawner lasherSpawner = new SewerHeart.LasherSpawner();
		lasherSpawner.act();
		check(count(level, SewerHeart.SewerLasher.class) == 1,
				"下水道之心召唤计量首次满格后没有生成触手");
		PlagueDoctor.ShadowRatSummon ratSpawner = new PlagueDoctor.ShadowRatSummon();
		ratSpawner.act();
		check(count(level, PlagueDoctor.ShadowRat.class) == 1,
				"瘟疫医生召唤计量首次满格后没有生成暗影鼠");
	}

	private static int totalGasAt(int cell) {
		return Blob.volumeAt(cell, ToxicGas.class) + Blob.volumeAt(cell, ConfusionGas.class)
				+ Blob.volumeAt(cell, ParalyticGas.class) + Blob.volumeAt(cell, DarkGas.class);
	}

	private static RecordingLevel setup() {
		Actor.clear();
		Dungeon.depth = 5;
		Dungeon.branch = 0;
		Dungeon.challenges = 0;
		Dungeon.quickslot = new QuickSlot();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = 2 + 2 * WIDTH;
		hero.lvl = Hero.MAX_LEVEL;
		hero.HP = hero.HT = 1000;
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

	private static int count(RecordingLevel level, Class<? extends Mob> type) {
		int count = 0;
		for (Mob mob : level.mobs) if (type.isInstance(mob) && mob.isAlive()) count++;
		return count;
	}

	private static void setInt(Object target, Class<?> owner, String name, int value) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		field.setInt(target, value);
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

	private static final class TestGoo extends SpsGoo {
		boolean takeTurn() { return act(); }
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
		boolean forcedMiss(Char enemy) { return attack(enemy, 1f, 0f, 0f); }
	}

	private static final class TestPoisonGoo extends SpsGoo.PoisonGoo {
		boolean takeTurn() { return act(); }
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
		int defend(Char enemy, int damage) { return defenseProc(enemy, damage); }
	}

	private static final class TestSewerHeart extends SewerHeart {
		boolean takeTurn() { return act(); }
		boolean canFire(Char enemy) { return canAttack(enemy); }
		boolean fire(Char enemy) { return doAttack(enemy); }
	}

	private static final class TestPlagueDoctor extends PlagueDoctor {
		boolean takeTurn() { return act(); }
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
		int defend(Char enemy, int damage) { return defenseProc(enemy, damage); }
	}

	private static final class TestSewerLasher extends SewerHeart.SewerLasher {
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
	}

	private static final class TestShadowRat extends PlagueDoctor.ShadowRat {
		int proc(Char enemy, int damage) { return attackProc(enemy, damage); }
	}

	private static final class Target extends Mob {
		Target() { HP = HT = 1000; defenseSkill = 0; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
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
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsSewerBossTimelineTest() { }
}
