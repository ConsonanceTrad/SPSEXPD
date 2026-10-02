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

package pd.items.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.StoneOre;
import pd.items.TriforceOfCourage;
import pd.items.TriforceOfPower;
import pd.items.TriforceOfWisdom;
import pd.items.bombs.Bomb;
import pd.items.wands.Wand;
import pd.items.weapon.guns.GunWeapon;
import pd.items.weapon.rockcode.RockCode;
import pd.items.weapon.spammo.SpAmmo;

public class MagicalHolster extends Bag {

	{
		image = EquipmentBagsDict.HOLSTER;
	}

	public static final float HOLSTER_SCALE_FACTOR = 0.85f;
	public static final float HOLSTER_DURABILITY_FACTOR = 1.2f;
	
	@Override
	public boolean canHold( Item item ) {
		//SPS: 合并法杖套收纳（三角神力/特种弹药/枪械/岩石密码，用户裁决 2026-09-28），容量并入 30 格
		//SPS: 投掷武器统一存放暗器袋（用户裁决 2026-09-28），魔法套筒不再收纳
		if (item instanceof Wand || item instanceof Bomb
				|| item instanceof TriforceOfCourage || item instanceof TriforceOfPower
				|| item instanceof TriforceOfWisdom || item instanceof SpAmmo
				|| item instanceof GunWeapon || item instanceof RockCode){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	public int capacity(){
		return 34;
	}
	
	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (owner != null) {
				for (Item item : items) {
					if (item instanceof Wand) {
						((Wand) item).charge(owner, HOLSTER_SCALE_FACTOR);
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDetach( ) {
		super.onDetach();
		for (Item item : items) {
			if (item instanceof Wand) {
				((Wand)item).stopCharging();
			}
		}
	}
	
	@Override
	public int value() {
		return 60;
	}

}
