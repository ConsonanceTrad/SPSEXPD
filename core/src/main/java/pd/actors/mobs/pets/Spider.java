/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Web;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.items.Item;
import pd.items.armor.normalarmor.WoodenArmor;
import pd.items.food.completefood.PetFood;
import pd.items.food.meatfood.MeatFood;
import pd.scenes.GameScene;
import pd.sprites.NewSpinnerSprite;
import render.utils.Random;
public class Spider extends PET {
	{ spriteClass=NewSpinnerSprite.class;cooldown=50;properties.add(Property.PLANT);updateStats(true); }
	@Override protected Kind kind(){return Kind.SPIDER;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof MeatFood;}
	@Override public Item SupercreateLoot(){return new WoodenArmor();}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel()*3/2;if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel()/2,5+petLevel()*5/2);}
	@Override public int drRoll(){return Random.IntRange(petLevel()*2,Math.max(petLevel()*2,petLevel()*5));}
	@Override public int attackSkill(Char target){return petLevel()+5;}
	@Override public int attackProc(Char enemy,int damage){if(enemy!=null&&Random.Int(10)==0){Buff.affect(enemy,Poison.class).set(Random.IntRange(petLevel(),petLevel()+1));GameScene.add(Blob.seed(enemy.pos,Random.IntRange(4,5),Web.class));}cooldown--;return super.attackProc(enemy,damage);}
	@Override public int defenseProc(Char enemy,int damage){if(enemy!=null&&cooldown<=0){Buff.affect(enemy,Poison.class).set(Random.IntRange(petLevel()*3/2,petLevel()*2));GameScene.add(Blob.seed(enemy.pos,Random.IntRange(5,6),Web.class));cooldown=Math.max(10,30-petLevel());}return super.defenseProc(enemy,damage);}
}
