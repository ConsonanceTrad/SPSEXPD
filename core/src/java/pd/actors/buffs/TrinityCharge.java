package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import pd.messages.InlineText;

/**
 * SPSEXPD: 三相之力的「战舞蓄势」。
 *
 * <p>只用于显示：图标上的数字就是 {@link TrinityStance} 当前叠加的攻速层数。
 * 它自己跟随层数存活（层数归零即消散），真实状态仍存在 {@link TrinityStance} 里。</p>
 */
public class TrinityCharge extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TrinityCharge.class)
			.t("name", "热诚")
			.t("desc", "当前攻速加成：%1$d%%\n\n连续 2 回合未命中后，每回合衰减 20%%。");
	}

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		//SPSEXPD: 层数归零（或姿态消失）就一起消失；衰减期间继续显示
		TrinityStance stance = TrinityStance.of(target);
		if (stance == null || stance.layers() <= 0) {
			detach();
			return true;
		}
		spend(TICK);
		return true;
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
		return attackBonusPercent() + "%";
	}

	/** 当前攻速加成（每层 +20%），图标与描述共用。 */
	private int attackBonusPercent() {
		TrinityStance stance = TrinityStance.of(target);
		int layers = stance == null ? 0 : stance.layers();
		return Math.round(layers * TrinityStance.ATTACK_STEP * 100);
	}

	@Override
	public float iconFadePercent() {
		TrinityStance stance = TrinityStance.of(target);
		int layers = stance == null ? 0 : stance.layers();
		return Math.max(0, layers / (float) TrinityStance.MAX_LAYERS);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", attackBonusPercent());
	}
}
