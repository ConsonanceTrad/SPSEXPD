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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.buffs;

import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.SparkParticle;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 狂野符文的标记。
 *
 * <p>被标记的目标挨了**任何法杖**的一击就会引爆：引爆伤害 = 那一击的伤害 × 本 buff 的倍率
 * （倍率由施加标记的狂野符文等级决定，20% + 4%/级）。引爆还会向周围扩散，扩散伤害逐格衰减一半，
 * 且扩散伤害同样会引爆附近目标身上的标记。
 */
public class WildRuneMark extends Buff {

	static {
		InlineText.of(WildRuneMark.class)
				.t("desc", "狂野符文烙下的印子。下一次被法杖命中时会引爆，并向周围扩散。");
	}

	private static final String RATIO = "ratio";
	private static final String LEFT = "left";

	/** 引爆倍率（相对触发法杖那次伤害）。 */
	public float ratio = 0.2f;
	/** 剩余回合，归零后标记自然消失。 */
	private float left = 15f;
	/** 防止扩散引爆无限递归。 */
	private static int chainDepth = 0;
	private static final int MAX_CHAIN = 2;

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.MARK;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	public WildRuneMark setup(float ratio, float duration) {
		this.ratio = Math.max(0.1f, ratio);
		this.left = Math.max(this.left, duration);
		return this;
	}

	@Override
	public boolean act() {
		left -= TICK;
		if (left <= 0f) {
			detach();
		} else {
			spend(TICK);
		}
		return true;
	}

	/**
	 * SPSEXPD: 引爆——由 {@link pd.actors.Char#damage(int, Object)} 在法杖命中时调用。
	 *
	 * @param target 被标记的目标
	 * @param sourceDamage 触发这次引爆的那一击的伤害
	 */
	public static void detonate(Char target, int sourceDamage) {
		if (target == null || sourceDamage <= 0 || chainDepth >= MAX_CHAIN) return;
		WildRuneMark mark = target.buff(WildRuneMark.class);
		if (mark == null) return;

		float ratio = mark.ratio;
		chat_chain(target, sourceDamage, ratio, chainDepth);
	}

	private static void chat_chain(Char target, int sourceDamage, float ratio, int depth) {
		chainDepth = depth + 1;
		try {
			//标记本身先炸掉，避免同一次爆炸重复引炸
			WildRuneMark mark = target.buff(WildRuneMark.class);
			if (mark != null) mark.detach();

			int blast = Math.max(1, Math.round(sourceDamage * ratio));
			CellEmitter.get(target.pos).burst(SparkParticle.FACTORY, 10);
			target.damage(blast, null);

			//向周围扩散：逐格衰减一半，扩散伤害同样可以引炸
			int spread = Math.max(1, blast / 2);
			for (int offset : PathFinder.NEIGHBOURS8) {
				Char near = Actor.findChar(target.pos + offset);
				if (near == null || near == target || !near.isAlive()) continue;
				if (!near.isAlive()) continue;
				near.damage(spread, null);
				if (near.isAlive() && near.buff(WildRuneMark.class) != null) {
					chat_chain(near, spread, ratio, depth + 1);
				}
				spread = Math.max(1, spread / 2);
			}
		} finally {
			chainDepth = depth;
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(RATIO, ratio);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		ratio = bundle.getFloat(RATIO);
		left = bundle.getFloat(LEFT);
	}
}
