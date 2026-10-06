/* 宠物能力特质：蓝女之缚 —— 命中时麻痹目标。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BlueGirlAbility extends PetAbilityPerk {

	static {
		InlineText.of(BlueGirlAbility.class)
			.t("title", "蓝女之缚")
			.t("desc", "命中时麻痹目标。");
	}

	public BlueGirlAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Paralysis.class, 3f);
	}
}