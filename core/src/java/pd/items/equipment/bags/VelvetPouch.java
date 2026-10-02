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

package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.StoneOre;
import pd.items.nornstone.NornStone;
import pd.items.quest.GooBlob;
import pd.items.quest.MetalShard;
import pd.items.consum.stones.Runestone;
import pd.plants.Plant;

public class VelvetPouch extends Bag {

	{
		image = EquipmentBagsDict.POUCH;
	}

	@Override
	public boolean canHold( Item item ) {
		//SPS: 合并种子包收纳（矿石/诺恩石，用户裁决 2026-09-28），容量并入种子包 30 格
		if (item instanceof Plant.Seed || item instanceof Runestone
				|| item instanceof GooBlob || item instanceof MetalShard
				|| item instanceof StoneOre || item instanceof NornStone){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	public int capacity(){
		return 34;
	}
	
	@Override
	public int value() {
		return 30;
	}

}
