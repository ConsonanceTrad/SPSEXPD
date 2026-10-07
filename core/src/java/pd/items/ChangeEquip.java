/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.equipment.armor.Armor;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.HeroSprite;
import pd.ui.AttackIndicator;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Virtual SPS inventory control which swaps both primary and secondary equipment. */
public class ChangeEquip extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChangeEquip.class)
			.t("name", "装备切换")
			.t("desc", "将主副武器与主副护甲对换。")
			.t("ac_change", "交换")
			.t("change", "装备切换");
	}




	public static final String AC_CHANGE = "CHANGE";

	{
		image = SpecificPlaceHolderDict.SPS_GOLD_TO_SCOIN;
		defaultAction = AC_CHANGE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_CHANGE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHANGE.equals(action)) {
			swap(hero);
			if (hero.sprite != null) {
				hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "change"));
			}
		} else {
			super.execute(hero, action);
		}
	}

	public static void swap(Hero hero) {
		//SPSEXPD: 只能装备在副武器栏的武器（神木圆盾）不参与主/副互换
		if (hero.belongings.secondWep == null || hero.belongings.secondWep.canEquipPrimary()) {
			KindOfWeapon weapon = hero.belongings.weapon;
			hero.belongings.weapon = hero.belongings.secondWep;
			hero.belongings.secondWep = weapon;
			if (hero.belongings.weapon != null) hero.belongings.weapon.activate(hero);
		}

		Armor armor = hero.belongings.armor;
		if (armor != null) armor.deactivate(hero);
		hero.belongings.armor = hero.belongings.secondArmor;
		hero.belongings.secondArmor = armor;
		if (hero.belongings.armor != null) hero.belongings.armor.activate(hero);

		if (hero.sprite instanceof HeroSprite) {
			((HeroSprite)hero.sprite).updateArmor();
			AttackIndicator.updateState();
		}
		Item.updateQuickslot();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}
}
