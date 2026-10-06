/* 宠物能力特质：火莲之焰 —— 命中时点燃目标。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class LeryFireAbility extends PetAbilityPerk {

	static {
		InlineText.of(LeryFireAbility.class)
			.t("title", "火莲之焰")
			.t("desc", "命中时点燃目标。");
	}

	public LeryFireAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Burning.class).reignite(enemy, 6f);
	}
}