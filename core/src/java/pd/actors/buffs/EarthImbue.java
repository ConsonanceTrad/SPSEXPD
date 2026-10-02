/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.EarthParticle;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class EarthImbue extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(EarthImbue.class)
			.t("name", "大地之力")
			.t("desc", "你被灌注了大地的力量！\n\n直到该效果结束前，你所有的物理攻击都会使敌人脚下的地面发生变化，使它们残废一段时间。\n\n大地之力剩余时长：%s回合");
	}

	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(Paralysis.class);
		immunities.add(Roots.class);
		immunities.add(Slow.class);
	}
	public void proc(Char enemy) {
		Buff.prolong(enemy, Roots.class, 2f);
		if (enemy.sprite != null) CellEmitter.bottom(enemy.pos).start(EarthParticle.FACTORY, 0.05f, 8);
	}
	@Override public int icon() { return BuffIndicator.IMBUE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
