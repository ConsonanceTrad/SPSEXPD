/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.actors.hero.Hero;
import pd.items.food.fruit.Fruit;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import render.utils.math.Random;

public class GoldenNut extends Fruit {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 100f; hornValue = 2; }

	@Override protected void onEat(Hero hero) {
		applyBlessing(hero, Random.Int(2));
		Badges.validateStrengthAttained();
	}

	protected void applyBlessing(Hero hero, int blessing) {
		if (blessing == 0) {
			hero.HTBoost += 40;
			hero.STR++;
			hero.improveCombatSkills(1);
			hero.improveMagicSkill(1);
			hero.updateHT(true);
			if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "effect_1"));
		} else {
			hero.HTBoost += 10;
			hero.STR += 3;
			hero.updateHT(true);
			if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "effect_2"));
		}
	}
}
