/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class ThornAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ThornAmmo.class)
			.t("name", "荆棘弹")
			.t("desc", "将原石和地种组锻造成的特殊子弹，能使武器附带流血或残废效果。");
	}

	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(5) == 3) {
			int upper = Math.max(5, damage);
			Buff.affect(defender, Bleeding.class).set(Random.IntRange(5, upper));
		} else {
			Buff.prolong(defender, Cripple.class, 3f);
		}
		defender.damage((int)(0.20f * damage), Bleeding.class);
	}
}
