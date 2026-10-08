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

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.weapon.Weapon;

/**
 * SPSEXPD: 副手装备（副武器 secondWep / 副护甲 secondArmor）的全部规则集中在这里。
 *
 * 规则（用户裁决）：
 * - 副武器：主武器普通攻击后必定追加一次副武器的连携攻击（不消耗回合），
 *   但两把武器的力量需求都提高 50%（向上取整），且主、副武器的命中都受 0.85 倍负向修正。
 * - 副护甲：与主护甲一样提供护甲值并触发刻印，但两件护甲的力量需求都提高 50%，
 *   且副护甲每 1 阶位使攻击与移动速度降低 20%（累乘 0.8^tier）。
 * - 只能装备在副武器栏的装备（神木圆盾等 canEquipPrimary()==false）不参与双持规则，
 *   维持原版行为（仅提供副手防护）。
 */
public final class SecondaryEquip {

	/** 力量需求倍率：主副同时装备时双方都提高 50%。 */
	public static final float STR_MULT = 1.5f;
	/** 命中负向修正：双持时主、副武器都乘这个系数。 */
	public static final float HIT_MULT = 0.85f;
	/** 副护甲每 1 阶位对攻击/移动速度的倍率（累乘）。 */
	public static final float SPEED_PER_TIER = 0.8f;

	private SecondaryEquip(){}

	/** 双持真武器：主、副手都有武器，且副手武器本身可装备在主手（盾类不算）。 */
	public static boolean dualWeapons( Hero hero ){
		if (hero == null || hero.belongings == null) return false;
		KindOfWeapon primary = hero.belongings.weapon();
		KindOfWeapon second = hero.belongings.secondWep();
		//SPSEXPD: 投掷武器（含果实）不允许进副手栏，旧档里已装在副手的也不参与双持
		return primary != null && second != null
				&& second.canEquipPrimary()
				&& second.canEquipSecondary();
	}

	/** 双甲：主、副护甲栏都有护甲。 */
	public static boolean dualArmor( Hero hero ){
		if (hero == null || hero.belongings == null) return false;
		return hero.belongings.armor() != null && hero.belongings.secondArmor() != null;
	}

	/**
	 * 武器/护甲的力量需求：仅当英雄正处于对应的副手双持状态、
	 * 且 query 的就是当前装备槽里的那一件时才提高 50%（背包里未装备的同名物品不受影响）。
	 */
	public static int weaponSTRReq( KindOfWeapon weapon, int base ){
		Hero hero = Dungeon.hero;
		if (dualWeapons(hero)
				&& (hero.belongings.weapon() == weapon || hero.belongings.secondWep() == weapon)){
			return increasedStrengthReq(base);
		}
		return base;
	}

	public static int armorSTRReq( Armor armor, int base ){
		Hero hero = Dungeon.hero;
		if (dualArmor(hero)
				&& (hero.belongings.armor() == armor || hero.belongings.secondArmor() == armor)){
			return increasedStrengthReq(base);
		}
		return base;
	}

	/** +50%，向上取整（STRReq 为整数需求）。 */
	public static int increasedStrengthReq( int base ){
		return (int)Math.ceil( base * STR_MULT );
	}

	/** 主武器普通攻击的命中乘子：双持时主、副武器都受 0.85 倍负向修正。 */
	public static float hitMultiplier( Hero hero ){
		return dualWeapons(hero) ? HIT_MULT : 1f;
	}

	/**
	 * SPSEXPD: 装备前预演——若把武器装到目标栏位后会形成双持，
	 * 且其中任意一把武器的 +50% 力量需求超出英雄当前力量，则视为「难以掌控」
	 * （供装备前的二次确认提示使用）。
	 */
	public static boolean dualWieldTooHeavy( Hero hero, KindOfWeapon primary, KindOfWeapon second ){
		if (hero == null || primary == null || second == null) return false;
		if (!second.canEquipPrimary()) return false;
		return tooHeavy(hero, primary) || tooHeavy(hero, second);
	}

	private static boolean tooHeavy( Hero hero, KindOfWeapon weapon ){
		if (!(weapon instanceof Weapon)) return false;
		Weapon w = (Weapon)weapon;
		return increasedStrengthReq( w.STRReq( w.level() ) ) > hero.STR();
	}

	/**
	 * SPSEXPD: 装备前预演——若把护甲装到副护甲栏后会形成双甲，
	 * 且其中任意一件护甲的 +50% 力量需求超出英雄当前力量，则视为「难以驾驭」。
	 */
	public static boolean dualArmorTooHeavy( Hero hero, Armor primary, Armor second ){
		if (hero == null || primary == null || second == null) return false;
		return tooHeavy(hero, primary) || tooHeavy(hero, second);
	}

	private static boolean tooHeavy( Hero hero, Armor armor ){
		return increasedStrengthReq( armor.STRReq( armor.level() ) ) > hero.STR();
	}

	/** 副护甲带来的攻击/移动速度倍率：0.8^tier（无副甲时为 1）。 */
	public static float armorSpeedMultiplier( Armor secondArmor ){
		if (secondArmor == null) return 1f;
		return (float)Math.pow( SPEED_PER_TIER, secondArmor.tier );
	}

	public static float armorSpeedMultiplier( Hero hero ){
		if (hero == null || hero.belongings == null) return 1f;
		return armorSpeedMultiplier( hero.belongings.secondArmor() );
	}
}
