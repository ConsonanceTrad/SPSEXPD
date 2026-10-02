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

import pd.actors.hero.Hero;
import pd.items.Item;
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
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.consum.stones.Runestone;
import pd.journal.Catalog;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import pd.messages.InlineText;

public class UnstableSpell extends Spell {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UnstableSpell.class)
			.t("name", "无序结晶")
			.t("desc", "这块黑色方形小水晶的每个面都浮动着变幻莫测的符文。\n\n激活时，它将随机触发一种卷轴效果，若视野内存在敌人，则必定触发战斗类卷轴效果，反之则必定触发非战斗类卷轴效果。");
	}


	{
		image = ConsumScrollAmuletCrystalDict.UNSTABLE_SPELL_0;
	}
	
	private static HashMap<Class<? extends Scroll>, Float> scrollChances = new HashMap<>();
	static{
		scrollChances.put( ScrollOfIdentify.class,      3f );
		scrollChances.put( ScrollOfRemoveCurse.class,   2f );
		scrollChances.put( ScrollOfMagicMapping.class,  2f );
		scrollChances.put( ScrollOfMirrorImage.class,   2f );
		scrollChances.put( ScrollOfRecharging.class,    2f );
		scrollChances.put( ScrollOfLullaby.class,       2f );
		scrollChances.put( ScrollOfRetribution.class,   2f );
		scrollChances.put( ScrollOfRage.class,          2f );
		scrollChances.put( ScrollOfTeleportation.class, 2f );
		scrollChances.put( ScrollOfTerror.class,        2f );
		scrollChances.put( ScrollOfTransmutation.class, 1f );
	}

	private static HashSet<Class<? extends Scroll>> nonCombatScrolls = new HashSet<>();
	static {
		nonCombatScrolls.add( ScrollOfIdentify.class );
		nonCombatScrolls.add( ScrollOfRemoveCurse.class );
		nonCombatScrolls.add( ScrollOfMagicMapping.class );
		nonCombatScrolls.add( ScrollOfRecharging.class );
		nonCombatScrolls.add( ScrollOfLullaby.class );
		nonCombatScrolls.add( ScrollOfTeleportation.class );
		nonCombatScrolls.add( ScrollOfTransmutation.class );
	}

	private static HashSet<Class<? extends Scroll>> combatScrolls = new HashSet<>();
	static {
		combatScrolls.add( ScrollOfMirrorImage.class );
		combatScrolls.add( ScrollOfRecharging.class );
		combatScrolls.add( ScrollOfLullaby.class );
		combatScrolls.add( ScrollOfRetribution.class );
		combatScrolls.add( ScrollOfRage.class );
		combatScrolls.add( ScrollOfTeleportation.class );
		combatScrolls.add( ScrollOfTerror.class );
	}
	
	@Override
	protected void onCast(Hero hero) {
		
		detach( curUser.belongings.backpack );
		updateQuickslot();
		
		Scroll s = Reflection.newInstance(Random.chances(scrollChances));

		//reroll the scroll until it is relevant for the situation (whether there are visible enemies)
		if (hero.visibleEnemies() == 0){
			while (!nonCombatScrolls.contains(s.getClass())){
				s = Reflection.newInstance(Random.chances(scrollChances));
			}
		} else {
			while (!combatScrolls.contains(s.getClass())){
				s = Reflection.newInstance(Random.chances(scrollChances));
			}
		}

		s.anonymize();
		s.talentChance = s.talentFactor = 1;
		curItem = s;
		s.doRead();

		Catalog.countUse(getClass());
		//don't trigger talents, as they'll be triggered by the scroll
	}

	//lower values, as it's cheaper to make
	@Override
	public int value() {
		return 40 * quantity;
	}

	@Override
	public int energyVal() {
		return 8 * quantity;
	}

	public static class Recipe extends pd.items.Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean scroll = false;
			boolean stone = false;

			for (Item i : ingredients){
				if (i instanceof Runestone){
					stone = true;
					//if it is a regular or exotic potion
				} else if (ExoticScroll.regToExo.containsKey(i.getClass())
						|| ExoticScroll.regToExo.containsValue(i.getClass())) {
					scroll = true;
				}
			}

			return scroll && stone;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 1;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {

			for (Item i : ingredients){
				i.quantity(i.quantity()-1);
			}

			return sampleOutput(null);
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new UnstableSpell();
		}
	}
}
