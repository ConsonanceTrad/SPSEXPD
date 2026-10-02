package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.messages.InlineText;

public class EmptyAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EmptyAmmo.class)
			.t("name", "空心弹")
			.t("desc", "将原石和无味种锻造而成的特殊子弹……这有什么用？");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Char.hasProp(defender, Char.Property.BOSS)) {
			defender.damage(Math.min(defender.HT / 20, 3000), this);
		}
	}
}
