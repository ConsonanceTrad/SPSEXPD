/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ManySkeletonSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the huge skull. */
public class ManySkeleton extends SpsCityMobs.ManySkeleton {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ManySkeleton.class)
			.t("name", "骷髅球")
			.t("desc", "由一大堆骷髅组成的球，每次受到有力攻击都会掉出一只骷髅。");
	}


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
