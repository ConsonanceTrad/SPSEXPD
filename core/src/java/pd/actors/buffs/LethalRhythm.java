/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 致命节奏——接住飞回的回旋镖时获得，持续 {@link #TURNS} 回合。
 *
 * <p>期间「远程 / 射击伤害」每层 +{@link #BONUS_PER_STACK}（首层 7%）。重复接住会**刷新持续时间**并**叠加层数**。</p>
 */
public class LethalRhythm extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LethalRhythm.class)
			.t("name", "致命节奏")
			.t("desc", "接住回旋镖带来的节奏感。\n\n持续回合：%1$d\n当前层数：%2$d（远程/射击伤害 +%3$d%%）");
	}

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	/** SPSEXPD: 每次接住刷新的持续回合数。 */
	public static final float TURNS = 9f;
	/** SPSEXPD: 每层增幅（远程/射击伤害 +7%）。 */
	public static final float BONUS_PER_STACK = 0.07f;

	private static final String STACKS = "stacks";
	private static final String LEFT = "left";

	//SPSEXPD: 初始 0 层——首次接住时 gainStack() 把它加到 1（若初始为 1，第一次接住会错误地显示 2 层）
	private int stacks = 0;
	private float left = TURNS;

	/** SPSEXPD: 远程/射击伤害倍率（无该 buff 时为 1）。 */
	public static float multiplier(Char ch) {
		LethalRhythm b = ch == null ? null : ch.buff(LethalRhythm.class);
		return b == null ? 1f : 1f + BONUS_PER_STACK * b.stacks;
	}

	public int stacks() { return stacks; }

	public int turnsLeft() { return Math.max(0, (int)Math.ceil(left)); }

	/** SPSEXPD: 叠加一层并刷新持续时间（首次接住 → 1 层）。 */
	public void gainStack() {
		stacks = Math.max(0, stacks) + 1;
		left = TURNS;
	}

	@Override
	public boolean act() {
		left -= 1f;
		if (left <= 0f) {
			detach();
			return true;
		}
		spend(TICK);
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", turnsLeft(), stacks, Math.round(BONUS_PER_STACK * stacks * 100));
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STACKS, stacks);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stacks = Math.max(1, bundle.getInt(STACKS));
		left = bundle.getFloat(LEFT);
	}
}
