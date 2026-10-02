/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.particles.ShadowParticle;
import pd.items.Item;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;

/** The unstable zero-tier weapon produced by RobotDMT. */
public class ErrorW extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ErrorW.class)
			.t("name", "错误武器")
			.t("desc", "这是个错误。——??? \n混沌");
	}



	private float legacyAccuracy = 1f;
	private float legacyDelay = 1f;

	{
		tier = 0;
		image = ConsumGoodsMaterialsGoodsDict.TEST_SUBJECT;
	}

	@Override public int min(int level) { return 0; }
	@Override public int max(int level) { return Math.max(0, level); }
	@Override public int STRReq(int level) { return 0; }
	@Override public float accuracyFactor(Char owner, Char target) { return legacyAccuracy; }
	@Override protected float baseDelay(Char owner) { return augment.delayFactor(legacyDelay); }
	@Override public int reachFactor(Char owner) { return 1; }

	@Override
	public Item upgrade(boolean enchant) {
		if (Random.Int(10) > 4 || legacyAccuracy < 0.5f) legacyAccuracy += 0.1f;
		else legacyAccuracy -= 0.1f;
		if (Random.Int(10) > 4 || legacyDelay < 0.5f) legacyDelay += 0.05f;
		else legacyDelay -= 0.05f;
		return super.upgrade(enchant);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		switch (Random.Int(10)) {
			case 0:
				boolean boss = Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS);
				int min = boss ? defender.HT / 8 : defender.HT;
				int max = boss ? defender.HT / 4 : defender.HT * 2;
				defender.damage(safeRange(min, max), this);
				if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
				if (!defender.isAlive() && attacker instanceof Hero) Badges.validateGrimWeapon();
				break;
			case 1: Buff.affect(defender, Cripple.class, 3f); break;
			case 2: Buff.affect(defender, Bleeding.class).set(5); break;
			case 3:
				Buff.affect(defender, Vertigo.class, 5f);
				Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker.id();
				break;
			case 4: Buff.affect(defender, Paralysis.class, 3f); break;
			case 5: Buff.affect(defender, Roots.class, 3f); break;
			case 6:
				if (attacker.HP < attacker.HT) {
					attacker.HP = Math.min(attacker.HT, attacker.HP + attacker.HT / 10);
					if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
				}
				break;
			case 7: Buff.affect(defender, Ooze.class).set(5f); break;
			case 8: Buff.affect(defender, Charm.class, 3f).object = attacker.id(); break;
			default: break;
		}
		return super.proc(attacker, defender, damage);
	}

	public float legacyAccuracy() { return legacyAccuracy; }
	public float legacyDelay() { return legacyDelay; }

	private static int safeRange(int min, int max) {
		return max <= min ? Math.max(0, min) : Random.Int(Math.max(0, min), max);
	}

	private static final String ACCURACY = "legacy_accuracy";
	private static final String DELAY = "legacy_delay";

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ACCURACY, legacyAccuracy);
		bundle.put(DELAY, legacyDelay);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		legacyAccuracy = bundle.contains(ACCURACY) ? bundle.getFloat(ACCURACY) : 1f;
		legacyDelay = bundle.contains(DELAY) ? bundle.getFloat(DELAY) : 1f;
	}
}
