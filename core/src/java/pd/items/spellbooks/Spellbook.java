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
			.t("no_mana", "你的法力不够支撑这本法术书的消耗。")
			.t("cast_prompt_default", "选择施法位置");
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

	/** 施法效果（无目标路径；定向书退回以脚下为落点） */
	public void onCast( Hero hero ) {
		castAt( hero, hero.pos );
	}

	/** SPSEXPD: 是否需要点选目标格（定向书覆写为 true，走基类选点器） */
	public boolean targetRequired() { return false; }

	/** SPSEXPD: 定向施法的落点结算（选点回调里、法力已扣除后调用）；默认退回无目标效果 */
	public void castAt( Hero hero, int cell ) {
		onCast( hero );
	}

	/** SPSEXPD: 弹道特效类型（定向书覆写，如 MagicMissile.FIRE / FROST） */
	protected int missileType() {
		return pd.effects.MagicMissile.MAGIC_MISSILE;
	}

	/** SPSEXPD: 选点提示（子类可注册自己的 cast_prompt 键） */
	public String castPrompt() {
		return pd.messages.InlineText.get( pd.messages.InlineText.key( getClass(), "cast_prompt" ) ) != null
				? pd.messages.InlineText.get( pd.messages.InlineText.key( getClass(), "cast_prompt" ) )
				: Messages.get( Spellbook.class, "cast_prompt_default" );
	}

	//SPSEXPD: 通用定向选点器——先扣法力（拒绝则不施放），弹道 FX 走完后结算 castAt
	private static Hero dirUser;
	private static Spellbook dirBook;

	private final pd.scenes.CellSelector.Listener dirCaster = new pd.scenes.CellSelector.Listener() {

		@Override
		public void onSelect( Integer target ) {
			if (target == null || dirUser == null || dirBook == null) return;
			if (!dirBook.spendFor( dirUser )) return;
			pd.effects.MagicMissile.boltFromChar( dirUser.sprite.parent, dirBook.missileType(),
					dirUser.sprite, target, () -> {
						dirBook.castAt( dirUser, target );
						//SPSEXPD: 施法耗 1 回合（FX 走完再结算，与法杖 zap 同节奏）
						dirUser.spendAndNext( 1f );
					} );
		}

		@Override
		public String prompt() {
			return dirBook != null ? dirBook.castPrompt() : Messages.get( Spellbook.class, "cast_prompt_default" );
		}
	};

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
			if (targetRequired()) {
				//SPSEXPD: 定向书——进选点器（法力在选点回调里扣）
				dirUser = hero;
				dirBook = this;
				pd.scenes.GameScene.selectCell( dirCaster );
			} else if (tryCast( hero )) {
				hero.spendAndNext( 1f );
			}
		} else {
			super.execute( hero, action );
		}
	}

	/** SPSEXPD: 仅扣除法力并提示不足（不结算效果）；供 tryCast 与定向选点器共用 */
	final boolean spendFor( Hero hero ) {
		if (!Mana.has( hero, manaCost() )) {
			GLog.w( Messages.get( Spellbook.class, "no_mana" ) );
			return false;
		}
		Mana.spend( hero, manaCost() );
		return true;
	}

	/** SPSEXPD: 施法单点（无目标路径）；成功返回 true（已扣法力） */
	public boolean tryCast( Hero hero ) {
		if (!spendFor( hero )) return false;
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
