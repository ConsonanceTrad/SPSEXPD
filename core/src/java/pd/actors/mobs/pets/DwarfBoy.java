/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.items.Item;
import pd.items.consum.food.completefood.CompleteFood;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.staplefood.NormalRation;
import pd.sprites.DwarfBoySprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DwarfBoy extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DwarfBoy.class)
			.t("name", "矮人学徒")
			.t("desc", "一个勇敢的矮人，但不知为何打扮成羊的样子。");
	}



	{
		spriteClass = DwarfBoySprite.class;
		cooldown = 50;
		properties.add(Property.DWARF);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.DWARF_BOY; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof CompleteFood; }
	@Override public Item SupercreateLoot() { return new NormalRation(); }

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
		if (Random.Int(5) == 0) Buff.affect(enemy, Vertigo.class, 6f);
		return super.attackProc(enemy, damage);
	}
	@Override public int defenseProc(Char enemy, int damage) {
		if (enemy != null) enemy.damage(petLevel(), this);
		cooldown--;
		if (cooldown <= 0) {
			if (enemy != null) enemy.damage(petLevel() * 2, this);
			cooldown = Math.max(10, 30 - petLevel());
		}
		return super.defenseProc(enemy, damage);
	}
}
