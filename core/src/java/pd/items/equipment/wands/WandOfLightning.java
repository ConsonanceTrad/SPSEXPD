/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.Lightning;
import pd.effects.particles.SparkParticle;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.PixelScene;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.math.Random;

import java.util.ArrayList;

/** The original SPS-PD chaining lightning wand. */
public class WandOfLightning extends DamageWand {

	{
		image = EquipmentWandBasicWandDict.WAND_SPS_LIGHTNING;
		collisionProperties = Ballistica.PROJECTILE;
	}

	private final ArrayList<Char> affected = new ArrayList<>();
	private final ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	@Override public int min(int level) { return 5 + level; }
	@Override public int max(int level) { return Math.round(10 + level * level / 4f); }

	public static float chainMultiplier(int targets, boolean targetInWater) {
		if (targets <= 0) return 0f;
		float multiplier = 0.4f + 0.6f / targets;
		return targetInWater ? multiplier * 1.5f : multiplier;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		float multiplier = chainMultiplier(affected.size(), Dungeon.level.water[bolt.collisionPos]);
		for (Char target : affected) {
			wandProc(target, chargesPerCast());
			int rolled = Random.NormalIntRange(min(level()), max(level()));
			int damage = (int)(magicSkillMultiplier(Dungeon.hero.magicSkill())
					* Math.round(rolled * multiplier));
			target.damage(damage, this);
			if (target == Dungeon.hero) PixelScene.shake(2, 0.3f);
			if (target.sprite != null) {
				target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				target.sprite.flash();
			}
		}
		if (!curUser.isAlive()) Dungeon.fail(this);

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.shockhit();
	}

	private void arc(Char target) {
		affected.add(target);

		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = target.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char next = Actor.findChar(cell);
			if (next != null && !affected.contains(next)) {
				arcs.add(new Lightning.Arc(target.pos, next.pos));
				arc(next);
			}
		}

		if (Dungeon.level.water[target.pos] && !target.flying) {
			PathFinder.buildDistanceMap(target.pos, BArray.not(Dungeon.level.solid, null), 2);
			for (int cell = 0; cell < PathFinder.distance.length; cell++) {
				if (PathFinder.distance[cell] == Integer.MAX_VALUE || !Dungeon.level.insideMap(cell)) continue;
				Char next = Actor.findChar(cell);
				if (next == Dungeon.hero || next == null || affected.contains(next)) continue;
				arcs.add(new Lightning.Arc(target.pos, next.pos));
				arc(next);
			}
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		affected.clear();
		arcs.clear();
		arcs.add(new Lightning.Arc(bolt.sourcePos, bolt.collisionPos));

		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			arc(target);
		} else {
			CellEmitter.center(bolt.collisionPos).burst(SparkParticle.FACTORY, 3);
		}
		curUser.sprite.parent.add(new Lightning(arcs, null));
		callback.call();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}
}
