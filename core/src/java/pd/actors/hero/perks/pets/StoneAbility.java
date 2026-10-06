/* 宠物能力特质：石之意志 —— 命中时使目标麻痹。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class StoneAbility extends PetAbilityPerk {

	static {
		InlineText.of(StoneAbility.class)
			.t("title", "石之意志")
			.t("desc", "命中时使目标麻痹。");
	}

	public StoneAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Paralysis.class, 2f);
	}
}