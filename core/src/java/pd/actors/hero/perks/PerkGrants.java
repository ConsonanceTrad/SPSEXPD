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
		if (hero.heroPerk.has(perk.getClass())) {
			hero.heroPerk.add(perk); // 升级
			PerkGain.announce(hero, perk);
			return;
		}
		hero.heroPerk.add(perk);
		PerkGain.announce(hero, perk);
	}

	/**
	 * 职业初始特质 —— 把角色的特殊点以特质形式呈现。
	 * 目前按暗黑的原型映射到本项目职业，待 CSV 裁决后可继续调整。
	 */
	public static ArrayList<Perk> initialPerks(Hero hero) {
		ArrayList<Perk> result = new ArrayList<>();
		if (hero == null) return result;

		switch (hero.heroClass) {
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

		if (hero.subClass == HeroSubClass.NONE) {
			//子职业未定，保持基础初始特质
		}
		return result;
	}
}
