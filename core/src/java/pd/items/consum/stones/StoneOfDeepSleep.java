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

package pd.items.consum.stones;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicalSleep;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class StoneOfDeepSleep extends Runestone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfDeepSleep.class)
			.t("name", "沉睡符石")
			.t("desc", "当把这颗符石掷向一个敌人时，被命中的敌人会陷入魔法睡眠。陷入魔法睡眠的敌人会永远沉睡下去，除非受到外界打扰。");
	}



	
	{
		image = ConsumScrollAmuletAmuletDict.STONE_SLEEP_0;
	}
	
	@Override
	protected void activate(int cell) {

		if (Actor.findChar(cell) != null) {

			Char c = Actor.findChar(cell);

			if (c instanceof Mob){

				Buff.affect(c, MagicalSleep.class);
				c.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );

			}

		}
		
		Sample.INSTANCE.play( Assets.Sounds.LULLABY );
		
	}
}
