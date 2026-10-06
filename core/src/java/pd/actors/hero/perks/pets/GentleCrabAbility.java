/* 宠物能力特质：温柔蟹之钳 —— 命中时削减目标护甲。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class GentleCrabAbility extends PetAbilityPerk {

	static {
		InlineText.of(GentleCrabAbility.class)
			.t("title", "温柔蟹之钳")
			.t("desc", "命中时削减目标护甲。");
	}

	public GentleCrabAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.ArmorBreak.class, 5f);
	}
}