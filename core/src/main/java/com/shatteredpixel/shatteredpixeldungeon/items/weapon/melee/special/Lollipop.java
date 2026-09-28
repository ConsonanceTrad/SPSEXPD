/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HolyStun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Lollipop extends SpsSpecialMeleeWeapon {
	public Lollipop() { super(1, 1f, 1f, 1, 50, 50, ItemSpriteSheet.SPS_LOLLIPOP); usesTargeting = true; }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.affect(defender, Tar.class);
		if (Random.Int(100) < 60) Buff.affect(defender, Charm.class, 5f).object = attacker.id();
		if (Random.Int(50) == 1) {
			Buff.prolong(attacker, HolyStun.class, 5f);
			Buff.prolong(attacker, STRDown.class, 20f);
			int loss = Math.min(15, Math.max(0, attacker.HT - 1));
			attacker.HT -= loss;
			attacker.HP = Math.min(attacker.HP, attacker.HT);
			if (attacker instanceof Hero) {
				Hero hero = (Hero)attacker;
				hero.HTBoost -= loss;
				if (hero.belongings.weapon == this) hero.belongings.weapon = null;
				if (hero.belongings.secondWep == this) hero.belongings.secondWep = null;
			}
			Item.updateQuickslot();
			if (Gdx.app != null) GLog.n(Messages.get(com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon.class, "destory"));
		}
		return super.proc(attacker, defender, damage);
	}
}
