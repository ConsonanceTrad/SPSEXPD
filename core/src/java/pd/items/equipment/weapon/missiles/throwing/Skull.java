/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.ConsumThrowsDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SoulMark;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Skull extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Skull.class)
			.t("name", "鬼骨镖")
			.t("desc", "一种邪恶的投掷武器，能够标记目标的灵魂，使英雄从它身上汲取生命。");
	}


	{
		image = ConsumThrowsDict.SKULL;
		tier = 1;
		baseUses = 1;
		DLY = 0.1f;
		levelKnown = true;
	}

	public Skull() { this(1); }
	public Skull(int number) { quantity(number); }
	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 4; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, SoulMark.class, 10f);
		return super.proc(attacker, defender, damage);
	}
	@Override public Item random() { return quantity(Random.Int(3, 5)); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return quantity * 10; }
}
