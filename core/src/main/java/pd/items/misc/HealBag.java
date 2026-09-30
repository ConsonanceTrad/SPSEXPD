/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class HealBag extends Item {

	public static final String AC_HEAL = "HEAL";
	public static final String AC_COOK = "COOK";
	public static final int FULL_CHARGE = 40;
	public static final int HEAL_COST = 15;
	public static final int COOK_COST = 40;
	private static final String CHARGE = "charge";

	private int charge;

	{ image = ItemSpriteSheet.SPS_HEAL_BAG; unique = true; defaultAction = AC_HEAL; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		if (charge >= HEAL_COST) actions.add(AC_HEAL);
		if (charge >= COOK_COST) actions.add(AC_COOK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_HEAL.equals(action)) {
			if (!heal(hero)) GLog.p(Messages.get(this, "need_charge"));
		} else if (AC_COOK.equals(action)) {
			if (!cook(hero)) GLog.p(Messages.get(this, "need_charge"));
		} else {
			super.execute(hero, action);
		}
	}

	public boolean heal(Hero hero) {
		if (charge < HEAL_COST || Dungeon.level == null) return false;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char target = Actor.findChar(cell);
			if (target != null && target.HP < target.HT * 0.75f) {
				target.HP = Math.min(target.HT, target.HP + target.HT / 2);
			}
		}
		charge -= HEAL_COST;
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, AttackDown.class);
		Buff.detach(hero, ArmorBreak.class);
		updateQuickslot();
		hero.spendAndNext(1f);
		return true;
	}

	public boolean cook(Hero hero) {
		if (charge < COOK_COST || Dungeon.level == null) return false;
		Item result = randomCookedItem();
		Heap heap = Dungeon.level.drop(result, hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		charge -= COOK_COST;
		updateQuickslot();
		hero.spendAndNext(1f);
		return true;
	}

	public static Item randomCookedItem() {
		if (Random.Int(3) > 1) return Generator.random(Generator.Category.POTION);
		if (Random.Int(5) == 0) return Generator.random(Generator.Category.HIGHFOOD);
		if (Random.Int(2) == 0) return Generator.random(Generator.Category.MUSHROOM);
		return Generator.random(Generator.Category.PILL);
	}

	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }
	@Override public String status() { return Integer.toString(charge / COOK_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }
}
