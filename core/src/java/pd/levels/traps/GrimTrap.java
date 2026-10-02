/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Wound;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GrimTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(GrimTrap.class)
			.t("name", "即死陷阱")
			.t("ondeath", "你被即死陷阱的冲击彻底击杀...")
			.t("desc", "非常强大的破坏魔法储存在这个陷阱里，足以瞬间杀死除了状态最佳的英雄外的所有生物。触发它将向最近的生物发送一个致命的远程冲击魔法。\n\n幸好的是，触发机关并没有被隐藏起来。");
	}

	{
		color = GREY;
		shape = LARGE_DOT;
		canBeHidden = false;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play(Assets.Sounds.HIT);
			Wound.hit(pos);
		}
		Char target = Actor.findChar(pos);
		if (target == null) return;
		int damage = Random.NormalIntRange(target.HP / 2, target.HP);
		damage -= Random.IntRange(target.drRoll() / 2, target.drRoll());
		target.damage(Math.max(damage, 0), this);
		if (!target.isAlive() && target == Dungeon.hero) Dungeon.fail(this);
	}
}
