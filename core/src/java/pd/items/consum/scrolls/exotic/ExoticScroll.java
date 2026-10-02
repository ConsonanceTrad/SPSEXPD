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

package pd.items.consum.scrolls.exotic;

import pd.items.Item;
import pd.items.Recipe;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfLullaby;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfMirrorImage;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.scrolls.ScrollOfRetribution;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.consum.scrolls.ScrollOfTerror;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public abstract class ExoticScroll extends Scroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ExoticScroll.class)
			.t("kaunan", "KAUNAN秘卷")
			.t("sowilo", "SOWILO秘卷")
			.t("laguz", "LAGUZ秘卷")
			.t("yngvi", "YNGVI秘卷")
			.t("gyfu", "GYFU秘卷")
			.t("raido", "RAIDO秘卷")
			.t("isaz", "ISAZ秘卷")
			.t("mannaz", "MANNAZ秘卷")
			.t("naudiz", "NAUDIZ秘卷")
			.t("berkanan", "BERKANAN秘卷")
			.t("odal", "ODAL秘卷")
			.t("tiwaz", "TIWAZ秘卷")
			.t("unknown_desc", "这张漆黑的羊皮纸上刻满了闪耀着魔力光芒的晦涩符文。看起来这不是这个世界的物品。大声念出来会发生什么？")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}

	
	
	public static final LinkedHashMap<Class<?extends Scroll>, Class<?extends ExoticScroll>> regToExo = new LinkedHashMap<>();
	public static final LinkedHashMap<Class<?extends ExoticScroll>, Class<?extends Scroll>> exoToReg = new LinkedHashMap<>();
	static{
		regToExo.put(ScrollOfUpgrade.class, ScrollOfEnchantment.class);
		exoToReg.put(ScrollOfEnchantment.class, ScrollOfUpgrade.class);

		regToExo.put(ScrollOfIdentify.class, ScrollOfDivination.class);
		exoToReg.put(ScrollOfDivination.class, ScrollOfIdentify.class);
		
		regToExo.put(ScrollOfRemoveCurse.class, ScrollOfAntiMagic.class);
		exoToReg.put(ScrollOfAntiMagic.class, ScrollOfRemoveCurse.class);

		regToExo.put(ScrollOfMirrorImage.class, ScrollOfPrismaticImage.class);
		exoToReg.put(ScrollOfPrismaticImage.class, ScrollOfMirrorImage.class);

		regToExo.put(ScrollOfRecharging.class, ScrollOfMysticalEnergy.class);
		exoToReg.put(ScrollOfMysticalEnergy.class, ScrollOfRecharging.class);

		regToExo.put(ScrollOfTeleportation.class, ScrollOfPassage.class);
		exoToReg.put(ScrollOfPassage.class, ScrollOfTeleportation.class);

		regToExo.put(ScrollOfLullaby.class, ScrollOfSirensSong.class);
		exoToReg.put(ScrollOfSirensSong.class, ScrollOfLullaby.class);

		regToExo.put(ScrollOfMagicMapping.class, ScrollOfForesight.class);
		exoToReg.put(ScrollOfForesight.class, ScrollOfMagicMapping.class);

		regToExo.put(ScrollOfRage.class, ScrollOfChallenge.class);
		exoToReg.put(ScrollOfChallenge.class, ScrollOfRage.class);

		regToExo.put(ScrollOfRetribution.class, ScrollOfPsionicBlast.class);
		exoToReg.put(ScrollOfPsionicBlast.class, ScrollOfRetribution.class);
		
		regToExo.put(ScrollOfTerror.class, ScrollOfDread.class);
		exoToReg.put(ScrollOfDread.class, ScrollOfTerror.class);
		
		regToExo.put(ScrollOfTransmutation.class, ScrollOfMetamorphosis.class);
		exoToReg.put(ScrollOfMetamorphosis.class, ScrollOfTransmutation.class);
	}
	
	@Override
	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( exoToReg.get(this.getClass()) ));
	}
	
	@Override
	public void setKnown() {
		if (!isKnown()) {
			handler.know(exoToReg.get(this.getClass()));
			updateQuickslot();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		if (handler != null && handler.contains(exoToReg.get(this.getClass()))) {
			image = handler.image(exoToReg.get(this.getClass()));
			rune = handler.label(exoToReg.get(this.getClass()));
		}
	}
	
	@Override
	//20 gold more than its none-exotic equivalent
	public int value() {
		return (Reflection.newInstance(exoToReg.get(getClass())).value() + 30) * quantity;
	}

	@Override
	//6 more energy than its none-exotic equivalent
	public int energyVal() {
		return (Reflection.newInstance(exoToReg.get(getClass())).energyVal() + 6) * quantity;
	}
	
	public static class ScrollToExotic extends Recipe {
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() == 1 && regToExo.containsKey(ingredients.get(0).getClass())){
				return true;
			}

			return false;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 6;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			for (Item i : ingredients){
				i.quantity(i.quantity()-1);
			}

			return Reflection.newInstance(regToExo.get(ingredients.get(0).getClass()));
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return Reflection.newInstance(regToExo.get(ingredients.get(0).getClass()));
		}
	}
}
