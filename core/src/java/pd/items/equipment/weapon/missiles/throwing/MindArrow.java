/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.BeCorrupt;
import pd.actors.buffs.Buff;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.InlineText;

public class MindArrow extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MindArrow.class)
			.t("name", "意识之矢")
			.t("desc", "利用积累的疯狂伤害并腐化目标。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		baseUses = 1;
		DLY = 0.1f;
		levelKnown = true;
		bones = false;
	}

	public MindArrow() { this(1); }
	public MindArrow(int number) { quantity(number); }
	@Override public int min(int lvl) { return 0; }
	@Override public int max(int lvl) { return 10; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public Item random() { return quantity(2); }
	@Override public int value() { return 0; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		int points = Dungeon.hero == null ? 0 : Math.max(0, Dungeon.hero.spp);
		if (points > 0) defender.damage(points, this);
		Buff.affect(defender, BeCorrupt.class).level(points);
		return result;
	}
}
