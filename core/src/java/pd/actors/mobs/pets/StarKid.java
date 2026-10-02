/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LightShootAttack;
import pd.actors.damagetype.DamageType;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.potions.PotionOfExperience;
import pd.sprites.StarKidSprite;
import render.utils.math.Random;
import pd.messages.InlineText;
public class StarKid extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(StarKid.class)
			.t("name", "星芒")
			.t("desc", "星之子，能够用光属性力量攻击敌人。");
	}



	{ spriteClass=StarKidSprite.class;cooldown=50;properties.add(Property.ALIEN);updateStats(true); }
	@Override protected Kind kind(){return Kind.STAR_KID;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof StoneOre;}
	@Override public Item SupercreateLoot(){return new PotionOfExperience();}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel();if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel(),5+petLevel()*2);}
	@Override public int drRoll(){return Random.IntRange(0,petLevel()*2);}
	@Override public int attackSkill(Char target){return petLevel()+10;}
	@Override public int attackProc(Char enemy,int damage){if(enemy==null)return 0;if(cooldown<=0&&enemy.isAlive()){Buff.affect(enemy,LightShootAttack.class).level(petLevel());cooldown=Math.max(9,29-petLevel());}if(cooldown>0)cooldown--;enemy.damage(damageRoll(),DamageType.LIGHT_DAMAGE);return super.attackProc(enemy,0);}
}
