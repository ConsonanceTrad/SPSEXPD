/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.actors.hero.Hero;
import pd.items.consum.food.fruit.Fruit;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GoldenNut extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldenNut.class)
			.t("name", "受hmdzl001祝福的金色坚果")
			.t("desc", "由hmdzl001亲自种植的秘密坚果，食用后会获得永久祝福。")
			.t("effect_1", "全技能+1，力量+1，生命上限+40")
			.t("effect_2", "力量+3，生命上限+10");
	}



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
