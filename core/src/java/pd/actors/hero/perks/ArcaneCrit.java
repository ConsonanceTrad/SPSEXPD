/*
 * 奥术暴击 —— 法术也可以暴击，并随等级提高法术暴击率。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ArcaneCrit extends Perk {

	static {
		InlineText.of(ArcaneCrit.class)
				.t("title", "奥术暴击")
				.t("desc", "1 级：法杖施法伤害享受暴击几率，造成 1.75 倍伤害。\n2 级：法术暴击几率提高 9%%，此后每提升 1 级再提高 9%%。");
	}

	public ArcaneCrit() {
		super(5);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.ARCANE_CRIT;
	}

	/** 额外法术暴击率 */
	public float extraChance() {
		return level() > 1 ? (level() - 1) * 0.09f : 0f;
	}

	public float multiplier() {
		return 1.75f;
	}
}
