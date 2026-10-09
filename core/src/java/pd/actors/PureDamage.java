/* Special Surprise Pixel Dungeon, GPLv3 or later. */

package pd.actors;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroStats;
import pd.actors.hero.perks.Optimistic;
import pd.sprites.CharSprite;

/**
 * SPSEXPD: 纯粹伤害统一入口（暗黑式）—— 无视一切防御直接扣血。
 *
 * <p>裁决口径：纯粹伤害绕过护甲 dr、抗性/免疫、护盾（ShieldBuff）等一切减伤与吸收，
 * 只保留飘字与死亡流程。此前纯粹伤害散落为 {@code HP -=} 直减、dr=0、绕过 attack 调
 * damage() 等互不兼容的写法，本类把「对敌造成纯粹伤害」收口到一处：</p>
 * <ul>
 *   <li>输出端：英雄的纯粹伤害加成（{@link HeroStats#pureDamageBonus}）；</li>
 *   <li>受击端：「乐观」特质按比例抵挡（{@link Optimistic#resistOf}）；</li>
 *   <li>致死走 {@code target.die(src)} 正常死亡流程。</li>
 * </ul>
 */
public class PureDamage {

	private PureDamage() {
	}

	/** 完整纯粹伤害：扣血 + 飘字 + 致死走正常死亡流程 */
	public static void deal(Char target, int dmg, Object src) {
		apply(target, dmg, src);
		if (target != null && !target.isAlive()) {
			target.die(src);
		}
	}

	/**
	 * 只扣血与飘字、不处理死亡（调用方需要自行安排死亡结算顺序时用，如啃咬的击杀奖励）。
	 * 返回实际造成的伤害（0 表示未命中/被完全抵挡）。
	 */
	public static int apply(Char target, int dmg, Object src) {
		if (target == null || !target.isAlive() || dmg <= 0) return 0;

		//输出端：英雄的纯粹伤害加成
		if (src instanceof Hero) dmg = HeroStats.applyPure((Hero) src, dmg);
		//受击端：「乐观」特质按比例抵挡
		if (target instanceof Hero) dmg = Optimistic.resistOf((Hero) target, dmg);
		if (dmg <= 0) return 0;

		target.HP -= dmg;
		if (target.HP < 0) target.HP = 0;
		if (target.sprite != null) {
			target.sprite.showStatus(CharSprite.NEGATIVE, Integer.toString(dmg));
		}
		return dmg;
	}
}
