/* 宠物能力特质：可可猫之火 —— 命中时点燃目标。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class CocoCatAbility extends PetAbilityPerk {

	static {
		InlineText.of(CocoCatAbility.class)
			.t("title", "可可猫之火")
			.t("desc", "命中时点燃目标。");
	}

	public CocoCatAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Burning.class).reignite(enemy, 6f);
	}
}