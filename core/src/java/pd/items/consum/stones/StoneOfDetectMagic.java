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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.consum.stones;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.actors.hero.Belongings;
import pd.actors.hero.Talent;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.utils.GLog;

public class StoneOfDetectMagic extends InventoryStone {

	{
		preferredBag = Belongings.Backpack.class;
		image = ConsumScrollAmuletAmuletDict.STONE_DETECT_0;
	}

	@Override
	public boolean usableOnItem(Item item){
		return (item instanceof EquipableItem || item instanceof Wand)
				&& (!item.isIdentified() || !item.cursedKnown);
	}

	@Override
	protected void onItemSelected(Item item) {

		item.cursedKnown = true;
		useAnimation();

		boolean negativeMagic = false;
		boolean positiveMagic = false;

		negativeMagic = item.cursed;
		if (!negativeMagic){
			if (item instanceof Weapon && ((Weapon) item).hasCurseEnchant()){
				negativeMagic = true;
			} else if (item instanceof Armor && ((Armor) item).hasCurseGlyph()){
				negativeMagic = true;
			}
		}

		positiveMagic = item.trueLevel() > 0;
		if (!positiveMagic){
			if (item instanceof Weapon && ((Weapon) item).hasGoodEnchant()){
				positiveMagic = true;
			} else if (item instanceof Armor && ((Armor) item).hasGoodGlyph()){
				positiveMagic = true;
			}
		}

		if (!positiveMagic && !negativeMagic){
			GLog.i(Messages.get(this, "detected_none"));
		} else if (positiveMagic && negativeMagic) {
			GLog.h(Messages.get(this, "detected_both"));
		} else if (positiveMagic){
			GLog.p(Messages.get(this, "detected_good"));
		} else if (negativeMagic){
			GLog.w(Messages.get(this, "detected_bad"));
		}

		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}

	}

}
