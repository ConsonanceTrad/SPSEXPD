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

package pd.items.consum.food;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.food.fruit.Fruit;
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
import pd.messages.Messages;
import pd.plants.Plant.Seed;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.windows.WndUseItem;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;
import pd.messages.InlineText;

public class Blandfruit extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Blandfruit.class)
			.t("name", "无味果")
			.t("cooked", "熟无味果")
			.t("sunfruit", "阳光果")
			.t("rotfruit", "腐朽果")
			.t("earthfruit", "地缚果")
			.t("blindfruit", "目盲果")
			.t("firefruit", "火焰果")
			.t("icefruit", "冰霜果")
			.t("fadefruit", "渐隐果")
			.t("sorrowfruit", "忧伤果")
			.t("stormfruit", "暴风果")
			.t("dreamfruit", "法皇果")
			.t("starfruit", "星陨果")
			.t("swiftfruit", "速行果")
			.t("raw", "你没法忍受生吃这玩意儿。")
			.t("desc", "干燥且几乎没有味道的果实。可以直接食用，饱腹感与标准干粮相当。")
			.t("desc_cooked", "这个果实已经因为吸收锅中的汤而鼓胀，并且吸收了其中种子的属性。它具有这粒种子对应的药剂效果。")
			.t("desc_eat", "看起来已经可以吃了！")
			.t("desc_throw", "它似乎性质很不稳定，最好作为武器丢出去。")
			.t("$chunks.name", "无味果块")
			.t("$chunks.desc", "无味果触地爆炸，碎成了一地的普通果块。\n\n尽管上面沾上了尘土，这些大块的熟制无味果应该可以安全食用。");
	}




	public Potion potionAttrib = null;
	public ItemSprite.Glowing potionGlow = null;

	{
		stackable = true;
		image = pd.atlas.items.ConsumPotionSeedSeedDict.BLANDFRUIT;

		//SPSEXPD: 无味果可直接食用，饱食度等同标准干粮
		energy = 300f; //SPSEXPD: 原 Hunger.HUNGRY
		hornValue = 2;

		bones = true;
	}

	@Override
	public boolean isSimilar( Item item ) {
		if ( super.isSimilar(item) ){
			Blandfruit other = (Blandfruit) item;
			if (potionAttrib == null && other.potionAttrib == null) {
					return true;
			} else if (potionAttrib != null && other.potionAttrib != null
					&& potionAttrib.isSimilar(other.potionAttrib)){
					return true;
			}
		}
		return false;
	}

	@Override
	public String defaultAction() {
		if (potionAttrib == null){
			return AC_EAT;
		} else if (potionAttrib.defaultAction().equals(Potion.AC_DRINK)) {
			return AC_EAT;
		} else {
			return potionAttrib.defaultAction();
		}
	}

	@Override
	public void execute( Hero hero, String action ) {

		if (action.equals( Potion.AC_CHOOSE )){

			GameScene.show(new WndUseItem(null, this) );
			return;

		}

		super.execute(hero, action);

		if (action.equals( AC_EAT ) && potionAttrib != null){

			potionAttrib.apply(hero);

		}
	}

	@Override
	public String name() {
		if (potionAttrib instanceof PotionOfHealing)        return Messages.get(this, "sunfruit");
		if (potionAttrib instanceof PotionOfStrength)       return Messages.get(this, "rotfruit");
		if (potionAttrib instanceof PotionOfParalyticGas)   return Messages.get(this, "earthfruit");
		if (potionAttrib instanceof PotionOfInvisibility)   return Messages.get(this, "blindfruit");
		if (potionAttrib instanceof PotionOfLiquidFlame)    return Messages.get(this, "firefruit");
		if (potionAttrib instanceof PotionOfFrost)          return Messages.get(this, "icefruit");
		if (potionAttrib instanceof PotionOfMindVision)     return Messages.get(this, "fadefruit");
		if (potionAttrib instanceof PotionOfToxicGas)       return Messages.get(this, "sorrowfruit");
		if (potionAttrib instanceof PotionOfLevitation)     return Messages.get(this, "stormfruit");
		if (potionAttrib instanceof PotionOfPurity)         return Messages.get(this, "dreamfruit");
		if (potionAttrib instanceof PotionOfExperience)     return Messages.get(this, "starfruit");
		if (potionAttrib instanceof PotionOfHaste)          return Messages.get(this, "swiftfruit");
		return super.name();
	}

	@Override
	public String desc() {
		if (potionAttrib== null) {
			return super.desc();
		} else {
			String desc = Messages.get(this, "desc_cooked") + "\n\n";
			if (potionAttrib instanceof PotionOfFrost
				|| potionAttrib instanceof PotionOfLiquidFlame
				|| potionAttrib instanceof PotionOfToxicGas
				|| potionAttrib instanceof PotionOfParalyticGas) {
				desc += Messages.get(this, "desc_throw");
			} else {
				desc += Messages.get(this, "desc_eat");
			}
			return desc;
		}
	}

	@Override
	public int value() {
		return 20 * quantity;
	}

	@Override
	protected float eatingTime() {
		return Food.TIME_TO_EAT;
	}

	public Item cook(Seed seed){
		return imbuePotion(Reflection.newInstance(Potion.SeedToPotion.types.get(seed.getClass())));
	}

	public Item imbuePotion(Potion potion){

		potionAttrib = potion;
		// Cooked Blandfruit only exists in saves made by earlier SPS-SPD builds.
		// Keep those items usable without allowing this variant back into normal generation.
		energy = 450f; //SPSEXPD: 原 Hunger.STARVING
		potionAttrib.anonymize();

		potionAttrib.image = pd.atlas.items.ConsumPotionSeedSeedDict.BLANDFRUIT;

		if (potionAttrib instanceof PotionOfHealing)        potionGlow = new ItemSprite.Glowing( 0x2EE62E );
		if (potionAttrib instanceof PotionOfStrength)       potionGlow = new ItemSprite.Glowing( 0xCC0022 );
		if (potionAttrib instanceof PotionOfParalyticGas)   potionGlow = new ItemSprite.Glowing( 0x67583D );
		if (potionAttrib instanceof PotionOfInvisibility)   potionGlow = new ItemSprite.Glowing( 0xD9D9D9 );
		if (potionAttrib instanceof PotionOfLiquidFlame)    potionGlow = new ItemSprite.Glowing( 0xFF7F00 );
		if (potionAttrib instanceof PotionOfFrost)          potionGlow = new ItemSprite.Glowing( 0x66B3FF );
		if (potionAttrib instanceof PotionOfMindVision)     potionGlow = new ItemSprite.Glowing( 0x919999 );
		if (potionAttrib instanceof PotionOfToxicGas)       potionGlow = new ItemSprite.Glowing( 0xA15CE5 );
		if (potionAttrib instanceof PotionOfLevitation)     potionGlow = new ItemSprite.Glowing( 0x1B5F79 );
		if (potionAttrib instanceof PotionOfPurity)         potionGlow = new ItemSprite.Glowing( 0xC152AA );
		if (potionAttrib instanceof PotionOfExperience)     potionGlow = new ItemSprite.Glowing( 0x404040 );
		if (potionAttrib instanceof PotionOfHaste)          potionGlow = new ItemSprite.Glowing( 0xCCBB00 );

		return this;
	}

	public static final String POTIONATTRIB = "potionattrib";
	
	@Override
	public void reset() {
		super.reset();
		if (potionAttrib != null) {
			imbuePotion(potionAttrib);
		}
	}
	
	@Override
	public void storeInBundle(Bundle bundle){
		super.storeInBundle(bundle);
		bundle.put( POTIONATTRIB , potionAttrib);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(POTIONATTRIB)) {
			imbuePotion((Potion) bundle.get(POTIONATTRIB));
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return potionGlow;
	}
	
	/** Kept only so loose chunks written by earlier SPS-SPD builds can still be loaded. */
	@Deprecated
	public static class Chunks extends Food {

		{
			stackable = true;
			image = ConsumFoodFoodDict.BLAND_CHUNKS;

			energy = 450f; //SPSEXPD: 原 Hunger.STARVING

			bones = true;
		}

	}

}
