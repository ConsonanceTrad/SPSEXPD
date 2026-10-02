/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.particles.PoisonParticle;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Original SPS ageing damage-over-time effect used by the town guardian dragon. */
public class BeOld extends Buff implements Hero.Doom, Buff.DOTbuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BeOld.class)
			.t("name", "老化")
			.t("desc", "你的躯体老化了。 \n\n老化每回合造成少量伤害，但会阻止正常生命恢复。 \n\n剩余的老化效果时长：%s回合")
			.t("heromsg", "你老化了！");
	}


	private static final String LEFT = "left";
	private float left;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public void set(float duration) {
		left = Math.max(left, duration);
		if (target != null) target.needsIncomingDOTUpdate = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.POISON;
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int) left);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left));
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target.sprite != null) CellEmitter.center(target.pos).burst(PoisonParticle.SPLASH, 5);
		target.needsIncomingDOTUpdate = true;
		return true;
	}

	@Override
	public void detach() {
		if (target != null) target.needsIncomingDOTUpdate = true;
		super.detach();
	}

	@Override
	public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		target.damage(2, this);
		spend(TICK);
		left -= TICK;
		target.needsIncomingDOTUpdate = true;
		if (left <= 0) detach();
		return true;
	}

	@Override
	public int totalIncomingDMG() {
		return Math.max(0, (int) Math.ceil(left)) * 2;
	}

	@Override
	public void onDeath() {
		Badges.validateDeathFromPoison();
		Dungeon.fail(this);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat(LEFT);
	}
}
