/* 宠物能力特质：虫龙之力 —— 命中时使目标眩晕。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BugDragonAbility extends PetAbilityPerk {

	static {
		InlineText.of(BugDragonAbility.class)
			.t("title", "虫龙之力")
			.t("desc", "命中时使目标眩晕。");
	}

	public BugDragonAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.HolyStun.class, 3f);
	}
}