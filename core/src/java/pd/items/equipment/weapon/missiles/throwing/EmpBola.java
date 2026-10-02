/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.Shocked;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;

public class EmpBola extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EmpBola.class)
			.t("name", "电磁套索")
			.t("desc", "用于抓捕或狩猎的工具，会致残并电击目标、移除能量护盾，并对机械生物造成重创。");
	}




	{
		image = ConsumThrowsDict.TRAP_NET;
		hitSound = Assets.Sounds.HIT_CRUSH;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
	}

	public EmpBola() { this(1); }
	public EmpBola(int number) { quantity(number); }

	@Override public int min(int lvl) { return 5; }
	@Override public int max(int lvl) { return 10; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		Buff.prolong(defender, Cripple.class, Cripple.DURATION);
		Buff.affect(defender, Shocked.class).level(5);
		Buff.detach(defender, EnergyArmor.class);
		if (defender.properties().contains(Char.Property.INORGANIC)) {
			defender.damage(Math.max(1, defender.HT / 3), this);
		}
		return result;
	}

	@Override public Item random() { return quantity(Random.Int(2, 5)); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 10 * quantity; }
}
