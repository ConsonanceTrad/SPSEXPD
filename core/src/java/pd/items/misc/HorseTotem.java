/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

public class HorseTotem extends MiscEquippable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HorseTotem.class)
			.t("name", "赤兔图腾")
			.t("desc", "为狩猎年兽专门准备的图腾。携带时有20%%概率提高本次攻击伤害，并获得4回合急速；装备后触发概率提升至100%%。");
	}




	{ image = EquipmentNonEquipDict.RED_HARE_TOTEM; unique = true; }

	@Override protected MiscBuff createBuff() { return new HorseTotemBless(); }

	public boolean shouldTrigger(Hero hero) {
		return isEquipped(hero) || Random.Int(5) == 0;
	}

	public int empower(Hero hero, int damage) {
		if (damage <= 0) return damage;
		int limit = Math.max(1, damage / 3);
		int bonus = limit <= 1 ? 1 : Random.Int(1, limit);
		Buff.prolong(hero, HasteBuff.class, 4f);
		return damage + bonus;
	}

	public class HorseTotemBless extends MiscBuff { }
}
