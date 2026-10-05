/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.items.Dewdrop;
import pd.messages.InlineText;

public class FreshFruit extends SpsFruit {
	static {
		InlineText.of(FreshFruit.class)
			.t("name", "鲜莓果实")
			.t("desc", "人工种植的腐梅结出的果实。食用后额外恢复 25 点饱食度，命中则让双方各恢复少量饱食。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_ROT_BERRY; }//SPSEXPD: 贴图待指认

	public FreshFruit() { this(1); }
	public FreshFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_ROT_BERRY, 10, 10); quantity(number); }




	@Override public int proc(Char attacker, Char defender, int damage) {
		if (attacker != null) Buff.affect(attacker, Hunger.class).satisfy(25f);
		if (defender != null && defender != attacker) Buff.affect(defender, Hunger.class).satisfy(5f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Hunger.class).satisfy(25f);
	}
}
