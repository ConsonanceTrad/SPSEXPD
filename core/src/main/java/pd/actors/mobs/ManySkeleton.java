/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.scenes.GameScene;
import pd.sprites.ManySkeletonSprite;
import watabou.utils.PathFinder;

/** Original SPS-PD runtime and save identity for the huge skull. */
public class ManySkeleton extends SpsCityMobs.ManySkeleton {

	{
		spriteClass = ManySkeletonSprite.class;
	}

	public static void spawnAround(int center) {
		for (int offset : PathFinder.NEIGHBOURS4) spawnAt(center + offset);
	}

	public static ManySkeleton spawnAt(int cell) {
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
		ManySkeleton skeleton = new ManySkeleton();
		skeleton.pos = cell;
		skeleton.state = skeleton.HUNTING;
		GameScene.add(skeleton, 1f);
		return skeleton;
	}
}
