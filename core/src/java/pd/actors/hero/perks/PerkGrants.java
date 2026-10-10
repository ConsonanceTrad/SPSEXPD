/*
 * 特质授予表 —— 「特定等级必然获得」与「满足条件即获得」的统一入口。
 */

package pd.actors.hero.perks;

import java.util.ArrayList;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import render.utils.serialize.Reflection;

public final class PerkGrants {

	private PerkGrants() {
	}

	/** 条件型特质：满足 conditionMet 即自动获得 */
	private static final Class<? extends Perk>[] CONDITIONAL = new Class[]{
			HardenedStomach.class
	};

	/** 特定等级必然获得的重要特质（不参与随机抽取） */
	public static void onLevelUp(Hero hero, int newLevel) {
		if (hero == null) return;

		//例：战士在 10 级获得「刻印转移」
		if (hero.heroClass == HeroClass.WARRIOR && newLevel == 10) {
			grant(hero, new RunicTransference());
		}

		checkConditions(hero);
	}

	/** 条件满足即获得 */
	public static void checkConditions(Hero hero) {
		if (hero == null || hero.heroPerk == null) return;
		for (Class<? extends Perk> c : CONDITIONAL) {
			if (hero.heroPerk.has(c)) continue;
			Perk p = Reflection.newInstance(c);
			if (p != null && p.conditionMet(hero)) grant(hero, p);
		}
	}

	public static void grant(Hero hero, Perk perk) {
		if (hero == null || perk == null) return;
		boolean owned = hero.heroPerk.has(perk.getClass());
		//已拥有 -> HeroPerk.add 内部提示「升级」；未拥有 -> 这里提示「获得」
		if (hero.heroPerk.add(perk) && !owned) {
			PerkGain.announce(hero, perk);
		}
	}

	/**
	 * 职业初始特质（静态版）—— 选角界面等没有 Hero 实例的场景用它。
	 */
	public static ArrayList<Perk> initialPerksFor(HeroClass cls) {
		ArrayList<Perk> result = new ArrayList<>();
		if (cls == null) return result;

		switch (cls) {
			case WARRIOR:
				result.add(new GoodAppetite());
				result.add(new RavenousAppetite());   // 裁决：负向特质保留
				break;
			case MAGE:
				result.add(new GoodAppetite());
				result.add(new WandPerception());
				result.add(new PreheatedZap());
				break;
			case ROGUE:
				//盗贼：搜索更远
				result.add(new EfficientSearch());
				result.add(new Keen());
				break;
			case HUNTRESS:
				//女猎手：复生步伐（以夜视/疾行体现）
				result.add(new NightVision());
				result.add(new BaredSwiftness());
				break;
			case DUELIST:
				result.add(new ExtraDexterous());
				break;
			case CLERIC:
				//修士：更加幸运
				result.add(new Perk.LuckFromAuthor());
				result.add(new FastRegeneration());
				break;
			default:
				break;
		}

		return result;
	}

	/**
	 * 职业后续专属特质 + 获取条件（供 UI 列表展示）。
	 * 条件只带 key 与参数，不带文案 —— 由 UI 层用 Messages.get(WndHeroInfo.class, …) 翻译。
	 */
	public static final class Exclusive {
		public final Perk perk;
		public final String conditionKey;
		public final Object[] conditionArgs;

		public Exclusive(Perk perk, String conditionKey, Object... conditionArgs) {
			this.perk = perk;
			this.conditionKey = conditionKey;
			this.conditionArgs = conditionArgs;
		}
	}

	/**
	 * 职业后续专属特质（静态表）—— 按职业/等级或职业条件授予的那些。
	 *
	 * 与实际授予点保持一致：目前只有 onLevelUp() 里的「战士 10 级 → 刻印转移」。
	 * 以后新增按职业的授予时，这里要同步登记一条。
	 */
	public static ArrayList<Exclusive> exclusivePerksFor(HeroClass cls) {
		ArrayList<Exclusive> result = new ArrayList<>();
		if (cls == null) return result;

		switch (cls) {
			case WARRIOR:
				//10 级必然获得（见 onLevelUp）
				result.add(new Exclusive(new RunicTransference(), "perks_cond_class_level", 10));
				break;
			default:
				break;
		}

		return result;
	}

	/**
	 * 职业初始特质 —— 把角色的特殊点以特质形式呈现（按英雄实例，供开局发放）。
	 */
	public static ArrayList<Perk> initialPerks(Hero hero) {
		if (hero == null) return new ArrayList<>();
		return initialPerksFor(hero.heroClass);
	}
}
