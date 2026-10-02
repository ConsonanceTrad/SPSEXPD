/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Heap;
import pd.items.Item;
import pd.items.UpgradeBlobRed;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.plants.Plant;
import pd.sprites.FoxHelperSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FoxHelper extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FoxHelper.class)
			.t("name", "狐女仆")
			.t("desc", "有事她会干，没事嘛……她会定期给你带来升级卷轴。");
	}



	{
		spriteClass = FoxHelperSprite.class; cooldown = 50; properties.add(Property.ORC); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.FOX_HELPER; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Plant.Seed || item instanceof Vegetable || item instanceof Fruit; }
	@Override public Item SupercreateLoot() { return new UpgradeBlobRed(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel(), Math.max(petLevel(), petLevel() * 3)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }
	@Override protected boolean act() { supportHero(); return super.act(); }
	void supportHero() {
		if (Dungeon.hero != null && Dungeon.level != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos) && cooldown <= 0) {
			if (sprite != null) sprite.emitter().start(pd.effects.Speck.factory(
					pd.effects.Speck.UP), 0.4f, 1);
			Heap heap = Dungeon.level.drop(supportReward(), pos);
			if (heap.sprite != null) heap.sprite.drop();
			cooldown = Math.max(45, 65 - petLevel());
		}
	}
	protected Item supportReward() { return new ScrollOfUpgrade(); }
	@Override public int attackProc(Char enemy, int damage) { cooldown--; return super.attackProc(enemy, damage); }
	@Override public int defenseProc(Char enemy, int damage) { cooldown--; return super.defenseProc(enemy, damage); }
}
