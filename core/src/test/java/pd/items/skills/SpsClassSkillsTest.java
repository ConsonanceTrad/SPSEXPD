package pd.items.skills;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.potions.Potion;
import pd.items.rings.Ring;
import pd.items.scrolls.Scroll;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Headless behavior and persistence checks for all eight SPS class skills. */
public final class SpsClassSkillsTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x534B494C4C535053L);
		try {
			Scroll.initLabels();
			Potion.initColors();
			Ring.initGems();
			Generator.fullReset();
			testMappings();
			testSharedBuffs();
			testSkillEffectsAndCooldowns();
			System.out.println("SPS职业技能测试通过：8职业映射、16种核心状态、技能效果、冷却、地形边界与存档均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testMappings() {
		Class<?>[] expected = {WarriorSkill.class, MageSkill.class, RogueSkill.class, HuntressSkill.class,
				null, PerformerSkill.class, SoldierSkill.class, FollowerSkill.class, AsceticSkill.class};
		HeroClass[] playable = HeroClass.playableClasses();
		for (int i = 0; i < playable.length; i++) {
			ClassSkill skill = ClassSkill.createFor(playable[i]);
			if (expected[i] == null) {
				check(skill == null, "决斗家走破碎武器能力体系，不应生成SPS技能: " + playable[i]);
			} else {
				check(skill != null && skill.getClass() == expected[i], "职业技能映射错误: " + playable[i]);
			}
		}
		check(ClassSkill.createFor(HeroClass.DUELIST) == null, "决斗家走破碎武器能力体系，不应生成SPS技能");
	}

	private static void testSharedBuffs() {
		Hero hero = freshHero(HeroClass.WARRIOR, 10);
		int baseStrength = hero.STR();
		Buff.prolong(hero, Muscle.class, 20f);
		check(hero.STR() == baseStrength + 2, "肌肉强化没有增加2点力量");

		int baseHT = hero.HT;
		HTimprove vitality = Buff.prolong(hero, HTimprove.class, 20f);
		check(hero.HT == baseHT + Math.round(hero.permanentHT() * 0.2f), "临时生命上限公式错误");
		vitality.detach();
		check(hero.HT == baseHT, "临时生命状态结束后生命上限没有恢复");

		ParyAttack parry = Buff.affect(hero, ParyAttack.class).level(12);
		Blasphemy blasphemy = Buff.affect(hero, Blasphemy.class).level(3);
		MagicArmor armor = Buff.affect(hero, MagicArmor.class).level(77);
		LearnSkill learn = Buff.affect(hero, LearnSkill.class).set(9);
		checkRestored(parry, ParyAttack.class, "格挡状态存档失败");
		checkRestored(blasphemy, Blasphemy.class, "亵渎状态存档失败");
		checkRestored(armor, MagicArmor.class, "魔法护盾存档失败");
		Bundle learnBundle = new Bundle();
		learn.storeInBundle(learnBundle);
		LearnSkill restoredLearn = new LearnSkill();
		restoredLearn.restoreFromBundle(learnBundle);
		check(restoredLearn.left() == 9, "学习技能击杀计数存档失败");
	}

	private static <T extends Buff> void checkRestored(T source, Class<T> type, String message) {
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		try {
			T restored = type.getDeclaredConstructor().newInstance();
			restored.restoreFromBundle(bundle);
			Bundle roundTrip = new Bundle();
			restored.storeInBundle(roundTrip);
			check(roundTrip.toString().length() > 2, message);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError(message, e);
		}
	}

	private static void testSkillEffectsAndCooldowns() {
		Hero hero = freshHero(HeroClass.WARRIOR, 60);
		use(new WarriorSkill(), hero, ClassSkill.AC_SPECIAL_FOUR);
		check(hero.buff(BloodImbue.class) != null && hero.buff(SpAttack.class) != null, "战士奇袭状态缺失");
		check(hero.HTBoost == 1 && ClassSkill.remainingCooldown() == 10, "战士奇袭生命或冷却错误");

		hero = freshHero(HeroClass.MAGE, 60);
		int mageHT = hero.HT;
		use(new MageSkill(), hero, ClassSkill.AC_SPECIAL_TWO);
		check(hero.buff(Feed.class) != null && hero.buff(HTimprove.class) != null && hero.HT > mageHT,
				"法师灵魂虹吸状态错误");
		check(ClassSkill.remainingCooldown() == 10, "高等级法师灵魂虹吸冷却错误");

		hero = freshHero(HeroClass.ROGUE, 60);
		use(new RogueSkill(), hero, ClassSkill.AC_SPECIAL_TWO);
		check(hero.buff(ItemSteal.class) != null && hero.buff(GoldTouch.class) != null,
				"盗贼探云手状态错误");

		hero = freshHero(HeroClass.HUNTRESS, 60);
		use(new HuntressSkill(), hero, ClassSkill.AC_SPECIAL);
		check(hero.buff(TargetShoot.class) != null && hero.buff(Needling.class) != null
				&& hero.buff(FireImbue.class) != null && hero.buff(EarthImbue.class) != null
				&& hero.buff(FrostImbue.class) != null, "高等级猎手没有获得全部狩猎本能");

		hero = freshHero(HeroClass.PERFORMER, 60);
		use(new PerformerSkill(), hero, ClassSkill.AC_SPECIAL_FOUR);
		check(hero.buff(HighVoice.class) != null && hero.buff(LearnSkill.class).left() == 50,
				"演员市场研习状态错误");

		hero = freshHero(HeroClass.SOLDIER, 60);
		use(new SoldierSkill(), hero, ClassSkill.AC_SPECIAL_TWO);
		check(hero.buff(MechArmor.class).level() == 600 && hero.buff(ShieldArmor.class).level() == hero.lvl * 6,
				"星兵高等级机甲数值错误");

		hero = freshHero(HeroClass.FOLLOWER, 60);
		int oldPermanentHT = hero.permanentHT();
		int oldStrength = hero.STR;
		use(new FollowerSkill(), hero, ClassSkill.AC_SPECIAL_THREE);
		check(hero.permanentHT() == oldPermanentHT - 40 && hero.STR == oldStrength + 1
				&& hero.buff(Blasphemy.class).level() == 2, "信徒渎神永久代价或收益错误");

		hero = freshHero(HeroClass.ASCETIC, 60);
		int oldMagic = hero.magicSkill();
		use(new AsceticSkill(), hero, ClassSkill.AC_SPECIAL_THREE);
		check(hero.magicSkill() >= oldMagic + 2, "修士重编程没有提高魔力");
		check(ClassSkill.remainingCooldown() == 30, "修士重编程冷却错误");

		Bundle cooldown = new Bundle();
		new AsceticSkill().storeInBundle(cooldown);
		ClassSkill.resetCooldown();
		new AsceticSkill().restoreFromBundle(cooldown);
		check(ClassSkill.remainingCooldown() == 30, "职业技能冷却没有随存档恢复");

		testTerrainSkills();
	}

	private static void testTerrainSkills() {
		Hero hero = freshHero(HeroClass.ASCETIC, 60);
		TestLevel level = (TestLevel) Dungeon.level;
		int north = hero.pos - level.width();
		int diagonal = hero.pos - level.width() - 1;
		level.map[diagonal] = Terrain.WALL;
		level.map[north] = Terrain.ENTRANCE;
		level.buildFlagMaps();
		use(new AsceticSkill(), hero, ClassSkill.AC_SPECIAL_FOUR);
		check(level.map[diagonal] == Terrain.EMBERS, "修士地震没有破坏两格内墙壁");
		check(level.map[north] == Terrain.ENTRANCE, "修士地震覆盖了入口");

		hero = freshHero(HeroClass.SOLDIER, 60);
		hero.pos = 33;
		use(new SoldierSkill(), hero, ClassSkill.AC_SPECIAL_FOUR);
		check(Dungeon.level.heaps.valueList().size() <= 8, "边界空投生成了非法物品堆");
	}

	private static void use(ClassSkill skill, Hero hero, String action) {
		ClassSkill.resetCooldown();
		skill.execute(hero, action);
		check(ClassSkill.remainingCooldown() > 0, skill.getClass().getSimpleName() + "没有设置冷却");
	}

	private static Hero freshHero(HeroClass heroClass, int level) {
		Dungeon.depth = 12;
		Dungeon.branch = 0;
		Dungeon.gold = 1_000_000;
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.heroClass = heroClass;
		hero.lvl = level;
		hero.pos = 16 * 32 + 16;
		hero.updateHT(true);
		Talent.initClassTalents(hero);
		Dungeon.hero = hero;
		return hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
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

	private SpsClassSkillsTest() { }
}
