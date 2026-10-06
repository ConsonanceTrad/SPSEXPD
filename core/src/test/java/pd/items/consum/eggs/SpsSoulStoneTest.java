/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 无图形验证「魂石」（宠物体系重写）：
 *   - 魂石占神器位（Artifact）、不可堆叠
 *   - 宠物数据（血量/培养等级/喂食次数/复活冷却）随魂石存档往返
 *   - 召唤出的投影类型正确，冷却按回合数门控
 *   - 投影语义：标记、不入档、死亡回写血量与冷却
 */

package pd.items.consum.eggs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;

import java.lang.reflect.Method;

import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.items.equipment.artifacts.Artifact;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public final class SpsSoulStoneTest {

	private static int checks = 0;

	private static void check(boolean cond, String what) {
		checks++;
		if (!cond) throw new AssertionError("FAIL: " + what);
	}

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x534F554C4C53544FL);
		try {
			testIsArtifact();
			testDataRoundTrip();
			testSummonTypeAndCooldown();
			testProjectionSemantics();
			testEnergyGateAndForming();
			testBreakRingBoost();
			testSacrificeAndMastery();

			System.out.println("SpsSoulStoneTest PASS (" + checks + " checks)");
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/** 魂石必须占神器位、且每种一颗（不可堆叠） */
	private static void testIsArtifact() {
		check(new DogpetEgg() instanceof Artifact, "魂石必须是 Artifact（占神器位）");
		//装备路径：魂石没有神器被动效果，activate 不应崩溃
		DogpetEgg worn = new DogpetEgg();
		worn.activate(null);
		
		check(!new DogpetEgg().stackable, "魂石不该可堆叠（每种一颗）");
	}

	/** 宠物数据随魂石存档往返 */
	private static void testDataRoundTrip() {
		DogpetEgg s = new DogpetEgg();
		s.petHp = 42;
		s.petLevel = 3;
		s.feedCount = 9;
		s.reviveAtTurn = 120f;

		Bundle b = new Bundle();
		s.storeInBundle(b);

		DogpetEgg r = new DogpetEgg();
		r.restoreFromBundle(b);
		check(r.petHp == 42, "魂石血量未持久化");
		check(r.petLevel == 3, "魂石培养等级未持久化");
		check(r.feedCount == 9, "魂石喂食次数未持久化");
		check(Math.abs(r.reviveAtTurn - 120f) < 0.01f, "魂石复活冷却未持久化");
	}

	/** 召唤类型 + 回合制冷却门控 */
	private static void testSummonTypeAndCooldown() throws Exception {
		check(hatch(new DogpetEgg()) instanceof DogPet, "魂石召唤出的投影类型错误");

		DogpetEgg s = new DogpetEgg();
		s.reviveAtTurn = Game.timeTotal + 10f;
		check(!s.canSummon(null), "冷却未结束时不该允许召唤");
		s.reviveAtTurn = Game.timeTotal - 1f;
		check(s.canSummon(null), "冷却结束后应允许召唤");
	}

	/** 投影语义：标记 / 不入档 / 死亡回写血量与回合冷却 */
	private static void testProjectionSemantics() throws Exception {
		DogpetEgg stone = new DogpetEgg();
		DogPet pet = (DogPet) hatch(new DogpetEgg());
		pet.markProjection(stone);
		check(pet.projection, "投影标记失败");
		check(pet.stone == stone, "投影没有记住来源魂石");

		//投影不入档：存档时不该写入自身数据
		Bundle pb = new Bundle();
		pet.storeInBundle(pb);
		check(!pb.contains("cooldown"), "投影把数据写进了存档");

		//死亡回写：血量清零 + 复活冷却从当前回合起算
		Method death = LegacyPet.class.getDeclaredMethod("onProjectionDeath");
		death.setAccessible(true);
		death.invoke(pet);
		check(stone.petHp == 0, "投影死亡没有把血量清零");
		check(stone.reviveAtTurn >= Game.timeTotal + LegacyPet.REVIVE_TURNS - 1f,
				"投影死亡没有写入回合制复活冷却");
		check(!stone.canSummon(null), "投影刚死亡时不该允许立刻召唤");
	}

	/** 首次召唤门槛（高低不一）、食材元素主题、成型固化 */
	private static void testEnergyGateAndForming() {
		//普通宠物：moves >= 10
		DogpetEgg dog = new DogpetEgg();
		check(!dog.energyReady(), "能量为 0 时不该达到门槛");
		check(!dog.formed, "未召唤前不该是已成型");
		dog.moves = 10;
		check(dog.energyReady(), "普通魂石应在 moves 达标后可召唤");

		//龙系魂石自带对应属性的能量（出生即满足门槛）
		BlueDragonEgg dragon = new BlueDragonEgg();
		check(dragon.freezes >= 20 && dragon.energyReady(), "蓝龙之魂应自带满足门槛的冰属性能量");
		check(dragon.hatchling() instanceof pd.actors.mobs.pets.BlueDragon, "蓝龙之魂应召唤蓝龙");

		//基类魔物之魂没有预置能量：喂食攒够门槛后才成型
		Egg raw = new Egg();
		check(!raw.energyReady(), "未攒能量的魔物之魂不该达门槛");
		raw.burns = 20;
		check(raw.energyReady() && raw.hatchling() instanceof pd.actors.mobs.pets.RedDragon,
				"火属性达标后魔物之魂应成型为红龙");
		check(!raw.formed, "未召唤前不该是已成型");

		//食材 -> 属性（按元素主题）
		check(Egg.energyOf(new pd.items.consum.food.meatfood.FireMeat())[1] == 5, "火肉应给火属性");
		check(Egg.energyOf(new pd.items.consum.food.meatfood.IceMeat())[2] == 5, "冰肉应给冰属性");
		check(Egg.energyOf(new pd.items.consum.food.meatfood.EarthMeat())[3] == 5, "地肉应给地属性");
		check(Egg.energyOf(new pd.items.consum.food.meatfood.DarkMeat())[5] == 5, "暗肉应给暗属性");
		check(Egg.energyOf(new pd.items.consum.food.meatfood.Meat())[0] == 3, "普通肉应给无属性能量");

		//成型固化：formedPet 决定召唤结果（基类魔物之魂按能量成型后固定）
		Egg base = new Egg();
		base.formed = true;
		base.formedPet = "pd.actors.mobs.pets.DogPet";
		check(base.hatchling() instanceof DogPet, "固化后的魂石应固定给出对应生物");

		//固化字段入档
		Bundle b = new Bundle();
		base.storeInBundle(b);
		Egg back = new Egg();
		back.restoreFromBundle(b);
		check(back.formed && "pd.actors.mobs.pets.DogPet".equals(back.formedPet), "成型固化未持久化");
	}

	/** 炸环：增幅按生物种类分档，并随培养程度提升 */
	private static void testBreakRingBoost() {
		int dog = Egg.strikeBoost(new pd.actors.mobs.pets.DogPet(), 0);
		int dragon = Egg.strikeBoost(new pd.actors.mobs.pets.BlueDragon(), 0);
		int gold = Egg.strikeBoost(new pd.actors.mobs.pets.GoldDragon(), 0);
		check(dragon > dog, "龙系的炸环增幅应高于普通宠物");
		check(gold > dragon, "金龙的炸环增幅应高于普通龙");
		check(Egg.strikeBoost(new pd.actors.mobs.pets.DogPet(), 4) > dog, "炸环增幅应随培养程度提升");
		check(Egg.strikeBoost(new pd.actors.mobs.pets.DogPet(), 8) > Egg.strikeBoost(new pd.actors.mobs.pets.DogPet(), 4),
				"炸环增幅应随培养程度持续提升");
	}

	/**
	 * 献祭映射（生物 -> 能力特质）。
	 * 注意：这里只做不需要实例化 Perk 的检查 —— 特质类的静态块会注册 InlineText，
	 * 而 InlineText 依赖 Gdx.app（headless 单测环境没有），所以具体映射在游戏内验证。
	 */
	private static void testSacrificeAndMastery() {
		check(Egg.petAbilityOf(null) == null, "空生物不应有对应能力特质");
	}

	private static LegacyPet hatch(Egg egg) throws Exception {
		Method m = Egg.class.getDeclaredMethod("hatchling");
		m.setAccessible(true);
		return (LegacyPet) m.invoke(egg);
	}
}
