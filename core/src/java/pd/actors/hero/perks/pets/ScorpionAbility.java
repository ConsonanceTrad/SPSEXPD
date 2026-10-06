/* 宠物能力特质：蝎子之毒 —— 命中时使目标中毒并为你自身恢复少量生命。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ScorpionAbility extends PetAbilityPerk {

	static {
		InlineText.of(ScorpionAbility.class)
			.t("title", "蝎子之毒")
			.t("desc", "命中时使目标中毒并为你自身恢复少量生命。");
	}

	public ScorpionAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Poison.class).set(5);
		hero.HP = Math.min(hero.HT, hero.HP + 2);
	}
}