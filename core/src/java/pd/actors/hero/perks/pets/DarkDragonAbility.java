/* 宠物能力特质：暗龙之咒 —— 命中时使目标陷入妖术。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class DarkDragonAbility extends PetAbilityPerk {

	static {
		InlineText.of(DarkDragonAbility.class)
			.t("title", "暗龙之咒")
			.t("desc", "命中时使目标陷入妖术。");
	}

	public DarkDragonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Hex.class, 6f);
	}
}