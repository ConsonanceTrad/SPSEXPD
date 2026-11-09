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

package pd.rituals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 仪式注册表与激发单点（仪式/施法拓展批次 B）。
 *
 * <p>激发规则：以锚点（弹道落点或施术者自身格）为中心扫描 5×5 内的普通地面堆
 * （{@code Heap.Type.HEAP}），统计各物品类的数量；命中某个仪式配方（每条材料皆足量，
 * 材料条目多者优先、总需求数量大者次之）即按配方数量消耗材料并作一次性结算；
 * 布阵石只负责材料的快速布局（批次 C），仪式本身始终由激发结算。</p>
 */
public class Rituals {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Rituals.class)
			.t("channel", "仪式「%s」被激发了！");
	}

	/** 仪式半径（5×5 → 锚点居中，半径 2；材料与配方均只认这个范围） */
	public static final int RADIUS = 2;

	/** 注册表：仪式 id（= 讲解页 id）→ 仪式；HashMap 即可（匹配顺序由候选序列保证） */
	private static final Map<String, Ritual> REGISTRY = new HashMap<>();

	public static void register( Ritual ritual ) {
		if (ritual == null || ritual.id() == null || REGISTRY.containsKey( ritual.id() )) return;
		REGISTRY.put( ritual.id(), ritual );
		//SPSEXPD: 仪式讲解册同步登记页（页 id = 仪式 id；debug 档直接已读）
		pd.journal.Document.RITUAL_GUIDE.registerPage( ritual.id() );
	}

	public static Ritual byId( String id ) {
		return id == null ? null : REGISTRY.get( id );
	}

	/** 讲解页 id → 仪式（讲解页 id 直接取仪式 id） */
	public static Ritual byPage( String page ) {
		return byId( page );
	}

	public static Collection<Ritual> all() {
		return Collections.unmodifiableCollection( REGISTRY.values() );
	}

	/** SPSEXPD: 5×5 锚点偏移（按 level 宽度生成，含锚点自身 0 偏移，行序固定） */
	public static int[] radiusOffsets( Level level ) {
		List<Integer> offs = new ArrayList<>();
		for (int dy = -RADIUS; dy <= RADIUS; dy++) {
			for (int dx = -RADIUS; dx <= RADIUS; dx++) {
				offs.add( dy * level.width() + dx );
			}
		}
		int[] arr = new int[offs.size()];
		for (int i = 0; i < arr.length; i++) arr[i] = offs.get(i);
		return arr;
	}

	/**
	 * SPSEXPD: 统计 5×5 内普通地面堆的各物品类数量（按堆内数量合并；忽略非 HEAP 堆）。
	 */
	public static Map<Class<? extends Item>, Integer> collectAt( Level level, int center ) {
		Map<Class<? extends Item>, Integer> counts = new HashMap<>();
		if (level == null) return counts;
		int length = level.length();
		for (int off : radiusOffsets( level )) {
			int cell = center + off;
			if (cell < 0 || cell >= length) continue;
			Heap heap = level.heaps.get( cell );
			if (heap == null || heap.type != Heap.Type.HEAP) continue;
			for (Item item : heap.items) {
				if (item == null) continue;
				addCount( counts, item.getClass(), item.quantity() );
			}
		}
		return counts;
	}

	private static void addCount( Map<Class<? extends Item>, Integer> counts, Class<? extends Item> cls, int qty ) {
		Integer cur = counts.get(cls);
		counts.put( cls, (cur == null ? 0 : cur) + Math.max(0, qty) );
	}

	/**
	 * SPSEXPD: 纯匹配（无头可测，不碰 Dungeon/Level）——给定材料量与候选序列，
	 * 返回命中的仪式；无命中返回 null。
	 * 规则：所有材料条目都足量即候选；条目数多者优先，并列时总需求数量大者优先，
	 * 候选顺序（注册表顺序）作为最终并列裁决。
	 */
	public static Ritual pickMatch( Map<Class<? extends Item>, Integer> counts, Iterable<Ritual> candidates ) {
		Ritual best = null;
		int bestEntries = -1;
		int bestQty = -1;
		for (Ritual ritual : candidates) {
			if (ritual == null) continue;
			int entries = 0;
			int totalQty = 0;
			boolean covered = true;
			for (RitualMaterial m : ritual.materials()) {
				Integer have = counts.get( m.itemClass );
				if (have == null || have < m.quantity) {
					covered = false;
					break;
				}
				entries++;
				totalQty += m.quantity;
			}
			if (!covered) continue;
			if (entries > bestEntries || (entries == bestEntries && totalQty > bestQty)) {
				best = ritual;
				bestEntries = entries;
				bestQty = totalQty;
			}
		}
		return best;
	}

	/** 在锚点扫描并匹配仪式（不改任何状态） */
	public static Ritual matchAt( Level level, int center ) {
		return pickMatch( collectAt( level, center ), REGISTRY.values() );
	}

	/**
	 * SPSEXPD: 按配方消耗材料——逐条从 5×5 范围内的普通堆里扣足数量
	 * （扣完的物品从堆里移除，堆清空即销毁）。
	 * @return false 表示材料实际不足（可能已被别的逻辑取走等），此时不做任何改动
	 */
	public static boolean consumeAt( Level level, int center, Ritual ritual ) {
		if (level == null || ritual == null) return false;
		//重扫一遍拿最新计数确认足量（避免 pickMatch 与消耗之间材料被取走）
		Map<Class<? extends Item>, Integer> counts = collectAt( level, center );
		for (RitualMaterial m : ritual.materials()) {
			Integer have = counts.get( m.itemClass );
			if (have == null || have < m.quantity) return false;
		}
		for (RitualMaterial m : ritual.materials()) {
			int need = m.quantity;
			for (int off : radiusOffsets( level )) {
				if (need <= 0) break;
				int cell = center + off;
				if (cell < 0 || cell >= level.length()) continue;
				Heap heap = level.heaps.get( cell );
				if (heap == null || heap.type != Heap.Type.HEAP) continue;
				while (need > 0) {
					Item found = null;
					for (Item item : heap.items) {
						if (item != null && m.itemClass.isInstance( item )) {
							found = item;
							break;
						}
					}
					if (found == null) break;
					int take = Math.min( need, found.quantity() );
					need -= take;
					if (take >= found.quantity()) {
						heap.remove( found );
					} else {
						found.quantity( found.quantity() - take );
						if (heap.sprite != null) {
							heap.sprite.view( heap ).place( cell );
						}
					}
				}
			}
			if (need > 0) return false; //理论上不可达（上一步已确认足量）
		}
		return true;
	}

	/**
	 * SPSEXPD: 激发单点——匹配 → 消耗 → 结算效果。成功返回触发的仪式，否则 null。
	 * 弹道落点与自我中心都走这里（自我中心传 caster.pos）。
	 */
	public static Ritual channel( Hero caster, int center ) {
		Level level = Dungeon.level;
		if (level == null || caster == null) return null;
		if (center < 0 || center >= level.length()) return null;

		Ritual ritual = matchAt( level, center );
		if (ritual == null) return null;
		if (!consumeAt( level, center, ritual )) return null;

		//SPSEXPD: 通用反馈——星爆粒子 + evoke 音效 + 激发日志（效果本体由 ritual.act 结算）
		if (level.heroFOV != null && center >= 0 && level.heroFOV.length > center && level.heroFOV[center]) {
			CellEmitter.center( center ).burst( Speck.factory( Speck.STAR ), 14 );
			Sample.INSTANCE.play( Assets.Sounds.EVOKE );
			GLog.i( Messages.get( Rituals.class, "channel", ritual.name() ) );
		}
		ritual.act( caster, center );
		return ritual;
	}
}
