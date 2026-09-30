/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.buffs.Buff;
import pd.actors.buffs.OnePunch;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class SeriousPunch extends Item {

	public static final String AC_CAST = "CAST";
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = ItemSpriteSheet.SPS_SERIOUS_PUNCH;
		stackable = false;
		unique = true;
		defaultAction = AC_CAST;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		actions.add(AC_CAST);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CAST.equals(action)) cast(hero);
		else super.execute(hero, action);
	}

	public boolean cast(Hero hero) {
		if (hero == null) return false;
		Buff.affect(hero, OnePunch.class).level(charge);
		charge = 0;
		updateQuickslot();
		return true;
	}

	public void gainCharge() {
		charge++;
		updateQuickslot();
	}

	public int charge() {
		return charge;
	}

	public void charge(int value) {
		charge = Math.max(0, value);
		updateQuickslot();
	}

	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge(bundle.getInt(CHARGE));
	}
}
