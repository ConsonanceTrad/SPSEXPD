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
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 抄本《妙手抄本》（法术书化批次3d）——魔术之手法杖的书化：
 * 轻伤害 + 顺手偷窃（抄本成功率 30%），亦可隔空开箱/顺货架/取物。
 */
public class SleightCodex extends SleightSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SleightCodex.class)
			.t("name", "妙手抄本")
			.t("cast_prompt", "选择妙手所及的位置")
			.t("desc", "薄薄几页，教你把手藏得更深一点。\n\n施法（消耗 2 法力）：向点选位置伸出一只看不见的小手：轻伤目标并_三成概率_顺走它身上的东西；也可以隔空_开箱、顺走货架商品、取来地上的物品_。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override protected int castLevel() { return 0; }
	@Override protected int stealChancePercent() { return 30; }
	@Override protected int minDamage() { return 2; }
	@Override protected int maxDamage() { return 6; }
	@Override protected int missileType() { return MagicMissile.PURPLE_LIGHT; }
}
