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

package pd.items.food;

import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.food.fruit.Fruit;
import pd.items.potions.Potion;
import pd.items.potions.PotionOfExperience;
import pd.items.potions.PotionOfFrost;
import pd.items.potions.PotionOfHaste;
import pd.items.potions.PotionOfHealing;
import pd.items.potions.PotionOfInvisibility;
import pd.items.potions.PotionOfLevitation;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.potions.PotionOfMindVision;
import pd.items.potions.PotionOfParalyticGas;
import pd.items.potions.PotionOfPurity;
import pd.items.potions.PotionOfStrength;
import pd.items.potions.PotionOfToxicGas;
import pd.messages.Messages;
import pd.plants.Plant.Seed;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndUseItem;
import watabou.utils.Bundle;
import watabou.utils.Reflection;

public class Blandfruit extends Fruit {

	public Potion potionAttrib = null;
	public ItemSprite.Glowing potionGlow = null;

	{
		stackable = true;
		image = ItemSpriteSheet.BLANDFRUIT;

		energy = 100f;
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
		energy = Hunger.STARVING;
		potionAttrib.anonymize();

		potionAttrib.image = ItemSpriteSheet.BLANDFRUIT;

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
			image = ItemSpriteSheet.BLAND_CHUNKS;

			energy = Hunger.STARVING;

			bones = true;
		}

	}

}
