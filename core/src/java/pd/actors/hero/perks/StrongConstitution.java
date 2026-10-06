/*
 * 强健体魄 —— 升级时获得更多生命上限。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class StrongConstitution extends Perk {

	static {
		InlineText.of(StrongConstitution.class)
				.t("title", "强健体魄")
				.t("desc", "每次升级额外获得 %d 点生命上限。");
	}

	public StrongConstitution() {
		super(5);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.STRONG_COSTITUION;
	}

	public int extraHT() {
		return level() * 2;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", extraHT());
	}

	/** 由 HeroClass 升级流程调用 */
	public void onHeroUpgrade(Hero hero) {
		int extra = extraHT();
		hero.HT += extra;
		if (!pd.actors.hero.perks.BloodShield.convert(hero, extra)) hero.HP += extra;
	}

	public static void apply(Hero hero) {
		if (hero == null || hero.heroPerk == null) return;
		StrongConstitution sc = hero.heroPerk.get(StrongConstitution.class);
		if (sc != null) sc.onHeroUpgrade(hero);
	}
}
