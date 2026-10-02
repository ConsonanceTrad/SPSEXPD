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
import pd.items.equipment.wands.WandOfAcid;
import pd.items.equipment.wands.WandOfSwamp;
import pd.sprites.DustElementSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the dust elemental. */
public class DustElement extends SpsSewerMobs.DustElement {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DustElement.class)
			.t("name", "尘埃元素")
			.t("blind", "灰尘阻碍了你的视线。")
			.t("desc", "年久失修的下水道里面的尘埃和向外扩散的黑暗力量融合，生成了这一种羸弱的元素。");
	}




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
