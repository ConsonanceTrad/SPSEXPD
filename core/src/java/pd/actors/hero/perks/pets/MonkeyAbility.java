/* 宠物能力特质：猴子之巧 —— 命中时使目标迟缓。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class MonkeyAbility extends PetAbilityPerk {

	static {
		InlineText.of(MonkeyAbility.class)
			.t("title", "猴子之巧")
			.t("desc", "命中时使目标迟缓。");
	}

	public MonkeyAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Slow.class, 4f);
	}
}