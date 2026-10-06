/* 宠物能力特质：星孩之光 —— 命中时向目标射出圣光。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class StarKidAbility extends PetAbilityPerk {

	static {
		InlineText.of(StarKidAbility.class)
			.t("title", "星孩之光")
			.t("desc", "命中时向目标射出圣光。");
	}

	public StarKidAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.LightShootAttack.class).level(2);
	}
}