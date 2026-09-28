/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.items.AdamantRing;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.Random;
public class ThiefKing extends Mob {
	{spriteClass=ThiefKingSprite.class;HP=HT=2000;defenseSkill=28;EXP=60;flying=true;loot=Generator.Category.SCROLL;lootChance=1f;properties.add(Property.ELF);properties.add(Property.BOSS);resistances.add(Electricity.class);}
	@Override public int damageRoll(){return Random.NormalIntRange(20,70);}
	@Override public int attackSkill(Char target){return 25;}
	@Override public int drRoll(){return Random.NormalIntRange(6,14);}
	@Override protected boolean canAttack(Char enemy){return buff(Locked.class)!=null?Dungeon.level.adjacent(pos,enemy.pos)&&!isCharmedBy(enemy):new Ballistica(pos,enemy.pos,Ballistica.PROJECTILE).collisionPos==enemy.pos;}
	@Override public int attackProc(Char enemy,int damage){if(!Dungeon.level.adjacent(pos,enemy.pos)){if(Random.Int(10)==0)Buff.prolong(enemy,Slow.class,2f);if(Random.Int(10)==0)Buff.affect(enemy,Poison.class).set(Random.Int(7,9));}return super.attackProc(enemy,damage);}
	@Override public void notice(){super.notice();BossHealthBar.assignBoss(this);yell(Messages.get(this,"notice",Dungeon.hero==null?"":Dungeon.hero.name()));}
	@Override public void die(Object cause){int cell=pos;super.die(cause);Dungeon.banditKingKilled=true;AdventureJournal.complete(13);Dungeon.level.unseal();GameScene.bossSlain();Dungeon.level.drop(new AdamantRing(),cell).sprite.drop();Dungeon.level.drop(new Gold(Random.Int(1900,4000)),cell).sprite.drop();yell(Messages.get(this,"die"));}
}
