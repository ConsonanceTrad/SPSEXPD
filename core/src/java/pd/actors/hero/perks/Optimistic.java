/*
 * 乐观 —— 裁决改写：以 25%~50% 的法术防御抵抗纯粹伤害。
 * （原版依赖压力/理智体系，本项目不引入压力系统，故按裁决改为纯粹伤害抵抗。）
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Optimistic extends Perk {

	static {
		InlineText.of(Optimistic.class)
				.t("title", "乐观")
				.t("desc", "受到的纯粹伤害有 %d%% 由法术防御抵挡（等级越高比例越高）。");
	}

	public Optimistic() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.OPTIMISTIC;
	}

	/** 抵挡比例：1 级 25%，2 级 50% */
	public float resistRatio() {
		return level() >= 2 ? 0.5f : 0.25f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(resistRatio() * 100));
	}

	/** 把纯粹伤害按比例削减为可被法术防御抵挡的量 */
	public int resist(int pureDamage) {
		return Math.max(0, Math.round(pureDamage * (1f - resistRatio())));
	}

	public static int resistOf(pd.actors.hero.Hero hero, int pureDamage) {
		if (hero == null || hero.heroPerk == null) return pureDamage;
		Optimistic o = hero.heroPerk.get(Optimistic.class);
		return o == null ? pureDamage : o.resist(pureDamage);
	}
}
