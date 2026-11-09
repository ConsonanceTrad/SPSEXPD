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

/**
 * SPSEXPD: 法力值系统（仪式/施法拓展批次 A）。
 *
 * <p>法力值是独立于 {@code Hero.spp}（普通物品共用的旧 SP 能量）之外的施法资源：
 * 法术书类效果按「当前法力/上限」消耗它，并按 {@link #spellStrength(Hero)} 的法术强度加成结算效果量；
 * 魔杖（法器）仍走自己的充能模型，不消耗也不受法术强度加成（见 Wan 类注释约定）。</p>
 *
 * <p>数值存放于 {@code Hero.spsMana / spsManaBonusCap}（随 Hero 存档），
 * 回复走 {@code Hero.onTurnCharge()}（每消耗 1 时间结算一次，与 act 次数无关）。
 * HUD 上法力值占用血条下原来的 SP 点数格（见 StatusPane），SP 数字挪到右上角按钮组渲染。</p>
 */
public class Mana {

	/** 基础法力上限（不含等级与额外加成） */
	public static final int BASE_CAP = 10;
	/** 每个英雄等级增加的法力上限 */
	public static final int CAP_PER_LEVEL = 1;
	/** 每间隔这么多回合回复 1 点法力 */
	public static final int REGEN_TURNS = 6;

	/** 法力上限 = 基础 + (等级-1)×每级 + 升级外的额外加成 */
	public static int cap( Hero hero ) {
		if (hero == null) return 0;
		return BASE_CAP + Math.max(0, hero.lvl - 1) * CAP_PER_LEVEL + Math.max(0, hero.spsManaBonusCap);
	}

	/** 当前法力（截到 [0, cap]） */
	public static int cur( Hero hero ) {
		return hero == null ? 0 : Math.max(0, Math.min(hero.spsMana, cap(hero)));
	}

	/** 新档/旧档读档缺键时把法力补满 */
	public static void setFull( Hero hero ) {
		if (hero != null) hero.spsMana = cap( hero );
	}

	/** 增加法力（封顶，不超上限） */
	public static void add( Hero hero, int amount ) {
		if (hero == null || amount <= 0) return;
		hero.spsMana = Math.min(cap(hero), hero.spsMana + amount);
	}

	/** 当前法力是否够扣（不足则不扣） */
	public static boolean has( Hero hero, int amount ) {
		return cur(hero) >= amount;
	}

	/** 消耗法力；成功返回 true（法术书类调用后应继续施放），不足返回 false 且不动数值 */
	public static boolean spend( Hero hero, int amount ) {
		if (hero == null || amount <= 0) return true;
		if (!has(hero, amount)) return false;
		hero.spsMana -= amount;
		return true;
	}

	/** 每回合（真实时间）结算一次：每 REGEN_TURNS 回合回 1 点；由 Hero.onTurnCharge() 调用 */
	public static void regenTick( Hero hero ) {
		if (hero == null || cur(hero) >= cap(hero)) return;
		hero.spsManaRegenTurns++;
		if (hero.spsManaRegenTurns >= REGEN_TURNS) {
			hero.spsManaRegenTurns = 0;
			add( hero, 1 );
		}
	}

	/**
	 * SPSEXPD: 法术强度（法强）——只对「消耗法力值」的法术类效果生效。
	 * 与项目法强收口（commit 3494478af）同源：法强 = {@code Hero.magicSkill()}，
	 * 每 1 点法强 +10% 效果量，直接委托 {@code Wand.spellAmplifier()}，不另起炉灶；
	 * 法器（Wand）自身的伤害增幅沿用收口时的既定接入，本方法不改变法器行为。
	 */
	public static float spellStrength( Hero hero ) {
		return pd.items.equipment.wands.Wand.spellAmplifier();
	}
}
