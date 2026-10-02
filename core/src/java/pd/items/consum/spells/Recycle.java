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

package pd.items.consum.spells;

import pd.atlas.items.ConsumScrollAmuletCrystalDict;

import pd.Challenges;
import pd.Dungeon;
import pd.effects.Speck;
import pd.effects.Transmuting;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.brews.Brew;
import pd.items.consum.potions.elixirs.Elixir;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.consum.stones.Runestone;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.utils.GLog;
import render.utils.serialize.Reflection;
import pd.messages.InlineText;

public class Recycle extends InventorySpell {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Recycle.class)
			.t("name", "转换菱晶")
			.t("inv_title", "转换一件物品")
			.t("recycled", "你的物品被转换为%s。")
			.t("desc", "这个菱晶蕴含着弱化的嬗变魔力。虽然不能对装备使用，但它能将卷轴、药剂、种子、符石或涂药飞镖转换为一个随机的同类物品。");
	}

	
	{
		image = ConsumScrollAmuletCrystalDict.RECYCLE_0;

		talentFactor = 2;
		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew)) ||
				item instanceof Scroll ||
				item instanceof Plant.Seed ||
				item instanceof Runestone ||
				item instanceof TippedDart;
	}

	@Override
	protected void onItemSelected(Item item) {
		Item result;
		do {
			if (item instanceof Potion) {
				result = Generator.randomUsingDefaults(Generator.Category.POTION);
				if (item instanceof ExoticPotion){
					result = Reflection.newInstance(ExoticPotion.regToExo.get(result.getClass()));
				}
			} else if (item instanceof Scroll) {
				result = Generator.randomUsingDefaults(Generator.Category.SCROLL);
				if (item instanceof ExoticScroll){
					result = Reflection.newInstance(ExoticScroll.regToExo.get(result.getClass()));
				}
			} else if (item instanceof Plant.Seed) {
				result = Generator.randomUsingDefaults(Generator.Category.SEED);
			} else if (item instanceof Runestone) {
				result = Generator.randomUsingDefaults(Generator.Category.STONE);
			} else {
				result = TippedDart.randomTipped(1);
			}
		} while (result.getClass() == item.getClass() || Challenges.isItemBlocked(result));
		
		item.detach(curUser.belongings.backpack);
		GLog.p(Messages.get(this, "recycled", result.name()));
		if (!result.collect()){
			Dungeon.level.drop(result, curUser.pos).sprite.drop();
		}
		Transmuting.show(curUser, item, result);
		curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
	}
	
	@Override
	public int value() {
		return (int)(60 * (quantity/(float)Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)(12 * (quantity/(float)Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 12;
		
		{
			inputs =  new Class[]{ScrollOfTransmutation.class};
			inQuantity = new int[]{1};
			
			cost = 12;
			
			output = Recycle.class;
			outQuantity = OUT_QUANTITY;
		}
		
	}
}
