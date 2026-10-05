/*
 * 无声潜行 —— 合并破碎的 SILENT_STEPS / INSCRIBED_STEALTH / SPEEDY_STEALTH。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class SilentStalker extends Perk {

	static {
		InlineText.of(SilentStalker.class)
				.t("title", "无声潜行")
				.t("desc", "不会惊醒远处的敌人；阅读卷轴或使用法术结晶后获得隐形；隐形期间持续积累动量。");
	}

	public SilentStalker() {
		super(2);
		addTags(Tag.Evade);
	}

	@Override
	public int image() {
		return PerkImageSheet.SILENT_STALKER;
	}

	/** 不会惊醒敌人的距离（0 = 只要不相邻都不惊醒） */
	public int wakeRange() {
		return level() >= 2 ? 0 : 3;
	}

	/** 阅读卷轴/使用法术结晶后的隐形回合 */
	public float invisibilityOnScroll() {
		return 3f * level();
	}

	/** 隐形时每回合获得的动量层数 */
	public float momentumPerTurn() {
		return 2f * level();
	}
}
