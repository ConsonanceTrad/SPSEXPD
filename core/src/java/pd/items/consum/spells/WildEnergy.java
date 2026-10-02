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
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.items.Item;
import pd.items.quest.MetalShard;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.items.equipment.wands.CursedWand;
import pd.journal.Catalog;
import pd.mechanics.Ballistica;
import render.noosa.audio.Sample;
import render.utils.data.Callback;

import java.util.ArrayList;
import pd.messages.InlineText;

public class WildEnergy extends TargetedSpell {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WildEnergy.class)
			.t("name", "强能结晶")
			.t("desc", "这个结晶中含有部分驱动DM-300的诅咒之力。当施放时，它会为你的法杖与佩戴着的神器充能，但同时也会随机触发一种诅咒法杖效果。幸运的是你可以指定这种诅咒魔法的施放方向。");
	}

	
	{
		image = ConsumScrollAmuletCrystalDict.WILD_ENERGY_0;

		usesTargeting = true;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}
	
	//we rely on cursedWand to do fx instead
	@Override
	protected void fx(Ballistica bolt, Callback callback) {
		CursedWand.cursedZap(this, curUser, bolt, callback);
	}
	
	@Override
	protected void affectTarget(Ballistica bolt, final Hero hero) {
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
		ScrollOfRecharging.charge(hero);
		SpellSprite.show(hero, SpellSprite.CHARGE);

		hero.belongings.charge(1f);
		ArtifactRecharge.chargeArtifacts(hero, 4f);

		Buff.affect(hero, Recharging.class, 8f);
		Buff.affect(hero, ArtifactRecharge.class).extend( 8 ).ignoreHornOfPlenty = false;

		onSpellused();
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

		private static final int OUT_QUANTITY = 5;
		
		{
			inputs =  new Class[]{ScrollOfRecharging.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 4;
			
			output = WildEnergy.class;
			outQuantity = OUT_QUANTITY;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(MetalShard.class);
			return super.brew(ingredients);
		}
	}
}
