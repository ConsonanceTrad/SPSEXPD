/*
 * 自然馈赠 —— 来自破碎 NATURES_BOUNTY。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class NaturesBounty extends Perk {

	static {
		InlineText.of(NaturesBounty.class)
				.t("title", "自然馈赠")
				.t("desc", "在探索后续楼层时，可以从高草丛中找出 %d 颗隐藏的浆果。");
	}

	public NaturesBounty() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.NATURES_BOUNTY;
	}

	public int berriesPerFloor() {
		return 2 + 2 * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", berriesPerFloor());
	}
}
