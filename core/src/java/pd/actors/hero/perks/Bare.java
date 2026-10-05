/*
 * 无甲系特质公共判定。
 *
 * 注意：本项目（SPD 3.x 基线）默认始终穿着护甲，armor() 通常不为 null，
 * 因此「无甲」被定义为「未装备任何护甲」；若护甲被卸下/丢弃即为无甲。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;

public final class Bare {

	private Bare() {
	}

	public static boolean isBare(Hero hero) {
		return hero != null && hero.belongings != null && hero.belongings.armor() == null;
	}
}
