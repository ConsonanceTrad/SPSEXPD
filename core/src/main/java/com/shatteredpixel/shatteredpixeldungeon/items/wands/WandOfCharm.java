/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpsCharm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

/** The half-charge charm wand from SPS-PD 0.9.8. */
public class WandOfCharm extends Wand {

	{
		image = ItemSpriteSheet.WAND_CHARM;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int charges = chargesPerCast();
			wandProc(target, charges);
			if (target == Dungeon.hero) {
				Buff.affect(target, Vertigo.class, 5f);
			} else {
				Buff.affect(target, Amok.class, charges + level());
				SpsCharm charm = Buff.affect(target, SpsCharm.class,
						Random.IntRange(charges, charmDurationMax(level(), charges)));
				charm.object = curUser.id();
				if (target.sprite != null) {
					target.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
				}
				Sample.INSTANCE.play(Assets.Sounds.CHARMS);
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.lighthit();
	}

	@Override
	public int initialCharges() {
		return 2;
	}

	@Override
	protected int chargesPerCast() {
		return chargesToSpend(curCharges);
	}

	public static int chargesToSpend(int currentCharges) {
		return Math.max(1, (int) Math.ceil(currentCharges * 0.5f));
	}

	public static int charmDurationMax(int lvl, int charges) {
		return Math.max(charges, 3 * lvl);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.PURPLE_LIGHT,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
