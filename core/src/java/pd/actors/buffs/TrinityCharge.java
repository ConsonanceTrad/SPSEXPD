package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import pd.messages.InlineText;

/**
 * SPSEXPD: 三相之力的「战舞蓄势」。
 *
 * <p>只用于显示：图标上的数字就是 {@link TrinityStance} 当前叠加的攻速层数，
 * 剩余时长与层数的清零节奏一致（连续 5 回合未命中即消散）。真实状态仍存在 {@link TrinityStance} 里。</p>
 */
public class TrinityCharge extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TrinityCharge.class)
			.t("name", "战舞蓄势")
			.t("desc", "七刃随战舞依次递进，你的攻势正在加快。\n\n当前攻速层数：%d\n连续 5 回合未命中则清零。\n\n剩余时长：%s回合");
	}

	public static final float DURATION = TrinityStance.IDLE_RESET;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.UPGRADE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.7f, 0.5f, 1f);
	}

	@Override
	public String iconTextDisplay() {
		TrinityStance stance = TrinityStance.of(target);
		return Integer.toString(stance == null ? 0 : stance.layers());
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public String desc() {
		TrinityStance stance = TrinityStance.of(target);
		return Messages.get(this, "desc", stance == null ? 0 : stance.layers(), dispTurns());
	}
}
