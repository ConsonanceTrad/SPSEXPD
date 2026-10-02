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

package pd.items.equipment.weapon.missiles.darts;

import pd.atlas.items.ConsumThrowsDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class IncendiaryDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IncendiaryDart.class)
			.t("name", "燃烧飞镖")
			.t("desc", "这些飞镖上涂着一种由烈焰花制成的药物，命中后会燃起烈焰。");
	}




	{
		image = ConsumThrowsDict.INCENDIARY_DART_0;
	}
	
	@Override
	protected void onThrow( int cell ) {
		Char enemy = Actor.findChar( cell );
		if ((enemy == null || enemy == curUser) && Dungeon.level.flamable[cell]) {
			GameScene.add(Blob.seed(cell, 4, Fire.class));
			decrementDurability();
			if (durability > 0 || spawnedForEffect){
				super.onThrow(cell);
			} else {
				Dungeon.level.drop(new Dart().quantity(1), cell).sprite.drop();
			}
		} else{
			super.onThrow(cell);
		}
	}
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		//when processing charged shot, only burn enemies
		if (!processingChargedShot || attacker.alignment != defender.alignment) {
			Buff.affect(defender, Burning.class).reignite(defender);
		}
		return super.proc( attacker, defender, damage );
	}
	
}
