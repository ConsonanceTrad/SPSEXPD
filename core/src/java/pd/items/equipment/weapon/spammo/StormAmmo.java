/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.actors.damagetype.DamageType;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class StormAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StormAmmo.class)
			.t("name", "雷暴弹")
			.t("desc", "将原石和风暴种锻造而成的特殊子弹，能使武器附带雷电伤害。");
	}

	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);
	@Override public ItemSprite.Glowing glowing() { return WHITE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(6) == 3) Buff.affect(defender, Shocked.class).level(2);
		else defender.damage((int)(0.40f * damage), DamageType.SHOCK_DAMAGE);
	}
}
