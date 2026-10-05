/*
 * 长柄大师 —— 使用长柄武器时更精准，并有几率造成残废与击退。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class PolearmMaster extends Perk {

	static {
		InlineText.of(PolearmMaster.class)
				.t("title", "长柄大师")
				.t("desc", "使用长柄武器时获得额外的精准加成，并有几率使敌人残废与击退。");
	}

	public PolearmMaster() {
		super(2);
	}

	@Override
	public int image() {
		return PerkImageSheet.POLEARM;
	}

	//裁决：释放到公共池（去掉获得限制）
	public float accuracyBonus() {
		return 0.1f * level();
	}

	public float crippleChance() {
		return 0.1f + 0.1f * level();
	}

	public float knockbackChance() {
		return 0.05f * level();
	}
}
