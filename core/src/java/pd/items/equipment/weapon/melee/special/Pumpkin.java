/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Light;
import pd.actors.buffs.Terror;
import pd.effects.Speck;
import pd.effects.particles.FlameParticle;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The original Halloween pumpkin lamp weapon. */
public class Pumpkin extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Pumpkin.class)
			.t("name", "南瓜灯")
			.t("desc", "已经被点燃的南瓜灯。万圣节快乐！\n引燃，照明，恐吓，甜食。");
	}


	public static final int EFFECT_CHANCE = 20;
	public static final int HEALING = 10;
	public static final float LIGHT_DURATION = 50f;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 5 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < EFFECT_CHANCE) {
			Buff.affect(defender, Burning.class).reignite(defender, 5f);
			defender.damage(Random.Int(1, Math.max(2, buffedLvl() + 2)), this);
			if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, buffedLvl() + 1);
		}
		if (Random.Int(100) < EFFECT_CHANCE) {
			Buff.affect(defender, Terror.class, 3f).object = attacker.id();
		}
		if (Random.Int(100) < EFFECT_CHANCE && attacker.HP < attacker.HT) {
			attacker.HP = Math.min(attacker.HT, attacker.HP + HEALING);
			if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
		}
		Buff.prolong(attacker, Light.class, LIGHT_DURATION);
		return super.proc(attacker, defender, damage);
	}
}
