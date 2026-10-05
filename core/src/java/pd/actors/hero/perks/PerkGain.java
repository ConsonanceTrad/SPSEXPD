/*
 * 特质获得提示。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

public final class PerkGain {

	static {
		InlineText.of(PerkGain.class)
				.t("gain", "获得特质：%s");
	}

	private PerkGain() {
	}

	/** 获得特质时的反馈（日志 + 屏幕提示） */
	public static void announce(Hero hero, Perk perk) {
		if (perk == null) return;
		GLog.p(Messages.get(PerkGain.class, "gain", perk.title()));
		if (hero != null && hero.sprite != null) {
			hero.sprite.showStatus(pd.sprites.CharSprite.POSITIVE, perk.title());
		}
	}
}
