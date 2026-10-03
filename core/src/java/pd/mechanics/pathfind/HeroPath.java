/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.mechanics.pathfind;

import pd.Dungeon;
import pd.actors.Actor;

import java.util.ArrayList;

/**
 * SPS: 从目标格反推回起点的可行走路径，供移动路径提示使用。
 *
 * 只走「可站立且没有角色占据」的格；起点和终点自身允许被占据
 * （英雄站在起点、目标可能是怪物脚下的空地）。
 *
 * {@link #computeExplored} 额外要求沿途都是英雄探索过的格——谨慎移动用它来
 * 判断"知不知道该怎么过去"，走不通时由调用方给出提示而不是显示路径。
 */
public final class HeroPath {

	private HeroPath() { }

	/** 沿途是否必须都是英雄探索过的格子。 */
	private static final int EXPLORED = 1;

	/**
	 * 计算 from → to 的有序路径（含两端）。不可达或无需移动时返回 null。
	 */
	public static ArrayList<Integer> compute( int from, int to ){
		return compute( from, to, 0 );
	}

	/**
	 * 同 {@link #compute}，但只允许经过英雄已探索（visited）的格子。
	 * 用于谨慎移动：路径要"路过未知区域"时返回 null，调用方据此提示玩家。
	 */
	public static ArrayList<Integer> computeExplored( int from, int to ){
		return compute( from, to, EXPLORED );
	}

	private static ArrayList<Integer> compute( int from, int to, int flags ){

		if (from == to) return null;
		if (Dungeon.level == null) return null;
		if (!Dungeon.level.insideMap(from) || !Dungeon.level.insideMap(to)) return null;

		boolean exploredOnly = (flags & EXPLORED) != 0;
		boolean[] visited = Dungeon.level.visited;

		boolean[] passable = new boolean[Dungeon.level.length()];
		for (int i = 0; i < passable.length; i++){
			passable[i] = Dungeon.level.passable[i] && Actor.findChar(i) == null;
			if (exploredOnly && (visited == null || !visited[i])) passable[i] = false;
		}
		//起点与终点自身放行（终点可能是尚未踏进去的可见格）
		passable[from] = true;
		passable[to]   = true;

		ArrayList<Integer> path = new ArrayList<>();
		int cur = from;
		path.add( cur );

		//逐格走 distance 下坡；guard 防止异常数据导致死循环
		for (int guard = 0; cur != to && guard < passable.length; guard++){
			int step = PathFinder.getStep( cur, to, passable );
			if (step < 0 || step == cur) return null;
			cur = step;
			path.add( cur );
		}

		return cur == to ? path : null;
	}
}
