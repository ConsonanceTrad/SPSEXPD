/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Shocked;
import pd.effects.MagicMissile;
import pd.items.Item;
import pd.mechanics.Ballistica;
import pd.items.weapon.melee.MagesStaff;
import pd.sprites.ItemSpriteSheet;
import render.noosa.audio.Sample;
import render.utils.Callback;
import render.utils.Random;

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
