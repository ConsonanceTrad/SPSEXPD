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
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class ToxicFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_SORROWMOSS;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ToxicFruit.class)
			.t("name", "断肠果实")
			.t("desc", "人工种植的腐梅草结出的果实。落地会散出剧毒气体，命中则使目标中毒。");
	}



	public ToxicFruit() { this(1); }
	public ToxicFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_SORROWMOSS, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		//SPSEXPD: 小型 3×3、大型 5×5 圆形；浓度整体 ×3
		if (landsAt(cell)) seedArea(cell, scaled(4), ToxicGas.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		//SPSEXPD: 大型果实为小型果实的 3 倍
		Buff.affect(defender, Poison.class).set(Math.max(2f, scaled(Math.round(defender.HT * 0.03f))));
		return super.proc(attacker, defender, 0);
	}

	/** SPSEXPD: 食用会让自己中毒——默认动作为「投掷」。 */
	@Override protected boolean harmfulOnEat() { return true; }

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Poison.class).set(scaled(3f));
	}
}
