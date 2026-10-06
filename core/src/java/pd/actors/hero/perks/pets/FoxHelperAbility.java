/* 宠物能力特质：狐狸之典 —— 命中时为你自己附加加速。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class FoxHelperAbility extends PetAbilityPerk {

	static {
		InlineText.of(FoxHelperAbility.class)
			.t("title", "狐狸之典")
			.t("desc", "命中时为你自己附加加速。");
	}

	public FoxHelperAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(hero, pd.actors.buffs.HasteBuff.class, 4f);
	}
}