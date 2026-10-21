/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.ui.BuffIndicator;
import render.noosa.Image;

/**
 * SPSEXPD: 经验增益（docs/alchemy-rework.md §8）。
 *
 * <p>经验药剂从「一次性给一大截经验」改为「一段时间内经验获取 +50%」，
 * 让它在战斗中更有意义，而不是攒到最后当升级道具。
 */
public class ExpBoost extends FlavourBuff {

	public static final float DURATION = 100f;
	public static final float FACTOR = 1.5f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ExpBoost.class)
			.t("name", "经验增益")
			.t("desc", "药力让你的头脑格外清明，短时间内从战斗中吸收的经验更多（+50%）。\n\n剩余时长：%s回合");
	}

	{
		type = buffType.POSITIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.4f, 1f, 0.4f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	/** 该角色的经验倍率（没有 buff 时为 1）。 */
	public static float factor(Hero hero) {
		if (hero == null || hero.buff(ExpBoost.class) == null) return 1f;
		return FACTOR;
	}
}
