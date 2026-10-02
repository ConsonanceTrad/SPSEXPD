/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.scenes.GameScene;
import pd.sprites.AssassinSprite;
import pd.messages.InlineText;

/** Original SPS runtime/save identity for the fully migrated assassin. */
public class Assassin extends SpsPrisonMobs.Assassin {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Assassin.class)
			.t("name", "暗杀者")
			.t("desc", "由天狗所训练出来的忍者之一，极其擅长远程攻击。");
	}



	{ spriteClass = AssassinSprite.class; }

	public static Assassin spawnAt(int cell) {
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell]
				|| Actor.findChar(cell) != null) return null;
		Assassin mob = new Assassin();
		mob.pos = cell;
		mob.state = mob.HUNTING;
		GameScene.add(mob, 2f);
		return mob;
	}
}
