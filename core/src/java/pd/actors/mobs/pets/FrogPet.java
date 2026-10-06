/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.equipment.weapon.melee.Whip;
import pd.plants.Plant;
import pd.sprites.FrogPetSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FrogPet extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FrogPet.class)
			.t("name", "呆头蛙")
			.t("desc", "这只绿色的青蛙能攻击较远的敌人，并可能在完成致命一击时找到物品。");
	}



	{
		spriteClass = FrogPetSprite.class; cooldown = 50; properties.add(Property.BEAST); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.FROG; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Plant.Seed || item instanceof Vegetable || item instanceof Fruit; }
	@Override public Item SupercreateLoot() { return new Whip(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level != null && Dungeon.level.distance(pos, enemy.pos) <= 2; }
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel(), Math.max(petLevel(), petLevel() * 3)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }
	/** 掉落型/无目标技能：施法目标是自身 */
	@Override protected Char castTarget() { return this; }

	@Override public boolean hasAbility() { return true; }

	/** 技能是掉落随机物品 */
	@Override protected boolean castOn(Char target) {
		if (Dungeon.level == null) return false;
		Dungeon.level.drop(Generator.random(), pos).sprite.drop();
		cooldown = Math.max(15, 45 - petLevel());
		return true;
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (cooldown > 0) cooldown--;
		if (Dungeon.level != null && enemy != null && damage > enemy.HP && cooldown <= 0) castOn(enemy);
		return super.attackProc(enemy, damage);
	}
}
