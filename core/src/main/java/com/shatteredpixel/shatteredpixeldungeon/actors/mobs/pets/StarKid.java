/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LightShootAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PetFood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StarKidSprite;
import com.watabou.utils.Random;
public class StarKid extends PET {
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
