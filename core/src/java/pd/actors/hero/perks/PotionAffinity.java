/*
 * 药剂亲和 —— 合并破碎的 LIQUID_WILLPOWER / LIQUID_NATURE /
 * LIQUID_AGILITY / DURABLE_TIPS（饮用或投掷药剂时的各种增益）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class PotionAffinity extends Perk {

	static {
		InlineText.of(PotionAffinity.class)
				.t("title", "药剂亲和")
				.t("desc", "饮用或投掷药剂时获得护盾与高闪避，周围会长出高草缠绕敌人；涂药飞镖更耐久。\n当前：护盾 %1$s%% 最大生命、闪避 %2$s 倍、高草 %3$s 株、飞镖耐久 %4$s 倍。");
	}

	public PotionAffinity() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.POTION_AFFINITY;
	}

	/** 饮用/投掷药剂获得的最大生命护盾比例 */
	public float shieldRatio() {
		return 0.065f * level();
	}

	/** 饮用期间闪避倍率 */
	public float evasionMultiplier() {
		return level() >= 2 ? 4f : 2f;
	}

	/** 周围长出的高草数量 */
	public int grassGrown() {
		return 2 + 2 * level();
	}

	/** 涂药飞镖耐久倍率 */
	public float tipDurability() {
		return 1f + level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc",
				num(Math.round(shieldRatio() * 100)), num(Math.round(evasionMultiplier())),
				num(grassGrown()), num(Math.round(tipDurability())));
	}
}
