package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class BattleAmmo extends SpAmmo {
	private static final ItemSprite.Glowing DEEP_GREEN = new ItemSprite.Glowing(0x006633);
	@Override public ItemSprite.Glowing glowing() { return DEEP_GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.5f * damage), attacker);
		Buff.prolong(attacker, AttackUp.class, 5f).level(35);
		Buff.prolong(attacker, DefenceUp.class, 5f).level(35);
	}
}
