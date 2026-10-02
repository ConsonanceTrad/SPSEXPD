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

package pd.levels.rooms.secret;

import pd.actors.blobs.Alchemy;
import pd.actors.blobs.Blob;
import pd.items.EnergyCrystal;
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
import pd.items.consum.potions.PotionOfToxicGas;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.equipment.trinkets.ExoticCrystals;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import render.utils.geom.Point;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.HashMap;

public class SecretLaboratoryRoom extends SecretRoom {
	
	private static HashMap<Class<? extends Potion>, Float> potionChances = new HashMap<>();
	static{
		potionChances.put(PotionOfHealing.class,        1f);
		potionChances.put(PotionOfMindVision.class,     2f);
		potionChances.put(PotionOfFrost.class,          3f);
		potionChances.put(PotionOfLiquidFlame.class,    3f);
		potionChances.put(PotionOfToxicGas.class,       3f);
		potionChances.put(PotionOfHaste.class,          4f);
		potionChances.put(PotionOfInvisibility.class,   4f);
		potionChances.put(PotionOfLevitation.class,     4f);
		potionChances.put(PotionOfParalyticGas.class,   4f);
		potionChances.put(PotionOfPurity.class,         4f);
		potionChances.put(PotionOfExperience.class,     6f);
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );
		
		entrance().set( Door.Type.HIDDEN );
		
		Point pot = center();
		Painter.set( level, pot, Terrain.ALCHEMY );

		Blob.seed( pot.x + level.width() * pot.y, 1, Alchemy.class, level );

		int pos;
		do {
			pos = level.pointToCell(random());
		} while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
		level.drop( new EnergyCrystal().quantity(Random.IntRange(3, 5)), pos );

		do {
			pos = level.pointToCell(random());
		} while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
		level.drop( new EnergyCrystal().quantity(Random.IntRange(3, 5)), pos );

		int n = Random.IntRange( 2, 3 );
		HashMap<Class<? extends Potion>, Float> chances = new HashMap<>(potionChances);
		for (int i=0; i < n; i++) {
			do {
				pos = level.pointToCell(random());
			} while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
			
			Class<?extends Potion> potionCls = Random.chances(chances);
			chances.put(potionCls, 0f);

			if (ExoticPotion.regToExo.containsKey(potionCls)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					potionCls = ExoticPotion.regToExo.get(potionCls);
				}
			}

			level.drop( Reflection.newInstance(potionCls), pos );
		}
		
	}
	
}
