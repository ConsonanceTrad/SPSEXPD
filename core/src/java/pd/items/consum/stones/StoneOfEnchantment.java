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
import pd.effects.Enchanting;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.consum.scrolls.exotic.ScrollOfEnchantment;
import pd.items.equipment.weapon.Weapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class StoneOfEnchantment extends InventoryStone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfEnchantment.class)
			.t("name", "附魔符石")
			.t("inv_title", "附魔一件物品")
			.t("weapon", "你的武器在暗中微微发光！")
			.t("armor", "你的护甲在暗中微微发光！")
			.t("desc", "这颗符石拥有施加附魔的能力。和升级卷轴不同，它不会直接加强一个道具的能力，但能给武器或者护甲施加附魔，使其拥有新的特性。");
	}



	
	{
		preferredBag = Belongings.Backpack.class;
		image = ConsumScrollAmuletAmuletDict.STONE_ENCHANT_0;

		unique = true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ScrollOfEnchantment.enchantable(item);
	}
	
	@Override
	protected void onItemSelected(Item item) {
		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}
		
		if (item instanceof Weapon) {
			
			((Weapon)item).enchant();
			
		} else {
			
			((Armor)item).inscribe();
			
		}
		
		curUser.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
		Enchanting.show( curUser, item );
		
		if (item instanceof Weapon) {
			GLog.p(Messages.get(this, "weapon"));
		} else {
			GLog.p(Messages.get(this, "armor"));
		}
		
		useAnimation();
		
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 5 * quantity;
	}

}
