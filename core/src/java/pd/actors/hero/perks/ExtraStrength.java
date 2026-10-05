/*
 * 力大无穷 —— 直接提高力量（数值型特质）。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ExtraStrength extends Perk.Additional {

	static {
		InlineText.of(ExtraStrength.class)
				.t("title", "力大无穷")
				.t("desc", "永久提升 %d 点力量。");
	}

	public ExtraStrength() {
		super(3);
	}

	@Override
	public int image() {
		return PerkImageSheet.STRENGTH_EXTRA;
	}

	public int str() {
		return level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", str());
	}

	@Override
	public void onGain() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.STR += str();
	}

	@Override
	public void onLose() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.STR = Math.max(1, hero.STR - str());
	}
}
