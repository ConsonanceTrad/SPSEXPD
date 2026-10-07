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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.messages.InlineText;

public class RotFruit extends SpsFruit {
	static {
		InlineText.of(RotFruit.class)
			.t("name", "腐莓果实")
			.t("desc", "人工种植的腐梅结出的果实。落地会散出剧毒气体，命中则让目标身中猛毒。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_ROT_BERRY; }//SPSEXPD: 贴图待指认

	public RotFruit() { this(1); }
	public RotFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_ROT_BERRY, 10, 10); quantity(number); }




	@Override protected void onThrow(int cell) {
		//SPSEXPD: 小型 3×3、大型 5×5 圆形；浓度整体 ×3
		if (landsAt(cell)) seedArea(cell, scaled(8), ToxicGas.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, LokisPoison.class).set(scaled(4f));
		return super.proc(attacker, defender, 0);
	}

	/** SPSEXPD: 食用会让自己中猛毒——默认动作为「投掷」。 */
	@Override protected boolean harmfulOnEat() { return true; }

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, LokisPoison.class).set(scaled(6f));
	}
}
