/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.hero.Hero;

/**
 * SPSEXPD: 许愿特质适配点。
 *
 * 「许愿的强大特质」依赖特质（Perk）系统。特质系统接入许愿前，
 * {@link #grant} 恒返回 false，不改变任何状态；接入后在此按等级发放对应特质。
 */
public final class WishTraitGrant {

	private WishTraitGrant() {
	}

	/**
	 * 尝试为英雄发放与奖励等级匹配的许愿特质。
	 *
	 * @return 是否真的发放了特质
	 */
	public static boolean grant(Hero hero, int tier) {
		//SPSEXPD: 特质系统尚未对接许愿，预留空实现
		return false;
	}
}
