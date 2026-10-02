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

package pd.items.consum.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.SpellSprite;
import pd.levels.CellFlags;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ScrollOfMagicMapping extends Scroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfMagicMapping.class)
			.t("name", "探地卷轴")
			.t("layout", "你熟悉了这层的地形。")
			.t("desc", "阅读这张卷轴时，一副明晰的景象会刻入你的记忆中，告知你整个楼层的精确布局并揭开所有隐藏的秘密。不过道具位置和生物分布依旧是未知状态。");
	}




	{
		icon = ItemIconSheet.SCROLL_MAGICMAP;
	}

	@Override
	public void doRead() {
		readMap(false);
	}

	private void readMap(boolean discoverSecrets) {

		detach(curUser.belongings.backpack);
		int length = Dungeon.level.length();
		int[] map = Dungeon.level.map;
		boolean[] mapped = Dungeon.level.mapped;
		boolean[] discoverable = Dungeon.level.discoverable;
		
		boolean noticed = false;
		
		for (int i=0; i < length; i++) {
			
			int terr = map[i];
			
			if (discoverable[i]) {
				
				mapped[i] = true;
				if (discoverSecrets && (Terrain.flags[terr] & Terrain.SECRET) != 0) {
					
					CellFlags.discover( Dungeon.level,  i );
					
					if (Dungeon.level.heroFOV[i]) {
						GameScene.discoverTile( i, terr );
						discover( i );
						
						noticed = true;
					}
				}
			}
		}
		GameScene.updateFog();
		
		GLog.i( Messages.get(this, "layout") );
		if (noticed) {
			Sample.INSTANCE.play( Assets.Sounds.SECRET );
		}
		
		if (discoverSecrets) {
			SpellSprite.show(curUser, SpellSprite.MAP);
			Sample.INSTANCE.play(Assets.Sounds.READ);
		}

		identify();

		readAnimation();
	}

	@Override
	public void empoweredRead() {
		readMap(true);
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
	
	public static void discover( int cell ) {
		CellEmitter.get( cell ).start( Speck.factory( Speck.DISCOVER ), 0.1f, 4 );
	}
}
