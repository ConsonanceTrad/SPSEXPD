/*
 * 汲血暴击 —— 暴击时按比例回复生命。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class VampiricCrit extends Perk {

	static {
		InlineText.of(VampiricCrit.class)
				.t("title", "汲血暴击")
				.t("desc", "暴击能从敌人身上汲取生命（每级约 %s%% 造成伤害）。");
	}

	public VampiricCrit() {
		super(5);
		addTags(Tag.Crit, Tag.Melee, Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.CRIT_VAMP;
	}

	public float ratio() {
		return level() * 0.15f + 0.1f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(ratio() * 100)));
	}

	/** 暴击命中后调用；恢复量不超过已损失生命 */
	public void onCrit(Hero hero, int damage) {
		int gain = Math.min(hero.HT - hero.HP, Math.round(ratio() * damage));
		if (gain > 0) if (!pd.actors.hero.perks.BloodShield.convert(hero, gain)) hero.HP += gain;
	}

	public static void tryProc(Hero hero, int damage) {
		if (hero == null || hero.heroPerk == null) return;
		VampiricCrit vc = hero.heroPerk.get(VampiricCrit.class);
		if (vc != null) vc.onCrit(hero, damage);
	}

	public static VampiricCrit of(Hero hero) {
		return hero == null || hero.heroPerk == null ? null : hero.heroPerk.get(VampiricCrit.class);
	}

	/** 便捷判断（Dungeon.hero 版本） */
	public static void tryProc(int damage) {
		tryProc(Dungeon.hero, damage);
	}
}
