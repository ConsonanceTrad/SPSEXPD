/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.effects.particles.ShadowParticle;
import pd.scenes.GameScene;
import pd.sprites.RedWraithSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the chaos wraith. */
public class RedWraith extends SpsCityMobs.RedWraith {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RedWraith.class)
			.t("name", "混沌幽灵")
			.t("desc", "蕴含强烈混沌能量的红色幽灵。相传有些戒指的魔力就来源于它。")
			.t("def_verb", "躲避");
	}




	{
		spriteClass = RedWraithSprite.class;
	}

	public static RedWraith spawnAt(int cell) {
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
		RedWraith wraith = new RedWraith();
		wraith.adjustStats(Math.max(1, Dungeon.legacyDepth()));
		wraith.pos = cell;
		wraith.state = wraith.HUNTING;
		GameScene.add(wraith, 2f);
		if (wraith.sprite != null) {
			wraith.sprite.alpha(0);
			wraith.sprite.emitter().burst(ShadowParticle.CURSE, 5);
		}
		return wraith;
	}
}
