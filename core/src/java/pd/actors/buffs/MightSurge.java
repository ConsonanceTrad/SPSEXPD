/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.messages.InlineText;
import pd.ui.BuffIndicator;

/**
 * SPSEXPD: 力量药剂的临时力量提升。
 *
 * 永久力量由「力量之瓶」（{@code pd.items.StrBottle}）提供；药剂改成稀有战术道具，
 * 只在一段时间内提高力量（临时穿得起更重的装备 / 多打一点伤害）。
 * 实际的加成数值在 {@link pd.actors.hero.Hero#STR()} 里应用。
 */
public class MightSurge extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MightSurge.class)
			.t("name", "力量涌动")
			.t("desc", "力量临时提高%d点。\n\n剩余效果时长：%s回合。");
	}

	/** SPSEXPD: 临时力量点数。 */
	public static final int STRENGTH_BONUS = 3;
	/** SPSEXPD: 持续回合数。 */
	public static final float DURATION = 60f;

	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FURY; }
	@Override public String desc() { return Messages.get(this, "desc", STRENGTH_BONUS, dispTurns()); }
}
