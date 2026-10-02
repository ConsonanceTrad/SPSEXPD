/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.actors.buffs.Hot;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Tree extends MeleeThrowWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Tree.class)
			.t("name", "圣诞树")
			.t("desc", "一个简单的圣诞树装饰物。圣诞节快乐！\n飞掷，溅射，虚弱，礼物");
	}

	public Tree() { super(1, 1, 5, SpecificPlaceHolderDict.SOMETHING_0); }

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
