/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

/** The freeze wand from SPS-PD 0.9.8, distinct from Shattered's frost wand. */
public class WandOfFreeze extends DamageWand {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public int min(int lvl) {
		return 5 + 2 * lvl;
	}

	@Override
	public int max(int lvl) {
		return 10 + 4 * lvl;
	}

	public static int freezeThreshold(int lvl) {
		return 8 - lvl;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int damage = damageRoll();
			if (target.buff(Frost.class) != null) return;

			wandProc(target, chargesPerCast());
			target.damage(damage, this);
			if (target.isAlive()) {
				if (Dungeon.level.water[target.pos]
						&& Random.Int(10) >= freezeThreshold(level())) {
					Buff.affect(target, Frost.class, 5f * Random.Float(2f, 4f));
				} else {
					Buff.affect(target, FrostIce.class).level(5 + level());
				}
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.freeze();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.FROST,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
