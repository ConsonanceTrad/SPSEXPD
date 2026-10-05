/*
 * 坚韧 —— 每次成功闪避都会累积护盾。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EvasionTenacity extends Perk {

	static {
		InlineText.of(EvasionTenacity.class)
				.t("title", "坚韧")
				.t("desc", "每次成功闪避都会为你累积护盾（习得时提高 %d 点护盾上限）。");
	}

	public EvasionTenacity() {
		super(3);
		addTags(Tag.Evade, Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.EVASION_TENACITY;
	}

	public int shieldPerEvasion() {
		return level();
	}

	/** 习得时一次性提高的护盾上限 */
	public int learnBonusShield() {
		return 3;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", learnBonusShield());
	}
}
