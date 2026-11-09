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

package pd.magic.blue;

import pd.actors.Char;

/**
 * SPSEXPD: 青魔法机制占坑（批次F，机制延后）。
 *
 * <p>设计方向（青魔法笔记 No.x 的配套机制，待细化后实装）：</p>
 * <ul>
 * <li>青魔法 = 从不同怪物身上<b>习得它们的特殊法术</b>的体系；习得入口与知识记录、
 *     释放消耗（法力值，见 pd.actors.hero.Mana）、法强加成口径都尚未定稿。</li>
 * <li>占位接口约定：每一条「青魔法」是一个 {@code BlueSpell}，携带来源怪物类
 *     （{@link #sourceMob()}）、习得条件与施放效果；本批次只立包名与接口，
 *     <b>不注册任何实际法术</b>，防止后续批次撞名。</li>
 * <li>载体：{@code pd.items.unique.items.BlueMagicNotes}（青魔法笔记 No.1，仅占位道具）。</li>
 * </ul>
 */
public interface BlueMagic {

	/** 该青魔法习自哪种怪物的特殊法术（占位；机制批次再定语义） */
	Class<? extends Char> sourceMob();

	/** 青魔法名（占位） */
	String name();

	/** 施放效果（占位：机制批次实装；届时走 Mana.spend + 法强加成口径） */
	void cast( Char caster );
}
