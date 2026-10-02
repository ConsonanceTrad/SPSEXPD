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

package pd.levels.traps;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Roots;
import pd.effects.Wound;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GrippingTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(GrippingTrap.class)
			.t("name", "捕猎陷阱")
			.t("desc", "触发这个陷阱将使一对钳子合上，伤害受害者并将他们固定在这里。\n\n由于其简单的构造，这种陷阱可被多次激活而不损毁。");
	}


	{
		color = GREY;
		shape = DIAMOND;
	}

	@Override
	public void activate() {

		Char c = Actor.findChar( pos );

		if (c != null) {
			int damage = Math.max(0, scalingDepth() - Random.IntRange(0, c.drRoll() / 2));
			Buff.affect( c, Bleeding.class ).set( damage );
			Buff.prolong( c, Cripple.class, 15f);
			Buff.prolong( c, Roots.class, 5f);
			Wound.hit( c );
		} else {
			Wound.hit( pos );
		}

	}
}
