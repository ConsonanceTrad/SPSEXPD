/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique;

import pd.actors.hero.HeroClass;

/**
 * SPSEXPD: 特殊物品「族」——同一职业那两件开局专属物品的整条演化线。
 *
 * <p>一个族对应一个职业；族内物品由 {@link UniqueItemId} 的 (family, tier, branch) 三元组定位：
 * tier 0 = 开局初始，tier 1 = 天狗面具的一档强化，tier 2 = 矮人国王皇冠的二次强化；
 * branch 1/2 = 一档强化时玩家二选一的两条路线（皇冠沿选定路线继续，不再选择）。</p>
 */
public enum UniqueFamily {

	WARRIOR( HeroClass.WARRIOR ),
	MAGE( HeroClass.MAGE ),
	ROGUE( HeroClass.ROGUE ),
	HUNTRESS( HeroClass.HUNTRESS ),
	DUELIST( HeroClass.DUELIST ),
	PERFORMER( HeroClass.PERFORMER ),
	SOLDIER( HeroClass.SOLDIER ),
	FOLLOWER( HeroClass.FOLLOWER ),
	ASCETIC( HeroClass.ASCETIC ),
	;

	public final HeroClass heroClass;

	UniqueFamily( HeroClass heroClass ) {
		this.heroClass = heroClass;
	}

	/** SPSEXPD: 按职业找族；没有对应族时返回 null。 */
	public static UniqueFamily of( HeroClass cls ) {
		if (cls == null) return null;
		for (UniqueFamily f : values()) {
			if (f.heroClass == cls) return f;
		}
		return null;
	}
}
