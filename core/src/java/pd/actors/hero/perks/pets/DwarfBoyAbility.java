/* 宠物能力特质：矮人之铁 —— 命中时削减目标护甲。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class DwarfBoyAbility extends PetAbilityPerk {

	static {
		InlineText.of(DwarfBoyAbility.class)
			.t("title", "矮人之铁")
			.t("desc", "命中时削减目标护甲。");
	}

	public DwarfBoyAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.ArmorBreak.class, 6f);
	}
}