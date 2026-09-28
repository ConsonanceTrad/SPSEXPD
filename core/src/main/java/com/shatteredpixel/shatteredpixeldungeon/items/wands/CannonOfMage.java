/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class CannonOfMage extends DamageWand {
	{
		image = ItemSpriteSheet.LEGACY_CANNON_OF_MAGE;
		collisionProperties = Ballistica.MAGIC_BOLT;
		reinforced = true;
	}
	@Override public int min(int lvl) { return 1 + lvl; }
	@Override public int max(int lvl) { return 5 + 2 * lvl; }
	@Override public int initialCharges() { return 7; }
	@Override public Item upgrade() { super.upgrade(); maxCharges = 7; curCharges = Math.min(curCharges, maxCharges); updateQuickslot(); return this; }

	@Override public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null || !target.isAlive()) return;
		applyRandomEffect(target, Random.Int(7));
		wandProc(target, chargesPerCast());
		target.damage(Math.round(damageRoll() * (1f + .6f * Dungeon.hero.magicSkill())), this);
	}

	public void applyRandomEffect(Char target, int effect) {
		switch (effect) {
			case 0: target.damage(Math.round(damageRoll() * (1f + .3f * Dungeon.hero.magicSkill())), this); break;
			case 1: Buff.affect(target, Burning.class).reignite(target, 3f); break;
			case 2: Buff.affect(target, Shocked.class).set(5f); break;
			case 3: Buff.affect(target, Ooze.class).set(5f); break;
			case 4: Buff.affect(target, Frost.class, 5f); break;
			case 5:
				Buff.affect(target, AttackDown.class, 10f).level(30);
				Buff.affect(target, ArmorBreak.class, 10f).level(30);
				break;
			case 6: Buff.prolong(target, Blindness.class, 5f); break;
			default: break;
		}
	}

	@Override public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.RAINBOW, curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		applyRandomEffect(defender, Random.Int(7));
	}
}
