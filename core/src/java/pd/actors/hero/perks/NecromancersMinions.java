/*
 * 怨灵爪牙 —— 来自破碎 NECROMANCERS_MINIONS。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class NecromancersMinions extends Perk {

	static {
		InlineText.of(NecromancersMinions.class)
				.t("title", "怨灵爪牙")
				.t("desc", "被灵魂标记的敌人死亡时，有 %s%% 的概率被唤起成为腐化的怨灵为你作战。");
	}

	public NecromancersMinions() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.NECROMANCERS_MINIONS;
	}

	public float raiseChance() {
		return 0.13f + 0.07f * (level() - 1);
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(raiseChance() * 100)));
	}
}
