/*
 * 狂热 —— 视野内敌人越多，攻击速度越快。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Maniac extends Perk {

	static {
		InlineText.of(Maniac.class)
				.t("title", "狂热")
				.t("desc", "视野范围内的敌人数量越多，你的攻击速度越快。");
	}

	public Maniac() {
		super(1);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.MANIAC;
	}

	/** 每个视野内敌人带来的攻击耗时缩减 */
	public float speedReductionPerEnemy() {
		return 0.05f;
	}

	public float minMultiplier() {
		return 0.5f;
	}
}
