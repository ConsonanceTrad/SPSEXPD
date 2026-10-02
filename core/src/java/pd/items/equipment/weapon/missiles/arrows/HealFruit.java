/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.HealLight;
import pd.effects.Speck;
import pd.sprites.CharSprite;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class HealFruit extends SpsFruit {
	{
		image = SpecificPlaceHolderDict.SEED_HOLDER_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HealFruit.class)
			.t("name", "疗伤果")
			.t("desc", "人工种植的阳春草结出的果实。直接命中会治疗目标，落地则会释放治疗光芒。");
	}



	public HealFruit() { this(1); }
	public HealFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_SUNGRASS, 20, 20); quantity(number); }

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
