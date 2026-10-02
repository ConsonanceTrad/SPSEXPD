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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Electricity;
import pd.effects.Lightning;
import pd.sprites.CharSprite;
import render.noosa.audio.Sample;
import render.utils.geom.PointF;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ShockingDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShockingDart.class)
			.t("name", "电击飞镖")
			.t("desc", "这些飞镖上涂着一种由风暴藤制成的药物，能让目标受到强烈的电击。");
	}



	
	{
		image = ConsumThrowsDict.SHOCKING_DART_0;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//when processing charged shot, only shock enemies
		if (!processingChargedShot || attacker.alignment != defender.alignment) {
			defender.damage(Random.NormalIntRange(5 + Dungeon.scalingDepth() / 4, 10 + Dungeon.scalingDepth() / 4), new Electricity());

			CharSprite s = defender.sprite;
			if (s != null && s.parent != null) {
				ArrayList<Lightning.Arc> arcs = new ArrayList<>();
				arcs.add(new Lightning.Arc(new PointF(s.x, s.y + s.height / 2), new PointF(s.x + s.width, s.y + s.height / 2)));
				arcs.add(new Lightning.Arc(new PointF(s.x + s.width / 2, s.y), new PointF(s.x + s.width / 2, s.y + s.height)));
				s.parent.add(new Lightning(arcs, null));
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
			}
		}
		
		return super.proc(attacker, defender, damage);
	}
}
