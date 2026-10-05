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
import pd.actors.blobs.Web;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class RootFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_EARTHROOT;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RootFruit.class)
			.t("name", "地缚果实")
			.t("desc", "人工种植的地缚根结出的果实。落地会长出高草，命中则束缚目标。");
	}



	public RootFruit() { this(1); }
	public RootFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_EARTHROOT, 20, 20); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			if (Dungeon.level != null && Dungeon.level.insideMap(cell)) {
				pd.levels.Level.set(cell, pd.levels.Terrain.HIGH_GRASS);
				GameScene.updateMap(cell);
			}
		} else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Roots.class, 3f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Roots.class, 2f);
	}
}
