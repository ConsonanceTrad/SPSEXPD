/*
 * 坚忍肠胃 —— 条件型特质示例：解除饥饿 10 次后自动获得，饥饿上限 +200。
 *
 * 说明：这是「满足条件即获得」接口的参考实现，后续可按同样方式继续添加。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.TraitCounters;
import pd.messages.InlineText;

public class HardenedStomach extends Perk {

	static {
		InlineText.of(HardenedStomach.class)
				.t("title", "坚忍肠胃")
				.t("desc", "累计解除饥饿 %d 次后自动习得，饥饿值上限增加 %d。");
	}

	/** 触发所需的解除饥饿次数 */
	public static final int REQUIRED = 10;

	public HardenedStomach() {
		super(1, 0);
	}

	@Override
	public int image() {
		return PerkImageSheet.HARDENED_STOMACH;
	}

	public int hungerCapBonus() {
		return 200;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", REQUIRED, hungerCapBonus());
	}

	@Override
	public boolean conditionMet(Hero hero) {
		return hero != null && hero.traitCounters != null
				&& hero.traitCounters.get(TraitCounters.HUNGER_RELIEVED) >= REQUIRED;
	}

	/** 供 Hunger 查询饥饿上限加成 */
	public static int capBonus(Hero hero) {
		if (hero == null || hero.heroPerk == null) return 0;
		HardenedStomach p = hero.heroPerk.get(HardenedStomach.class);
		return p == null ? 0 : p.hungerCapBonus();
	}
}
