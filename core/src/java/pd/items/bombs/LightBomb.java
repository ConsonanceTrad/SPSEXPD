/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bombs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LightShootAttack;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class LightBomb extends Bomb {

	{
		image = ItemSpriteSheet.LIGHT_BOMB;
	}

	public LightBomb() { this(1); }
	public LightBomb(int quantity) { this.quantity = quantity; }

	@Override public void explode(int cell) {
		super.explode(cell);
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.heroFOV[target]) {
				CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			}
			Char ch = Actor.findChar(target);
			if (ch == null) continue;
			Buff.prolong(ch, Blindness.class, 10f);
			Buff.affect(ch, LightShootAttack.class).level(10);
			boolean unnatural = dealsHeavyDamageTo(ch);
			ch.damage(Random.NormalIntRange(unnatural ? 200 : 50, unnatural ? 400 : 100), this);
		}
		Dungeon.observe();
	}

	/** Maps the broad SPS-PD 0.9.8 creature families onto Shattered's finer properties. */
	public static boolean dealsHeavyDamageTo(Char ch) {
		return Char.hasProp(ch, Char.Property.UNDEAD)
				|| Char.hasProp(ch, Char.Property.UNKNOW)
				|| Char.hasProp(ch, Char.Property.MECH)
				|| Char.hasProp(ch, Char.Property.ELEMENT)
				|| Char.hasProp(ch, Char.Property.OBJECT)
				|| Char.hasProp(ch, Char.Property.INORGANIC)
				|| Char.hasProp(ch, Char.Property.DEMONIC)
				|| Char.hasProp(ch, Char.Property.DRAGON)
				|| Char.hasProp(ch, Char.Property.ACIDIC)
				|| Char.hasProp(ch, Char.Property.ICY)
				|| Char.hasProp(ch, Char.Property.FIERY)
				|| Char.hasProp(ch, Char.Property.ELECTRIC)
				|| Char.hasProp(ch, Char.Property.BOSS)
				|| Char.hasProp(ch, Char.Property.MINIBOSS);
	}

	@Override public LightBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
