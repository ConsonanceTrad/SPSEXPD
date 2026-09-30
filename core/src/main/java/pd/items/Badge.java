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

package pd.items;

/**
 * SPS: 徽章 —— 特殊穿戴物，占用独立的徽章槽（与 5 个通用饰品槽互不通用）。
 *
 * 本基底尚无具体徽章物品，先立基类与槽位；后续按设计填充子类即可复用
 * KindofMisc 的装备/卸下/存档逻辑。
 */
public abstract class Badge extends KindofMisc {
}
