/*
 * 血能护盾 —— 自然恢复之外的一切回血都不再治疗你，而是按 70% 转化为奥术护盾（会随时间流逝）。
 */

package pd.actors.hero.perks;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BloodShield extends Perk {

	/** 回血 -> 护盾的倍率 */
	public static final float RATIO = 0.7f;

	static {
		InlineText.of(BloodShield.class)
			.t("title", "血能护盾")
			.t("desc", "你的生命只能靠自然恢复回复：其它一切回血手段都不再治疗你，"
					+ "而是把回复量的 70%% 转化为奥术护盾（护盾会随时间自行流逝）。");
	}

	public BloodShield() {
		super(1);
	}

	/** 该角色是否受此特质影响 */
	public static boolean active(Char ch) {
		if (!(ch instanceof Hero)) return false;
		Hero hero = (Hero) ch;
		return hero.heroPerk != null && hero.heroPerk.has(BloodShield.class);
	}

	/** 把本次本该回复的血量转为奥术护盾；返回 true 表示已被接管（不再回血） */
	public static boolean convert(Char ch, int amount) {
		if (amount <= 0 || !active(ch)) return false;
		pd.actors.buffs.Buff.affect(ch, pd.actors.buffs.Barrier.class)
				.incShield(Math.round(amount * RATIO));
		return true;
	}
}