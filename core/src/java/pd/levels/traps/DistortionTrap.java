package pd.levels.traps;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.Item;
import pd.items.specific.keys.Key;
import pd.scenes.InterlevelScene;
import render.noosa.Game;
import pd.messages.InlineText;

public class DistortionTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DistortionTrap.class)
			.t("name", "重构陷阱")
			.t("desc", "这种陷阱由来源不明的奇特魔法制作，触发后会召唤各式各样的怪物到这里。");
	}


	{
		color = WHITE;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		Char target = Actor.findChar(pos);
		if (!prepareLegacyReset(target)) return;
		Game.switchScene(InterlevelScene.class);
	}

	protected boolean prepareLegacyReset(Char target) {
		if (target == null || !target.isAlive() || Dungeon.hero == null) return false;

		InterlevelScene.returnDepth = Dungeon.depth;
		InterlevelScene.returnBranch = Dungeon.branch;
		for (Item item : Dungeon.hero.belongings.backpack.items.toArray(new Item[0])) {
			if (item instanceof Key && ((Key)item).depth == Dungeon.depth) {
				item.detachAll(Dungeon.hero.belongings.backpack);
			}
		}
		InterlevelScene.mode = InterlevelScene.Mode.RESET;
		return true;
	}
}
