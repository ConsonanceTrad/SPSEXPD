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

package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.BlastParticle;
import pd.mechanics.ShadowCaster;
import render.utils.geom.Point;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ShrapnelBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShrapnelBomb.class)
			.t("name", "破片炸弹")
			.t("desc", "这枚改造过的炸弹的外壳由DM-300的金属残骸制成，当其爆炸时外壳会碎裂为破片并向四周迸发，在炸弹周围的巨大范围内造成_%1$d~%2$d点伤害_。使用时你最好躲在某处掩体后面...")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}

	
	{
		image = EquipmentEquipWeaponBombDict.SHRAPNEL_BOMB_0;
	}
	
	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	protected int explosionRange() {
		return 8;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		
		boolean[] FOV = new boolean[Dungeon.level.length()];
		Point c = Dungeon.level.cellToPoint(cell);
		ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, explosionRange());
		
		ArrayList<Char> affected = new ArrayList<>();
		
		for (int i = 0; i < FOV.length; i++) {
			if (FOV[i]) {
				if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
					CellEmitter.center( i ).burst( BlastParticle.FACTORY, 5 );
				}
				Char ch = Actor.findChar(i);
				if (ch != null){
					affected.add(ch);
				}
			}
		}
		
		for (Char ch : affected){
			//regular bomb damage over an FOV up to 8-range
			int damage = Random.NormalIntRange( 4 + Dungeon.scalingDepth(), 12 + 3*Dungeon.scalingDepth() );
			damage -= ch.drRoll();
			ch.damage(damage, this);
			if (ch == Dungeon.hero && !ch.isAlive()) {
				Dungeon.fail(this);
			}
		}
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 50);
	}
}
