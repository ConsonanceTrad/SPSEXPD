/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class GoldenNut extends Fruit {
	{ image = ItemSpriteSheet.GOLDEN_NUT; energy = 100f; hornValue = 2; }

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
