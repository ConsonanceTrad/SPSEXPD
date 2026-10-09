/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 无图形验证 SPS 特色属性体系：
 * - Element 七系元素映射（DamageType / SpsMagicDamage 两套令牌收口）；
 * - HeroStats 统一属性定义层（暴击倍率、物理/元素/纯粹加成、抗性、null 守卫）；
 * - Perk 属性增幅钩子累加；
 * - PureDamage 纯粹伤害（无视一切防御直接扣血、乐观抵挡、致死走 die(src)）。
 */

package pd.actors.hero;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;

import pd.actors.Char;
import pd.actors.PureDamage;
import pd.actors.damagetype.DamageType;
import pd.actors.damagetype.Element;
import pd.actors.damagetype.SpsMagicDamage;
import pd.actors.hero.perks.ExtraMagicalResistance;
import pd.actors.hero.perks.HardCrit;
import pd.actors.hero.perks.Optimistic;
import pd.actors.hero.perks.Perk;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public final class SpsHeroStatsTest {

	private static int checks = 0;

	private static void check(boolean cond, String what) {
		checks++;
		if (!cond) throw new AssertionError("FAIL: " + what);
	}

	private static void checkF(float actual, float expected, String what) {
		check(Math.abs(actual - expected) < 0.0001f, what + "（期望 " + expected + "，实际 " + actual + "）");
	}

	public static void main(String[] args) {
		Game.version = "test";
		GdxNativesLoader.load();
		new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		pd.messages.Messages.setup(pd.messages.Languages.CHI_SMPL);
		Random.pushGenerator(0x4852535441545350L);
		try {
			testElementMapping();
			testNullSafety();
			testCritMultiplier();
			testPerkHooks();
			testPureDamage();
			testOptimisticResist();
			testRegenAndMagicResist();

			System.out.println("SpsHeroStatsTest PASS (" + checks + " checks)");
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		System.out.flush();
		System.exit(0);
	}

	/** 七系元素与两套来源令牌的映射 */
	private static void testElementMapping() {
		check(Element.values().length == 7, "元素共七系");
		check(Element.of(DamageType.FIRE_DAMAGE) == Element.FIRE, "DamageType 火映射到 FIRE");
		check(Element.of(DamageType.LIGHT_DAMAGE) == Element.LIGHT, "DamageType 光映射到 LIGHT");
		check(Element.of(SpsMagicDamage.ICE) == Element.ICE, "SpsMagicDamage 冰映射到 ICE");
		check(Element.of(SpsMagicDamage.SHOCK) == Element.SHOCK, "SpsMagicDamage 雷映射到 SHOCK");
		check(Element.FIRE.damageType() == DamageType.FIRE_DAMAGE, "FIRE 映射回来源令牌");
		check(Element.of(new Object()) == null, "非元素来源解析为 null");
		check(Element.of((Object) null) == null, "null 来源解析为 null");
		check(Element.of((SpsMagicDamage) null) == null, "null 令牌解析为 null");
	}

	/** 全部查询对 null 安全（供无头校验与运行期临时对象调用） */
	private static void testNullSafety() {
		checkF(HeroStats.critChance(null), 0f, "null 英雄暴击率为 0");
		checkF(HeroStats.critMultiplier(null), Critical.BASE_MULTIPLIER, "null 英雄暴击倍率为基数");
		checkF(HeroStats.physicalDamageBonus(null), 0f, "null 英雄物理加成为 0");
		checkF(HeroStats.elementBonus(null, Element.FIRE), 0f, "null 英雄元素加成为 0");
		checkF(HeroStats.pureDamageBonus(null), 0f, "null 英雄纯粹加成为 0");
		checkF(HeroStats.elementResistance(null, Element.ICE), 0f, "null 英雄元素抗性为 0");
		checkF(HeroStats.magicResistance(null), 0f, "null 英雄魔法抗性为 0");
		checkF(HeroStats.regenBonus(null), 0f, "null 英雄回血加成为 0");
		check(HeroStats.applyPhysical(null, 10) == 10, "null 英雄物理加成不改变伤害");
		check(HeroStats.applyElement(null, 10, Element.FIRE) == 10, "null 英雄元素加成不改变伤害");
		Hero hero = new Hero();
		checkF(HeroStats.elementBonus(hero, null), 0f, "null 元素加成为 0");
		check(HeroStats.applyElement(hero, 10, null) == 10, "null 元素不改变伤害");
	}

	/** 暴击倍率 = 1.5 基数 + 重击 + 特质/装备加成 */
	private static void testCritMultiplier() {
		Hero hero = new Hero();
		checkF(HeroStats.critMultiplier(hero), Critical.BASE_MULTIPLIER, "无特质时暴击倍率为基数");
		check(Critical.scale(hero, 100) == 150, "无特质暴击 100 → 150");

		hero.heroPerk.add(new HardCrit());
		float withHard = HeroStats.critMultiplier(hero);
		check(withHard > Critical.BASE_MULTIPLIER, "重击提高暴击倍率");
		checkF(withHard, Critical.BASE_MULTIPLIER + Critical.hardCritBonus(hero), "倍率与重击加成一致");
		check(Critical.scale(hero, 100) == Math.round(100 * withHard), "scale 与统一倍率一致");

		hero.heroPerk.add(new HookPerk());
		checkF(HeroStats.critMultiplier(hero), withHard + 0.5f, "特质钩子追加暴击倍率");
	}

	/** Perk 属性增幅钩子累加与夹取 */
	private static void testPerkHooks() {
		Hero hero = new Hero();
		hero.heroPerk.add(new HookPerk());

		checkF(HeroStats.physicalDamageBonus(hero), 0.25f, "物理伤害加成钩子");
		checkF(HeroStats.elementBonus(hero, Element.FIRE), 0.4f, "火系伤害加成钩子");
		checkF(HeroStats.elementBonus(hero, Element.ICE), 0f, "未贡献的元素系为 0");
		checkF(HeroStats.pureDamageBonus(hero), 0.1f, "纯粹伤害加成钩子");
		checkF(HeroStats.elementResistance(hero, Element.ICE), 0.3f, "冰系抗性钩子");
		checkF(HeroStats.elementResistance(hero, Element.DARK), 0f, "未贡献的抗性为 0");

		check(HeroStats.applyPhysical(hero, 100) == 125, "物理加成 +25% → 100 变 125");
		check(HeroStats.applyElement(hero, 100, Element.FIRE) == 140, "火加成 +40% → 100 变 140");
		check(HeroStats.applyElement(hero, 100, Element.ICE) == 100, "无加成元素伤害不变");
		check(HeroStats.applyPure(hero, 100) == 110, "纯粹加成 +10% → 100 变 110");
		checkF(HeroStats.elementTakenMultiplier(hero, Element.ICE), 0.7f, "冰抗 30% → 受击系数 0.7");

		//抗性上限 0.9
		Hero greedy = new Hero();
		greedy.heroPerk.add(new GreedyPerk());
		checkF(HeroStats.elementResistance(greedy, Element.FIRE), 0.9f, "元素抗性夹取到 0.9");
		checkF(HeroStats.magicResistance(greedy), 0.9f, "魔法抗性夹取到 0.9");
	}

	/** 纯粹伤害：无视一切防御直接扣血，致死走 die(src) */
	private static void testPureDamage() {
		TstChar target = new TstChar();
		target.HP = target.HT = 30;

		int dealt = PureDamage.apply(target, 10, new Object());
		check(dealt == 10, "纯粹伤害足额结算");
		check(target.HP == 20, "直接扣血（不吃任何防御）");
		check(!target.died, "未致死不触发 die");

		//致死流程
		PureDamage.deal(target, 25, new Object());
		check(target.HP == 0, "致死扣到 0 不为负");
		check(!target.isAlive(), "致死目标死亡");
		check(target.died, "致死走 die(src)");

		//非法伤害不生效
		TstChar fresh = new TstChar();
		fresh.HP = fresh.HT = 30;
		check(PureDamage.apply(fresh, 0, new Object()) == 0, "0 伤害不结算");
		check(PureDamage.apply(fresh, -5, new Object()) == 0, "负伤害不结算");
		check(fresh.HP == 30, "非法伤害不扣血");
		check(PureDamage.apply(null, 10, new Object()) == 0, "null 目标安全");
	}

	/** 乐观特质按比例抵挡纯粹伤害 */
	private static void testOptimisticResist() {
		Hero hero = new Hero();
		int hp = hero.HP;
		PureDamage.apply(hero, 10, new Object());
		check(hero.HP == hp - 10, "无乐观时纯粹伤害足额");

		Hero optimistic = new Hero();
		optimistic.heroPerk.add(new Optimistic());
		int hp2 = optimistic.HP;
		int expected = Math.round(20 * (1f - 0.25f));
		PureDamage.apply(optimistic, 20, new Object());
		check(optimistic.HP == hp2 - expected, "乐观 1 级抵挡 25% 纯粹伤害");
	}

	/** 回血加成与魔法抗性收口 */
	private static void testRegenAndMagicResist() {
		Hero hero = new Hero();
		checkF(HeroStats.regenBonus(hero), 0f, "默认回血加成为 0");
		hero.regenerationBonus = 0.5f;
		checkF(HeroStats.regenBonus(hero), 0.5f, "回血加成读取字段");
		hero.regenerationBonus = -1f;
		checkF(HeroStats.regenBonus(hero), 0f, "负回血加成夹取为 0");

		Hero plain = new Hero();
		checkF(plain.magicalResistance(), 0f, "无特质魔法抗性为 0");
		plain.heroPerk.add(new ExtraMagicalResistance());
		checkF(plain.magicalResistance(), HeroStats.magicResistance(plain), "Hero.magicalResistance 收口到 HeroStats");
		check(plain.magicalResistance() > 0f, "魔法抗性特质生效");
	}

	/** 测试用特质：贡献各项属性钩子 */
	private static class HookPerk extends Perk {
		HookPerk() { super(1); }
		@Override public float physicalDamageBonus() { return 0.25f; }
		@Override public float elementBonus(Element element) {
			return element == Element.FIRE ? 0.4f : 0f;
		}
		@Override public float pureDamageBonus() { return 0.1f; }
		@Override public float critMultiplierBonus() { return 0.5f; }
		@Override public float elementResistance(Element element) {
			return element == Element.ICE ? 0.3f : 0f;
		}
		@Override public void storeInBundle(Bundle bundle) { }
		@Override public void restoreFromBundle(Bundle bundle) { }
	}

	/** 测试用特质：抗性超额，验证 0.9 夹取 */
	private static class GreedyPerk extends Perk {
		GreedyPerk() { super(1); }
		@Override public float elementResistance(Element element) { return 1.5f; }
		@Override public float magicResistance() { return 2f; }
		@Override public void storeInBundle(Bundle bundle) { }
		@Override public void restoreFromBundle(Bundle bundle) { }
	}

	/** 测试用生物：die 只记录、不触发世界状态逻辑 */
	private static class TstChar extends Char {
		boolean died = false;
		@Override public boolean act() { return true; }
		@Override public void die(Object cause) { died = true; }
	}
}
