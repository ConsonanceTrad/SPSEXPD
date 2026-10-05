/*
 * 噬魂秘法 —— 来自破碎 SOUL_EATER。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class SoulEater extends Perk {

	static {
		InlineText.of(SoulEater.class)
				.t("title", "噬魂秘法")
				.t("desc", "被灵魂标记的单位受到的每点物理伤害都会提供饥饿值；标记敌人死亡时有几率触发进食。");
	}

	public SoulEater() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.SOUL_EATER;
	}

	/** 每点伤害转化的饥饿值 */
	public float hungerPerDamage() {
		return 0.33f;
	}

	/** 标记敌人死亡时触发进食的几率 */
	public float eatChance() {
		return 0.10f + 0.10f * (level() - 1);
	}
}
