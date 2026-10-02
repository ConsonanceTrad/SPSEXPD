/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import pd.messages.InlineText;

public class BloodImbue extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BloodImbue.class)
			.t("name", "鲜血灌注")
			.t("desc", "成功攻击时可能使目标残废、缠绕或麻痹，并免疫多种妨碍状态。\n\n剩余效果时长：%s回合。");
	}

	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(Paralysis.class);
		immunities.add(Roots.class);
		immunities.add(Slow.class);
		immunities.add(Bleeding.class);
		immunities.add(STRDown.class);
	}
	public void proc(Char enemy) {
		switch (Random.Int(4)) {
			case 0: Buff.prolong(enemy, Cripple.class, 3f); break;
			case 1: Buff.prolong(enemy, Roots.class, 3f); break;
			case 2: Buff.prolong(enemy, Paralysis.class, 3f); break;
			default: break;
		}
	}
	@Override public int icon() { return BuffIndicator.IMBUE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
