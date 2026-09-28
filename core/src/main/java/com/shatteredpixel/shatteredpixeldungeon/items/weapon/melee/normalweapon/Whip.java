package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Whip extends NormalMeleeWeapon {
	public Whip() { super(3, 1f, 1f, 2, 24, 35, ItemSpriteSheet.SPS_WEP_WHIP); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.3f) s.accuracy += .05f;
		if (s.reach < 3 && s.accuracy > 1.3f) s.reach++;
		s.min++; s.max += 2;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.affect(defender, Roots.class, 1f);
		return super.proc(attacker, defender, damage);
	}
}
