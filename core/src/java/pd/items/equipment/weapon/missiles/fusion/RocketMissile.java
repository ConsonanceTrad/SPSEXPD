package pd.items.equipment.weapon.missiles.fusion;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.items.equipment.weapon.missiles.Javelin;
import render.utils.math.Random;
import pd.messages.InlineText;

public class RocketMissile extends Javelin {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RocketMissile.class)
			.t("name", "火箭弹")
			.t("desc", "四阶投掷弹药，最大伤害低于标枪，但命中时有四分之一概率使目标短暂燃烧。");
	}


	@Override
	public int max(int lvl) {
		return Math.max(min(lvl), super.max(lvl) - 3 - lvl);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(4) == 0) Buff.affect(defender, Burning.class).reignite(defender, 3f);
		return super.proc(attacker, defender, damage);
	}
}
