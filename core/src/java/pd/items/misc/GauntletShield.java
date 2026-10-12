/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.hero.Hero;
import pd.messages.Messages;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 护手盾 —— 由神木圆盾改造而来：去掉了投掷功能，只保留装备在副手时的格挡。
 */
public class GauntletShield extends MissileShield {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GauntletShield.class)
			.t("name", "护手盾")
			.t("attack_block", "副手专用，装备时能格挡 _%1$d-%2$d 点伤害_。")
			.t("desc", "一面固定在护手上的小圆盾，只为近身格挡而生，无法投掷。");
	}

	@Override public String defaultAction() { return AC_EQUIP; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		//SPSEXPD: 护手盾无法投掷
		actions.remove(AC_CAST);
		return actions;
	}

	/** SPSEXPD: 没有充能概念，不显示角标。 */
	@Override public String status() { return null; }

	@Override public String statsInfo() {
		return Messages.get(this, "attack_block", drMin(null), DRMax());
	}
}
