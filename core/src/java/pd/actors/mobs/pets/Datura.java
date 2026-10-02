/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.items.Item;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.PetFood;
import pd.plants.Dewcatcher;
import pd.plants.Plant;
import pd.sprites.DaturaSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Datura extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Datura.class)
			.t("name", "曼陀罗")
			.t("desc", "通过浇灌牛奶使其获得活动能力的植物类魔法生物，喜欢跟随着体积大的生物身后奔跑，但同时又非常害怕牲畜，所以一般会选择跟在人类的身后。与记载中不同，它是一种非常安静的生物。");
	}



	{
		spriteClass = DaturaSprite.class;
		cooldown = 50;
		properties.add(Property.PLANT);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.DATURA; }
	@Override public boolean lovefood(Item item) {
		return item instanceof PetFood || item instanceof WaterItem || item instanceof Plant.Seed;
	}
	@Override public Item SupercreateLoot() { return new Dewcatcher.Seed(); }

	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 2;
		defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel(), Math.max(petLevel(), petLevel() * 3)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }

	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0) Buff.affect(enemy, Terror.class, petLevel() * 2f).object = id();
		return super.attackProc(enemy, damage);
	}

	@Override public int defenseProc(Char enemy, int damage) {
		if (Dungeon.level != null && Random.Int(5) == 0) Dungeon.level.drop(new YellowDewdrop(), pos).sprite.drop();
		cooldown--;
		if (Dungeon.level != null && cooldown < 0) {
			Dungeon.level.drop(new VioletDewdrop(), pos).sprite.drop();
			cooldown = Math.max(25, 50 - petLevel());
		}
		return super.defenseProc(enemy, damage);
	}
}
