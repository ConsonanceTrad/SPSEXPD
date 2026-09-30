/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.WatchOut;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Flare;
import pd.items.Item;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class Ankhshield extends Item {

	public static final String AC_DEFENCE = "DEFENCE";
	public static final int FULL_CHARGE = 100;
	public static final int DEFENCE_COST = 30;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = ItemSpriteSheet.SPS_ANKH_SHIELD;
		defaultAction = AC_DEFENCE;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		if (charge >= DEFENCE_COST) actions.add(AC_DEFENCE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_DEFENCE.equals(action)) {
			if (!defend(hero)) GLog.i(Messages.get(this, "rest"));
		} else {
			super.execute(hero, action);
		}
	}

	public boolean defend(Hero hero) {
		if (hero == null || Dungeon.level == null || charge < DEFENCE_COST) return false;
		if (hero.sprite != null) new Flare(6, 32).color(0x33FF33, true).show(hero.sprite, 2f);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (!mob.isAlive() || !Dungeon.level.heroFOV[mob.pos]) continue;
			if (Dungeon.level.distance(hero.pos, mob.pos) < 4) {
				mob.damage(5, this);
				if (mob.isAlive()) Buff.prolong(mob, HolyStun.class, 3f);
			} else {
				Buff.prolong(mob, WatchOut.class, 15f);
			}
		}
		charge -= DEFENCE_COST;
		updateQuickslot();
		return true;
	}

	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public String status() { return Integer.toString(charge / DEFENCE_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }

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
