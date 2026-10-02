package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class HeavyAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HeavyAmmo.class)
			.t("name", "重铅弹")
			.t("desc", "将两枚原石组锻造成的特殊子弹，能使武器附带更高的伤害。");
	}



	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.75f * damage), attacker);
	}
}
