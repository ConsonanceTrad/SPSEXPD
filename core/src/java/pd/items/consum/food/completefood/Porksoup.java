/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class Porksoup extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Porksoup.class)
			.t("name", "排骨汤")
			.t("desc", "你可以看到浮在上面的油水。\n使用_1份蔬菜、1份肉、1份水_炼制。");
	}


	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 200f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public int value() { return 3 * quantity; }
}
