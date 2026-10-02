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

package pd.items.consum.potions.exotic;

import pd.items.Item;
import pd.items.Recipe;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionOfExperience;
import pd.items.consum.potions.PotionOfFrost;
import pd.items.consum.potions.PotionOfHaste;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfInvisibility;
import pd.items.consum.potions.PotionOfLevitation;
import pd.items.consum.potions.PotionOfLiquidFlame;
import pd.items.consum.potions.PotionOfMindVision;
import pd.items.consum.potions.PotionOfParalyticGas;
import pd.items.consum.potions.PotionOfPurity;
import pd.items.consum.potions.PotionOfStrength;
import pd.items.consum.potions.PotionOfToxicGas;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public class ExoticPotion extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ExoticPotion.class)
			.t("turquoise", "青绿合剂")
			.t("crimson", "猩红合剂")
			.t("azure", "湛蓝合剂")
			.t("jade", "碧绿合剂")
			.t("golden", "金黄合剂")
			.t("magenta", "品红合剂")
			.t("charcoal", "煤黑合剂")
			.t("ivory", "乳白合剂")
			.t("amber", "琥珀合剂")
			.t("bistre", "深褐合剂")
			.t("indigo", "靛紫合剂")
			.t("silver", "银灰合剂")
			.t("unknown_desc", "这口圆底瓶里装着些有沉淀物的彩色液体。它似乎并不属于这个世界，谁知道饮用或投掷它时会有什么效果呢？")
			.t("warning", "你真的想终止这瓶药剂的使用？它仍会被消耗掉。")
			.t("yes", "是的，我确定")
			.t("no", "不，我改变主意了")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}



	
	{
		//sprite = equivalent potion sprite but one row down
	}
	
	public static final LinkedHashMap<Class<?extends Potion>, Class<?extends ExoticPotion>> regToExo = new LinkedHashMap<>();
	public static final LinkedHashMap<Class<?extends ExoticPotion>, Class<?extends Potion>> exoToReg = new LinkedHashMap<>();
	static{
		regToExo.put(PotionOfStrength.class, PotionOfMastery.class);
		exoToReg.put(PotionOfMastery.class, PotionOfStrength.class);

		regToExo.put(PotionOfHealing.class, PotionOfShielding.class);
		exoToReg.put(PotionOfShielding.class, PotionOfHealing.class);

		regToExo.put(PotionOfMindVision.class, PotionOfMagicalSight.class);
		exoToReg.put(PotionOfMagicalSight.class, PotionOfMindVision.class);

		regToExo.put(PotionOfFrost.class, PotionOfSnapFreeze.class);
		exoToReg.put(PotionOfSnapFreeze.class, PotionOfFrost.class);

		regToExo.put(PotionOfLiquidFlame.class, PotionOfDragonsBreath.class);
		exoToReg.put(PotionOfDragonsBreath.class, PotionOfLiquidFlame.class);

		regToExo.put(PotionOfToxicGas.class, PotionOfCorrosiveGas.class);
		exoToReg.put(PotionOfCorrosiveGas.class, PotionOfToxicGas.class);

		regToExo.put(PotionOfHaste.class, PotionOfStamina.class);
		exoToReg.put(PotionOfStamina.class, PotionOfHaste.class);

		regToExo.put(PotionOfInvisibility.class, PotionOfShroudingFog.class);
		exoToReg.put(PotionOfShroudingFog.class, PotionOfInvisibility.class);
		
		regToExo.put(PotionOfLevitation.class, PotionOfStormClouds.class);
		exoToReg.put(PotionOfStormClouds.class, PotionOfLevitation.class);

		regToExo.put(PotionOfParalyticGas.class, PotionOfEarthenArmor.class);
		exoToReg.put(PotionOfEarthenArmor.class, PotionOfParalyticGas.class);

		regToExo.put(PotionOfPurity.class, PotionOfCleansing.class);
		exoToReg.put(PotionOfCleansing.class, PotionOfPurity.class);

		regToExo.put(PotionOfExperience.class, PotionOfDivineInspiration.class);
		exoToReg.put(PotionOfDivineInspiration.class, PotionOfExperience.class);

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
			color = handler.label(exoToReg.get(this.getClass()));
		}
	}
	
	@Override
	//20 gold more than its none-exotic equivalent
	public int value() {
		return (Reflection.newInstance(exoToReg.get(getClass())).value() + 20) * quantity;
	}

	@Override
	//4 more energy than its none-exotic equivalent
	public int energyVal() {
		return (Reflection.newInstance(exoToReg.get(getClass())).energyVal() + 4) * quantity;
	}

	public static class PotionToExotic extends Recipe{

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() == 1 && regToExo.containsKey(ingredients.get(0).getClass())){
				return true;
			}

			return false;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 4;
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
