/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Wound;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class SpearTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(SpearTrap.class)
			.t("name", "长矛陷阱")
			.t("desc", "大量磨尖的长矛藏在这块压力板下。陷阱触发后仍会保持武装。")
			.t("ondeath", "你被长矛陷阱刺穿了……");
	}

	{
		color = GREY;
		shape = DOTS;
	}
	@Override public void activate() {
		Char ch = Actor.findChar(pos);
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play(Assets.Sounds.HIT);
			Wound.hit(pos);
		}
		if (ch != null) {
			int legacyDepth = Dungeon.legacyDepth();
			int damage = Random.NormalIntRange(legacyDepth * 2, legacyDepth * 4)
					- Random.IntRange(0, ch.drRoll());
			ch.damage(Math.max(0, damage), this);
			if (ch == Dungeon.hero && !ch.isAlive()) {
				Dungeon.fail(this);
			}
		}
	}
}
