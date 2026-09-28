package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.relic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.AresLeech;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AresSword extends RelicMeleeWeapon {

	public static final String AC_REGEN = "REGEN";

	public AresSword() {
		 super(1f, 1f, 1);
		image = ItemSpriteSheet.ARES_SWORD;
		enchant(new AresLeech());
	}

	@Override
	protected String relicAction() {
		return AC_REGEN;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(1, level()));
	}

}
