/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import pd.messages.InlineText;

/** SPS-PD's permanent acid effect. Water is the only normal way to remove it. */
public class AcidOoze extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AcidOoze.class)
			.t("name", "腐酸")
			.t("desc", "这种粘稠的酸性淤泥正在紧贴你的骨肉，并缓慢地将它们腐蚀融化。\n\n淤泥会一直造成稳定伤害直至被水冲洗。\n\n淤泥本身不会自然消失，必须在水中才能洗掉。")
			.t("heromsg", "污泥在腐蚀你的身体。洗掉它！");
	}


	{
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.OOZE;
	}

	public static int tickDamage(int targetHT, int randomSix) {
		return randomSix == 0 ? 1 : Math.min(500, targetHT / 15);
	}

	@Override
	public boolean act() {
		if (target == null) return true;
		if (!target.isAlive()) {
			detach();
			return true;
		}

		target.damage(tickDamage(target.HT, Random.Int(6)), this);
		if (!target.isAlive() && target == Dungeon.hero) {
			Dungeon.fail(this);
		}
		spend(TICK);

		if (Dungeon.level != null && Dungeon.level.water[target.pos]) detach();
		target.needsIncomingDOTUpdate = true;
		return true;
	}

	@Override
	public void detach() {
		if (target != null) target.needsIncomingDOTUpdate = true;
		super.detach();
	}

}
