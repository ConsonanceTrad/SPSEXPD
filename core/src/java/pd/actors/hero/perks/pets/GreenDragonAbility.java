/* 宠物能力特质：绿龙之雷 —— 命中时麻痹目标。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class GreenDragonAbility extends PetAbilityPerk {

	static {
		InlineText.of(GreenDragonAbility.class)
			.t("title", "绿龙之雷")
			.t("desc", "命中时麻痹目标。");
	}

	public GreenDragonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Paralysis.class, 3f);
	}
}