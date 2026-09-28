/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class HookHam extends SpsSpecialMeleeWeapon {
	public HookHam() { super(1, 1f, 1f, 1, 1, 5, ItemSpriteSheet.SPS_HOOK_HAM); usesTargeting = true; }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) > 40) Buff.affect(defender, Bleeding.class).set(safeRandom(5, damage));
		if (Random.Int(100) < 20 && attacker.HP < attacker.HT) {
			attacker.HP = Math.min(attacker.HT, attacker.HP + 10);
			if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), .4f, 1);
		}
		if (Random.Int(100) == 98) dropRandomItems(defender.pos, 1);
		return super.proc(attacker, defender, damage);
	}
}
