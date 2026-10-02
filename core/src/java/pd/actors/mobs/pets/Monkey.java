/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;
import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.fusion.Nut;
import pd.plants.Plant;
import pd.sprites.MonkeySprite;
import render.utils.math.Random;
import pd.messages.InlineText;
public class Monkey extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Monkey.class)
			.t("name", "绿皮猴")
			.t("desc", "严格意义上它并不是猴子，它只是名字上带猴而已。");
	}

	{ spriteClass = MonkeySprite.class; cooldown = 50; properties.add(Property.HUMAN); updateStats(true); }
	@Override protected Kind kind() { return Kind.MONKEY; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Fruit || item instanceof Nut; }
	@Override public Item SupercreateLoot() { return Generator.random(Generator.Category.BERRY); }
	@Override public void updateStats(boolean refill) { int old=HT; HT=150+petLevel()*2; defenseSkill=petLevel(); if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old); }
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level != null && Dungeon.level.distance(pos, enemy.pos) <= 2; }
	@Override public int damageRoll() { return Random.NormalIntRange(5+petLevel()/2,5+petLevel()*3/2); }
	@Override public int drRoll() { return Random.IntRange(petLevel(),Math.max(petLevel(),petLevel()*3)); }
	@Override public int attackSkill(Char target) { return petLevel()+5; }
	@Override public int attackProc(Char enemy,int damage) { cooldown--; if(Dungeon.level!=null&&enemy!=null&&cooldown<=0&&Random.Int(4)==0){Dungeon.level.drop(Generator.random(Generator.Category.BERRY),enemy.pos).sprite.drop();cooldown=Math.max(15,45-petLevel());} return super.attackProc(enemy,damage); }
}
