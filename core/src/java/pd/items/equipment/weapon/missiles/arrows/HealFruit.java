/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.actors.hero.Hero;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.Item;
import pd.items.StoneOre;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import render.utils.math.Random;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.HealLight;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class HealFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_SUNGRASS;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HealFruit.class)
			.t("name", "阳春果实")
			.t("desc", "人工种植的阳春草结出的果实。命中或食用后会给予缓慢治愈，而不是立刻回复生命。");
	}



	public HealFruit() { this(1); }
	public HealFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_SUNGRASS, 20, 20); quantity(number); }

	@Override public int damageRoll(Char owner) { return 0; }



	@Override public int proc(Char attacker, Char defender, int damage) {
		if (defender != null) {
			//SPSEXPD: 大型果实为小型果实的 3 倍
			int lo = Math.max(1, Math.round(scaled(defender.HT * 0.05f)));
			int hi = Math.max(lo, Math.round(scaled(defender.HT * 0.10f)));
			//SPSEXPD: 缓慢治愈（与治疗药剂同机制），而不是瞬时治疗
			Buff.affect(defender, Healing.class).setHeal(Random.IntRange(lo, hi), 0.25f, 0, true);
		}
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		int lo = Math.max(1, Math.round(scaled(hero.HT * 0.10f)));
		int hi = Math.max(lo, Math.round(scaled(hero.HT * 0.25f)));
		int actual = Math.min(hero.HT - hero.HP, Random.IntRange(lo, hi));
		if (actual > 0 && !pd.actors.hero.perks.BloodShield.convert(hero, actual)) hero.HP += actual;
	}
}
