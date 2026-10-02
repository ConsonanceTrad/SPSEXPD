/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.Char;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.potions.PotionOfMending;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.sprites.LitDemonSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class LitDemon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LitDemon.class)
			.t("name", "链锯魔")
			.t("desc", "这个长着电锯的恶魔好可爱啊。它能用电锯连续切割敌人。");
	}



	{
		spriteClass = LitDemonSprite.class; cooldown = 50; properties.add(Property.DEMONIC); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.LIT_DEMON; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof PotionOfMending; }
	@Override public Item SupercreateLoot() { return new ScrollOfRage(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel(), 5 + petLevel() * 2); }
	@Override public int drRoll() { return Random.IntRange(0, petLevel() * 2); }
	@Override public int attackSkill(Char target) { return petLevel() + 10; }
	@Override public int attackProc(Char enemy, int damage) {
		if (enemy == null) return super.attackProc(null, damage);
		int fragmentMax = Math.max(1, damage / 5);
		enemy.damage(Random.IntRange(1, fragmentMax), Item.class);
		if (cooldown <= 0 && enemy.isAlive()) {
			for (int i = 0; i < 5; i++) enemy.damage(Random.IntRange(1, fragmentMax), Item.class);
			cooldown = Math.max(6, 26 - petLevel());
		}
		if (cooldown > 0) cooldown--;
		return super.attackProc(enemy, damage);
	}
}
