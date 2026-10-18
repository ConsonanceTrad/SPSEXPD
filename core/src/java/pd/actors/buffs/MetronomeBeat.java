/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.unique.items.Metronome;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 节拍器——「踩拍」计数。
 *
 * <p>节拍不靠玩家指定：英雄_每行动一次_，系统就在他周围可抵达的格里随机标出下一拍
 * （陷阱格、虚空格也算，踩上去只是有后果）。下一步正好走到那一格就算踩中，连击 +1，否则清零。
 *
 * <p>连击满 {@link #BEATS_REQUIRED} 后不再立刻发奖，而是进入「等待结算」状态：
 * <b>接下来那一次行动的类型</b>决定奖励——移动给护盾，攻击（或其它非移动动作）给强化攻击。</p>
 */
public class MetronomeBeat extends Buff {

	private static final String BEATS = "beats";
	private static final String BEAT_CELL = "beatCell";
	private static final String AWAITING = "awaiting";

	/** 触发所需的连击数。 */
	public static final int BEATS_REQUIRED = 4;

	/** 奖励：护盾。 */
	public static final int MODE_SHIELD = 0;
	/** 奖励：强化攻击。 */
	public static final int MODE_POWER = 1;

	private int beats = 0;
	/** 系统标出的下一拍格（-1 = 还没标）。 */
	private int beatCell = -1;
	/** 连击已满、正等待「下一次行动」决定奖励类型。 */
	private boolean awaiting = false;

	{
		type = buffType.POSITIVE;
	}

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MetronomeBeat.class)
			.t("name", "节拍")
			.t("desc", "节拍器标出的「下一拍」。走到标出的那一格就算踩中，连击 +1；踩满后下一次行动会被强化且不消耗回合——移动给护盾，攻击则强化该次攻击。");
	}

	/** SPSEXPD: 让它真正出现在 buff 栏——平时显示连击图标与「x/4」进度，
	 *  攒满待结算时换成醒目的怒火图标并只显示「!」，提醒下一次行动会拿奖励。 */
	@Override
	public int icon() {
		return awaiting ? BuffIndicator.FURY : BuffIndicator.COMBO;
	}

	/** SPSEXPD: 这一格能不能被标成拍子——可抵达（含陷阱格与虚空格）且格上（暂时）没有生物。 */
	public static boolean designatable(int cell) {
		if (Dungeon.level == null) return false;
		if (!Dungeon.level.insideMap(cell)) return false;
		if (!(Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) return false;
		//SPSEXPD: 格子上站着生物（含英雄自己）则不作为候选
		return Actor.findChar(cell) == null;
	}

	public int beats() {
		return beats;
	}

	public int beatCell() {
		return beatCell;
	}

	public boolean awaiting() {
		return awaiting;
	}

	/** SPSEXPD: 在英雄周围可抵达的八格里随机定下一拍。 */
	public void autoBeat(Hero hero) {
		if (hero == null || Dungeon.level == null) {
			beatCell = -1;
			return;
		}
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (designatable(cell)) candidates.add(cell);
		}
		beatCell = candidates.isEmpty() ? hero.pos : candidates.get(Random.Int(candidates.size()));
	}

	/**
	 * SPSEXPD: 一次移动<b>结束时</b>调用（长距离移动是逐格 act，只在落点结算）：先结算上一拍是否踩中，
	 * 未攒满则生成下一拍。
	 */
	public void afterAction(Hero hero) {
		if (hero == null) return;
		if (beatCell >= 0 && hero.pos == beatCell) {
			beats++;
			if (beats >= BEATS_REQUIRED) {
				beats = 0;
				awaiting = true;
			}
		} else {
			beats = 0;
		}
		if (!awaiting) autoBeat(hero);
	}

	/** SPSEXPD: 取走这次待结算的奖励类型（并把状态清干净）。 */
	public int consumeAwaiting(int movedReward, int otherReward) {
		int mode = awaiting ? movedReward : otherReward;
		awaiting = false;
		return mode;
	}

	/** SPSEXPD: 结束本轮（清 awaiting 与连击）。是否重掷新节拍格由调用方决定——
	 *  只有「这一次行为是移动」才重掷，保证高亮格只在移动后变化。 */
	public void finishReward() {
		awaiting = false;
		beats = 0;
	}

	@Override
	public boolean act() {
		//SPSEXPD: 节拍只在英雄行动后推进（见 afterAction），buff 自己不做任何事。
		//这里只做两件兜底：①没装节拍器神器（只拿着魔音贝斯/闪电竖琴）时没人帮着定拍，由自己补上；
		//②身上已经没有任何节拍源（节拍器与魔音贝斯都不在了）时把自己摘掉，免得 buff 与高亮残留。
		if (target instanceof Hero) {
			Hero hero = (Hero) target;
			if (!Metronome.equipped(hero)) {
				detach();
				return true;
			}
			if (beatCell < 0 && !awaiting) autoBeat(hero);
		}
		spend(TICK);
		return true;
	}

	@Override
	public String iconTextDisplay() {
		return awaiting ? "!" : beats + "/" + BEATS_REQUIRED;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BEATS, beats);
		bundle.put(BEAT_CELL, beatCell);
		bundle.put(AWAITING, awaiting);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		beats = bundle.getInt(BEATS);
		beatCell = bundle.getInt(BEAT_CELL);
		awaiting = bundle.getBoolean(AWAITING);
	}
}
