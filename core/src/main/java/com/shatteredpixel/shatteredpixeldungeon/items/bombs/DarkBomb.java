/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShadowCurse;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DarkBomb extends Bomb {

	{ image = ItemSpriteSheet.DARK_BOMB; }

	@Override
	public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			Char ch = Actor.findChar(target);
			if (ch == null || !ch.isAlive()) continue;
			Terror terror = Buff.affect(ch, Terror.class, Terror.DURATION);
			if (Dungeon.hero != null) terror.object = Dungeon.hero.id();
			Buff.affect(ch, ShadowCurse.class);
			boolean living = dealsHeavyDamageTo(ch);
			ch.damage(Random.NormalIntRange(living ? 200 : 50, living ? 400 : 100), this);
		}
		Dungeon.observe();
	}

	public static boolean dealsHeavyDamageTo(Char ch) {
		return Char.hasProp(ch, Char.Property.HUMAN)
				|| Char.hasProp(ch, Char.Property.PLANT)
				|| Char.hasProp(ch, Char.Property.ORC)
				|| Char.hasProp(ch, Char.Property.TROLL)
				|| Char.hasProp(ch, Char.Property.DWARF)
				|| Char.hasProp(ch, Char.Property.BEAST)
				|| Char.hasProp(ch, Char.Property.ELF)
				|| Char.hasProp(ch, Char.Property.GOBLIN)
				|| Char.hasProp(ch, Char.Property.BOSS)
				|| Char.hasProp(ch, Char.Property.MINIBOSS);
	}

	@Override public DarkBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
