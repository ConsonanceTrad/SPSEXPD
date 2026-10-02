package pd.items.consum.food.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Nut extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Nut.class)
			.t("name", "节庆坚果")
			.t("eat_msg", "坚果虽小，吃起来却很扎实。")
			.t("desc", "从异界保存下来的节庆零食。食用后可恢复少量饱食度与生命，并有小概率获得短暂的树肤保护。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
