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

import java.util.List;

import pd.actors.hero.Hero;

/**
 * SPSEXPD: 仪式机制（仪式/施法拓展批次 B 基类）。
 *
 * <p>仪式 = 以锚点（弹道落点或自身）为中心的 5×5 地面材料组合 → 满足配方即触发一次
 * 短暂的强力效果（魔力结晶 / 强力 buff / 地面效果 / 落地产物，由 {@link #act} 决定）。
 * 材料由激发消耗，仪式本身除特殊说明外一次性生效。</p>
 *
 * <p>约定：</p>
 * <ul>
 * <li>{@link #id()} 同时是「仪式讲解」册（{@code Document.RITUAL_GUIDE}）的页 id；
 *     未获得讲解页时仪式仍可激发，但布阵石不可布置该仪式（判定见 RitualKnowledge）。</li>
 * <li>子类 <b>不得引用 Document/Rituals</b>——Document 的静态初始化会经由 Rituals
 *     加载本包类，引用回去会造成类初始化循环。</li>
 * <li>文案在子类静态块用 {@code InlineText.of(X.class).t(...)} 注册内联简中。</li>
 * </ul>
 */
public abstract class Ritual {

	/** 讲解页 id / 注册表主键（小写下划线风格，如 "ember_spray"） */
	public abstract String id();

	/** 仪式名（内联文本） */
	public abstract String name();

	/** 仪式描述：材料清单 + 效果说明（内联文本） */
	public abstract String desc();

	/** 材料配方（有序；匹配 = 每条皆满足；上限条目越多越优先命中） */
	public abstract List<RitualMaterial> materials();

	/** 是否允许以自身为中心激发（默认允许；特定落点仪式可覆写关闭） */
	public boolean selfChannel() { return true; }

	/**
	 * 仪式效果：调用时材料已被移除。center 可能在任意可到达格（弹道落点或施术者所在格）。
	 * 注意世界状态 null 守卫（Dungeon.level 可能是校验时的临时对象）。
	 */
	public abstract void act( Hero caster, int center );
}
