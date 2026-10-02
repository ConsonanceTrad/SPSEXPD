/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.mobs.Mob;
import pd.effects.Pushing;
import pd.effects.Speck;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import render.noosa.audio.Sample;
import render.utils.math.Random;

/** The original sound-wave club awarded by the velocirooster. */
public class SJRBMusic extends MeleeWeapon {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
	}

	@Override public int min(int lvl) { return 3 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 6 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		int extraDamage = Dungeon.hero != null ? Dungeon.hero.damageRoll() : attacker.damageRoll();
		if (Random.Int(100) < 40) Buff.affect(defender, Charm.class, 5f).object = attacker.id();
		if (Random.Int(100) > 60 && Dungeon.level != null) {
			for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) mob.beckon(attacker.pos);
			if (attacker.sprite != null) {
				attacker.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
				attacker.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "rap"));
			}
			Sample.INSTANCE.play(Assets.Sounds.BEACON);
		}
		Ballistica route = new Ballistica(attacker.pos, defender.pos, Ballistica.PROJECTILE);
		if (route.dist == 2) {
			int from = attacker.pos;
			int cell = route.path.get(route.dist - 1);
			attacker.pos = cell;
			Dungeon.level.occupyCell(attacker);
			Actor.add(new SourceTimedPushing(attacker, from, cell));
			defender.damage(extraDamage, this);
		}
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char target = Actor.findChar(defender.pos + offset);
			if (target != null && target != defender && target != attacker && target.isAlive()) {
				target.damage(Math.max(extraDamage, 0), attacker);
			}
		}
		return damage;
	}

	private static final class SourceTimedPushing extends Pushing {
		SourceTimedPushing(Char ch, int from, int to) {
			super(ch, from, to);
			// Shattered clamps negative addDelayed values, while SPS-PD scheduled this at now - 1.
			spend(-1f);
		}
	}
}
