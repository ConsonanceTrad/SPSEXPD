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
import pd.actors.buffs.Buff;
import pd.items.equipment.artifacts.TalismanOfForesight;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.data.BArray;

import java.util.ArrayList;
import pd.messages.InlineText;

public class DisplacingDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DisplacingDart.class)
			.t("name", "传送飞镖")
			.t("desc", "这些飞镖上涂着一种由消逝草制成的药物，能把目标传送走一小段距离。");
	}

	
	{
		image = ConsumThrowsDict.DISPLACING_DART_0;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//only display enemies when processing charge shot
		if (processingChargedShot && attacker.alignment == defender.alignment) {
			return super.proc(attacker, defender, damage);
		}

		//attempts to teleport the enemy to a position 8-10 cells away from the hero
		//prioritizes the closest visible cell to the defender, or closest non-visible if no visible are present
		//grants vision on the defender if teleport goes to non-visible
		if (!defender.properties().contains(Char.Property.IMMOVABLE)){
			
			ArrayList<Integer> visiblePositions = new ArrayList<>();
			ArrayList<Integer> nonVisiblePositions = new ArrayList<>();

			PathFinder.buildDistanceMap(attacker.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));

			for (int pos = 0; pos < Dungeon.level.length(); pos++){
				if (Dungeon.level.passable[pos]
						&& PathFinder.distance[pos] >= 8
						&& PathFinder.distance[pos] <= 10
						&& (!Char.hasProp(defender, Char.Property.LARGE) || Dungeon.level.openSpace[pos])
						&& Actor.findChar(pos) == null){

					if (Dungeon.level.heroFOV[pos]){
						visiblePositions.add(pos);
					} else {
						nonVisiblePositions.add(pos);
					}

				}
			}

			int chosenPos = -1;

			if (!visiblePositions.isEmpty()) {
				for (int pos : visiblePositions) {
					if (chosenPos == -1 || Dungeon.level.trueDistance(defender.pos, chosenPos)
							> Dungeon.level.trueDistance(defender.pos, pos)){
						chosenPos = pos;
					}
				}
			} else {
				for (int pos : nonVisiblePositions) {
					if (chosenPos == -1 || Dungeon.level.trueDistance(defender.pos, chosenPos)
							> Dungeon.level.trueDistance(defender.pos, pos)){
						chosenPos = pos;
					}
				}
			}
			
			if (chosenPos != -1){
				ScrollOfTeleportation.appear( defender, chosenPos );
				Dungeon.level.occupyCell(defender );
				if (defender == Dungeon.hero){
					Dungeon.observe();
					GameScene.updateFog();
				} else if (!Dungeon.level.heroFOV[chosenPos]){
					Buff.append(attacker, TalismanOfForesight.CharAwareness.class, 5f).charID = defender.id();
				}
			}
		
		}
		
		return super.proc(attacker, defender, damage);
	}
}
