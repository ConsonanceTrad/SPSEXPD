/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.ui.BuffIndicator;

/**
 * SPSEXPD: 露珠爆破。
 *
 * 玩家侧为「次数制」：每击杀一个敌人消耗 1 次，在它周围爆出露珠；没有时长限制。
 * 怪物侧（本层初始怪物）为永久：{@link #FOREVER}，死亡时同样爆一次、不消耗次数。
 */
public class Dewcharge extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Dewcharge.class)
			.t("name", "露珠爆炸")
			.t("desc", "击杀敌人时会在它周围爆出露珠。\n\n剩余次数：%s");
	}

	/** 永久（怪物用）：不消耗次数 */
	public static final int FOREVER = -1;

	/** 换算比例：原时长 20 回合 = 1 次 */
	public static final float TURNS_PER_CHARGE = 20f;

	/** 剩余可触发次数；FOREVER 表示永久 */
	public int charges = 1;

	{
		type = buffType.POSITIVE;
	}

	/** 玩家侧入口：按时长换算成次数并累加（每 20 回合 1 次，至少 1 次） */
	public static void charge(Char ch, float turns) {
		if (ch == null || turns <= 0f) return;

		chargeCount(ch, Math.max(1, Math.round(turns / TURNS_PER_CHARGE)));
	}

	/** 玩家侧入口：直接给予固定次数（累加） */
	public static void chargeCount(Char ch, int count) {
		if (ch == null || count <= 0) return;

		Dewcharge dc = ch.buff(Dewcharge.class);
		if (dc == null) {
			dc = Buff.affect(ch, Dewcharge.class);
			dc.charges = count;
		} else if (dc.charges != FOREVER) {
			dc.charges += count;
		}
	}

	/** 触发一次爆露珠；返回是否应当爆。玩家侧每次扣 1，扣完即消失 */
	public boolean consume() {
		if (charges == FOREVER) return true;
		if (charges <= 0) return false;

		charges--;
		if (charges <= 0) detach();
		return true;
	}

	/** 次数制：不随时间过期，只等 consume() 扣完 */
	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}

	public boolean isDewing() {
		return charges != 0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", charges == FOREVER ? "∞" : Integer.toString(charges));
	}

	@Override
	public String iconTextDisplay() {
		return charges == FOREVER ? "∞" : Integer.toString(charges);
	}

	@Override
	public int icon() {
		return BuffIndicator.BLESS;
	}
}