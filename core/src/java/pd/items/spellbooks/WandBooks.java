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

package pd.items.spellbooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pd.items.Item;
import pd.items.equipment.wands.Wand;

/**
 * SPSEXPD: 法杖 → 原典/抄本 映射表（法术书化批次1）——「常规法术原典/抄本化」的唯一真源。
 *
 * <p>每根常规法杖登记一对原典/抄本；{@code Generator.Category.WAND} 的常规生成位
 * 产出书目（{@link #bookClasses()}），稀有杖位走 WAND_RARE（法杖匠任务/稀有宝箱）。
 * 效果在书类里直接重写（不走内嵌法杖，用户裁决）。</p>
 */
public class WandBooks {

	/** 一对原典/抄本 */
	public static final class Pair {
		public final Class<? extends Wand> wand;
		public final Class<? extends Spellbook> tome;
		public final Class<? extends Spellbook> codex;

		Pair( Class<? extends Wand> wand, Class<? extends Spellbook> tome, Class<? extends Spellbook> codex ) {
			this.wand = wand;
			this.tome = tome;
			this.codex = codex;
		}
	}

	private static final Map<Class<? extends Wand>, Pair> MAP = new LinkedHashMap<>();

	private static void register( Class<? extends Wand> wand, Class<? extends Spellbook> tome, Class<? extends Spellbook> codex ) {
		MAP.put( wand, new Pair( wand, tome, codex ) );
	}

	static {
		register( pd.items.equipment.wands.WandOfFirebolt.class, pd.items.spellbooks.wands.FireboltTome.class, pd.items.spellbooks.wands.FireboltCodex.class );
		register( pd.items.equipment.wands.WandOfFrost.class, pd.items.spellbooks.wands.FrostTome.class, pd.items.spellbooks.wands.FrostCodex.class );
		register( pd.items.equipment.wands.WandOfLightning.class, pd.items.spellbooks.wands.LightningTome.class, pd.items.spellbooks.wands.LightningCodex.class );
	}

	/** 某法杖对应的书对（无则 null） */
	public static Pair of( Class<? extends Wand> wand ) {
		return wand == null ? null : MAP.get( wand );
	}

	/** 全部书对（注册顺序） */
	public static Collection<Pair> all() {
		return Collections.unmodifiableCollection( MAP.values() );
	}

	/** 书目类表（供 Generator 的常规法术生成位）——原典/抄本交错排列 */
	public static List<Class<? extends Item>> bookClasses() {
		List<Class<? extends Item>> classes = new ArrayList<>();
		for (Pair pair : MAP.values()) {
			classes.add( pair.tome );
			classes.add( pair.codex );
		}
		return classes;
	}
}
