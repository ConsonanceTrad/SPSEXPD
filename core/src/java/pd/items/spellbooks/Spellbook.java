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
 * <p>原典/抄本分级：{@link #isTome()} 为真的是<b>原典</b>——效果更强，但
 * <b>同时持有超过 1 本原典会产生负面效果</b>（首批占位数值：施法效果打七折、每次施法
 * 额外多耗 2 点法力，见 {@link #overloadScale}/{@link #extraCost}，待用户定稿调参）。
 * 抄本默认 {@code isTome()} 为假，基本都有对应原典。</p>
 */
public abstract class Spellbook extends Item {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Spellbook.class)
			.t("no_mana", "你的法力不够支撑这本法术书的消耗。")
			.t("overload", "多本原典的法则在你脑中互相冲撞——这场施法变得既昂贵又不稳定！");
	}

	/** SPSEXPD: 施法动作（子类的定向施法也要引用） */
	public static final String AC_CAST = "CAST";

	{
		stackable = false;
		unique = true;
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
		int cost = manaCost() + extraCost( hero );
		if (!Mana.has( hero, cost )) {
			GLog.w( Messages.get( Spellbook.class, "no_mana" ) );
			return false;
		}
		if (overloaded( hero )) {
			GLog.w( Messages.get( Spellbook.class, "overload" ) );
		}
		Mana.spend( hero, cost );
		onCast( hero );
		return true;
	}

	/** SPSEXPD: 原典过载判定——同时持有超过 1 本原典（≥2）即过载 */
	public static boolean overloaded( Hero hero ) {
		return heldTomes( hero ) >= 2;
	}

	/** 背包内原典数量 */
	public static int heldTomes( Hero hero ) {
		if (hero == null || hero.belongings == null) return 0;
		int tomes = 0;
		for (Item item : hero.belongings.getAllItems( Spellbook.class )) {
			if (item instanceof Spellbook && ((Spellbook) item).isTome()) tomes++;
		}
		return tomes;
	}

	/** 过载时的额外法力消耗（占位数值：+2） */
	public static int extraCost( Hero hero ) {
		return overloaded( hero ) ? 2 : 0;
	}

	/** 过载时的效果折算（占位数值：×0.7） */
	public static float overloadScale( Hero hero ) {
		return overloaded( hero ) ? 0.7f : 1f;
	}

	/**
	 * SPSEXPD: 效果量单点——基础值 × 法术强度（每点法强 +10%）× 原典过载折算。
	 * 法术书效果结算一律经由本方法取量。
	 */
	protected final int power( Hero hero, int base ) {
		return Math.max(1, Math.round( base * Mana.spellStrength( hero ) * overloadScale( hero ) ));
	}
}
