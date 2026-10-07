/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;
import pd.actors.hero.perks.PerkGrants;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/**
 * SPSEXPD: 许愿特质。
 *
 * 两条获得路径：
 * <ul>
 *   <li><b>精确名</b>：愿望文本与特质名分毫不差时直接授予（见 {@link #grantByName}）；</li>
 *   <li><b>随机</b>：许愿物品时按奖励等级与幸运的概率附赠一个可获得的特质（见 {@link #grantRandom}）。</li>
 * </ul>
 */
public final class WishTraitGrant {

	/** 精确名许愿的结果。 */
	public enum Outcome {
		/** 没有匹配到任何特质名 */
		NONE,
		/** 已经授予（或升级） */
		GRANTED,
		/** 匹配到了，但当前无法获得（已满级或条件不允许） */
		UNAVAILABLE
	}

	private WishTraitGrant() {
	}

	/**
	 * 愿望文本与特质名分毫不差时授予该特质。
	 *
	 * @return NONE（没对上名字）/ GRANTED / UNAVAILABLE（对上但不可获得）
	 */
	public static Outcome grantByName(Hero hero, String text) {
		if (hero == null || hero.heroPerk == null || text == null) return Outcome.NONE;
		String wish = text.trim();
		if (wish.isEmpty()) return Outcome.NONE;

		for (Class<? extends Perk> type : Perk.Companion.allClasses()) {
			Perk perk = newPerk(type);
			if (perk == null) continue;
			String title = titleOf(perk);
			if (title == null || !title.equals(wish)) continue;
			if (!perk.isAcquireAllowed(hero)) return Outcome.UNAVAILABLE;
			PerkGrants.grant(hero, perk);
			return Outcome.GRANTED;
		}
		return Outcome.NONE;
	}

	/** 随机附赠一个许愿特质；池空返回 false（概率判定在调用处）。 */
	public static boolean grantRandom(Hero hero, int tier) {
		if (hero == null || hero.heroPerk == null) return false;
		ArrayList<Perk> pool = new ArrayList<>();
		for (Class<? extends Perk> type : Perk.Companion.allClasses()) {
			Perk perk = newPerk(type);
			if (perk != null && perk.isAcquireAllowed(hero)) pool.add(perk);
		}
		if (pool.isEmpty()) return false;
		PerkGrants.grant(hero, Random.element(pool));
		return true;
	}

	private static Perk newPerk(Class<? extends Perk> type) {
		try {
			return Reflection.newInstance(type);
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static String titleOf(Perk perk) {
		try {
			String title = perk.title();
			return title == null || title.isEmpty() ? null : title;
		} catch (Throwable ignored) {
			return null;
		}
	}
}
