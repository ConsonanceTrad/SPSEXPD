/*
 * 飞速投掷 —— 来自破碎 PROJECTILE_MOMENTUM。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ProjectileMomentum extends Perk {

	static {
		InlineText.of(ProjectileMomentum.class)
				.t("title", "飞速投掷")
				.t("desc", "处于逸动状态时，你的投掷武器获得额外 %d%% 精准与 %s%% 伤害。");
	}

	public ProjectileMomentum() {
		super(2);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.PROJECTILE_MOMENTUM;
	}

	public float accuracyBonus() {
		return 0.5f;
	}

	public float damageBonus() {
		return 0.15f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc",
				Math.round(accuracyBonus() * 100), num(Math.round(damageBonus() * 100)));
	}
}
