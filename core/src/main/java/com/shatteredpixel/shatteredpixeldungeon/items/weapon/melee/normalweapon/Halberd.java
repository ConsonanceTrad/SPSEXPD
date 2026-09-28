package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Halberd extends NormalMeleeWeapon {
	public Halberd() { super(5, 1f, 2f, 2, 62, 82, ItemSpriteSheet.SPS_WEP_HALBERD); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > 1.5f) s.delay -= .05f; s.max += 5; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 10) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
