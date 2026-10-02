/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.sprites.ItemSprite;

public class ShockMeat extends MeatFood {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x00FF00);
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { ShockMeat result = new ShockMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, Drowsy.class);
		Buff.detach(hero, Slow.class);
		Buff.detach(hero, Vertigo.class);
	}
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public int value() { return 3 * quantity; }
}
