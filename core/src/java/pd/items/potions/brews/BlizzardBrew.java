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

package pd.items.potions.brews;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.blobs.Blizzard;
import pd.actors.blobs.Blob;
import pd.items.potions.PotionOfFrost;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;

public class BlizzardBrew extends Brew {
	
	{
		image = ConsumPotionSeedBasicPotionDict.BREW_BLIZZARD_0;
	}
	
	@Override
	public void shatter(int cell) {
		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.GAS );
		}

		int centerVolume = 120;
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[cell+i]){
				GameScene.add( Blob.seed( cell+i, 120, Blizzard.class ) );
			} else {
				centerVolume += 120;
			}
		}

		GameScene.add( Blob.seed( cell, centerVolume, Blizzard.class ) );
	}

	public static class Recipe extends pd.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfFrost.class};
			inQuantity = new int[]{1};
			
			cost = 8;
			
			output = BlizzardBrew.class;
			outQuantity = 1;
		}
		
	}
}
