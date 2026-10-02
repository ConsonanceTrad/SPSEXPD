/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class LightShootAttack extends Buff implements Hero.Doom, Buff.DOTbuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LightShootAttack.class)
			.t("name", "圣光打击")
			.t("desc", "强力光芒持续打击这个地点，远离这个区域可以回避后续伤害。剩余打击时长：%s回合。");
	}


	private static final String LEFT = "left";
	private static final String POS = "pos";
	private float left;
	private int origin;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		origin = target.pos;
		return super.attachTo(target);
	}

	@Override
	public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (Dungeon.level.distance(target.pos, origin) < 3) {
			target.damage(Math.min(500, Math.max(1, target.HT / 30)), this);
		} else {
			detach();
			return true;
		}
		spend(TICK);
		left -= TICK;
		if (left <= 0) detach();
		return true;
	}

	public LightShootAttack level(int value) {
		left = Math.max(left, value);
		return this;
	}

	@Override public int totalIncomingDMG() { return Math.min(500, Math.max(1, target.HT / 30)); }
	@Override public int icon() { return BuffIndicator.LIGHT; }
	@Override public String iconTextDisplay() { return Integer.toString((int)Math.ceil(left)); }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(left)); }
	@Override public void onDeath() { Dungeon.fail(this); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(POS, origin); bundle.put(LEFT, left); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); origin = bundle.getInt(POS); left = bundle.getFloat(LEFT); }
}
