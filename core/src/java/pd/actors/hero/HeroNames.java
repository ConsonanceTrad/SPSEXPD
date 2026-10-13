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

package pd.actors.hero;

import render.utils.math.Random;

/**
 * SPSEXPD: 新建角色时随机分配的自定义名池（中/外音译风格）。
 * 名单与职业/主题无关，可直接增删。
 */
public class HeroNames {

	private static final String[] NAMES = {
			"齐尔诺夫", "薇拉", "迪米特里", "叶莲娜", "伊万", "娜塔莎",
			"阿列克谢", "卡捷琳娜", "谢尔盖", "奥尔加", "米哈伊尔", "塔季扬娜",
			"阿拉贡", "瑟琳娜", "德里克", "伊莎贝尔", "加雷斯", "莉莉安娜",
			"罗兰", "薇奥拉", "阿尔文", "莫甘娜", "赫尔曼", "格温多琳",
			"洛基", "弗蕾亚", "卡西乌斯", "奥利维亚", "塞德里克", "罗莎琳德",
			"维克托", "阿纳斯塔西娅", "诺瓦", "米拉", "凯尔", "艾琳",
			"泽维尔", "西尔维亚", "莱昂", "伯莎"
	};

	public static String random(){
		return NAMES[Random.Int( NAMES.length )];
	}
}
