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

package pd.rituals;

import pd.items.Item;

/**
 * SPSEXPD: 仪式材料条目——一个仪式配方对某类物品的需求数量。
 *
 * <p>匹配按 {@code itemClass.isInstance(item)} 判定（接受子类），消耗时按条目数量
 * 从 5×5 范围内的地面堆（{@code Heap.Type.HEAP}）里扣。</p>
 */
public class RitualMaterial {

	public final Class<? extends Item> itemClass;
	public final int quantity;

	public RitualMaterial( Class<? extends Item> itemClass, int quantity ) {
		this.itemClass = itemClass;
		this.quantity = Math.max(1, quantity);
	}

	@Override
	public boolean equals( Object obj ) {
		if (!(obj instanceof RitualMaterial)) return false;
		RitualMaterial other = (RitualMaterial) obj;
		return itemClass == other.itemClass && quantity == other.quantity;
	}

	@Override
	public int hashCode() {
		return itemClass.hashCode() * 31 + quantity;
	}

	/** 文案：物品名 × 数量（例「火药×2」）；物品名取自 Item 实例，实例化失败时退化为类简名 */
	public String describe() {
		Item item = render.utils.serialize.Reflection.newInstance( itemClass );
		if (item != null) return item.name() + "×" + quantity;
		return itemClass.getSimpleName() + "×" + quantity;
	}

	/** 文案：多个材料条目按序以「+」连接（供仪式描述里的材料清单） */
	public static String describeList( Iterable<RitualMaterial> materials ) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (RitualMaterial m : materials) {
			if (!first) sb.append(" + ");
			sb.append(m.describe());
			first = false;
		}
		return sb.toString();
	}
}
