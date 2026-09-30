/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.meleethrow;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.actors.buffs.Hot;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.PathFinder;
import watabou.utils.Random;

public class Tree extends MeleeThrowWeapon {
	public Tree() { super(1, 1, 5, ItemSpriteSheet.SPS_EASTER_TREE); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) > 40) Buff.affect(defender, Dry.class, 10f);
		if (Random.Int(100) > 40) Buff.affect(defender, Hot.class, 10f);
		if (Random.Int(100) == 98) dropRandomItems(defender.pos, 1);
		if (Dungeon.level != null) for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = defender.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char target = Actor.findChar(cell);
			if (target == null || target == defender || target == attacker || !target.isAlive()) continue;
			int rolled = safeRandom(min(), max());
			target.damage(Math.max(0, rolled - Math.max(0, target.drRoll())), attacker);
		}
		return super.proc(attacker, defender, damage);
	}
}
