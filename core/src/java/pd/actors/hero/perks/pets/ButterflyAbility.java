/* 宠物能力特质：蝴蝶之息 —— 命中时为你自己恢复少量生命。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ButterflyAbility extends PetAbilityPerk {

	static {
		InlineText.of(ButterflyAbility.class)
			.t("title", "蝴蝶之息")
			.t("desc", "命中时为你自己恢复少量生命。");
	}

	public ButterflyAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		hero.HP = Math.min(hero.HT, hero.HP + 4);
	}
}