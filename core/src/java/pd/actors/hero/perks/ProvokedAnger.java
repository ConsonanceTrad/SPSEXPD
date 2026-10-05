/*
 * 受衅怒火 —— 来自破碎 PROVOKED_ANGER。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ProvokedAnger extends Perk {

	static {
		InlineText.of(ProvokedAnger.class)
				.t("title", "受衅怒火")
				.t("desc", "所获的护盾被击碎后，你的下一次物理攻击造成额外伤害。");
	}

	public ProvokedAnger() {
		super(2);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.ANGER_PROVOKED;
	}

	public int bonusDamage() {
		return 3 + 2 * (level() - 1);
	}
}
