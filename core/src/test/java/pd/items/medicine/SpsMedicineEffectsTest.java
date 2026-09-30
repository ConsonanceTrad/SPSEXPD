package pd.items.medicine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsMedicineEffectsTest {

	private static final int CENTER = 8 + 8 * 16;

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x4D45444943494E45L);
		try {
			testMushrooms();
			testGrowSeedTurnsAndSave();
			testCombatPills();
			testRecoveryAndDewPills();
			testImagesAndValues();
			System.out.println("SPS药丸测试通过：全层蘑菇、寄生逐回合与存档、战斗药丸、恢复、露珠条件、元素亲和、原始图标槽和售价均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testMushrooms() {
		State state = state();
		state.hero.HP = 80;
		new BlueMilk().onUse(state.hero);
		check(state.mob.buff(Slow.class) != null && state.mob.buff(Slow.class).cooldown() >= 50f,
				"蓝奶伞没有给予敌人50回合迟缓");
		check(state.mob.buff(AttackDown.class) != null && state.mob.buff(AttackDown.class).level() == 50,
				"蓝奶伞没有给予敌人50级降攻");
		check(state.hero.buff(HasteBuff.class) != null && state.hero.buff(BerryRegeneration.class).level() == 40,
				"蓝奶伞没有给予10回合加速和当前生命一半的莓果恢复");

		state = state();
		state.hero.HP = 80;
		new DeathCap().onUse(state.hero);
		check(state.mob.buff(BeOld.class) != null && state.mob.buff(BeCorrupt.class) != null
				&& state.mob.buff(BeCorrupt.class).level() == 50, "致死帽没有给予衰老与50级腐化");
		check(state.hero.HP == 40 && state.hero.buff(Cripple.class) != null,
				"致死帽没有扣除一半当前生命并致残");

		state = state();
		state.hero.HP = 80;
		new Earthstar().onUse(state.hero);
		check(state.mob.buff(Bleeding.class) != null && state.hero.HP == 60
				&& state.hero.buff(Blindness.class) != null, "地裂星的全层流血或自伤失明错误");

		state = state();
		new GoldenJelly().onUse(state.hero);
		check(state.mob.buff(GrowSeed.class) != null && state.hero.buff(Vertigo.class) != null,
				"凝胶团没有给予寄生与眩晕");

		state = state();
		new JackOLantern().onUse(state.hero);
		check(state.mob.buff(DBurning.class) != null, "灯笼球没有给予旧版深度燃烧");

		state = state();
		new PixieParasol().onUse(state.hero);
		check(state.mob.buff(Drowsy.class) != null && state.mob.buff(Paralysis.class) != null
				&& state.mob.buff(ArmorBreak.class) != null && state.mob.buff(ArmorBreak.class).level() == 30,
				"单色块没有给予睡意、麻痹和30级破甲");
		check(state.hero.buff(Bless.class) != null, "单色块没有给予20回合祝福");
	}

	private static void testCombatPills() {
		State state = state();
		new Hardpill().onUse(state.hero);
		check(state.hero.buff(DefenceUp.class) != null && state.hero.buff(DefenceUp.class).level() == 50
				&& state.hero.buff(DefenceUp.class).cooldown() >= 800f, "硬化药丸不是800回合50级防御");

		state = state();
		new Smashpill().onUse(state.hero);
		check(state.hero.buff(AttackUp.class) != null && state.hero.buff(AttackUp.class).level() == 50
				&& state.hero.buff(AttackUp.class).cooldown() >= 800f, "增幅药丸不是800回合50级攻击");

		state = state();
		new Powerpill().onUse(state.hero);
		check(state.hero.buff(Muscle.class) != null && state.hero.buff(Muscle.class).cooldown() >= 1440f,
				"力量药丸没有给予1440回合肌力");

		state = state();
		new Shootpill().onUse(state.hero);
		check(state.hero.buff(TargetShoot.class) != null && state.hero.buff(TargetShoot.class).cooldown() >= 800f,
				"神射药丸没有给予800回合瞄准");

		state = state();
		new MagicPill().onUse(state.hero);
		check(state.hero.buff(Arcane.class) != null && state.hero.buff(Arcane.class).cooldown() >= 50f,
				"奥术药丸没有给予50回合奥术");

		state = state();
		state.hero.heroClass = HeroClass.PERFORMER;
		state.hero.subClass = HeroSubClass.SUPERSTAR;
		new Musicpill().onUse(state.hero);
		check(state.hero.buff(Rhythm.class) != null && state.hero.buff(Rhythm.class).cooldown() >= 800f
				&& state.hero.buff(WarGroove.class) != null && state.hero.buff(Rhythm2.class) != null,
				"节奏药丸没有给予表演者和超级明星完整状态");
	}

	private static void testGrowSeedTurnsAndSave() {
		State state = state();
		state.mob.pos = CENTER + 1;
		state.hero.HP = 90;
		GrowSeed growth = Buff.affect(state.mob, GrowSeed.class);
		growth.set(2f);
		int firstMobHP = state.mob.HP;
		int firstHeroHP = state.hero.HP;
		growth.act();
		check(state.mob.HP < firstMobHP && state.hero.HP > firstHeroHP,
				"寄生状态首回合没有伤害目标或治疗相邻角色");
		check(state.mob.buff(GrowSeed.class) == growth && growth.level() == 1f,
				"寄生状态首回合后提前消失或时长错误");
		growth.act();
		check(state.mob.buff(GrowSeed.class) == null, "寄生状态没有在第二回合结束");

		Bundle legacy = new Bundle();
		legacy.put("left", 7f);
		GrowSeed restored = new GrowSeed();
		restored.restoreFromBundle(legacy);
		check(restored.level() == 7f, "寄生状态没有读取旧版left字段");
		Bundle saved = new Bundle();
		restored.storeInBundle(saved);
		check(saved.getFloat("left") == 7f, "寄生状态没有保存剩余时长");
	}

	private static void testRecoveryAndDewPills() {
		State state = state();
		state.hero.HP = 40;
		Buff.affect(state.hero, Poison.class).set(10f);
		Buff.affect(state.hero, Cripple.class, 10f);
		Buff.affect(state.hero, STRDown.class, 10f);
		Buff.affect(state.hero, Bleeding.class).set(10);
		new Greaterpill().onUse(state.hero);
		check(state.hero.HP == 140 && state.hero.buff(BerryRegeneration.class).level() == 50,
				"生血丸没有按旧版越上限恢复或给予莓果恢复");
		check(state.hero.buff(Poison.class) == null && state.hero.buff(Cripple.class) == null
				&& state.hero.buff(STRDown.class) == null && state.hero.buff(Bleeding.class) == null,
				"生血丸没有清除四种旧版异常");

		state = state();
		Dungeon.dewDraw = Dungeon.dewWater = false;
		new GreenSpore().onUse(state.hero);
		check(state.hero.buff(Dewcharge.class) == null, "未升级露珠系统时绿菌孢错误生效");
		Dungeon.dewWater = true;
		new GreenSpore().onUse(state.hero);
		check(state.hero.buff(Dewcharge.class) != null && state.hero.buff(Dewcharge.class).cooldown() >= 100f,
				"升级露珠系统后绿菌孢没有给予100回合露珠充能");

		state = state();
		Buff.affect(state.hero, Poison.class).set(10f);
		Buff.affect(state.hero, Cripple.class, 10f);
		Buff.affect(state.hero, STRDown.class, 10f);
		Buff.affect(state.hero, Bleeding.class).set(10);
		new Foamedbeverage().onUse(state.hero);
		check(state.hero.buff(Poison.class) == null && state.hero.buff(Cripple.class) == null
				&& state.hero.buff(STRDown.class) == null && state.hero.buff(Bleeding.class) == null,
				"发泡饮料没有清除四种旧版异常");
		check(state.hero.buff(Bless.class) != null && state.hero.buff(BerryRegeneration.class).level() == 25,
				"发泡饮料的祝福或恢复等级错误");
		check(state.hero.buff(FireImbue.class) != null || state.hero.buff(FrostImbue.class) != null
				|| state.hero.buff(ToxicImbue.class) != null || state.hero.buff(EarthImbue.class) != null,
				"发泡饮料没有给予四选一元素亲和");
	}

	private static void testImagesAndValues() {
		check(new BlueMilk().image == ItemSpriteSheet.MUSHROOM_BLUEMILK
				&& new DeathCap().image == ItemSpriteSheet.MUSHROOM_DEATHCAP
				&& new Earthstar().image == ItemSpriteSheet.MUSHROOM_EARTHSTAR
				&& new GoldenJelly().image == ItemSpriteSheet.MUSHROOM_GOLDENJELLY
				&& new GreenSpore().image == ItemSpriteSheet.MUSHROOM_GREEN_SPORE
				&& new JackOLantern().image == ItemSpriteSheet.MUSHROOM_LANTERN
				&& new PixieParasol().image == ItemSpriteSheet.MUSHROOM_PIXIEPARASOL,
				"七种蘑菇没有使用0.9.8专属图标槽");
		check(new Greaterpill().image == ItemSpriteSheet.GREAT_PILL
				&& new Hardpill().image == ItemSpriteSheet.GREAT_PILL
				&& new MagicPill().image == ItemSpriteSheet.GREAT_PILL
				&& new Musicpill().image == ItemSpriteSheet.GREAT_PILL
				&& new Powerpill().image == ItemSpriteSheet.GREAT_PILL
				&& new Shootpill().image == ItemSpriteSheet.GREAT_PILL
				&& new Smashpill().image == ItemSpriteSheet.GREAT_PILL,
				"七种战斗药丸没有使用0.9.8强效药丸图标槽");
		check(new RealgarWine().image == ItemSpriteSheet.WINE && new RealgarWine().value() == 50,
				"雄黄酒图标或售价错误");
		check(new BlueMilk(3).quantity() == 3 && new DeathCap(4).quantity() == 4
				&& new GoldenJelly(5).quantity() == 5, "蘑菇数量构造器未保留");
	}

	private static State state() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.dewDraw = Dungeon.dewWater = false;
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = CENTER;
		Dungeon.hero = hero;
		Actor.add(hero);
		TestMob mob = new TestMob();
		mob.HP = mob.HT = 100;
		mob.pos = CENTER + 2;
		level.mobs().add(mob);
		Actor.add(mob);
		return new State(hero, mob);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class State {
		final Hero hero;
		final TestMob mob;
		State(Hero hero, TestMob mob) { this.hero = hero; this.mob = mob; }
	}

	private static final class TestMob extends Mob { }

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
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
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private SpsMedicineEffectsTest() { }
}
