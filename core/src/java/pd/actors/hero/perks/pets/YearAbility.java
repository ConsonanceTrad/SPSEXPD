/* 宠物能力特质：年兽之岁 —— 命中时使目标陷入魔法易伤。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class YearAbility extends PetAbilityPerk {

	static {
		InlineText.of(YearAbility.class)
			.t("title", "年兽之岁")
			.t("desc", "命中时使目标陷入魔法易伤。");
	}

	public YearAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.MagicWeak.class, 4f);
	}
}