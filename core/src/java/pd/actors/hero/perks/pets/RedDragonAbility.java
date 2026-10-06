/* 宠物能力特质：红龙之炎 —— 命中时点燃目标。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class RedDragonAbility extends PetAbilityPerk {

	static {
		InlineText.of(RedDragonAbility.class)
			.t("title", "红龙之炎")
			.t("desc", "命中时点燃目标。");
	}

	public RedDragonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Burning.class).reignite(enemy, 6f);
	}
}