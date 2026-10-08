package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Vertigo;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class WoodenAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenAmmo.class)
			.t("name", "软木弹")
			.t("desc", "将原石和坚果锻造而成的特殊子弹，能使目标眩晕并有概率将其麻痹。");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Vertigo.class, 3f);
		if (Random.Int(8) == 1) Buff.prolong(defender, Paralysis.class, 3f);
	}
}
