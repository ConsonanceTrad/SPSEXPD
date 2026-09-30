package pd.items.food.fusion;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.food.Food;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Nut extends Food {

	{
		image = ItemSpriteSheet.PASTY;
		energy = Hunger.HUNGRY / 6f;
		hornValue = 1;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		hero.HP = Math.min(hero.HT, hero.HP + 1);
		if (Random.Int(10) == 0) {
			Buff.affect(hero, Barkskin.class).set(2 + hero.lvl / 4, 1);
		}
	}

	@Override
	public int value() {
		return 2 * quantity;
	}
}
