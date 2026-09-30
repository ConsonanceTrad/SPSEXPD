/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.items.Amulet;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.artifacts.MasterThievesArmband;
import pd.items.misc.LuckyBadge;
import pd.messages.Messages;
import pd.sprites.BanditKingSprite;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.Bundle;
import render.utils.Random;

/** Dormant SPS-PD mob retained with its original amulet-stealing behavior. */
public class BlueCat extends Mob {

	public Item item;

	{
		spriteClass = BanditKingSprite.class;
		HP = HT = 20 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
		defenseSkill = 8 + legacyDepthAdjustment(0);
		EXP = 5;
		loot = Generator.Category.BERRY;
		lootChance = 1f;
		FLEEING = new BlueCatFleeing();
		properties.add(Property.ELF);
	}

	private static final String ITEM = "item";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ITEM, item);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		item = (Item) bundle.get(ITEM);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(1, 7 + legacyDepthAdjustment(0)); }
	@Override public float attackDelay() { return 0.5f; }
	@Override public int attackSkill(Char target) { return 120; }
	@Override public int drRoll() { return 3; }

	public static float armbandChance(int luckBonus) {
		return 0.01f + 0.02f * luckBonus;
	}

	@Override
	public Item createLoot() {
		if (Random.Float() < armbandChance(LuckyBadge.luckBonus(Dungeon.hero))) {
			return new MasterThievesArmband().identify();
		}
		return Generator.random(Generator.Category.BERRY);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		if (item != null && Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(item, pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
			item = null;
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (item == null && enemy instanceof Hero && steal((Hero) enemy)) state = FLEEING;
		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING && Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(new Gold(), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		return damage;
	}

	protected boolean steal(Hero hero) {
		Amulet amulet = hero.belongings.getItem(Amulet.class);
		if (amulet == null) return false;
		if (com.badlogic.gdx.Gdx.app != null) {
			GLog.w(Messages.get(BlueCat.class, "stole", amulet.name()));
		}
		item = amulet.detachAll(hero.belongings.backpack);
		Item.updateQuickslot();
		return true;
	}

	@Override
	public String description() {
		String desc = super.description();
		if (item != null) desc += Messages.get(this, "carries", item.name());
		return desc;
	}

	private class BlueCatFleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null) {
				if (sprite != null) sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}
}
