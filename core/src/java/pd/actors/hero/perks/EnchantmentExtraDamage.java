/*
 * 附魔强化 —— 附魔武器的攻击加成提高。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EnchantmentExtraDamage extends Perk {

	static {
		InlineText.of(EnchantmentExtraDamage.class)
				.t("title", "附魔强化")
				.t("desc", "使用附魔武器时获得 %s%% 的额外攻击加成。");
	}

	public EnchantmentExtraDamage() {
		super(3);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.ENCHANTMENT_EXTRA_DAMAGE;
	}

	public float ratio() {
		return level() * 0.1f + 0.05f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(ratio() * 100)));
	}
}
