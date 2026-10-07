/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.hero.Hero;
import pd.items.Waterskin;
import pd.messages.InlineText;

/**
 * SPSEXPD: 各种跳跃靴共用的规则与文案。
 *
 * <p>跳跃靴不再逐回合充能：每次跳跃固定消耗 100 露珠，之后进入固定 50 回合冷却，
 * 靴子的 charge 字段现在只用于显示冷却剩余回合。露珠不足时直接拒绝跳跃。</p>
 */
public final class JumpBoots {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JumpBoots.class)
			.t("blocked", "露珠不足或靴子仍在冷却，无法跳跃。")
			.t("cooldown", "冷却：%1$d / %2$d 回合。每次跳跃消耗 %3$d 露珠。");
	}

	/** 每次跳跃的固定露珠消耗。 */
	public static final int DEW_COST = 100;
	/** 跳跃之后的固定冷却回合数。 */
	public static final int COOLDOWN = 50;

	/** 英雄身上的露珠容器（露珠瓶是 Waterskin 的子类）。 */
	public static Waterskin flask(Hero hero) {
		return hero == null ? null : hero.belongings.getItem(Waterskin.class);
	}

	public static boolean hasDew(Hero hero) {
		Waterskin flask = flask(hero);
		return flask != null && flask.hasDew(DEW_COST);
	}

	public static boolean spendDew(Hero hero) {
		Waterskin flask = flask(hero);
		return flask != null && flask.spendDewStrict(DEW_COST);
	}

	private JumpBoots() { }
}
