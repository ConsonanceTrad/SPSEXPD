package pd.items.rings.fusion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.rings.Ring;
import pd.items.rings.RingOfAccuracy;
import pd.items.rings.RingOfElements;
import pd.items.rings.RingOfEnergy;
import pd.items.rings.RingOfEvasion;
import pd.items.rings.RingOfForce;
import pd.items.rings.RingOfFuror;
import pd.items.rings.RingOfHaste;
import pd.items.rings.RingOfMight;
import pd.items.rings.RingOfSharpshooting;
import pd.items.rings.RingOfTenacity;
import pd.items.wands.WandOfTest;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.Random;
import watabou.utils.SparseArray;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsMagicKnowledgeRingsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Ring.initGems();
		try {
			testMagicSkill();
			testLegacyBranchTier();
			testKnowledgeCritical();
			testKnowledgeDropState();
			testBossDropIntegration();
			testGeneratorPool();
			testResources();
			System.out.println("SPS奥术与学识戒指测试通过：法强、法术暴击、额外掉落、进度存档、首领倍率、生成权重及双语原文均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testLegacyBranchTier() {
		Dungeon.depth = pd.items.quest.AdventureJournal.anchorDepth(22);
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(22);
		check(RingOfKnowledge.rareEquipmentTier() == 18,
				"学识戒指稀有装备没有按混沌领域旧版85层选择阶级");
		Dungeon.depth = 1;
		Dungeon.branch = 0;
	}

	private static void testMagicSkill() {
		Hero hero = hero();
		RingOfMagic first = activeMagic(hero, 10);
		check(RingOfMagic.magicSkillBonus(hero) == 10 && hero.magicSkill() == 10,
				"+10奥术戒指没有接入英雄法强");
		first.level(40);
		check(RingOfMagic.magicSkillBonus(hero) == 30 && hero.magicSkill() == 30,
				"奥术戒指没有在每枚30级封顶");
		RingOfMagic second = activeMagic(hero, 20);
		check(RingOfMagic.magicSkillBonus(hero) == 50 && hero.magicSkill() == 50,
				"两枚奥术戒指没有按旧版分别封顶后叠加");
		first.level(-2);
		second.level(0);
		check(RingOfMagic.magicSkillBonus(hero) == -2 && hero.magicSkill() == -2,
				"奥术戒指负等级或原始等级行为错误");
	}

	private static void testKnowledgeCritical() {
		check(RingOfKnowledge.applyCriticalBonus(null, 100, 0) == 100,
				"无施法者的法杖伤害触发学识戒指空引用");
		Hero hero = hero();
		RingOfKnowledge ring = activeKnowledge(hero, 10, null);
		check(RingOfKnowledge.knowledgeBonus(hero) == 10, "学识戒指错误使用level+1");
		check(RingOfKnowledge.applyCriticalBonus(hero, 100, 0) == 180,
				"+10学识戒指法术暴击倍率错误");
		check(RingOfKnowledge.applyCriticalBonus(hero, 100, 5) == 100,
				"学识戒指在25%暴击范围外仍然增伤");

		ring.level(30);
		check(RingOfKnowledge.applyCriticalBonus(hero, 100, 4) == 300,
				"+30学识戒指没有达到3倍法术暴击上限");
		ring.level(-2);
		check(RingOfKnowledge.applyCriticalBonus(hero, 100, 0) == 100
				&& RingOfKnowledge.tryForBonusDrop(hero, 10) == null,
				"负等级学识戒指错误触发增益");

		ring.level(10);
		FixedWand wand = new FixedWand();
		wand.user(hero);
		Random.pushGenerator(0x5A17L);
		try {
			boolean sawNormal = false;
			boolean sawCritical = false;
			for (int i = 0; i < 100; i++) {
				int damage = wand.damageRoll();
				check(damage == 10 || damage == 18, "学识戒指实际法杖伤害出现非法值：" + damage);
				sawNormal |= damage == 10;
				sawCritical |= damage == 18;
			}
			check(sawNormal && sawCritical, "学识戒指未接入实际法杖伤害路径");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testKnowledgeDropState() {
		Hero hero = hero();
		Bundle firstState = dropState(20f, 5);
		Bundle secondState = dropState(30f, 7);
		RingOfKnowledge first = activeKnowledge(hero, 10, firstState);
		RingOfKnowledge second = activeKnowledge(hero, 10, secondState);
		ArrayList<Item> noDrop = RingOfKnowledge.tryForBonusDrop(hero, 1);
		check(noDrop != null && noDrop.isEmpty(), "学识戒指未按旧版倒计时产生额外掉落");

		Bundle firstSaved = new Bundle();
		Bundle secondSaved = new Bundle();
		first.storeInBundle(firstSaved);
		second.storeInBundle(secondSaved);
		check(close(firstSaved.getFloat("tries_to_drop"), 28f)
				&& close(secondSaved.getFloat("tries_to_drop"), 28f)
				&& firstSaved.getInt("drops_to_rare") == 7
				&& secondSaved.getInt("drops_to_rare") == 7,
				"两枚学识戒指没有同步并保存较大的掉落进度");

		RingOfKnowledge restored = new RingOfKnowledge();
		restored.restoreFromBundle(firstSaved);
		Bundle roundTrip = new Bundle();
		restored.storeInBundle(roundTrip);
		check(close(roundTrip.getFloat("tries_to_drop"), 28f)
				&& roundTrip.getInt("drops_to_rare") == 7, "学识戒指掉落进度无法恢复存档");
	}

	private static void testBossDropIntegration() {
		Hero hero = heroOnLevel();
		activeKnowledge(hero, 10, dropState(5f, 2));
		TestMob boss = new TestMob();
		boss.pos = 41;
		boss.makeBoss();
		Dungeon.level.mobs.add(boss);
		Actor.add(boss);
		boss.rollToDropLoot();
		check(((TestLevel)Dungeon.level).dropped.size() >= 1,
				"首领死亡没有按10次进度触发学识戒指额外掉落");
	}

	private static void testGeneratorPool() {
		Class<?>[] expected = {RingOfAccuracy.class, RingOfEvasion.class, RingOfElements.class,
				RingOfForce.class, RingOfFuror.class, RingOfHaste.class, RingOfMagic.class,
				RingOfMight.class, RingOfSharpshooting.class, RingOfTenacity.class,
				RingOfEnergy.class, RingOfKnowledge.class};
		check(Arrays.equals(Generator.Category.RING.classes, expected), "戒指生成池顺序未恢复为旧版十二种");
		for (float probability : Generator.Category.RING.defaultProbs) {
			check(close(probability, 1f), "十二枚戒指没有恢复旧版等权生成");
		}
	}

	private static void testResources() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(zh.contains("items.rings.fusion.ringofmagic.name=奥术戒指")
				&& zh.contains("你的法强值会提升_%d_点")
				&& zh.contains("items.rings.fusion.ringofknowledge.name=学识戒指")
				&& zh.contains("施法时有25%%的几率造成_%1$s_倍伤害")
				&& !zh.contains("�"), "奥术或学识戒指中文原文缺失或乱码");
		check(en.contains("MIG improve _%d_ .")
				&& en.contains("25%% chance to deal _%1$s_ times damage"),
				"奥术或学识戒指英文原文缺失");
	}

	private static RingOfMagic activeMagic(Hero hero, int level) {
		RingOfMagic ring = new RingOfMagic();
		ring.level(level);
		ring.activate(hero);
		return ring;
	}

	private static RingOfKnowledge activeKnowledge(Hero hero, int level, Bundle state) {
		RingOfKnowledge ring = new RingOfKnowledge();
		if (state != null) ring.restoreFromBundle(state);
		ring.level(level);
		ring.activate(hero);
		return ring;
	}

	private static Bundle dropState(float tries, int rare) {
		Bundle state = new Bundle();
		state.put("tries_to_drop", tries);
		state.put("drops_to_rare", rare);
		return state;
	}

	private static Hero hero() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		return hero;
	}

	private static Hero heroOnLevel() {
		Hero hero = hero();
		Dungeon.depth = 1;
		Dungeon.level = new TestLevel();
		hero.pos = 40;
		Actor.add(hero);
		return hero;
	}

	private static boolean close(float first, float second) {
		return Math.abs(first - second) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class FixedWand extends WandOfTest {
		void user(Hero hero) { curUser = hero; }
	}

	private static final class TestMob extends Mob {
		void makeBoss() { properties.add(Char.Property.BOSS); }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
	}

	private static final class TestLevel extends Level {
		final ArrayList<Item> dropped = new ArrayList<>();

		TestLevel() {
			setSize(9, 9);
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
			discoverable = new boolean[length()];
			Arrays.fill(discoverable, true);
			Arrays.fill(heroFOV, true);
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			dropped.add(item);
			Heap heap = new Heap();
			heap.pos = cell;
			return heap;
		}
	}

	private SpsMagicKnowledgeRingsTest() { }
}
