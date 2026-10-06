/* 宠物能力特质：蓝龙之霜 —— 命中时冰冻目标。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BlueDragonAbility extends PetAbilityPerk {

	static {
		InlineText.of(BlueDragonAbility.class)
			.t("title", "蓝龙之霜")
			.t("desc", "命中时冰冻目标。");
	}

	public BlueDragonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Frost.class, 6f);
	}
}