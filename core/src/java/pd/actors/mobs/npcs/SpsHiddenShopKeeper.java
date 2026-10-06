/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */

package pd.actors.mobs.npcs;

import pd.levels.rooms.special.SpsHiddenShopRoom;

/**
 * SPSXPD: 隐藏商店的店主。
 *
 * 隐藏商店原本从 TownNpc 的三个彩蛋 Spec（ICE13 / HONEY_POOOOT / SAID_BY_SUN）里随机取店主，
 * 而这几个 Spec 在 TownNpc.applyLegacyState 里被设为 WANDERING，店主会在店里来回走动。
 *
 * 这里做两层保证：
 *  1. configure 之后强制回到 PASSIVE（存档恢复时 restoreFromBundle 也会走 configure，所以必须在这里设，而不是外部赋值）
 *  2. 覆写 act()：店主只推进时间，不执行任何移动逻辑；同时负责货架见底时补货
 */
public class SpsHiddenShopKeeper extends TownNpc {

	/** SPSXPD: 所属秘密商店；货架见底时由本店主补货。不参与序列化。 */
	public SpsHiddenShopRoom shopRoom = null;

	@Override
	public SpsHiddenShopKeeper configure(Spec spec) {
		super.configure(spec);
		state = PASSIVE;
		return this;
	}

	/** 守摊：不走动；顺便检查货架是否需要补货 */
	@Override
	protected boolean act() {
		if (shopRoom != null) shopRoom.checkRestock();
		spend(TICK);
		return true;
	}
}