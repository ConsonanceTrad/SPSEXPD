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
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.effects.Flare;
import pd.tiles.DungeonTilemap;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class StoneOfFear extends Runestone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfFear.class)
			.t("name", "恐惧符石")
			.t("desc", "当把这颗符石掷向一个盟友或敌人时，被命中的角色会陷入深深的恐惧中，无法克制地逃离。攻击会刺激其从恐惧中更快恢复。");
	}

	
	{
		image = ConsumScrollAmuletAmuletDict.STONE_FEAR_0;
	}
	
	@Override
	protected void activate(int cell) {

		Char ch = Actor.findChar( cell );

		if (ch != null && ch.alignment != Char.Alignment.ALLY ){
			Buff.affect( ch, Terror.class, Terror.DURATION ).object = curUser.id();
		}

		new Flare( 5, 16 ).color( 0xFF0000, true ).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2f );
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
	}
	
}
