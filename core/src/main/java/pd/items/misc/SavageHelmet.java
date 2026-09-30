/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.buffs.Buff;
import pd.actors.buffs.DamageUp;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class SavageHelmet extends MiscEquippable {

	{ image = ItemSpriteSheet.SPS_SAVAGE_HELMET; unique = true; }

	@Override protected MiscBuff createBuff() { return new SavageHelmetBless(); }

	public boolean shouldTrigger(Hero hero) {
		return isEquipped(hero) || Random.Int(5) == 0;
	}

	public int absorb(Hero hero, int damage) {
		if (damage <= 0) return damage;
		int limit = Math.max(1, damage / 2);
		int absorbed = limit <= 1 ? 1 : Random.Int(1, limit);
		Buff.affect(hero, DamageUp.class).level(absorbed);
		return Math.max(0, damage - absorbed);
	}

	public class SavageHelmetBless extends MiscBuff { }
}
