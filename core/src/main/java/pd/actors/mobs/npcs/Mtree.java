/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.sprites.WarTreeSprite;
import com.watabou.utils.Random;

/** Huntress war tree summoned by the fourth legacy class skill. */
public class Mtree extends DirectableAlly {
	{
			spriteClass = WarTreeSprite.class;
		HP = HT = 1000;
		baseSpeed = 0.5f;
		viewDistance = 6;
		state = HUNTING;
		immunities.add(Poison.class);
		properties.add(Property.PLANT);
	}
	@Override public int attackSkill(Char target) { return 100; }
	@Override public int damageRoll() { return Random.NormalIntRange(100, 300); }
	@Override public int attackProc(Char enemy, int damage) {
		Buff.prolong(enemy, Roots.class, 3f);
		return super.attackProc(enemy, damage);
	}
	@Override protected boolean getCloser(int target) {
		if (Dungeon.level.distance(target, Dungeon.hero.pos) > 6) target = Dungeon.hero.pos;
		return super.getCloser(target);
	}
}
