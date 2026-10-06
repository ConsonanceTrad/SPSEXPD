/* 宠物能力特质：迅猛龙之威 —— 命中时使目标恐惧。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class VelociroosterAbility extends PetAbilityPerk {

	static {
		InlineText.of(VelociroosterAbility.class)
			.t("title", "迅猛龙之威")
			.t("desc", "命中时使目标恐惧。");
	}

	public VelociroosterAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Terror.class, 4f);
	}
}