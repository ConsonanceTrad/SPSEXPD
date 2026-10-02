/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.sprites.NewSnakeSprite;
import render.utils.math.Random;
public class Snake extends PET {
	{ spriteClass=NewSnakeSprite.class;cooldown=50;properties.add(Property.BEAST);updateStats(true); }
	@Override protected Kind kind(){return Kind.SNAKE;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof MeatFood;}
	@Override public Item SupercreateLoot(){return new PotionOfToxicGas();}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel();if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel(),5+petLevel()*2);}
	@Override public int drRoll(){return Random.IntRange(0,petLevel()*2);}
	@Override public int attackSkill(Char target){return petLevel()+10;}
	@Override public int attackProc(Char enemy,int damage){if(enemy==null)return damage;if(Random.Int(10)==0&&enemy.isAlive())Buff.affect(enemy,Poison.class).set(Random.IntRange(5,6));if(cooldown<=0&&enemy.isAlive()){enemy.damage(Math.max(1,enemy.HP/3),this);cooldown=Math.max(5,25-petLevel());}if(cooldown>0)cooldown--;return super.attackProc(enemy,damage);}
}
