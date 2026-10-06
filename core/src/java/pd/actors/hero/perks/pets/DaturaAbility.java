/* 宠物能力特质：曼陀罗之眠 —— 命中时使目标眩晕。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class DaturaAbility extends PetAbilityPerk {

	static {
		InlineText.of(DaturaAbility.class)
			.t("title", "曼陀罗之眠")
			.t("desc", "命中时使目标眩晕。");
	}

	public DaturaAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.Vertigo.class, 5f);
	}
}