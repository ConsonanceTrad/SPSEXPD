package pd.items.equipment.armor.fusion;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.equipment.armor.ScaleArmor;
import render.utils.math.Random;
import pd.messages.InlineText;

public class LifeArmor extends ScaleArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LifeArmor.class)
			.t("name", "生命护甲")
			.t("desc", "特别惊喜中的生命护甲经重新平衡后成为四阶护甲。它的最大格挡比鳞甲低1点，但每次真正受伤时有十二分之一概率恢复1点生命。");
	}


	@Override
	public int DRMax(int lvl) {
		return Math.max(0, super.DRMax(lvl) - 1);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		if (damage > 0 && defender == Dungeon.hero && Dungeon.hero.HP < Dungeon.hero.HT
				&& Random.Int(12) == 0) {
			Dungeon.hero.HP++;
		}
		return damage;
	}
}
