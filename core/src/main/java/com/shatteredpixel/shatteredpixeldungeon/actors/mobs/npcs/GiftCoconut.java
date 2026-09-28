/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;

public class GiftCoconut extends GiftNpc {
	{ properties.add(Property.MECH); properties.add(Property.BEAST); }
	@Override public Visual visual() { return Visual.COCONUT; }
	@Override public boolean acceptsGift(Item item) {
		return named(item, "NutCake", "AlienBag", "CocoCatEgg", "GoldAmmo", "PotionOfMixing",
				"TestArmor", "TestWeapon", "WandOfTest", "TestCloak");
	}
	@Override protected GiftResult reward(Hero hero) {
		switch (friendship()) {
			case 30: hero.improveAttackSkill(1); return result("reward2");
			case 50: hero.improveDefenseSkill(1); return result("reward3");
			case 70: hero.improveMagicSkill(1); return result("reward4");
			case 100: hero.STR++; return result("reward5");
			default:
				return friendship() % 40 == 0 ? result("reward1", new DungeonBomb()) : result("thank1");
		}
	}
}
