/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.items.Item;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.items.food.WaterItem;
import pd.items.food.completefood.PetFood;
import pd.plants.Dewcatcher;
import pd.plants.Plant;
import pd.sprites.DaturaSprite;
import com.watabou.utils.Random;

public class Datura extends PET {
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
