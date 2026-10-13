package pd.items.equipment.weapon.guns;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.mechanics.Ballistica;
import render.utils.math.GameMath;
import render.utils.math.Random;

/**
 * SPSEXPD: 枪械共享开火核心——弹匣型（GunWeapon）与充能型（GunOfSoldier）共用。
 *
 * 负责「出膛偏差 → 沿轨迹飞行（未命中不消失）→ 命中结算 → 穿透/撞墙终止」这一弹道模型，
 * 以及枪声唤醒。弹药来源（弹匣 / 自然充能 / 特殊弹药）由各自的实现负责。
 */
public final class GunFire {

	private GunFire() {}

	/** 出膛偏差的最大横向格数：距离超出精准范围、以及力量不足都会让子弹更容易打偏。 */
	public static int spreadOffset(int dist, int range, int perfectSTR, int heroSTR) {
		int offset = 0;
		if (dist > range) offset += (dist - range) / 2;
		if (heroSTR < perfectSTR) offset += (perfectSTR - heroSTR);
		return Math.min(3, offset);
	}

	/** 把终点格在垂直于视线的方向上平移 k 格（k 为 0 时即原目标）。 */
	public static int lateralOffset(int from, int to, int k) {
		if (k == 0 || Dungeon.level == null) return to;
		int w = Dungeon.level.width();
		int x0 = from % w, y0 = from / w;
		int x1 = to % w,   y1 = to / w;
		int dx = x1 - x0, dy = y1 - y0;
		double len = Math.sqrt((double)(dx * dx + dy * dy));
		if (len == 0) return to;
		//视线的法向量
		double nx = -dy / len, ny = dx / len;
		int tx = x1 + (int)Math.round(nx * k);
		int ty = y1 + (int)Math.round(ny * k);
		tx = (int)GameMath.gate(0, tx, w - 1);
		ty = (int)GameMath.gate(0, ty, Dungeon.level.height() - 1);
		return ty * w + tx;
	}

	/** 沿弹道逐格结算：命中即结算；未命中不消失，继续飞；pierce > 0 时命中也继续飞，直到撞墙。 */
	public static int resolveShot(Hero hero, MissileWeapon shot, int targetCell,
	                              int range, int perfectSTR, int pierce) {
		if (hero == null || Dungeon.level == null || shot == null) return 0;
		int from = hero.pos;
		int dist = Dungeon.level.distance(from, targetCell);
		int spread = spreadOffset(dist, range, perfectSTR, hero.STR());
		int to = spread > 0
				? lateralOffset(from, targetCell, Random.IntRange(-spread, spread))
				: targetCell;

		Ballistica traj = new Ballistica(from, to, Ballistica.WONT_STOP);
		int hits = 0;
		for (Integer cell : traj.path) {
			if (cell == null || cell == from) continue;
			Char enemy = Actor.findChar(cell);
			boolean solid = !Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell];
			if (enemy == null) {
				if (solid) break; //撞墙，弹道终止
				continue;         //空地：未命中的子弹继续飞
			}
			if (enemy == hero) continue;
			if (hero.shoot(enemy, shot)) {
				hits++;
				if (pierce <= 0) break; //普通枪命中即止
			}
			//未命中（或穿透型）：继续沿弹道飞行
		}
		return hits;
	}

	/** 枪声：按半径唤醒并吸引范围内的敌人。 */
	public static void alertNearby(int from, int radius) {
		if (radius <= 0 || Dungeon.level == null) return;
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (mob != null && Dungeon.level.distance(from, mob.pos) <= radius) {
				mob.beckon(from);
			}
		}
	}
}
