/*
 * 特质获得 / 升级提示。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

public final class PerkGain {

	static {
		InlineText.of(PerkGain.class)
				.t("gain", "获得特质：%s")
				.t("upgrade", "特质升级：%1$s（%2$d 级）")
				.t("upgrade_float", "%1$s %2$d 级");
	}

	private PerkGain() {
	}

	/** headless（自研单测 runner）环境下没有 Gdx.app，日志与屏幕提示都不可用 */
	private static boolean uiAvailable() {
		return com.badlogic.gdx.Gdx.app != null;
	}

	/** 升级提示文案（含等级）——UI 与无头校验共用 */
	public static String upgradeText(Perk perk) {
		return Messages.get(PerkGain.class, "upgrade", perk.title(), perk.level());
	}

	/** 获得特质时的反馈（日志 + 屏幕提示） */
	public static void announce(Hero hero, Perk perk) {
		if (perk == null || !uiAvailable()) return;
		GLog.p(Messages.get(PerkGain.class, "gain", perk.title()));
		if (hero != null && hero.sprite != null) {
			hero.sprite.showStatus(pd.sprites.CharSprite.POSITIVE, perk.title());
		}
	}

	/** 特质升级时的反馈（日志 + 屏幕提示，都带上新等级） */
	public static void announceUpgrade(Hero hero, Perk perk) {
		if (perk == null || !uiAvailable()) return;
		GLog.p(upgradeText(perk));
		if (hero != null && hero.sprite != null) {
			hero.sprite.showStatus(pd.sprites.CharSprite.POSITIVE,
					Messages.get(PerkGain.class, "upgrade_float", perk.title(), perk.level()));
		}
	}
}