package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Scimitar extends NormalMeleeWeapon {
	public Scimitar() { super(3, 1f, 1f, 1, 23, 35, ItemSpriteSheet.SPS_WEP_SCIMITAR); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.5f) s.accuracy += .025f;
		if (s.delay > .8f) s.delay -= .05f;
		s.min++; s.max += 4;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) {
			Buff.affect(defender, Bleeding.class).set(safeRandom(3, damage));
			Buff.affect(defender, ArmorBreak.class, 5f).level(30);
		}
		return super.proc(attacker, defender, damage);
	}
}
