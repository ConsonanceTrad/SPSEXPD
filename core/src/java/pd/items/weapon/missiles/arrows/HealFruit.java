/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.actors.Char;
import pd.actors.blobs.HealLight;
import pd.effects.Speck;
import pd.sprites.CharSprite;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class HealFruit extends SpsFruit {
	public HealFruit() { this(1); }
	public HealFruit(int number) { super(ItemSpriteSheet.SPS_SEED_SUNGRASS, 20, 20); quantity(number); }

	@Override public int damageRoll(Char owner) { return 0; }

	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seed(cell, 8, HealLight.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		int heal = Random.IntRange(min(), max());
		int actual = Math.min(defender.HT - defender.HP, heal);
		if (actual > 0) {
			defender.HP += actual;
			if (defender.sprite != null) {
				defender.sprite.showStatus(CharSprite.POSITIVE, "+" + actual);
				defender.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 3);
			}
		}
		return super.proc(attacker, defender, 0);
	}
}
