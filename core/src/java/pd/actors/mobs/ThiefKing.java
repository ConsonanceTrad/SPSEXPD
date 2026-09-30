/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Electricity;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Slow;
import pd.items.AdamantRing;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.quest.AdventureJournal;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ThiefKingSprite;
import pd.ui.BossHealthBar;
import render.utils.math.Random;
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
