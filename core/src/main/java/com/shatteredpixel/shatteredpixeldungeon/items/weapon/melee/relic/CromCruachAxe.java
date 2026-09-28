package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.relic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.CromLuck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CromCruachAxe extends RelicMeleeWeapon {

	public static final String AC_DISPEL = "DISPEL";

	public CromCruachAxe() {
		super(1.2f, 1f, 1);
		image = ItemSpriteSheet.CROM_CRUACH_AXE;
		enchant(new CromLuck());
	}

	@Override
	protected String relicAction() {
		return AC_DISPEL;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		Buff.prolong(hero, MagicImmunity.class, 2f * (level() / 10));
	}

}
