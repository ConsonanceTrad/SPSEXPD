/*
 * 快速再生 —— 提高生命回复速度（数值型特质）。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class FastRegeneration extends Perk.Additional {

	static {
		InlineText.of(FastRegeneration.class)
				.t("title", "快速再生")
				.t("desc", "生命回复速度提升 %s%%。");
	}

	public FastRegeneration() {
		super(5);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.FASTER_REG;
	}

	public float extraReg() {
		return level() * 0.25f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(extraReg() * 100)));
	}

	@Override
	public void onGain() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.regenerationBonus += extraReg();
	}

	@Override
	public void onLose() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.regenerationBonus = Math.max(0f, hero.regenerationBonus - extraReg());
	}
}
