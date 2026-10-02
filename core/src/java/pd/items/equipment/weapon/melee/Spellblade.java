/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.items.equipment.weapon.melee;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

/** A deliberately modest starter weapon for the Spellsword. */
public class Spellblade extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Spellblade.class)
			.t("name", "魔剑")
			.t("desc", "一柄刻有基础聚能符文的轻剑。它精准且容易使用，但牺牲了一部分直接伤害。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.COIN_SWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.15f;
		tier = 1;
		ACC = 1.1f;
	}

	@Override
	public int max(int lvl) {
		return 8 + 2 * lvl;
	}
}
