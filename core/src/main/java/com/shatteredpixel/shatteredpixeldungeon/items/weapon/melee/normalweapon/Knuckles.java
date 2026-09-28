package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Knuckles extends NormalMeleeWeapon {
	public Knuckles() { super(1, 1f, 1f, 1, 1, 10, ItemSpriteSheet.SPS_WEP_KNUCKLES); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.delay > .30f) s.delay -= .05f;
		if (s.delay < .35f && s.reach < 2) s.reach++;
		s.min++; s.max++;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 70) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
