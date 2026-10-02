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
import pd.items.equipment.armor.Armor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bags.Bag;
import pd.items.consum.food.Food;
import pd.items.consum.potions.Potion;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

public class NmImbue extends Buff implements Hero.Doom {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(NmImbue.class)
			.t("name", "纳米环绕")
			.t("burnsup", "%s被同化了！")
			.t("desc", "纳米机器人环绕着你。它们形成的云雾会伤害敌人，但也会定期同化携带的装备以继续增殖。");
	}


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
