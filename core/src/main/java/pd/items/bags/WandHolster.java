/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bags;

import pd.items.Item;
import pd.items.TriforceOfCourage;
import pd.items.TriforceOfPower;
import pd.items.TriforceOfWisdom;
import pd.items.wands.Wand;
import pd.items.weapon.guns.GunWeapon;
import pd.items.weapon.rockcode.RockCode;
import pd.items.weapon.spammo.SpAmmo;
import pd.sprites.ItemSpriteSheet;

/** The original thirty-slot SPS magic-weapon holster. */
public class WandHolster extends Bag {
	{
		image = ItemSpriteSheet.HOLSTER;
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
