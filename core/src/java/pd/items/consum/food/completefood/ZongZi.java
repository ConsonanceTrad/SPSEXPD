/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Tar;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ZongZi extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ZongZi.class)
			.t("name", "粽子")
			.t("desc", "厚重的糯米粽，能提高攻击并提供魔法护盾，但会使食用者沾满焦油并变得迟缓。");
	}



	{
		image = ConsumFoodFoodDict.ZONGZI;
		energy = 600f;
		stackable = false;
	}
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Tar.class);
		Buff.affect(hero, Slow.class, 30f);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}
	@Override public int value() { return 60 * quantity; }
}
