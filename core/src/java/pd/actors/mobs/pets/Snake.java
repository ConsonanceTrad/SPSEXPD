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
import pd.messages.InlineText;
public class Snake extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Snake.class)
			.t("name", "毒蛇")
			.t("desc", "这条蛇过去常常和它的兄弟在月光的柔和光辉下享受生活，直到它决定跟随流浪的欲望进入伟大的未知世界。");
	}



	{ spriteClass=NewSnakeSprite.class;cooldown=50;properties.add(Property.BEAST);updateStats(true); }
	@Override protected Kind kind(){return Kind.SNAKE;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof MeatFood;}
	@Override public Item SupercreateLoot(){return new PotionOfToxicGas();}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel();if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel(),5+petLevel()*2);}
	@Override public int drRoll(){return Random.IntRange(0,petLevel()*2);}
	@Override public int attackSkill(Char target){return petLevel()+10;}
	@Override public boolean hasAbility(){return true;}

	/** 技能是剧毒撕咬（造成目标当前生命三分之一的伤害） */
	@Override protected boolean castOn(Char target){
		if(target==null||target==this||!target.isAlive())return false;
		target.damage(Math.max(1,target.HP/3),this);
		cooldown=Math.max(5,25-petLevel());
		return true;
	}

	@Override public int attackProc(Char enemy,int damage){if(enemy==null)return damage;if(Random.Int(10)==0&&enemy.isAlive())Buff.affect(enemy,Poison.class).set(Random.IntRange(5,6));if(cooldown<=0)castOn(enemy);if(cooldown>0)cooldown--;return super.attackProc(enemy,damage);}
}
