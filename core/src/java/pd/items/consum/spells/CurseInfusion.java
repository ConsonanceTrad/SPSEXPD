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

import pd.Assets;
import pd.Badges;
import pd.effects.CellEmitter;
import pd.effects.particles.ShadowParticle;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.quest.MetalShard;
import pd.items.equipment.rings.RingOfMight;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.journal.Catalog;
import render.noosa.audio.Sample;

import java.util.ArrayList;

public class CurseInfusion extends InventorySpell {
	
	{
		image = ConsumScrollAmuletCrystalDict.CURSE_INFUSE_0;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ((item instanceof EquipableItem && item.isUpgradable()) || item instanceof Wand || item instanceof SpiritBow);
	}

	@Override
	protected void onItemSelected(Item item) {
		
		CellEmitter.get(curUser.pos).burst(ShadowParticle.UP, 5);
		Sample.INSTANCE.play(Assets.Sounds.CURSED);
		
		item.cursed = true;
		if (item instanceof Weapon) {
			Weapon w = (Weapon) item;
			if (w.enchantment != null) {
				//if we are freshly applying curse infusion, don't replace an existing curse
				if (w.hasGoodEnchant() || w.curseInfusionBonus) {
					w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
				}
			} else {
				w.enchant(Weapon.Enchantment.randomCurse());
			}
			w.curseInfusionBonus = true;
			if (w instanceof MagesStaff){
				((MagesStaff) w).updateWand(true);
			}
		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			if (a.glyph != null){
				//if we are freshly applying curse infusion, don't replace an existing curse
				if (a.hasGoodGlyph() || a.curseInfusionBonus) {
					a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
				}
			} else {
				a.inscribe(Armor.Glyph.randomCurse());
			}
			a.curseInfusionBonus = true;
		} else if (item instanceof Wand){
			((Wand) item).curseInfusionBonus = true;
			((Wand) item).updateLevel();
		} else if (item instanceof RingOfMight){
			curUser.updateHT(false);
		}
		Badges.validateItemLevelAquired(item);
		updateQuickslot();
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

		private static final int OUT_QUANTITY = 4;
		
		{
			inputs =  new Class[]{ScrollOfRemoveCurse.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 6;
			
			output = CurseInfusion.class;
			outQuantity = OUT_QUANTITY;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(MetalShard.class);
			return super.brew(ingredients);
		}
	}
}
