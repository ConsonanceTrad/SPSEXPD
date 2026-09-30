/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Wet;
import pd.actors.damagetype.DamageType;
import pd.items.Generator;
import pd.items.Item;
import pd.items.wands.WandOfAcid;
import pd.items.wands.WandOfSwamp;
import pd.sprites.DustElementSprite;
import watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the dust elemental. */
public class DustElement extends SpsSewerMobs.DustElement {

	{
		spriteClass = DustElementSprite.class;
		properties.remove(Property.INORGANIC);
		properties.add(Property.ELEMENT);
		resistances.add(DamageType.Earth.class);
		resistances.add(WandOfAcid.class);
		resistances.add(Ooze.class);
		resistances.add(WandOfSwamp.class);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) == 0) Buff.prolong(enemy, Blindness.class, Random.IntRange(3, 9));
		enemy.damage(damageRoll(), DamageType.EARTH_DAMAGE);
		return 0;
	}

	@Override
	public boolean add(Buff buff) {
		if (buff instanceof Wet) {
			boolean inWater = Dungeon.level != null && Dungeon.level.insideMap(pos) && Dungeon.level.water[pos];
			damage(Random.NormalIntRange(inWater ? HT / 2 : 1, inWater ? HT : HT * 2 / 3), buff);
			return false;
		}
		return super.add(buff);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.NORNSTONE);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.NORNSTONE;
	}
}
