/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PetFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fusion.Nut;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkeySprite;
import com.watabou.utils.Random;
public class Monkey extends PET {
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
