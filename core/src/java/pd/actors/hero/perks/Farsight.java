/*
 * 鹰眼远视 —— 来自破碎 FARSIGHT。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Farsight extends Perk {

	static {
		InlineText.of(Farsight.class)
				.t("title", "鹰眼远视")
				.t("desc", "视野范围扩大 %s%%。");
	}

	public Farsight() {
		super(3);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.FARSIGHT;
	}

	public float viewBonus() {
		return 0.25f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(viewBonus() * 100)));
	}
}
