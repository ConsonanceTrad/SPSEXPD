/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dry;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class SandAmmo extends SpAmmo {
	private static final ItemSprite.Glowing GREY = new ItemSprite.Glowing(0xCCCCCC);
	@Override public ItemSprite.Glowing glowing() { return GREY; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(5) == 3) {
			Buff.affect(defender, AttackDown.class, 3f).level(25);
			Buff.affect(defender, ArmorBreak.class, 3f).level(25);
			if (defender.sprite != null) defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
		} else if (Random.Int(4) == 1) {
			Buff.prolong(defender, Dry.class, 3f);
		} else {
			defender.damage((int)(0.15f * damage), attacker);
		}
	}
}
