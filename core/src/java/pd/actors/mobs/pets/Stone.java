/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.Paralysis;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.sprites.StoneSprite;
import render.utils.math.Random;
import pd.messages.InlineText;
public class Stone extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Stone.class)
			.t("name", "石拳石")
			.t("desc", "破碎中的一把武器是它的亲戚，但是它的父母来自精灵宝可梦。");
	}



	{ spriteClass=StoneSprite.class;cooldown=50;properties.add(Property.ELEMENT);updateStats(true); }
	@Override protected Kind kind(){return Kind.STONE;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof MissileWeapon;}
	@Override public Item SupercreateLoot(){return Generator.random(Generator.Category.NORNSTONE);}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel()*3/2;if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel()/2,5+petLevel()*3/2);}
	@Override public int drRoll(){return Random.IntRange(petLevel()*2,Math.max(petLevel()*2,petLevel()*5));}
	@Override public int attackSkill(Char target){return petLevel()+5;}
	@Override public int attackProc(Char enemy,int damage){if(enemy!=null&&Random.Int(20)==0)Buff.affect(enemy,Paralysis.class,3f);cooldown--;return super.attackProc(enemy,damage);}
	@Override public int defenseProc(Char enemy,int damage){if(enemy!=null&&cooldown<=0){Buff.affect(enemy,HolyStun.class,5f);cooldown=Math.max(10,30-petLevel());}return super.defenseProc(enemy,damage);}
}
