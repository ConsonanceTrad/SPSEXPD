/* 宠物能力特质：柯多拉之赐 —— 命中时使目标陷入魔法易伤。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class KodoraAbility extends PetAbilityPerk {

	static {
		InlineText.of(KodoraAbility.class)
			.t("title", "柯多拉之赐")
			.t("desc", "命中时使目标陷入魔法易伤。");
	}

	public KodoraAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.MagicWeak.class, 5f);
	}
}