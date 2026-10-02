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
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.Frost;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.PlateArmor;
import pd.items.equipment.armor.normalarmor.WoodenArmor;
import pd.items.equipment.wands.WandOfDisintegration;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import pd.sprites.MusketeerSprite;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Runtime parity checks for all three SPS-PD 0.9.8 city bosses. */
public final class SpsCityBossTimelineTest {

	private static final int WIDTH = 48;
	private static final int CENTER = 24 + 24 * WIDTH;
	private static final int WELL = 3 * WIDTH + 23;
	private static final int LEFT_PEDESTAL = 9 * WIDTH + 21;
	private static final int RIGHT_PEDESTAL = 9 * WIDTH + 25;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-city-boss-timeline" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x5350534349545930L);
		try {
			testKing();
			testLichDancer();
			testElderAvatar();
			System.out.println("SPS都市首领时序通过：矮人王召唤、巫妖墓碑/炸弹、长老方尖碑四阶段与随从规则均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Dungeon.challenges = 0;
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testKing() throws Exception {
		RecordingLevel level = setup();
		TestKing king = add(level, new TestKing(), CENTER);
		king.state = king.PASSIVE;
		check(king.resist(ToxicGas.class) == 0.5f
				&& king.resist(WandOfDisintegration.class) == 0.5f,
				"矮人王缺少旧版毒气或解离法杖抗性");
		king.HP = king.HT - 1;
		king.takeTurn();
		check(king.HP == king.HT + 2, "矮人王每回合回复被错误截断到生命上限");
		King.DwarfKingTomb kingTomb = first(level, King.DwarfKingTomb.class);
		check(kingTomb != null, "矮人王首次行动没有生成王墓");
		check(!kingTomb.properties().contains(Char.Property.OBJECT)
				&& !kingTomb.properties().contains(Char.Property.IMMOVABLE)
				&& !kingTomb.properties().contains(Char.Property.MINIBOSS),
				"王墓带有0.9.8不存在的破碎版属性");

		for (int i = 0; i < 20; i++) {
			king.HP = king.HT - 2;
			king.summonNow();
			check(king.HP == king.HT - 1,
					"矮人王召唤回复未使用旧版排他上界：" + king.HP);
		}

		Bundle bundle = new Bundle();
		king.storeInBundle(bundle);
		King restored = new King();
		restored.restoreFromBundle(bundle);
		check(getBoolean(restored, King.class, "nextPedestal")
				== getBoolean(king, King.class, "nextPedestal"), "矮人王祭坛方向未随存档恢复");
	}

	private static void testLichDancer() throws Exception {
		RecordingLevel level = setup();
		TestLichDancer lich = add(level, new TestLichDancer(), CENTER);
		lich.state = lich.PASSIVE;
		check(lich.resist(ToxicGas.class) == 0.5f
				&& lich.resist(EnchantmentDark.class) == 0.5f
				&& lich.resist(WandOfDisintegration.class) == 0.5f,
				"巫妖舞者缺少旧版毒气、暗附魔或解离法杖抗性");
		lich.HP = lich.HT - 1;
		lich.takeTurn();
		check(lich.HP == lich.HT + 2, "巫妖舞者每回合回复被错误截断到生命上限");

		lich.HP = 749;
		float before = lich.cooldown();
		lich.takeTurn();
		check(getInt(lich, LichDancer.class, "breaks") == 1,
				"巫妖舞者没有在四分之三生命阈值进入第一阶段");
		check(count(level, LichDancer.BatteryTomb.class) == 1,
				"巫妖舞者跳跃后没有生成电池墓碑");
		check(close(lich.cooldown() - before, 1f), "巫妖舞者阶段跳跃耗时不等于旧版一回合");

		level = setup();
		TestBatteryTomb tomb = add(level, new TestBatteryTomb(), CENTER);
		check(!tomb.properties().contains(Char.Property.INORGANIC)
				&& !tomb.properties().contains(Char.Property.IMMOVABLE),
				"电池墓碑带有0.9.8不存在的破碎版属性");
		for (int i = 0; i < 1000 && count(level, ManySkeleton.class) == 0; i++) tomb.takeTurn();
		check(count(level, ManySkeleton.class) == 4,
				"电池墓碑没有召唤四只旧版多重骷髅");
		check(countExact(level, Skeleton.class) == 0,
				"电池墓碑错误召唤了破碎版普通骷髅");

		level = setup();
		Arrays.fill(level.map, Terrain.EMPTY);
		level.map[CENTER + 1] = Terrain.WALL;
		level.rebuildFlags();
		TestLinkBomb bomb = add(level, new TestLinkBomb(), CENTER);
		check(!bomb.properties().contains(Char.Property.INORGANIC),
				"巫妖追踪炸弹带有0.9.8不存在的无机属性");
		for (int i = 0; i < 4; i++) bomb.takeTurn();
		check(level.map[CENTER + 1] == Terrain.EMPTY,
				"巫妖追踪炸弹没有使用可破墙的旧版地牢炸弹");

		Bundle bundle = new Bundle();
		setInt(lich, LichDancer.class, "breaks", 2);
		lich.storeInBundle(bundle);
		LichDancer restored = new LichDancer();
		restored.restoreFromBundle(bundle);
		check(getInt(restored, LichDancer.class, "breaks") == 2,
				"巫妖舞者阶段未随存档恢复");
	}

	private static void testElderAvatar() throws Exception {
		RecordingLevel level = setup();
		TestElder elder = add(level, new TestElder(), CENTER);
		elder.state = elder.PASSIVE;
		check(elder.resist(ToxicGas.class) == 0.5f
				&& elder.resist(Amok.class) == 0.25f
				&& elder.resist(WandOfDisintegration.class) == 0.5f,
				"长老化身缺少旧版毒气、狂乱或解离法杖抗性");
		Dungeon.challenges = Challenges.TEST_TIME;
		for (int i = 0; i < 100; i++) {
			int damage = elder.damageRoll();
			check(damage >= 0 && damage <= 1, "测试模式没有压低长老化身伤害");
		}
		Dungeon.challenges = 0;

		float before = elder.cooldown();
		elder.takeTurn();
		check(close(elder.cooldown(), before), "长老化身生成方尖碑错误消耗了额外回合");
		TestObelisk obelisk = replaceObelisk(level, elder);
		check(obelisk != null, "长老化身首次行动没有生成方尖碑");
		check(!obelisk.properties().contains(Char.Property.OBJECT)
				&& !obelisk.properties().contains(Char.Property.IMMOVABLE)
				&& !obelisk.properties().contains(Char.Property.MINIBOSS),
				"方尖碑带有0.9.8不存在的破碎版属性");
		int fullObeliskHp = obelisk.HP;
		obelisk.damage(100, SpsCityBossTimelineTest.class);
		check(obelisk.HP == fullObeliskHp, "长老化身高于50生命时方尖碑仍可受伤");

		Class<?>[] waves = {ElderAvatar.TheHunter.class, ElderAvatar.TheWarlock.class,
				ElderAvatar.TheMonk.class, ElderAvatar.TheMech.class};
		int[] phaseDamage = {251, 250, 250};
		for (int wave = 0; wave < waves.length; wave++) {
			elder.HP = 49;
			before = elder.cooldown();
			elder.takeTurn();
			check(close(elder.cooldown(), before), "长老化身第" + (wave + 1) + "波错误消耗了额外回合");
			check(countExact(level, waves[wave]) == 4,
					"长老化身第" + (wave + 1) + "波随从类型或数量错误");
			if (wave < phaseDamage.length) {
				obelisk.damage(phaseDamage[wave], SpsCityBossTimelineTest.class);
				check(getInt(obelisk, ElderAvatar.Obelisk.class, "breaks") == wave,
						"方尖碑在自身行动前提前推进了阶段");
				before = obelisk.cooldown();
				obelisk.takeTurn();
				check(getInt(obelisk, ElderAvatar.Obelisk.class, "breaks") == wave + 1,
						"方尖碑没有在旧版行动时点推进阶段");
				check(elder.HP == elder.HT, "方尖碑阶段推进没有完全治疗长老化身");
				check(close(obelisk.cooldown(), before), "方尖碑阶段推进错误消耗了额外回合");
			}
		}
		obelisk.damage(obelisk.HP, SpsCityBossTimelineTest.class);
		check(!obelisk.isAlive(), "完成三次破坏后方尖碑仍被错误锁血");

		testElderMinions(level);
		testElderArmorRule(elder, level);

		Bundle elderBundle = new Bundle();
		elder.storeInBundle(elderBundle);
		ElderAvatar restoredElder = new ElderAvatar();
		restoredElder.restoreFromBundle(elderBundle);
		check(getInt(restoredElder, ElderAvatar.class, "waves") == 4,
				"长老化身四波进度未随存档恢复");
		Bundle obeliskBundle = new Bundle();
		obelisk.storeInBundle(obeliskBundle);
		ElderAvatar.Obelisk restoredObelisk = new ElderAvatar.Obelisk();
		restoredObelisk.restoreFromBundle(obeliskBundle);
		check(getInt(restoredObelisk, ElderAvatar.Obelisk.class, "breaks") == 3,
				"方尖碑破坏进度未随存档恢复");
	}

	private static void testElderMinions(RecordingLevel level) throws Exception {
		TestHunter hunter = add(level, new TestHunter(), CENTER + WIDTH * 4);
		Target target = add(level, new Target(), hunter.pos + 3);
		check(hunter.spriteClass == MusketeerSprite.class, "长老火枪手使用了错误精灵");
		check(!hunter.properties().contains(Char.Property.BOSS_MINION),
				"长老随从带有0.9.8不存在的破碎版首领随从属性");
		check(hunter.canShoot(target), "长老火枪手初始无法直线射击");
		Dungeon.challenges = Challenges.ELE_STOME;
		Statistics.deepestFloor = 20;
		int targetHp = target.HP;
		hunter.strike(target, 20);
		Dungeon.challenges = 0;
		check(target.HP == targetHp, "长老火枪手错误触发了被旧版覆写的元素风暴父类伤害");
		check(hunter.buff(Disarm.class) != null && !hunter.canShoot(target),
				"长老火枪手没有使用旧版五回合缴械状态装填");

		ElderAvatar.TheMonk monk = new ElderAvatar.TheMonk();
		check(close(monk.attackDelay(), 0.5f), "长老武僧攻击耗时不是旧版固定半回合");
		TestMech mech = add(level, new TestMech(), CENTER - WIDTH * 4);
		mech.state = mech.PASSIVE;
		check(mech.isImmune(Frost.class) && mech.isImmune(Chill.class)
				&& mech.isImmune(ElectriShock.class) && mech.isImmune(ToxicGas.class)
				&& mech.isImmune(DarkGas.class) && mech.isImmune(GrowSeed.class)
				&& mech.isImmune(Charm.class) && mech.isImmune(Amok.class)
				&& mech.isImmune(Paralysis.class), "长老机甲缺少旧版元素或控制免疫");
		float before = mech.cooldown();
		mech.takeTurn();
		check(mech.buff(EnergyArmor.class) != null && close(mech.cooldown(), before),
				"长老机甲首次护盾错误消耗了额外回合");
		mech.buff(EnergyArmor.class).detach();
		Bundle bundle = new Bundle();
		mech.storeInBundle(bundle);
		TestMech restored = new TestMech();
		restored.restoreFromBundle(bundle);
		restored.state = restored.PASSIVE;
		restored.takeTurn();
		check(restored.buff(EnergyArmor.class) == null,
				"长老机甲一次性护盾在读取存档后被重复添加");
	}

	private static void testElderArmorRule(TestElder elder, RecordingLevel level) throws Exception {
		Hero hero = Dungeon.hero;
		hero.belongings.armor = new WoodenArmor();
		elder.disarm(hero);
		check(hero.belongings.armor instanceof WoodenArmor, "长老化身错误缴械了旧版木甲");
		PlateArmor cursed = new PlateArmor();
		cursed.cursed = true;
		hero.belongings.armor = cursed;
		elder.disarm(hero);
		check(hero.belongings.armor == cursed, "长老化身错误缴械了诅咒护甲");
		PlateArmor plate = new PlateArmor();
		hero.belongings.armor = plate;
		int before = level.itemCount();
		elder.disarm(hero);
		check(hero.belongings.armor == null && level.itemCount() == before + 1,
				"长老化身没有按旧版规则卸下并掉落普通护甲");
	}

	private static TestObelisk replaceObelisk(RecordingLevel level, TestElder elder) throws Exception {
		ElderAvatar.Obelisk original = first(level, ElderAvatar.Obelisk.class);
		if (original == null) return null;
		level.mobs().remove(original);
		Actor.remove(original);
		TestObelisk replacement = add(level, new TestObelisk(), original.pos);
		setInt(replacement, ElderAvatar.Obelisk.class, "ownerId", elder.id());
		setInt(elder, ElderAvatar.class, "obeliskId", replacement.id());
		return replacement;
	}

	private static RecordingLevel setup() {
		Actor.clear();
		Dungeon.depth = 20;
		Dungeon.branch = 0;
		Dungeon.challenges = 0;
		Dungeon.quickslot = new QuickSlot();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = CENTER - WIDTH * 8;
		hero.HP = hero.HT = 1000;
		hero.fieldOfView = new boolean[level.length()];
		Dungeon.hero = hero;
		Actor.add(hero);
		return level;
	}

	private static <T extends Mob> T add(RecordingLevel level, T mob, int pos) {
		mob.pos = pos;
		level.mobs().add(mob);
		Actor.add(mob);
		return mob;
	}

	private static <T extends Mob> T first(RecordingLevel level, Class<T> type) {
		for (Mob mob : level.mobs()) if (type.isInstance(mob) && mob.isAlive()) return type.cast(mob);
		return null;
	}

	private static int count(RecordingLevel level, Class<?> type) {
		int result = 0;
		for (Mob mob : level.mobs()) if (type.isInstance(mob) && mob.isAlive()) result++;
		return result;
	}

	private static int countExact(RecordingLevel level, Class<?> type) {
		int result = 0;
		for (Mob mob : level.mobs()) if (mob.getClass() == type && mob.isAlive()) result++;
		return result;
	}

	private static int getInt(Object object, Class<?> owner, String name) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(object);
	}

	private static void setInt(Object object, Class<?> owner, String name, int value) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		field.setInt(object, value);
	}

	private static boolean getBoolean(Object object, Class<?> owner, String name) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		return field.getBoolean(object);
	}

	private static boolean close(float first, float second) {
		return Math.abs(first - second) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestKing extends King {
		boolean takeTurn() { return act(); }
		void summonNow() throws Exception {
			Method method = King.class.getDeclaredMethod("summon");
			method.setAccessible(true);
			method.invoke(this);
		}
	}

	private static final class TestLichDancer extends LichDancer {
		boolean takeTurn() { return act(); }
	}

	private static final class TestBatteryTomb extends LichDancer.BatteryTomb {
		boolean takeTurn() { return act(); }
	}

	private static final class TestLinkBomb extends LichDancer.LinkBomb {
		boolean takeTurn() { return act(); }
	}

	private static final class TestElder extends ElderAvatar {
		boolean takeTurn() { return act(); }
		void disarm(Hero hero) throws Exception {
			Method method = ElderAvatar.class.getDeclaredMethod("disarmArmor", Hero.class);
			method.setAccessible(true);
			method.invoke(this, hero);
		}
	}

	private static final class TestObelisk extends ElderAvatar.Obelisk {
		boolean takeTurn() { return act(); }
	}

	private static final class TestHunter extends ElderAvatar.TheHunter {
		boolean canShoot(Char target) { return canAttack(target); }
		int strike(Char target, int damage) { return attackProc(target, damage); }
	}

	private static final class TestMech extends ElderAvatar.TheMech {
		boolean takeTurn() { return act(); }
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
			map[WELL] = Terrain.WELL;
			map[WELL + WIDTH * 2] = Terrain.STATUE_SP;
			map[LEFT_PEDESTAL] = Terrain.PEDESTAL;
			map[RIGHT_PEDESTAL] = Terrain.PEDESTAL;
			mobs().clear();
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
		}

		void rebuildFlags() {
			buildFlagMaps();
			heroFOV = new boolean[length()];
		}

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

	private SpsCityBossTimelineTest() { }
}
