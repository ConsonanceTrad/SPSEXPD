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

package pd.items.consum.materials;

import java.util.ArrayList;

import pd.actors.hero.Hero;
import pd.actors.hero.Mana;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 魔力结晶（批次C）——仪式产物。使用后回复 30 点法力值（受上限封顶）。
 */
public class ManaCrystal extends Item {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ManaCrystal.class)
			.t("name", "魔力结晶")
			.t("ac_use", "使用")
			.t("used", "结晶化作一阵清凉，你回复了 30 点法力。")
			.t("desc", "一枚凝着青蓝色辉光的结晶，里面封存着仪式里多余的法力。\n\n使用后回复 30 点法力值（受法力上限封顶）。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsDict.MANA_CRYSTAL;
		stackable = true;
	}

	private static final String AC_USE = "USE";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_USE );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		//SPSEXPD: 「使用」动作 —— 回 30 法力（封顶），走 spendAndNext 防回合卡死（项目坑约定）
		if (action.equals( AC_USE ) && hero != null) {
			detach( hero.belongings.backpack );
			Mana.add( hero, 30 );
			GLog.i( Messages.get( ManaCrystal.class, "used" ) );
			hero.spendAndNext( 1f );
		} else {
			super.execute( hero, action );
		}
	}
}
