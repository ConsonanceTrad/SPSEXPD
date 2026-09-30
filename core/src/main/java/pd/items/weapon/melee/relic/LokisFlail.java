package pd.items.weapon.melee.relic;

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shadows;
import pd.actors.hero.Hero;
import pd.items.weapon.enchantments.LokisPoison;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;

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
