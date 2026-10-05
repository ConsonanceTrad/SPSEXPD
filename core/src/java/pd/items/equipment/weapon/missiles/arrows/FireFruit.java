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
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class FireFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_FIREBLOOM;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FireFruit.class)
			.t("name", "火焰果实")
			.t("desc", "人工种植的火焰花结出的果实。落地会燃起火焰，命中则点燃目标。");
	}



	public FireFruit() { this(1); }
	public FireFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_FIREBLOOM, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seedArea(cell, 6, pd.actors.blobs.effectblobs.Fire.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender, 2f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Burning.class).reignite(hero, 4f);
	}
}
