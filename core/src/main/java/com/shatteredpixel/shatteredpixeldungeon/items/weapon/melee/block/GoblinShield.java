/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.block;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.TarGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** The chaos shield sold by the goblin tester after Otiluke is rescued. */
public class GoblinShield extends NormalMeleeWeapon {

	private static final String CHARGE = "charge";
	public static final int FULL_CHARGE = 11;
	private int charge;

	public GoblinShield() {
		super(3, 1f, 1f, 1, 8, 18, ItemSpriteSheet.SPS_GOBLIN_SHIELD);
	}

	@Override
	protected void applyLegacyUpgrade(Stats stats) {
		stats.min++;
		stats.max++;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker.buff(EnergyArmor.class) == null) {
			Buff.affect(attacker, EnergyArmor.class).level(attacker.HT / 8);
		}
		if (++charge >= FULL_CHARGE) {
			charge = 0;
			triggerEffect(Random.Int(16), attacker, defender, Math.max(0, damage));
		}
		return super.proc(attacker, defender, damage);
	}

	void triggerEffect(int effect, Char attacker, Char defender, int damage) {
		switch (effect) {
			case 1: Buff.affect(defender, Burning.class).reignite(defender, 3f); break;
			case 2: Buff.affect(defender, Frost.class, 3f); break;
			case 3: addGas(defender.pos, 20, TarGas.class); break;
			case 4: addGas(defender.pos, 30, ConfusionGas.class); break;
			case 5: addGas(defender.pos, 30, ToxicGas.class); break;
			case 6: addGas(defender.pos, 20, ParalyticGas.class); break;
			case 7: Buff.prolong(defender, Amok.class, 5f); break;
			case 8: Buff.affect(defender, Drowsy.class); break;
			case 9: Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker.id(); break;
			case 10: Buff.prolong(defender, Paralysis.class, 3f); break;
			case 11: Buff.prolong(defender, Blindness.class, 5f); break;
			case 12:
				if (Dungeon.level != null) {
					Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), defender.pos);
				}
				break;
			case 13: attacker.HP = Math.min(attacker.HT, attacker.HP + damage); break;
			case 14: Buff.prolong(attacker, Recharging.class, 20f); break;
			case 15: level(level() + 1); break;
			default: break;
		}
	}

	private void addGas(int cell, int amount, Class<? extends Blob> gas) {
		if (Dungeon.level != null) GameScene.add(Blob.seed(cell, amount, gas));
	}

	public int charge() {
		return charge;
	}

	@Override
	public String desc() {
		return super.desc() + "\n\n" + com.shatteredpixel.shatteredpixeldungeon.messages.Messages.get(this, "charge", charge);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt(CHARGE);
	}
}
