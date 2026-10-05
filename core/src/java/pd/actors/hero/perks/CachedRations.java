/*
 * 备用口粮 —— 来自破碎 CACHED_RATIONS。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class CachedRations extends Perk {

	static {
		InlineText.of(CachedRations.class)
				.t("title", "备用口粮")
				.t("desc", "在探索后续楼层时，可以从箱子中找出 %d 包备用口粮。");
	}

	public CachedRations() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.CACHED_RATIONS;
	}

	public int rationsPerFloor() {
		return 1 + level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", rationsPerFloor());
	}
}
