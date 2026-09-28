package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Spear extends NormalMeleeWeapon {
	public Spear() { super(2, 1f, 1.5f, 2, 14, 30, ItemSpriteSheet.SPS_WEP_SPEAR); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.delay > 1.2f) s.delay -= .05f;
		if (s.delay < 1.2f && s.reach < 3) s.reach++;
		s.min++; s.max += 4;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
