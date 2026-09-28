/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wet;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfAcid;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfSwamp;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DustElementSprite;
import com.watabou.utils.Random;

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
