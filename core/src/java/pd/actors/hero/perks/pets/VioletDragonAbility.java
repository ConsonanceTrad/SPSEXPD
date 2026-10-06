/* 宠物能力特质：紫龙之蚀 —— 命中时使目标软化（软泥）。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class VioletDragonAbility extends PetAbilityPerk {

	static {
		InlineText.of(VioletDragonAbility.class)
			.t("title", "紫龙之蚀")
			.t("desc", "命中时使目标软化（软泥）。");
	}

	public VioletDragonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Ooze.class).set(8f);
	}
}