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

package pd.items.equipment.trinkets;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.messages.Messages;
import pd.utils.GLog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import pd.messages.InlineText;

public class DimensionalSundial extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DimensionalSundial.class)
			.t("name", "位面日晷")
			.t("warning", "你的日晷不再显影，这使你倍感不安。")
			.t("desc", "不知为何，这块小型手持式日晷能在地牢深处显影，甚至你不将其摆正也是如此。更奇怪的是，晷影的方位似乎与这个世界的太阳无关。当晷影不再显现时，日晷似乎会招致危险。")
			.t("typical_stats_desc", "这件饰物通常会在昼间(8:00~20:00)降低_%1$d%%_的敌人生成速率并在夜间(20:00~8:00)提升_%2$d%%_的敌人生成速率。")
			.t("stats_desc", "在当前等级下，这件饰物会在昼间(8:00~20:00)降低_%1$d%%_的敌人生成速率并在夜间(20:00~8:00)提升_%2$d%%_的敌人生成速率。");
	}




	{
		image = EquipmentNonEquipDict.SUNDIAL_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this,
					"stats_desc",
					(int)(100*(1f - enemySpawnMultiplierDaytime(buffedLvl()))),
					(int)(100*(enemySpawnMultiplierNighttime(buffedLvl())-1f)));
		} else {
			return Messages.get(this, "typical_stats_desc",
					(int)(100*(1f - enemySpawnMultiplierDaytime(0))),
					(int)(100*(enemySpawnMultiplierNighttime(0)-1f)));
		}
	}

	public static boolean sundialWarned = false;

	public static float spawnMultiplierAtCurrentTime(){
		if (trinketLevel(DimensionalSundial.class) != -1) {
			Calendar cal = GregorianCalendar.getInstance();
			if (cal.get(Calendar.HOUR_OF_DAY) >= 20 || cal.get(Calendar.HOUR_OF_DAY) <= 7) {
				if (!sundialWarned){
					GLog.w(Messages.get(DimensionalSundial.class, "warning"));
					sundialWarned = true;
				}
				return enemySpawnMultiplierNighttime();
			} else {
				return enemySpawnMultiplierDaytime();
			}
		} else {
			return 1f;
		}
	}

	public static float enemySpawnMultiplierDaytime(){
		return enemySpawnMultiplierDaytime(trinketLevel(DimensionalSundial.class));
	}

	public static float enemySpawnMultiplierDaytime( int level ){
		if (level == -1){
			return 1f;
		} else {
			return 0.95f - 0.05f*level;
		}
	}

	public static float enemySpawnMultiplierNighttime(){
		return enemySpawnMultiplierNighttime(trinketLevel(DimensionalSundial.class));
	}

	public static float enemySpawnMultiplierNighttime( int level ){
		if (level == -1){
			return 1f;
		} else {
			return 1.25f + 0.25f*level;
		}
	}
}
