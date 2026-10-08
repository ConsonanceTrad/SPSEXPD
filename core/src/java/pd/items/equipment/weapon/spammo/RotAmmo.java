/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class RotAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RotAmmo.class)
			.t("name", "腐败弹")
			.t("desc", "将原石和腐梅种或鲜莓种锻造而成的特殊子弹，能使武器附带腐败效果。");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 3) Buff.prolong(defender, Roots.class, 3f);
		else Buff.affect(defender, Ooze.class).set(5f);
		defender.damage((int)(0.50f * damage), attacker);
	}
}
