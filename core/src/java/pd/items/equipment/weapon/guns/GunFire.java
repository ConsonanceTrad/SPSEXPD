package pd.items.equipment.weapon.guns;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Splash;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.mechanics.Ballistica;
import render.utils.math.Random;

/**
 * SPSEXPD: 枪械共享开火核心——弹匣型（GunWeapon）与充能型（GunOfSoldier）共用。
 *
 * 弹道模型：命中概率由距离决定（精准射击范围内必定命中，超出后每格衰减），
 * 命中后沿轨迹继续飞行直至撞墙（穿透型）——**不再有出膛偏差**，子弹直线飞向目标格。
 * 另外负责枪声唤醒。弹药来源（弹匣 / 自然充能 / 特殊弹药）由各自的实现负责。
 */
public final class GunFire {

	private GunFire() {}

	/** SPSEXPD: 超出精准射击范围后，每格的命中衰减比例（默认 17%）。 */
	public static final float HIT_DECAY_PER_TILE = 0.17f;
	/** SPSEXPD: 命中率下限（默认 5%，即累计最多衰减 95%）。 */
	public static final float MIN_HIT_CHANCE = 0.05f;

	/**
	 * SPSEXPD: 命中概率——在精准射击范围内必定命中；超出后每格乘 (1 - decayPerTile)，
	 * 并收敛到 minChance。子弹走直线，动画与结算完全一致。
	 */
	public static float hitChance(int dist, int range, float decayPerTile, float minChance) {
		if (dist <= range) return 1f;
		return Math.max(minChance, (float)Math.pow(1f - decayPerTile, dist - range));
	}

	/**
	 * 沿弹道逐格结算：命中即结算；未命中不消失，继续飞；pierce > 0 时命中也继续飞，直到撞墙。
	 * SPSEXPD: 先结算落点上的目标（与洛克手炮 MegaCannon.MegaAmmo 的写法一致），
	 * 命中与否由 hitChance 的距离概率裁定；之后沿弹道结算路径上的其它目标（流弹/穿透）。
	 */
	public static int resolveShot(Hero hero, MissileWeapon shot, int targetCell,
	                              int range, float decayPerTile, float minChance, int pierce) {
		if (hero == null || Dungeon.level == null || shot == null) return 0;
		int from = hero.pos;
		int hits = 0;

		Char direct = targetCell == from ? null : Actor.findChar(targetCell);
		if (direct != null && direct != hero) {
			if (!tryHit(hero, shot, direct, range, decayPerTile, minChance)) {
				Splash.at(targetCell, 0xCC99FFFF, 1); //打空：目标格只溅起尘土
				return hits;
			}
			hits++;
			if (pierce <= 0) return hits;
		}

		Ballistica traj = new Ballistica(from, targetCell, Ballistica.WONT_STOP);
		for (Integer cell : traj.path) {
			if (cell == null || cell == from || cell == targetCell) continue;
			Char enemy = Actor.findChar(cell);
			boolean solid = !Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell];
			if (enemy == null) {
				if (solid) break; //撞墙，弹道终止
				continue;         //空地：未命中的子弹继续飞
			}
			if (enemy == hero) continue;
			if (tryHit(hero, shot, enemy, range, decayPerTile, minChance)) {
				hits++;
				if (pierce <= 0) break; //普通枪命中即止
			}
			//未命中（或穿透型）：继续沿弹道飞行
		}
		return hits;
	}

	/** 一次命中裁定：按「射手到目标的距离」取命中概率，命中才结算伤害。 */
	private static boolean tryHit(Hero hero, MissileWeapon shot, Char enemy,
	                              int range, float decayPerTile, float minChance) {
		float chance = hitChance(Dungeon.level.distance(hero.pos, enemy.pos), range, decayPerTile, minChance);
		if (Random.Float() >= chance) return false;
		hero.shoot(enemy, shot);
		return true;
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
