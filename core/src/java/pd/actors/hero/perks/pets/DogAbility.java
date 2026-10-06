/* 宠物能力特质：忠犬之护 —— 命中时为你自己附加护盾护甲。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class DogAbility extends PetAbilityPerk {

	static {
		InlineText.of(DogAbility.class)
			.t("title", "忠犬之护")
			.t("desc", "命中时为你自己附加护盾护甲。");
	}

	public DogAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(hero, pd.actors.buffs.ShieldArmor.class).level(4);
	}
}