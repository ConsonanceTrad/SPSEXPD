package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.particles.ShadowParticle;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.SkeletonSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class SommonSkeleton extends Mob {
	private static final String LEVEL = "level";
	private int level;

	{
		spriteClass = SkeletonSprite.class;
		HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5);
		defenseSkill = 15 + legacyDepthAdjustment(0);
		baseSpeed = 0.8f;
		EXP = 0;
		maxLvl = 99;
		properties.add(Property.UNDEAD);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(15, 22 + legacyDepthAdjustment(0)); }
	@Override public int attackSkill(Char target) { return 16 + legacyDepthAdjustment(0); }
	@Override public int drRoll() { return Random.NormalIntRange(0, 4); }

	public void adjustStats(int depth) {
		level = Math.max(1, depth);
		defenseSkill = attackSkill(null) * 5;
		enemySeen = true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(LEVEL)) adjustStats(bundle.getInt(LEVEL));
	}

	public static SommonSkeleton spawnAt(int cell) {
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
		SommonSkeleton skeleton = new SommonSkeleton();
		skeleton.adjustStats(Math.max(1, Dungeon.legacyDepth()));
		skeleton.pos = cell;
		skeleton.state = skeleton.HUNTING;
		GameScene.add(skeleton, 2f);
		if (skeleton.sprite != null) {
			skeleton.sprite.alpha(0);
			skeleton.sprite.emitter().burst(ShadowParticle.CURSE, 5);
		}
		return skeleton;
	}

	public static SommonSkeleton spawnNear(int origin) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = origin + offset;
			if (Dungeon.level.insideMap(cell) && Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) candidates.add(cell);
		}
		if (candidates.isEmpty()) return null;
		return spawnAt(Random.element(candidates));
	}
}
