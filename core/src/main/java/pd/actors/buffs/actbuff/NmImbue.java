/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.actbuff;

import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.blobs.Blob;
import pd.actors.blobs.NmGas;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.artifacts.Artifact;
import pd.items.bags.Bag;
import pd.items.food.Food;
import pd.items.potions.Potion;
import pd.items.rings.Ring;
import pd.items.scrolls.Scroll;
import pd.items.wands.Wand;
import pd.items.weapon.Weapon;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import watabou.utils.Random;

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
		if (hero.sprite != null) pd.items.Heap.burnFX(hero.pos);
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
