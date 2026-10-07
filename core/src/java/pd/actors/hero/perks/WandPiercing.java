/*
 * 法术贯穿 —— 法术命中后降低目标的魔法抗性。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class WandPiercing extends Perk {

	static {
		InlineText.of(WandPiercing.class)
				.t("title", "法术贯穿")
				.t("desc", "法术命中后，使目标的魔法抗性降低 %s%%。");
	}

	public WandPiercing() {
		super(3);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.WAND_PIERCING;
	}

	public float reduction() {
		return 0.1f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(reduction() * 100)));
	}
}
