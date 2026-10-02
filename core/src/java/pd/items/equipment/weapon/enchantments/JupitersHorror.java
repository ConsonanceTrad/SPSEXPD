/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Threatens ordinary targets and disorients a hero target. */
public class JupitersHorror extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JupitersHorror.class)
			.t("name", "威慑%s")
			.t("desc", "威慑附魔能够恐吓目标。");
	}



	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max(0, weapon.level());
		if (Random.Int(level + 5) >= 4) {
			if (defender == Dungeon.hero) Buff.affect(defender, Vertigo.class, 10f);
			else Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker.id();
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x222222); }
}
