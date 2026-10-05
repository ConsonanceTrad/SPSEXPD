/*
 * 充能秘术 —— 来自破碎 MYSTICAL_CHARGE。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class MysticalCharge extends Perk {

	static {
		InlineText.of(MysticalCharge.class)
				.t("title", "充能秘术")
				.t("desc", "用魔杖近战命中敌人时，获得相当于 %.1f 回合的神器充能。");
	}

	public MysticalCharge() {
		super(2);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.MYSTICAL_CHARGE;
	}

	public float artifactChargeOnHit() {
		return 0.5f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", artifactChargeOnHit());
	}
}
