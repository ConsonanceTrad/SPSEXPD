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
import pd.effects.BlobEmitter;
import pd.journal.Notes;
import pd.levels.Level;
import render.utils.geom.Rect;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

public class Blob extends Actor {

	{
		actPriority = BLOB_PRIO;
	}
	
	public int volume = 0;
	
	public int[] cur;
	protected int[] off;
	
	public BlobEmitter emitter;

	public Rect area = new Rect();
	
	public boolean alwaysVisible = false;

	private static final String CUR		= "cur";
	private static final String START	= "start";
	private static final String LENGTH	= "length";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		
		if (volume > 0) {
		
			int start;
			for (start=0; start < Dungeon.level.length(); start++) {
				if (cur[start] > 0) {
					break;
				}
			}
			int end;
			for (end=Dungeon.level.length()-1; end > start; end--) {
				if (cur[end] > 0) {
					break;
				}
			}
			
			bundle.put( START, start );
			bundle.put( LENGTH, cur.length );
			bundle.put( CUR, trim( start, end + 1 ) );
			
		}
	}
	
	private int[] trim( int start, int end ) {
		int len = end - start;
		int[] copy = new int[len];
		System.arraycopy( cur, start, copy, 0, len );
		return copy;
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );

		if (bundle.contains( CUR )) {

			cur = new int[bundle.getInt(LENGTH)];
			off = new int[cur.length];

			int[] data = bundle.getIntArray(CUR);
			int start = bundle.getInt(START);
			for (int i = 0; i < data.length; i++) {
				cur[i + start] = data[i];
				volume += data[i];
			}

		}
	}

	protected ArrayList<Integer> cellsToFlagUpdate = new ArrayList<>();

	//SPSEXPD: 最小存活回合数（0 = 不限制，保持原版行为）
	public int minLifetime = 0;
	private int aliveTicks = 0;
	private int[] initialGrid = null;

	/** SPSEXPD: 让这种雾/场在消散前至少存在指定回合数（范围不变，只延长留存）。 */
	public Blob setMinLifetime( int turns ){
		this.minLifetime = Math.max( this.minLifetime, turns );
		if (initialGrid == null && cur != null) {
			initialGrid = new int[cur.length];
			System.arraycopy( cur, 0, initialGrid, 0, cur.length );
		}
		return this;
	}

	@Override
	public boolean act() {
		
		spend( TICK );
		aliveTicks++;
		
		if (volume > 0) {

			if (area.isEmpty())
				setupArea();

			volume = 0;

			evolve();
			int[] tmp = off;
			off = cur;
			cur = tmp;

			for (int i : cellsToFlagUpdate){
				Dungeon.level.updateCellFlags(i);
			}
			cellsToFlagUpdate.clear();
			
		} else {
			//SPSEXPD: 未满最短留存回合时，按初始分布续上，保持场上仍有该雾/场
			if (minLifetime > 0 && aliveTicks < minLifetime && initialGrid != null) {
				System.arraycopy( initialGrid, 0, cur, 0, cur.length );
				volume = 0;
				for (int v : cur) volume += v;
				if (volume > 0) {
					area.setEmpty();
					setupArea();
					for (int i : cellsToFlagUpdate){
						Dungeon.level.updateCellFlags(i);
					}
					cellsToFlagUpdate.clear();
					return true;
				}
			}
			if (!area.isEmpty()) {
				area.setEmpty();
				//clear any values remaining in off
				System.arraycopy(cur, 0, off, 0, cur.length);
			}
		}
		
		return true;
	}

	public void setupArea(){
		for (int cell=0; cell < cur.length; cell++) {
			if (cur[cell] != 0){
				area.union(cell%Dungeon.level.width(), cell/Dungeon.level.width());
			}
		}
	}
	
	public void use( BlobEmitter emitter ) {
		this.emitter = emitter;
	}
	
	protected void evolve() {
		
		boolean[] blocking = Dungeon.level.solid;
		int cell;
		for (int i=area.top-1; i <= area.bottom; i++) {
			for (int j = area.left-1; j <= area.right; j++) {
				cell = j + i*Dungeon.level.width();
				if (Dungeon.level.insideMap(cell)) {
					if (!blocking[cell]) {

						int count = 1;
						int sum = cur[cell];

						if (j > area.left && !blocking[cell-1]) {
							sum += cur[cell-1];
							count++;
						}
						if (j < area.right && !blocking[cell+1]) {
							sum += cur[cell+1];
							count++;
						}
						if (i > area.top && !blocking[cell-Dungeon.level.width()]) {
							sum += cur[cell-Dungeon.level.width()];
							count++;
						}
						if (i < area.bottom && !blocking[cell+Dungeon.level.width()]) {
							sum += cur[cell+Dungeon.level.width()];
							count++;
						}

						int value = sum >= count ? (sum / count) - 1 : 0;
						off[cell] = value;

						if (value > 0){
							if (i < area.top)
								area.top = i;
							else if (i >= area.bottom)
								area.bottom = i+1;
							if (j < area.left)
								area.left = j;
							else if (j >= area.right)
								area.right = j+1;
						}

						volume += value;
					} else {
						off[cell] = 0;
					}
				}
			}
		}
	}

	public void seed( Level level, int cell, int amount ) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];

		cur[cell] += amount;
		volume += amount;

		//SPSEXPD: 记录初始分布，供最短留存时间到时“续上”使用
		if (minLifetime > 0) {
			if (initialGrid == null || initialGrid.length != cur.length) initialGrid = new int[cur.length];
			initialGrid[cell] += amount;
		}

		area.union(cell%level.width(), cell/level.width());
	}
	
	public void clear( int cell ) {
		if (volume == 0) return;
		volume -= cur[cell];
		cur[cell] = 0;
	}

	public void fullyClear(){
		volume = 0;
		area.setEmpty();
		cur = new int[Dungeon.level.length()];
		off = new int[Dungeon.level.length()];
	}

	public void onBuildFlagMaps( Level l ){
		//do nothing by default, only some blobs affect flags
	}

	public void onUpdateCellFlags( Level l, int cell){
		//applies terrain flags to just one cell (e.g. right after terrain changes)
	}

	//some blobs have an associated landmark entry, which is added when the hero sees them
	//blobs may also remove this landmark in some cases, such as when they expire or are consumed
	public Notes.Landmark landmark(){
		return null;
	}
	
	public String tileDesc() {
		return null;
	}
	
	public static<T extends Blob> T seed( int cell, int amount, Class<T> type ) {
		return seed(cell, amount, type, Dungeon.level);
	}
	
	@SuppressWarnings("unchecked")
	public static<T extends Blob> T seed( int cell, int amount, Class<T> type, Level level ) {
		
		T gas = (T)level.blobs.get( type );
		
		if (gas == null) {
			gas = Reflection.newInstance(type);
			//this ensures that gasses do not get an 'extra turn' if they are added by a mob or buff
			if (Actor.curActorPriority() < gas.actPriority) {
				gas.spend(1f);
			}
		}
		
		if (gas != null){
			level.blobs.put( type, gas );
			gas.seed( level, cell, amount );
		}
		
		return gas;
	}

	public static int volumeAt( int cell, Class<? extends Blob> type){
		Blob gas = Dungeon.level.blobs.get( type );
		if (gas == null || gas.volume == 0) {
			return 0;
		} else {
			return gas.cur[cell];
		}
	}
}
