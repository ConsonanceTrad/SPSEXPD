/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 暴击系统 —— 照 Darkest Pixel Dungeon 实现：近战、法术、投掷共用同一入口。
 * 基础暴击率存放在 Hero.criticalChance（由特质/装备修改），暴击倍率固定 1.5x，
 * 再由 HardCrit 特质追加倍率、PureCrit 让暴击转为纯伤害、VampiricCrit 让暴击吸血。
 */

package pd.actors.hero;

import pd.actors.hero.perks.HardCrit;
import pd.actors.hero.perks.PureCrit;
import render.utils.math.Random;

public class Critical {

	/** 暴击倍率基数（暗黑同值） */
	public static final float BASE_MULTIPLIER = 1.5f;

	private static boolean lastCrit = false;

	private Critical() {
	}

	/** 当前暴击几率（0-1）——收口到统一属性层 HeroStats */
	public static float chance(Hero hero) {
		return HeroStats.critChance(hero);
	}

	/** 法术暴击率：在物理暴击率上叠加 ArcaneCrit（在法术流程里调用） */
	public static float magicalChance(Hero hero) {
		float c = chance(hero);
		if (hero.heroPerk != null) {
			pd.actors.hero.perks.ArcaneCrit ac = hero.heroPerk.get(pd.actors.hero.perks.ArcaneCrit.class);
			if (ac != null) c += ac.extraChance();
		}
		return Math.max(0f, Math.min(1f, c));
	}

	/** 上一次伤害判定是否暴击（供 PureCrit / VampiricCrit 等读取） */
	public static boolean lastWasCrit() {
		return lastCrit;
	}

	public static void clear() {
		lastCrit = false;
	}

	/** 掷一次暴击并放大伤害；未暴击原样返回 */
	public static int roll(Hero hero, int damage) {
		lastCrit = false;
		if (hero == null || damage <= 0) return damage;
		float c = chance(hero);
		if (c <= 0f || Random.Float() >= c) return damage;
		lastCrit = true;
		return scale(hero, damage);
	}

	/** 直接按暴击倍率放大（法术自带判定概率时用）——倍率收口到 HeroStats.critMultiplier */
	public static int scale(Hero hero, int damage) {
		float mult = HeroStats.critMultiplier(hero);
		return Math.max(1, Math.round(damage * mult));
	}

	public static float hardCritBonus(Hero hero) {
		if (hero == null || hero.heroPerk == null) return 0f;
		HardCrit hc = hero.heroPerk.get(HardCrit.class);
		return hc == null ? 0f : hc.critDamageBonus();
	}

	/** 是否已学会 PureCrit（暴击转为纯伤害） */
	public static boolean pureCrit(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(PureCrit.class);
	}
}
