/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.special;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.mobs.Mob;
import pd.actors.mobs.YearBeast;
import pd.actors.mobs.YearBeast2;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.items.weapon.melee.MeleeWeapon;
import pd.sprites.ItemSpriteSheet;
import render.utils.PathFinder;
import render.utils.Random;

/** The original 2018 firecracker weapon. */
public class FireCracker extends MeleeWeapon {

	{
		image = ItemSpriteSheet.FIRE_CRACKER;
		tier = 1;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 5 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		if (defender instanceof YearBeast || defender instanceof YearBeast2) defender.damage(1, this);

		if (Random.Int(100) > 75 && Dungeon.level != null) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) mob.beckon(attacker.pos);
		}
		if (Random.Int(100) > 50 && Dungeon.level != null) {
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = defender.pos + offset;
				if (!Dungeon.level.insideMap(cell)) continue;
				if (Dungeon.level.heroFOV[cell]) CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 4);
				Char target = Actor.findChar(cell);
				if (target == null) continue;
				int blast = Random.NormalIntRange(target.HT / 40, target.HT / 20)
						- Math.max(target.drRoll(), 0);
				if (blast > 0) target.damage(blast, this);
			}
		}
		if (Random.Int(100) > 70) Buff.affect(defender, Terror.class, 5f).object = attacker.id();
		return damage;
	}
}
