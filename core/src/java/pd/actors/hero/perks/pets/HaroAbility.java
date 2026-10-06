/* 宠物能力特质：哈罗之裁 —— 命中时使目标眩晕。（由「献祭」对应魂石获得） */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class HaroAbility extends PetAbilityPerk {

	static {
		InlineText.of(HaroAbility.class)
			.t("title", "哈罗之裁")
			.t("desc", "命中时使目标眩晕。");
	}

	public HaroAbility() {
		super(1);
	}

	@Override
	public void onHit(Hero hero, Char enemy, int damage) {
		if (enemy == null || !enemy.isAlive()) return;
		pd.actors.buffs.Buff.affect(enemy, pd.actors.buffs.HolyStun.class, 3f);
	}
}