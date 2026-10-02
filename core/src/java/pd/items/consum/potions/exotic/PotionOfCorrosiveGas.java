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

import pd.Assets;
import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class PotionOfCorrosiveGas extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfCorrosiveGas.class)
			.t("name", "腐蚀酸雾合剂")
			.t("desc", "打开或摔碎这个密封的药瓶将导致内容物爆发成一团强腐蚀性的锈色酸雾。这种酸雾的扩散速度与致命性都远超毒气。不过稳定性较差，不能在空气中存留太久。");
	}

	
	{
		icon = ItemIconSheet.POTION_CORROGAS;
	}
	
	@Override
	public void shatter( int cell ) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.GAS );
		}

		int centerVolume = 25;
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[cell+i]){
				GameScene.add( Blob.seed( cell+i, 25, CorrosiveGas.class ).setStrength( 2 + Dungeon.scalingDepth()/5));
			} else {
				centerVolume += 25;
			}
		}

		GameScene.add( Blob.seed( cell, centerVolume, CorrosiveGas.class ).setStrength( 2 + Dungeon.scalingDepth()/5));
	}
}
