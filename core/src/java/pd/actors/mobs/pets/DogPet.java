/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.items.Item;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.meatfood.MeatFood;
import pd.sprites.DogPetSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DogPet extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DogPet.class)
			.t("name", "忠犬")
			.t("desc", "在地表世界追随你旅行的大狗，无论是在何处，哪怕是怪物丛生的地下，它依旧会尽忠尽责地守护你的安全。");
	}



	{
		spriteClass = DogPetSprite.class;
		cooldown = 50;
		properties.add(Property.BEAST);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.DOG; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof MeatFood; }
	@Override public Item SupercreateLoot() { return new MoonCake(); }

	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 2;
		defenseSkill = petLevel() * 3 / 2;
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel() * 2, Math.max(petLevel() * 2, petLevel() * 5)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }

	@Override protected boolean act() {
		supportHero();
		return super.act();
	}

	void supportHero() {
		if (cooldown <= 0 && castTarget() != null) castOn(castTarget());
	}

	@Override public boolean hasAbility() { return true; }

	/** 技能目标是相邻的英雄（护盾） */
	@Override protected Char castTarget() {
		return (Dungeon.hero != null && Dungeon.level != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos))
				? Dungeon.hero : null;
	}

	@Override protected boolean castOn(Char target) {
		Buff.affect(target, ShieldArmor.class).level(petLevel() * 2);
		Buff.affect(this, ShieldArmor.class).level(petLevel() * 2);
		cooldown = Math.max(20, 40 - petLevel());
		return true;
	}
	@Override public int attackProc(Char enemy, int damage) { cooldown--; return super.attackProc(enemy, damage); }
	@Override public int defenseProc(Char enemy, int damage) { cooldown--; return super.defenseProc(enemy, damage); }
}
