/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.levels;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Dewcharge;
import pd.items.Dewdrop;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

import java.util.ArrayList;

/**
 * SPS-PD 原创的「露珠祝福」机制：进入主地牢普通层时直接发放露珠爆破
 * （次数 = 旧版布局房间数 / 4），以及该层露珠数量的统计口径。
 *
 * 从 Level 中独立出来：这一族只读写英雄状态与关卡统计，不参与覆写。
 * 相关的 markSpsOriginalMobs 仍留在 Level —— 它被 SpsRegularLevel 覆写，是关卡的钩子。
 */
public final class SpsDew {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(SpsDew.class)
			.t("order", "露珠女神赐予了你祝福，你获得了露珠爆炸。");
	}

	private SpsDew() { }

	/** SPSEXPD: 每 4 个房间（旧版布局房间数，含走廊格）换算 1 次露珠爆破。 */
	public static final int ROOMS_PER_CHARGE = 4;

	/**
	 * SPSEXPD: 进入新楼层时直接发放露珠爆破（露珠神像已移除，不再需要踩踏）。
	 *
	 * <p>次数 = 旧版布局房间数 / {@link #ROOMS_PER_CHARGE}（至少 1 次），累加到现有次数上；
	 * 同一层只发一次，读档回到该层或上下楼折返都不会重复发放。</p>
	 */
	public static void grantDewCharge( Level level ) {
		if (Dungeon.hero == null || level == null) return;
		//与旧的露珠神像生成层保持一致：主地牢 1~38 层、非首领层、非商店层、普通层
		if (Dungeon.branch != 0 || Dungeon.depth < 1 || Dungeon.depth >= 39
				|| Dungeon.bossLevel() || Dungeon.shopOnLevel()
				|| !(level instanceof SpsRegularLevel)) {
			return;
		}

		int floorKey = Dungeon.depth + 100 * Dungeon.branch;
		if (Statistics.spsDewChargeFloor == floorKey) return;
		Statistics.spsDewChargeFloor = floorKey;

		int rooms = ((SpsRegularLevel) level).legacyRoomCount();
		Dewcharge.chargeCount( Dungeon.hero, Math.max(1, rooms / ROOMS_PER_CHARGE) );
		GLog.h( Messages.get(SpsDew.class, "order") );
	}

	public static boolean isClearable( Level level ) {
		return Dungeon.branch == 0 && Dungeon.depth > 1 && Dungeon.depth < 39
				&& !Dungeon.bossLevel() && !(level instanceof BetweenLevel);
	}

	public static boolean hasDew( Level level ) {
		for (Heap heap : level.heaps.valueList()) {
			for (Item item : heap.items) {
				if (item instanceof Dewdrop) return true;
			}
		}
		return false;
	}

	//SPSEXPD: 清层限时已取消，原有的 par() 回合基准一并移除
}
