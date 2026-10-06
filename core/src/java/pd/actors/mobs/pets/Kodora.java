/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicWeak;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.potions.PotionOfLiquidFlame;
import pd.items.equipment.wands.WandOfMagicMissile;
import pd.sprites.KodoraSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Kodora extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Kodora.class)
			.t("name", "柯多拉")
			.t("desc", "希望成为出色巨龙的幼龙，懂得很多知识但依旧还是个菜鸟，甚至不能自如地吐出火焰，但是只要得到鼓励就会充满干劲。");
	}



	{
		spriteClass = KodoraSprite.class; cooldown = 50; properties.add(Property.DRAGON); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.KODORA; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof MeatFood; }
	@Override public Item SupercreateLoot() { return new PotionOfLiquidFlame(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel(), 5 + petLevel() * 2); }
	@Override public int drRoll() { return Random.IntRange(0, petLevel() * 2); }
	@Override public int attackSkill(Char target) { return petLevel() + 10; }
	@Override public boolean hasAbility() { return true; }

	/** 技能是魔法易伤 */
	@Override protected boolean castOn(Char target) {
		if (target == null || target == this || !target.isAlive()) return false;
		Buff.affect(target, MagicWeak.class, petLevel() * 2f);
		cooldown = Math.max(5, 25 - petLevel());
		return true;
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (enemy == null) return 0;
		if (cooldown <= 0) castOn(enemy);
		if (cooldown > 0) cooldown--;
		enemy.damage(damageRoll(), WandOfMagicMissile.class);
		return super.attackProc(enemy, 0);
	}
}
