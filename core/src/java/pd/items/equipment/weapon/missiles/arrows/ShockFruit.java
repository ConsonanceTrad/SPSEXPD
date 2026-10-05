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
import pd.actors.blobs.damageblobs.ShockEffectDamage;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class ShockFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_STORMVINE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShockFruit.class)
			.t("name", "风暴果实")
			.t("desc", "人工种植的风暴藤结出的果实。落地会释放电流，命中则麻痹目标。");
	}



	public ShockFruit() { this(1); }
	public ShockFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_STORMVINE, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seedArea(cell, 6, ElectriShock.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Paralysis.class, 1f);
	}
}
