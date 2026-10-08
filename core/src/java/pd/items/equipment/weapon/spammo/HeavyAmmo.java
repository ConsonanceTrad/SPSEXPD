package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class HeavyAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HeavyAmmo.class)
			.t("name", "重铅弹")
			.t("desc", "将两枚原石组锻造成的特殊子弹，能使武器附带更高的伤害。");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.75f * damage), attacker);
	}
}
