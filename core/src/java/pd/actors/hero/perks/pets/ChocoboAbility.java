/* 宠物能力特质：陆行鸟之风 —— 命中时为你自己附加加速。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ChocoboAbility extends PetAbilityPerk {

	static {
		InlineText.of(ChocoboAbility.class)
			.t("title", "陆行鸟之风")
			.t("desc", "命中时为你自己附加加速。");
	}

	public ChocoboAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(hero, pd.actors.buffs.HasteBuff.class, 5f);
	}
}