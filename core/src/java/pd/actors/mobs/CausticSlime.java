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

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.items.quest.GooBlob;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.CausticSlimeSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class CausticSlime extends Slime {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(CausticSlime.class)
			.t("name", "蚀化史莱姆")
			.t("desc", "这个史莱姆似乎已经被下方渗透的黑暗能量完全侵蚀了。它已经失去了体表原有的绿色光泽，并且在不断滴落腐蚀淤泥。")
			.t("discover_hint", "你可在某个地牢区域中中碰巧遇到该敌人。");
	}

	
	{
		spriteClass = CausticSlimeSprite.class;
		
		properties.add(Property.ACIDIC);
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 2 ) == 0) {
			Buff.affect( enemy, Ooze.class ).set( Ooze.DURATION );
			enemy.sprite.burst( 0x000000, 5 );
		}
		
		return super.attackProc( enemy, damage );
	}
	
	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + 2) return;
		
		super.rollToDropLoot();
		
		int ofs;
		do {
			ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
		Dungeon.level.drop( new GooBlob(), pos + ofs ).sprite.drop( pos );
	}
}
