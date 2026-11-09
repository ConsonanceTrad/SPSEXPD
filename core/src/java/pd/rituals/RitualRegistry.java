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

import pd.rituals.list.BarkskinOathRitual;
import pd.rituals.list.CrystalConvergenceRitual;
import pd.rituals.list.EmberSprayRitual;
import pd.rituals.list.HarvestBasisRitual;

/**
 * SPSEXPD: 首批仪式注册中心（批次C）——唯一的全集真源，由 Rituals 的静态块驱动加载。
 *
 * <p>新增仪式：在 {@code pd.rituals.list} 写 Ritual 子类（文案内联），然后在这里 new 一次。
 * 子类不得引用 Document / Rituals 之外的逻辑（防类初始化环），本类的加载只会被
 * Rituals.clinit 触发一次。</p>
 */
public class RitualRegistry {

	/** 由 Rituals 静态块调用，幂等 */
	public static void registerAll() {
		Rituals.register( new EmberSprayRitual() );
		Rituals.register( new CrystalConvergenceRitual() );
		Rituals.register( new BarkskinOathRitual() );
		Rituals.register( new HarvestBasisRitual() );
	}
}
