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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.spellbooks.wands;

import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.data.BArray;

/**
 * SPSEXPD: 原典《雷云原典》（法术书化批次3c）——雷云法杖的书化：
 * 在点选位置召下一朵雷云（5 级云），绕过充能门槛（法力代替聚能）。
 */
public class TCloudTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(TCloudTome.class)
			.t("name", "雷云原典")
			.t("cast_prompt", "选择雷云聚集的位置")
			.t("desc", "书页上方悬着一小片不肯散去的乌云。\n\n施法（消耗 5 法力）：在点选位置召下一朵_雷云_（5 级）——它会自己向敌人甩出闪电。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	/** 施法等级（照雷云法杖 5 级口径） */
	public int castLevel() { return 5; }
	@Override public int manaCost() { return 5; }
	@Override public boolean targetRequired() { return true; }

	@Override
	public void castAt( Hero hero, int cell ) {
		int spawnCell = findSpawnCell( cell );
		if (spawnCell < 0) return;

		pd.items.equipment.wands.WandOfTCloud.ThunderCloud cloud;
		if (hero.subClass == HeroSubClass.LEADER) {
			cloud = new pd.items.equipment.wands.WandOfTCloud.STCloud();
		} else {
			cloud = new pd.items.equipment.wands.WandOfTCloud.TCloud();
		}
		cloud.pos = spawnCell;
		setCloudLevel( cloud, castLevel() );
		GameScene.add( cloud );
		pd.Dungeon.level.occupyCell( cloud );
		CellEmitter.get( spawnCell ).burst( Speck.factory( Speck.WOOL ), 4 );

		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.shockhit();
	}

	private int findSpawnCell( int target ) {
		boolean[] passable = BArray.or( pd.Dungeon.level.passable, pd.Dungeon.level.avoid, null );
		for (pd.actors.Char ch : pd.actors.Actor.chars()) passable[ch.pos] = false;
		PathFinder.buildDistanceMap( target, passable, 1 );

		int distance = Actor.findChar( target ) == null ? 0 : 1;
		if (distance == 1) PathFinder.distance[target] = Integer.MAX_VALUE;
		for (int cell = 0; cell < pd.Dungeon.level.length(); cell++) {
			if (PathFinder.distance[cell] == distance && pd.Dungeon.level.insideMap( cell )
					&& !pd.Dungeon.level.pit[cell] && Actor.findChar( cell ) == null) return cell;
		}
		return -1;
	}

	/** SPSEXPD: ThunderCloud.wandLevel 是 protected（跨包不可写），反射赋值 */
	private void setCloudLevel( pd.items.equipment.wands.WandOfTCloud.ThunderCloud cloud, int level ) {
		try {
			java.lang.reflect.Field f = pd.items.equipment.wands.WandOfTCloud.ThunderCloud.class.getDeclaredField( "wandLevel" );
			f.setAccessible( true );
			f.setInt( cloud, level );
		} catch (Exception e) {
			//等级缺省为 0，可接受
		}
	}
}
