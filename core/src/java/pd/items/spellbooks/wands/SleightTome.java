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
 * SPSEXPD: 原典《妙手原典》（法术书化批次3d）——魔术之手法杖的书化：
 * 伤害 + 顺手偷窃（原典成功率 70%），亦可隔空开箱/顺货架/取物/惊醒宝箱怪。
 * 偷窃成功率采用书的固定值（不复刻原杖价位曲线，差异见提交说明）。
 */
public class SleightTome extends SleightSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SleightTome.class)
			.t("name", "妙手原典")
			.t("cast_prompt", "选择妙手所及的位置")
			.t("desc", "翻页时总感觉指间少了点什么——多半是被这本书顺走了。\n\n施法（消耗 5 法力）：向点选位置伸出一只看不见的手：伤害目标并_七成概率_顺走它身上的东西；也可以隔空_开箱、顺走货架商品、取来地上的物品_，或者把宝箱怪惊醒。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override protected int castLevel() { return 5; }
	@Override protected int stealChancePercent() { return 70; }
	@Override protected int minDamage() { return 10; }
	@Override protected int maxDamage() { return 20; }
	@Override protected int missileType() { return MagicMissile.PURPLE_LIGHT; }
}
