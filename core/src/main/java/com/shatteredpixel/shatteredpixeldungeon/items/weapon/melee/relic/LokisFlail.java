package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.relic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.LokisPoison;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class LokisFlail extends RelicMeleeWeapon {

	public static final String AC_STEALTH = "STEALTH";

	public LokisFlail() {
		super(0.8f, 1.2f, 2);
		image = ItemSpriteSheet.LOKIS_FLAIL;
		hitSound = Assets.Sounds.HIT_CRUSH;
		enchant(new LokisPoison());
	}

	@Override
	protected String relicAction() {
		return AC_STEALTH;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		Buff.affect(hero, Shadows.class, 10f);
		Sample.INSTANCE.play(Assets.Sounds.MELD);
	}

}
