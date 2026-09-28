/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.actbuff;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.NmGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class NmImbue extends Buff implements Hero.Doom {

	{ immunities.add(NmGas.class); }

	@Override
	public boolean act() {
		if (Dungeon.level != null && Dungeon.level.insideMap(target.pos)) {
			GameScene.add(Blob.seed(target.pos, 30, NmGas.class));
		}

		if (target.isAlive() && target instanceof Hero) {
			Hero hero = (Hero) target;
			Nmstop stop = hero.buff(Nmstop.class);
			int threshold = Statistics.deepestFloor * 10;
			boolean assimilate = ((Random.Int(250) < hero.spp && hero.spp < threshold + 20)
					&& stop == null) || hero.spp == 0;
			if (assimilate) {
				assimilateRandomItem(hero);
			} else if (hero.spp > threshold && stop == null && Random.Int(20) == 0) {
				Buff.affect(hero, Nmstop.class, 720f);
			} else if (stop != null && hero.spp > threshold) {
				hero.spp--;
			}
		}

		spend(TICK);
		return true;
	}

	private static void assimilateRandomItem(Hero hero) {
		Item item = hero.belongings.randomUnequipped();
		if (item instanceof Bag) item = Random.element(((Bag) item).items);
		if (!canAssimilate(item)) return;
		Item removed = item.detach(hero.belongings.backpack);
		if (removed == null) return;
		GLog.w(Messages.get(NmImbue.class, "burnsup", removed.toString()));
		hero.spp++;
		if (hero.sprite != null) com.shatteredpixel.shatteredpixeldungeon.items.Heap.burnFX(hero.pos);
	}

	static boolean canAssimilate(Item item) {
		return item != null && !item.unique && (item instanceof Scroll
				|| item instanceof Potion || item instanceof Food || item instanceof Wand
				|| item instanceof Plant.Seed || item instanceof Weapon || item instanceof Armor
				|| item instanceof Ring || item instanceof Artifact);
	}

	@Override public int icon() { return BuffIndicator.POISON; }
	@Override public void onDeath() {
		Badges.validateDeathFromFire();
		Dungeon.fail(this);
	}
}
