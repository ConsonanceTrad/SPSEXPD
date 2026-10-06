/* 宠物能力特质：蛇之毒牙 —— 命中时使目标中毒。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class SnakeAbility extends PetAbilityPerk {

	static {
		InlineText.of(SnakeAbility.class)
			.t("title", "蛇之毒牙")
			.t("desc", "命中时使目标中毒。");
	}

	public SnakeAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Poison.class).set(5);
	}
}