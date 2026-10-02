/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.actors.damagetype.DamageType;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class DreamAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DreamAmmo.class)
			.t("name", "催眠弹")
			.t("desc", "将原石和睡眠种锻造而成的特殊子弹，能使目标破甲并减速。");
	}

	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.20f * damage), DamageType.DARK_DAMAGE);
		Buff.prolong(defender, ArmorBreak.class, 6f).level(25);
		Buff.prolong(defender, Slow.class, 3f);
	}
}
