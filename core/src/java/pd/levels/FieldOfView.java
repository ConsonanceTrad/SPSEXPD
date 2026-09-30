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

package pd.levels;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SmokeScreen;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicalSight;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.RevealedArea;
import pd.actors.buffs.Shadows;
import pd.actors.buffs.TentSleep;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.actors.hero.abilities.huntress.SpiritHawk;
import pd.actors.hero.spells.DivineSense;
import pd.actors.mobs.GnollGeomancer;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.actors.mobs.YogFist;
import pd.actors.mobs.npcs.Blacksmith;
import pd.items.Heap;
import pd.items.artifacts.TalismanOfForesight;
import pd.items.trinkets.EyeOfNewt;
import pd.items.wands.WandOfRegrowth;
import pd.items.wands.WandOfWarding;
import pd.mechanics.ShadowCaster;
import pd.mechanics.pathfind.PathFinder;
import render.utils.data.BArray;

/**
 * 关卡的视野计算：光照明暗（ShadowCaster 投射）、心智视觉（MindVision/Talent 感知范围）、
 * 各类感知 buff（Awareness、TalismanOfForesight、RevealedArea）以及队友视野的合并。
 *
 * 从 Level 中独立出来：整块是纯算法，只读关卡的地形与在场者、写传入的 fieldOfView，
 * 不参与任何子类覆写，因此不必留在关卡基类里。
 */
public final class FieldOfView {

	//心智视觉的累积缓冲：按关卡长度复用，避免每帧分配
	private static boolean[] heroMindFov;

	//遮挡位的可改副本：某些感知者能看穿高草或烟雾，需要先拷贝再改
	private static boolean[] modifiableBlocking;

	private FieldOfView() { }

	public static void update( Level level, Char c, boolean[] fieldOfView ) {

		int cx = c.pos % level.width();
		int cy = c.pos / level.width();

		boolean sighted = c.buff( Blindness.class ) == null && c.buff( Shadows.class ) == null
						&& c.buff(TentSleep.class) == null
						&& c.isAlive();
		if (sighted) {
			boolean[] blocking = null;

			if (modifiableBlocking == null || modifiableBlocking.length != level.losBlocking.length){
				modifiableBlocking = new boolean[level.losBlocking.length];
			}

			//grass is see-through by some specific entities, but not during the fungi quest
			if (!(level instanceof  MiningLevel) || Blacksmith.Quest.Type() != Blacksmith.Quest.FUNGI){
				if ((c instanceof Hero && ((Hero) c).subClass == HeroSubClass.WARDEN)
						|| c instanceof YogFist.SoiledFist || c instanceof GnollGeomancer) {
					if (blocking == null) {
						System.arraycopy(level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
						blocking = modifiableBlocking;
					}
					for (int i = 0; i < blocking.length; i++) {
						if (blocking[i] && (level.map[i] == Terrain.HIGH_GRASS || level.map[i] == Terrain.FURROWED_GRASS)) {
							blocking[i] = false;
						}
					}
				}
			}

			//allies and specific enemies can see through shrouding fog
			if ((c.alignment != Char.Alignment.ALLY && !(c instanceof GnollGeomancer))
					&& level.blobs.containsKey(SmokeScreen.class)
					&& level.blobs.get(SmokeScreen.class).volume > 0) {
				if (blocking == null) {
					System.arraycopy(level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
					blocking = modifiableBlocking;
				}
				Blob s = level.blobs.get(SmokeScreen.class);
				for (int i = 0; i < blocking.length; i++){
					if (!blocking[i] && s.cur[i] > 0){
						blocking[i] = true;
					}
				}
			}

			if (blocking == null){
				blocking = level.losBlocking;
			}

			float viewDist = c.viewDistance;
			if (c instanceof Hero){
				viewDist *= 1f + 0.25f*((Hero) c).pointsInTalent(Talent.FARSIGHT);
				viewDist *= EyeOfNewt.visionRangeMultiplier();
			}

			ShadowCaster.castShadow( cx, cy, level.width(), fieldOfView, blocking, Math.round(viewDist) );
		} else {
			BArray.setFalse(fieldOfView);
		}

		int sense = 1;
		//Currently only the hero can get mind vision
		if (c.isAlive() && c == Dungeon.hero) {
			for (Buff b : c.buffs( MindVision.class )) {
				sense = Math.max( ((MindVision)b).distance, sense );
			}
			if (c.buff(MagicalSight.class) != null){
				sense = Math.max( MagicalSight.DISTANCE, sense );
			}
		}

		//uses rounding
		if (!sighted || sense > 1) {

			int[][] rounding = ShadowCaster.rounding;

			int left, right;
			int pos;
			for (int y = Math.max(0, cy - sense); y <= Math.min(level.height()-1, cy + sense); y++) {
				if (rounding[sense][Math.abs(cy - y)] < Math.abs(cy - y)) {
					left = cx - rounding[sense][Math.abs(cy - y)];
				} else {
					left = sense;
					while (rounding[sense][left] < rounding[sense][Math.abs(cy - y)]){
						left--;
					}
					left = cx - left;
				}
				right = Math.min(level.width()-1, cx + cx - left);
				left = Math.max(0, left);
				pos = left + y * level.width();
				System.arraycopy(level.discoverable, pos, fieldOfView, pos, right - left + 1);
			}
		}

		if (c instanceof SpiritHawk.HawkAlly && Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE) >= 3){
			int range = 1+(Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE)-2);
			for (Mob mob : level.mobs()) {
				int p = mob.pos;
				if (!fieldOfView[p] && level.distance(c.pos, p) <= range) {
					for (int i : PathFinder.NEIGHBOURS9) {
						fieldOfView[mob.pos + i] = true;
					}
				}
			}
		}

		//Currently only the hero can get mind vision or awareness
		if (c.isAlive() && c == Dungeon.hero) {

			if (heroMindFov == null || heroMindFov.length != level.length()){
				heroMindFov = new boolean[level.length()];
			} else {
				BArray.setFalse(heroMindFov);
			}

			Dungeon.hero.mindVisionEnemies.clear();

			int mindVisRange = 0;
			if (c.buff(MindVision.class) != null) {
				mindVisRange = Integer.MAX_VALUE;
			} else {
				if (((Hero) c).hasTalent(Talent.HEIGHTENED_SENSES)) {
					mindVisRange = 1 + ((Hero) c).pointsInTalent(Talent.HEIGHTENED_SENSES);
				}
				if (c.buff(DivineSense.DivineSenseTracker.class) != null) {
					if (((Hero) c).heroClass == HeroClass.CLERIC) {
						mindVisRange = 4 + 4 * ((Hero) c).pointsInTalent(Talent.DIVINE_SENSE);
					} else {
						mindVisRange = 1 + 2 * ((Hero) c).pointsInTalent(Talent.DIVINE_SENSE);
					}
				}
				mindVisRange = Math.max(mindVisRange, EyeOfNewt.mindVisionRange());
			}

			if (mindVisRange >= 1) {

				//power of many's life link spell allows allies to get divine sense
				Char ally = PowerOfMany.getPoweredAlly();
				if (ally != null && ally.buff(DivineSense.DivineSenseTracker.class) == null) {
					ally = null;
				}

				for (Mob mob : level.mobs()) {
					if ((mob instanceof Mimic && mob.alignment == Char.Alignment.NEUTRAL && ((Mimic) mob).stealthy())
						|| Char.hasProp(mob, Char.Property.OBJECT)){
						continue;
					}
					int p = mob.pos;
					if (!fieldOfView[p] && (level.distance(c.pos, p) <= mindVisRange || (ally != null && level.distance(ally.pos, p) <= mindVisRange))) {
						for (int i : PathFinder.NEIGHBOURS9) {
							heroMindFov[mob.pos + i] = true;
						}
					}
				}
			}

			if (c.buff( Awareness.class ) != null) {
				for (Heap heap : level.heaps.valueList()) {
					int p = heap.pos;
					for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
				}
			}

			for (TalismanOfForesight.CharAwareness a : c.buffs(TalismanOfForesight.CharAwareness.class)){
				Char ch = (Char) Actor.findById(a.charID);
				if (ch == null || !ch.isAlive() || Char.hasProp(ch, Char.Property.OBJECT)) {
					continue;
				}
				int p = ch.pos;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
			}

			for (TalismanOfForesight.HeapAwareness h : c.buffs(TalismanOfForesight.HeapAwareness.class)){
				if (Dungeon.depth != h.depth || Dungeon.branch != h.branch) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[h.pos+i] = true;
			}

			for (Mob m : level.mobs()){
				if (m instanceof WandOfWarding.Ward
						|| m instanceof WandOfRegrowth.Lotus
						|| m instanceof SpiritHawk.HawkAlly
						|| m.buff(PowerOfMany.PowerBuff.class) != null){
					if (m.fieldOfView == null || m.fieldOfView.length != level.length()){
						m.fieldOfView = new boolean[level.length()];
						FieldOfView.update( level, m, m.fieldOfView );
					}
					BArray.or(heroMindFov, m.fieldOfView, heroMindFov);
				}
			}

			for (RevealedArea a : c.buffs(RevealedArea.class)){
				if (Dungeon.depth != a.depth || Dungeon.branch != a.branch) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[a.pos+i] = true;
			}

			//set mind vision chars
			for (Mob mob : level.mobs()) {
				if (heroMindFov[mob.pos] && !fieldOfView[mob.pos]){
					Dungeon.hero.mindVisionEnemies.add(mob);
				}
			}

			BArray.or(heroMindFov, fieldOfView, fieldOfView);

		}

		if (c == Dungeon.hero) {
			for (Heap heap : level.heaps.valueList())
				if (!heap.seen && fieldOfView[heap.pos])
					heap.seen = true;
		}

	}
}
