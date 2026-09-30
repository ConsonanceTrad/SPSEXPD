/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.armor.specialarmor.RenBArmor;
import render.utils.math.Random;

public class GiftRen extends GiftNpc {
	{
		properties.add(Property.ELF);
	}
	@Override public Visual visual() { return Visual.REN; }
	@Override public boolean acceptsGift(Item item) {
		return named(item, "MiniMoai", "HugeShuriken", "ActiveMrDestructo", "Mobile", "ToyGun",
				"TrickSand", "MirrorDoll", "WindBottle", "HandLight", "CurseBox", "HolyWater",
				"PrayerWheel", "Triangolo", "Flute", "Wardrum", "Trumpet", "Harp", "Club",
				"RunicBlade", "Rapier", "Lance", "AresSword", "CromCruachAxe", "JupitersWraith",
				"LokisFlail", "NeptunusTrident", "WandOfFlock", "WandOfFlow", "WandOfTCloud",
				"StoneArmor", "CeramicsArmor", "ProtectiveclothingArmor", "MachineArmor",
				"StyrofoamArmor", "Strawberry", "Cherry", "Nut", "PerfectFood", "BlueMilk",
				"DeathCap", "Earthstar", "JackOLantern", "PixieParasol", "GoldenJelly",
				"GreenSpore", "RingOfKnowledge", "ScrollOfDummy", "ScrollOfRegrowth");
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward5", new RenBArmor());
		if (friendship() % 20 == 0) return result("reward" + Random.IntRange(1, 4),
				Generator.random(Generator.Category.NORNSTONE));
		return result("thank" + Random.IntRange(1, 3));
	}
}
