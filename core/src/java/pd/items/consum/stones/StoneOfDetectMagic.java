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
import pd.messages.InlineText;

public class StoneOfDetectMagic extends InventoryStone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfDetectMagic.class)
			.t("name", "探魔符石")
			.t("inv_title", "探测一件物品")
			.t("detected_none", "你探测出这件物品并不附有任何魔咒。")
			.t("detected_both", "你探测出这件物品附有附魔/升级与诅咒！")
			.t("detected_good", "你探测出这件物品附有附魔/升级！")
			.t("detected_bad", "你探测出这件物品附有诅咒！")
			.t("desc", "这颗符石能够侦测物品上的魔咒。对一件未鉴定物品使用此符石，可探测其是否附有诅咒，以及是否具有附魔或升级。");
	}


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
