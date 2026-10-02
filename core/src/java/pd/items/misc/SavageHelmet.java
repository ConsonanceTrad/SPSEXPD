/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.DamageUp;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class SavageHelmet extends MiscEquippable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SavageHelmet.class)
			.t("name", "蛮族头盔")
			.t("desc", "为狩猎年兽专门准备的头盔。携带时有20%%概率减少受到的伤害，并将减伤值积蓄到下一次攻击中；装备后触发概率提升至100%%。");
	}




	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; }

	@Override protected MiscBuff createBuff() { return new SavageHelmetBless(); }

	public boolean shouldTrigger(Hero hero) {
		return isEquipped(hero) || Random.Int(5) == 0;
	}

	public int absorb(Hero hero, int damage) {
		if (damage <= 0) return damage;
		int limit = Math.max(1, damage / 2);
		int absorbed = limit <= 1 ? 1 : Random.Int(1, limit);
		Buff.affect(hero, DamageUp.class).level(absorbed);
		return Math.max(0, damage - absorbed);
	}

	public class SavageHelmetBless extends MiscBuff { }
}
