/*
 * 即兴投掷 —— 来自破碎 IMPROVISED_PROJECTILES。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ImprovisedProjectiles extends Perk {

	static {
		InlineText.of(ImprovisedProjectiles.class)
				.t("title", "即兴投掷")
				.t("desc", "向敌人扔出非投掷武器的物品时会使其致盲 %d 回合（有冷却）。");
	}

	public ImprovisedProjectiles() {
		super(2);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.IMPROVISED_PROJECTILES;
	}

	public float blindTurns() {
		return 2f + level();
	}

	public float cooldown() {
		return 50f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(blindTurns()));
	}
}
