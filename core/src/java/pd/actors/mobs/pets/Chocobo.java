/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.items.Item;
import pd.items.consum.eggs.RandomEasterEgg;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.vegetable.Vegetable;
import pd.sprites.ChocoboSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Chocobo extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Chocobo.class)
			.t("name", "陆行鸟")
			.t("desc", "在地表世界常见的一种鸟类，成年后的个体甚至能载人飞奔，有力的双足和温和的个性是它们的特点，为了保护主人有时也会展现出好战的一面。");
	}

	{
		spriteClass = ChocoboSprite.class;
		cooldown = 50;
		properties.add(Property.BEAST);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.CHOCOBO; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Vegetable; }
	@Override public Item SupercreateLoot() { return new RandomEasterEgg(); }

	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 2;
		defenseSkill = petLevel() * 3 / 2;
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel() * 2, Math.max(petLevel() * 2, petLevel() * 5)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }

	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0) Buff.affect(this, HasteBuff.class, 5f);
		cooldown--;
		return super.attackProc(enemy, damage);
	}

	@Override public int defenseProc(Char enemy, int damage) {
		if (cooldown <= 0 && Dungeon.hero != null) {
			Buff.affect(Dungeon.hero, HasteBuff.class, petLevel());
			cooldown = Math.max(10, 30 - petLevel());
		}
		return super.defenseProc(enemy, damage);
	}
}
