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

/** Virtual SPS inventory control which swaps both primary and secondary equipment. */
public class ChangeEquip extends Item {

	public static final String AC_CHANGE = "CHANGE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
		KindOfWeapon weapon = hero.belongings.weapon;
		hero.belongings.weapon = hero.belongings.secondWep;
		hero.belongings.secondWep = weapon;
		if (hero.belongings.weapon != null) hero.belongings.weapon.activate(hero);

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
