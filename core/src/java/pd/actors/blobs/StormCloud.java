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

package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.messages.InlineText;

public class StormCloud extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(StormCloud.class)
			.t("name", "暴雨")
			.t("desc", "这里盘绕着一片翻腾的水汽。");
	}



	
	@Override
	protected void evolve() {
		super.evolve();
		
		int cell;

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0) {
					Dungeon.level.setCellToWater(true, cell);
					if (fire != null){
						fire.clear(cell);
					}

					//fiery enemies take damage as if they are in toxic gas
					Char ch = Actor.findChar(cell);
					if (ch != null
							&& !ch.isImmune(getClass())
							&& Char.hasProp(ch, Char.Property.FIERY)){
						ch.damage(1 + Dungeon.scalingDepth()/5, this);
					}
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( Speck.factory( Speck.STORM ), 0.4f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	
}
