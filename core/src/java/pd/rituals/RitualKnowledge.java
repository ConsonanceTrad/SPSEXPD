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

import pd.journal.Document;

/**
 * SPSEXPD: 仪式知识判定单点（批次B）。
 *
 * <p>规则（设计裁决）：未获得讲解页的仪式仍然可以被法器激发（仪式照常生效），
 * 但布阵石无法收录该仪式的材料，也无法选定它来布置——布阵石是「学会后」的加速道具。
 * 判定门槛取 {@code Document.RITUAL_GUIDE.isPageFound}（拾取仪式讲解残页即登记 found，
 * 拾取时会弹出对应读物窗口，视为已过目）。</p>
 */
public class RitualKnowledge {

	/** 该仪式是否已被玩家知晓（布阵石的收录/布置门槛） */
	public static boolean known( Ritual ritual ) {
		return ritual != null && Document.RITUAL_GUIDE.isPageFound( ritual.id() );
	}

	/** 调试用：直接登记某一讲解页为已获得 */
	public static void learn( Ritual ritual ) {
		if (ritual != null) Document.RITUAL_GUIDE.findPage( ritual.id() );
	}
}
