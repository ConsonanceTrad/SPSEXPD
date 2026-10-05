/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 无图形验证特质（Perk）体系：容器增删改、序列化往返、候选抽取数量、
 * 暴击入口、条件型特质、职业初始特质，以及破碎天赋已停用。
 */

package pd.actors.hero.perks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;

import java.util.ArrayList;

import pd.actors.hero.Critical;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.TraitCounters;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public final class SpsTraitSystemTest {

	private static int checks = 0;

	private static void check(boolean cond, String what) {
		checks++;
		if (!cond) throw new AssertionError("FAIL: " + what);
	}

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5452414954535053L);
		try {
			testContainer();
			testSerialization();
			testCandidateCount();
			testCritical();
			testConditionalPerk();
			testInitialPerks();
			testPerkPointSchedule();
			testTalentsRetired();

			System.out.println("SpsTraitSystemTest PASS (" + checks + " checks)");
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/** 容器：添加 / 重复获得=升级 / 降级 / 移除 */
	private static void testContainer() {
		HeroPerk hp = new HeroPerk();
		check(hp.getPerks().isEmpty(), "新容器为空");

		hp.add(new GoodAppetite());
		check(hp.has(GoodAppetite.class), "添加后拥有");
		check(hp.get(GoodAppetite.class).level() == 1, "初始等级为 1");

		hp.add(new GoodAppetite());
		check(hp.get(GoodAppetite.class).level() == 2, "重复获得即升级");
		check(hp.getPerks().size() == 1, "同类特质不重复占位");

		Perk p = hp.get(GoodAppetite.class);
		hp.downgrade(p);
		check(p.level() == 1, "降级到 1 级");
		hp.downgrade(p);
		check(!hp.has(GoodAppetite.class), "降到 0 级自动移除");
	}

	/** 序列化往返（等级随特质存档） */
	private static void testSerialization() {
		HeroPerk a = new HeroPerk();
		a.add(new HardCrit());
		a.add(new ExtraStrength());
		a.get(HardCrit.class).setLevel(3);

		Bundle bundle = new Bundle();
		a.storeInBundle(bundle);

		HeroPerk b = new HeroPerk();
		b.restoreFromBundle(bundle);
		check(b.has(HardCrit.class), "特质类持久化");
		check(b.get(HardCrit.class).level() == 3, "特质等级持久化");
		check(b.has(ExtraStrength.class), "多特质持久化");
		check(b.getPerks().size() == 2, "数量持久化");

		// 空存档容错
		HeroPerk c = new HeroPerk();
		c.restoreFromBundle(new Bundle());
		check(c.getPerks().isEmpty(), "缺键时安全为空");
	}

	/** 候选数量：默认 3，拥有「额外特质位」时 5 */
	private static void testCandidateCount() {
		check(Perk.Companion.candidateCount(null) == 3, "默认候选 3 个");

		Hero hero = new Hero();
		check(Perk.Companion.candidateCount(hero) == 3, "无特质时候选 3 个");
		hero.heroPerk.add(new ExtraPerkChoice());
		check(Perk.Companion.candidateCount(hero) == 5, "有额外特质位时候选 5 个");

		ArrayList<Perk> three = Perk.Companion.randomPositives(hero, 3);
		check(three.size() == 3, "抽取 3 个候选");
		ArrayList<Perk> five = Perk.Companion.randomPositives(hero, 5);
		check(five.size() == 5, "抽取 5 个候选");
		boolean distinct = five.get(0).getClass() != five.get(1).getClass();
		check(distinct, "候选互不重复");
	}

	/** 暴击入口：无英雄/零暴击率不触发，满暴击率必触发并放大 */
	private static void testCritical() {
		check(Critical.roll(null, 50) == 50, "无英雄时不暴击");
		check(!Critical.lastWasCrit(), "无英雄时无暴击标记");

		Hero hero = new Hero();
		hero.criticalChance = 0f;
		check(Critical.roll(hero, 50) == 50, "暴击率为 0 时伤害不变");
		check(!Critical.lastWasCrit(), "零暴击率无标记");

		hero.criticalChance = 1f;
		int crit = Critical.roll(hero, 100);
		check(crit == 150, "必暴击时按 1.5 倍放大");
		check(Critical.lastWasCrit(), "设置暴击标记");

		// 「重击」特质提高暴击倍率
		hero.heroPerk.add(new HardCrit());
		int crit2 = Critical.roll(hero, 100);
		check(crit2 > 150, "重击提高暴击伤害");
	}

	/** 条件型特质：解除饥饿 10 次自动获得，且提供饥饿上限加成 */
	private static void testConditionalPerk() {
		Hero hero = new Hero();
		HardenedStomach perk = new HardenedStomach();

		check(!perk.conditionMet(hero), "未达条件时不可获得");
		hero.traitCounters.set(TraitCounters.HUNGER_RELIEVED, 9);
		check(!perk.conditionMet(hero), "9 次仍未达条件");
		hero.traitCounters.add(TraitCounters.HUNGER_RELIEVED, 1);
		check(perk.conditionMet(hero), "10 次解除饥饿达条件");

		check(HardenedStomach.capBonus(hero) == 0, "未获得时无饥饿上限加成");
		hero.heroPerk.add(perk);
		check(HardenedStomach.capBonus(hero) == 200, "获得后饥饿上限 +200");

		// 计数器随英雄存档
		Bundle bundle = new Bundle();
		hero.traitCounters.storeInBundle(bundle);
		TraitCounters restored = new TraitCounters();
		restored.restoreFromBundle(bundle);
		check(restored.get(TraitCounters.HUNGER_RELIEVED) == 10, "计数器持久化");
	}

	/** 职业初始特质（把角色特殊点以特质呈现） */
	private static void testInitialPerks() {
		Hero rogue = new Hero();
		rogue.heroClass = HeroClass.ROGUE;
		ArrayList<Perk> roguePerks = PerkGrants.initialPerks(rogue);
		check(!roguePerks.isEmpty(), "盗贼有初始特质");
		boolean hasSearch = false;
		for (Perk p : roguePerks) if (p instanceof EfficientSearch) hasSearch = true;
		check(hasSearch, "盗贼初始含「高效搜索」（搜索更远）");

		Hero cleric = new Hero();
		cleric.heroClass = HeroClass.CLERIC;
		check(!PerkGrants.initialPerks(cleric).isEmpty(), "修士有初始特质");

		// 发放后确实进入容器
		for (Perk p : roguePerks) rogue.heroPerk.add(p);
		check(rogue.heroPerk.has(EfficientSearch.class), "初始特质已发放");
	}

	/** 特质点发放节奏：每 3 级一次 */
	private static void testPerkPointSchedule() {
		check(Hero.PERK_LEVEL_STEP == 3, "发点步长为 3");

		check(Hero.grantsPerkPoint(3), "3 级发点");
		check(Hero.grantsPerkPoint(30), "30 级发点");
		check(!Hero.grantsPerkPoint(1), "1 级不发点");
		check(!Hero.grantsPerkPoint(5), "5 级不发点（已由 5 改为 3）");
		check(!Hero.grantsPerkPoint(0), "0 级不发点");

		int count = 0;
		for (int lvl = 1; lvl <= Hero.MAX_LEVEL; lvl++) {
			if (Hero.grantsPerkPoint(lvl)) count++;
		}
		check(count == 10, "30 级内共 10 次发点（3/6/…/30）");
	}

	/** 破碎天赋已停用：点数恒为 0 */
	private static void testTalentsRetired() {
		Hero hero = new Hero();
		hero.lvl = 30;
		for (int tier = 1; tier <= 4; tier++) {
			check(hero.talentPointsAvailable(tier) == 0, "tier " + tier + " 天赋点已停用");
		}
		check(hero.reservedPerks == 0, "初始特质点为 0");
	}
}
