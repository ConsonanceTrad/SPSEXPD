package pd.items.equipment.weapon.missiles.fusion;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.items.equipment.weapon.missiles.HeavyBoomerang;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;

public class TempestBoomerang extends HeavyBoomerang {
	{
		image = ConsumThrowsDict.BOOMERANG_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TempestBoomerang.class)
			.t("name", "风暴回旋镖")
			.t("desc", "伤害略低的四阶回旋镖，命中时有四分之一概率使目标短暂残废，并会按正常回旋镖规则返回。");
	}




	@Override
	public int max(int lvl) {
		return Math.max(min(lvl), super.max(lvl) - 2);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(4) == 0) Buff.prolong(defender, Cripple.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
