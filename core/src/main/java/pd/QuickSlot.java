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

package pd;

import pd.items.Item;
import render.utils.Bundlable;
import render.utils.Bundle;
import render.utils.Random;

import java.util.ArrayList;
import java.util.Collection;

public class QuickSlot {

	/**
	 * Slots contain objects which are also in a player's inventory. The one exception to this is when quantity is 0,
	 * which can happen for a stackable item that has been 'used up', these are referred to as placeholders.
	 */

	//SPS: 快捷栏共 18 槽，三区固定槽位段（用户裁决 2026-09）：
	//下 0-9（设置显示 3-10 个）、左 10-13（0-4 个）、右 14-17（0-4 个）。
	//各区数量由设置控制，隐藏槽位的物品绑定保留（改设置不丢物品）。
	//旧存档 quickslotpos 0-8 落入下段，天然兼容。
	public static final int SIZE = 18;

	public static final int BOTTOM_START	= 0;
	public static final int BOTTOM_SIZE		= 10;
	public static final int LEFT_START		= 10;
	public static final int LEFT_SIZE		= 4;
	public static final int RIGHT_START		= 14;
	public static final int RIGHT_SIZE		= 4;

	private Item[] slots = new Item[SIZE];


	//direct array interaction methods, everything should build from these methods.
	public void setSlot(int slot, Item item){
		if (slot < 0 || slot >= SIZE) return;   //SPS: 旧档/越界索引保护，直接忽略
		clearItem(item); //we don't want to allow the same item in multiple slots.
		slots[slot] = item;
	}

	public void clearSlot(int slot){
		slots[slot] = null;
	}

	public void reset(){
		slots = new Item[SIZE];
	}

	public Item getItem(int slot){
		if (slot < 0 || slot >= SIZE) return null;   //SPS: 越界索引保护
		return slots[slot];
	}

	//utility methods, for easier use of the internal array.
	public int getSlot(Item item) {
		for (int i = 0; i < SIZE; i++) {
			if (getItem(i) == item) {
				return i;
			}
		}
		return -1;
	}

	public Boolean isPlaceholder(int slot){
		return getItem(slot) != null && getItem(slot).quantity() == 0;
	}

	public Boolean isNonePlaceholder(int slot){
		return getItem(slot) != null && getItem(slot).quantity() > 0;
	}

	public void clearItem(Item item){
		if (contains(item)) {
			clearSlot(getSlot(item));
		}
	}

	public boolean contains(Item item){
		return getSlot(item) != -1;
	}

	public void replacePlaceholder(Item item) {
		for (int i = 0; i < SIZE; i++) {
			if (isPlaceholder(i) && item.isSimilar(getItem(i))) {
				setSlot(i, item);
			}
		}
	}

	public void convertToPlaceholder(Item item){
		
		if (contains(item)) {
			Item placeholder = item.virtual();
			if (placeholder == null) return;
			
			for (int i = 0; i < SIZE; i++) {
				if (getItem(i) == item) setSlot(i, placeholder);
			}
		}
	}

	public Item randomNonePlaceholder(){

		ArrayList<Item> result = new ArrayList<>();
		for (int i = 0; i < SIZE; i ++) {
			if (getItem(i) != null && !isPlaceholder(i)) {
				result.add(getItem(i));
			}
		}
		return Random.element(result);
	}

	private final String PLACEHOLDERS = "placeholders";
	private final String PLACEMENTS = "placements";

	/**
	 * Placements array is used as order is preserved while bundling, but exact index is not, so if we
	 * bundle both the placeholders (which preserves their order) and an array telling us where the placeholders are,
	 * we can reconstruct them perfectly.
	 */

	public void storePlaceholders(Bundle bundle){
		ArrayList<Item> placeholders = new ArrayList<>(SIZE);
		boolean[] placements = new boolean[SIZE];

		for (int i = 0; i < SIZE; i++) {
			if (isPlaceholder(i)) {
				placeholders.add(getItem(i));
				placements[i] = true;
			}
		}
		bundle.put( PLACEHOLDERS, placeholders );
		bundle.put( PLACEMENTS, placements );
	}

	public void restorePlaceholders(Bundle bundle){
		Collection<Bundlable> placeholders = bundle.getCollection(PLACEHOLDERS);
		boolean[] placements = bundle.getBooleanArray( PLACEMENTS );

		int i = 0;
		for (Bundlable item : placeholders){
			while (i < placements.length && !placements[i]){
				i++;
			}
			if (i >= placements.length) return;   //SPS: 旧档 placements（长度 9）保护
			setSlot( i, (Item)item );
			i++;
		}

	}

}
