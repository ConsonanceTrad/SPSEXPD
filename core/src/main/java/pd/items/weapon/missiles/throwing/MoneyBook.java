/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.throwing;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

/** FruitCat's disposable blank ledger. */
public class MoneyBook extends TossWeapon {

	public static final String AC_CAST = "CAST";

	{
		image = ItemSpriteSheet.MONEY_BOOK;
		tier = 1;
		baseUses = 1;
		bones = false;
	}

	public MoneyBook() { this(1); }
	public MoneyBook(int number) { quantity(number); }

	@Override public int min(int lvl) { return 3; }
	@Override public int max(int lvl) { return 6; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CAST);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CAST.equals(action)) {
			castEffect(hero);
			detach(hero.belongings.backpack);
		} else {
			super.execute(hero, action);
		}
	}

	void castEffect(Hero hero) {
		Buff.affect(hero, Invisibility.class, 10f);
		if (Dungeon.level == null) return;
		PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid, null), 2);
		for (int cell = 0; cell < PathFinder.distance.length; cell++) {
			if (PathFinder.distance[cell] == Integer.MAX_VALUE || !Dungeon.level.insideMap(cell)) continue;
			Char target = Actor.findChar(cell);
			if (target != null) Buff.affect(target, HolyStun.class, 5f);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, HolyStun.class, 10f);
		return super.proc(attacker, defender, damage);
	}

	@Override public int value() { return 0; }
}
