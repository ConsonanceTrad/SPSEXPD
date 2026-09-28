/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** The zero-defense armor produced when RobotDMT's chaos analysis fails. */
public class ErrorArmor extends NormalArmor {
	public ErrorArmor() {
		super(0, 1f, 1f, 10, 0, 0, -8, 0, 0, ItemSpriteSheet.SPS_ERROR_ARMOR);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		switch (Random.Int(10)) {
			case 0:
				int min = Char.hasProp(attacker, Char.Property.BOSS) || Char.hasProp(attacker, Char.Property.MINIBOSS)
						? attacker.HT / 8 : attacker.HT;
				int max = Char.hasProp(attacker, Char.Property.BOSS) || Char.hasProp(attacker, Char.Property.MINIBOSS)
						? attacker.HT / 4 : attacker.HT * 2;
				attacker.damage(safeRange(min, max), this);
				if (attacker.sprite != null) attacker.sprite.emitter().burst(ShadowParticle.UP, 5);
				if (!attacker.isAlive() && defender instanceof Hero) Badges.validateGrimWeapon();
				break;
			case 1: Buff.affect(attacker, Bleeding.class).set(5); break;
			case 2: Buff.affect(attacker, Ooze.class).set(5f); break;
			case 3: Buff.affect(attacker, Terror.class, Terror.DURATION).object = defender.id(); break;
			case 4:
				if (defender.HP < defender.HT) {
					defender.HP = Math.min(defender.HT, defender.HP + defender.HT / 10);
					if (defender.sprite != null) defender.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
				}
				break;
			case 5: Buff.prolong(attacker, AttackDown.class, 5f).level(35); break;
			case 6: Buff.prolong(attacker, ArmorBreak.class, 5f).level(35); break;
			case 7: Buff.affect(attacker, GrowSeed.class).set(6f); break;
			case 8: Buff.affect(attacker, Shocked.class); break;
			default: break;
		}
		return super.proc(attacker, defender, damage);
	}

	private static int safeRange(int min, int max) {
		return max <= min ? Math.max(0, min) : Random.Int(Math.max(0, min), max);
	}
}
