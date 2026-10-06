/* 宠物能力特质：链锯之连 —— 命中时额外造成一段伤害。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class LitDemonAbility extends PetAbilityPerk {

	static {
		InlineText.of(LitDemonAbility.class)
			.t("title", "链锯之连")
			.t("desc", "命中时额外造成一段伤害。");
	}

	public LitDemonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		enemy.damage(Math.max(1, damage / 2), hero);
	}
}