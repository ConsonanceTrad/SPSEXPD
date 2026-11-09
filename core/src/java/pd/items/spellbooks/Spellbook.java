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

package pd.items.spellbooks;

import java.util.ArrayList;

import pd.actors.hero.Hero;
import pd.actors.hero.Mana;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 法术书（施法系统拓展批次E基类）。
 *
 * <p>法术书的使用一律<b>消耗法力值</b>（{@link Mana}，受法力上限约束），效果量
 * <b>受法强加成</b>（与项目法强收口同源：{@link Mana#spellStrength}）。法器（Wand）
 * 仍走自身充能模型，与本类无关。</p>
 *
 * <p>原典/抄本分级：{@link #isTome()} 为真的是<b>原典</b>——效果更强、法力消耗更高；
 * 抄本为假——效果按低等级折算。统一法力消耗：抄本 2 / 原典 5（无持有多本的惩罚机制）。
 * 法术书可重复生成与持有。</p>
 */
public abstract class Spellbook extends Item {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Spellbook.class)
			.t("no_mana", "你的法力不够支撑这本法术书的消耗。");
	}

	/** SPSEXPD: 施法动作（子类的定向施法也要引用） */
	public static final String AC_CAST = "CAST";

	{
		stackable = false;
	}

	/** 原典（true）/抄本（false） */
	public abstract boolean isTome();

	/** 施法消耗的基础法力 */
	public abstract int manaCost();

	/** 施法效果（材料已被判定，法力已扣除） */
	public abstract void onCast( Hero hero );

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_CAST );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		//SPSEXPD: 施法——法力不足直接拒绝；成功走 spendAndNext 防回合卡死
		if (action.equals( AC_CAST ) && hero != null) {
			if (tryCast( hero )) {
				hero.spendAndNext( 1f );
			}
		} else {
			super.execute( hero, action );
		}
	}

	/** SPSEXPD: 施法单点（供动作/选点回调共用）；成功返回 true（已扣法力） */
	public boolean tryCast( Hero hero ) {
		int cost = manaCost();
		if (!Mana.has( hero, cost )) {
			GLog.w( Messages.get( Spellbook.class, "no_mana" ) );
			return false;
		}
		Mana.spend( hero, cost );
		onCast( hero );
		return true;
	}

	/**
	 * SPSEXPD: 效果量单点——基础值 × 法术强度（每点法强 +10%）。
	 * 法术书效果结算一律经由本方法取量。
	 */
	protected final int power( Hero hero, int base ) {
		return Math.max(1, Math.round( base * Mana.spellStrength( hero ) ));
	}
}
