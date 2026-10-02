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

package pd.items.quest;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;

import pd.items.Item;
import pd.messages.InlineText;

public class MetalShard extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MetalShard.class)
			.t("name", "邪能碎片")
			.t("desc", "一块被诅咒的金属锈片，它是DM-300的装甲外壳被击碎时分离出来的。你能感受到某种尚未激活的恶毒魔力潜藏其中。\n\n该物品自身并没有什么实际用途，不过它在与特定卷轴或炸弹共炼时可能有妙用。再不济，它也能提供相当的炼金能量。")
			.t("discover_hint", "你可从某种敌人的掉落物中获得该物品。");
	}

	
	{
		image = ConsumGoodsMaterialsMaterialsDict.SHARD_0;
		stackable = true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return quantity * 50;
	}

	@Override
	public int energyVal() {
		return quantity * 3;
	}
}
