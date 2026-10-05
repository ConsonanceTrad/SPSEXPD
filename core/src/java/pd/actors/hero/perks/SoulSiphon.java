/*
 * 灵魂分食 —— 来自破碎 SOUL_SIPHON。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class SoulSiphon extends Perk {

	static {
		InlineText.of(SoulSiphon.class)
				.t("title", "灵魂分食")
				.t("desc", "其他单位造成的物理伤害会以 %d%% 的效率触发你的灵魂标记。");
	}

	public SoulSiphon() {
		super(3);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.SOUL_SIPHON;
	}

	public float allyEfficiency() {
		return 0.13f + 0.07f * (level() - 1);
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(allyEfficiency() * 100));
	}
}
