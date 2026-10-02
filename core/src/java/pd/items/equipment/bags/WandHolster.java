/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.TriforceOfCourage;
import pd.items.TriforceOfPower;
import pd.items.TriforceOfWisdom;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.equipment.weapon.rockcode.RockCode;
import pd.items.equipment.weapon.spammo.SpAmmo;
import pd.messages.InlineText;

/** The original thirty-slot SPS magic-weapon holster. */
public class WandHolster extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandHolster.class)
			.t("name", "法杖套")
			.t("desc", "这个修长的异兽皮套有三十格空间，可以紧密收纳法杖和其他SPS魔法武器。");
	}



	{
		image = EquipmentBagsDict.HOLSTER;
	}
	@Override public boolean canHold(Item item) {
		return (item instanceof Wand || item instanceof TriforceOfCourage
				|| item instanceof TriforceOfPower || item instanceof TriforceOfWisdom
				|| item instanceof SpAmmo || item instanceof GunWeapon || item instanceof RockCode)
				&& super.canHold(item);
	}
	@Override public boolean collect(Bag container) {
		if (!super.collect(container)) return false;
		if (owner != null) {
			for (Item item : items) {
				if (item instanceof Wand) ((Wand)item).charge(owner);
			}
		}
		return true;
	}
	@Override public void onDetach() {
		super.onDetach();
		for (Item item : items) {
			if (item instanceof Wand) ((Wand)item).stopCharging();
		}
	}
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
