/*
 * 不动如山 —— 来自破碎 HOLD_FAST（类名与既有 buff HoldFast 区分）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Steadfast extends Perk {

	static {
		InlineText.of(Steadfast.class)
				.t("title", "不动如山")
				.t("desc", "装备纹章原地等待时获得护甲，并大幅减缓连击与护盾的衰减，直至你移动为止。");
	}

	public Steadfast() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.HOLD_FAST;
	}

	/** 等待时获得的护甲 */
	public int armorOnWait() {
		return 1 + level();
	}

	/** 连击/护盾衰减减缓比例 */
	public float decaySlow() {
		return 0.5f;
	}
}
