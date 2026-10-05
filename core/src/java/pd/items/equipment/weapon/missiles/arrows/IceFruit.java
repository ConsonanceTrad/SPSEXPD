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
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class IceFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_ICECAP;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IceFruit.class)
			.t("name", "冰冠果实")
			.t("desc", "人工种植的寒冰草结出的果实。落地会散出冰霜云，命中则使目标寒冷。");
	}



	public IceFruit() { this(1); }
	public IceFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_ICECAP, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seedArea(cell, 6, FrostCloud.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Cold.class, 2f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Cold.class, 2f);
	}
}
