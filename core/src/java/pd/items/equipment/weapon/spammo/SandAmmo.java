/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.effects.Speck;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class SandAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SandAmmo.class)
			.t("name", "烟尘弹")
			.t("desc", "将原石和消逝种锻造而成的特殊子弹，能使武器附带削弱效果。");
	}



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
