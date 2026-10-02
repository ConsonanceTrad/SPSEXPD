/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.MirrorShield;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.bombs.BuildBomb;
import pd.items.equipment.bombs.DarkBomb;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.bombs.EarthBomb;
import pd.items.equipment.bombs.FishingBomb;
import pd.items.equipment.bombs.HugeBomb;
import pd.items.equipment.bombs.IceBomb;
import pd.items.equipment.bombs.LightBomb;
import pd.items.equipment.bombs.SpsFireBomb;
import pd.items.equipment.bombs.StormBomb;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.items.equipment.weapon.missiles.ShitBall;
import pd.items.equipment.weapon.missiles.darts.PoisonDart;
import pd.items.equipment.weapon.missiles.fusion.RocketMissile;
import pd.items.equipment.weapon.missiles.throwing.EmpBola;
import pd.items.equipment.weapon.missiles.throwing.EscapeKnive;
import pd.items.equipment.weapon.missiles.throwing.Skull;
import pd.items.equipment.weapon.missiles.throwing.Wave;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

public class LinkSword extends NormalMeleeWeapon {
	public static final String AC_POWER="POWER", AC_WISDOM="WISDOM", AC_COURAGE="COURAGE";
	public static final int FULL_CHARGE=30;
	private static final String CHARGE="charge", UPTIME="uptime";
	private static final Class<?>[] LINK_DROP_CLASSES={
			BuildBomb.class,DungeonBomb.class,HugeBomb.class,SpsFireBomb.class,IceBomb.class,
			EarthBomb.class,StormBomb.class,LightBomb.class,DarkBomb.class,FishingBomb.class,
			RocketMissile.class,EmpBola.class,EscapeKnive.class,PoisonDart.class,
			Skull.class,Wave.class,ShitBall.class};
	private static final float[] LINK_DROP_WEIGHTS={3,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2};
	private int charge, uptime;
	public LinkSword(){super(1,1f,1f,1,1,5,SpecificPlaceHolderDict.SOMETHING_0);unique=true;reinforced=true;defaultAction=AC_COURAGE;usesTargeting=true;}
	@Override protected void applyLegacyUpgrade(Stats stats){stats.min++;stats.max+=3;}
	@Override public int STRReq(int lvl){return uptime>0?10+2*uptime:10;}
	@Override public float accuracyFactor(Char owner,Char target){return uptime>0?Math.min(1.6f,1f+.1f*uptime):1f;}
	@Override protected float baseDelay(Char owner){return uptime>0?Math.max(.7f,1f-.05f*uptime):1f;}
	@Override public int reachFactor(Char owner){return owner.HP>=owner.HT?4:1;}
	@Override public ArrayList<String> actions(Hero hero){
		ArrayList<String>a=super.actions(hero);a.add(AC_COURAGE);if(charge>20)a.add(AC_WISDOM);if(hero.STR()-STRReq()>2)a.add(AC_POWER);return a;
	}
	@Override public void execute(Hero hero,String action){
		if(AC_POWER.equals(action)){power(hero);}
		else if(AC_WISDOM.equals(action)){wisdom(hero);}
		else if(AC_COURAGE.equals(action)){curUser=hero;GameScene.selectCell(courage);}
		else super.execute(hero,action);
	}
	public void power(Hero hero){uptime++;hero.spendAndNext(1f);updateQuickslot();}
	public boolean wisdom(Hero hero){if(charge<=20)return false;Buff.affect(hero,MirrorShield.class,2f);charge-=20;hero.spendAndNext(1f);updateQuickslot();return true;}
	public boolean courage(Hero hero,int target){
		if(hero==null||Dungeon.level==null||!Dungeon.level.insideMap(target)||target==hero.pos)return false;
		Ballistica shot=new Ballistica(hero.pos,target,Ballistica.MAGIC_BOLT);Char ch=Actor.findChar(shot.collisionPos);
		if(ch!=null&&ch!=hero){if(Random.Int(2)==0)Buff.affect(ch,Vertigo.class,5f);if(charge>10){charge-=10;ch.damage(Math.max(1,ch.HT/20),this);}}
		hero.spendAndNext(1.5f);updateQuickslot();return true;
	}
	private final CellSelector.Listener courage=new CellSelector.Listener(){@Override public void onSelect(Integer t){if(t!=null&&!courage(curUser,t))GLog.i(Messages.get(LinkSword.class,"no"));}@Override public String prompt(){return Messages.get(LinkSword.class,"prompt");}};
	@Override public int damageRoll(Char owner){return super.damageRoll(owner)+(uptime<=0?0:Random.IntRange(uptime,Math.max(uptime,2*uptime)));}
	@Override public int proc(Char attacker,Char defender,int damage){
		if(Random.Int(100)>60){int roll=Math.max(0,attacker.damageRoll());switch(Random.Int(7)){
			case 0:defender.damage(Random.IntRange(roll/4,Math.max(roll/4,roll/2)),this);break;
			case 1:Buff.affect(defender,Bleeding.class).set(Random.Int(3,Math.max(4,damage)));break;
			case 2:Buff.prolong(defender,Paralysis.class,2f);break;
			case 3:Buff.affect(defender,Cripple.class,3f);break;
			case 4:for(int o:PathFinder.NEIGHBOURS8){int c=attacker.pos+o;if(Dungeon.level!=null&&Dungeon.level.insideMap(c)){Char ch=Actor.findChar(c);if(ch!=null&&ch!=attacker&&ch!=defender&&ch.isAlive())ch.damage(roll/2,this);}}break;
			case 5:if(attacker.buff(ShieldArmor.class)==null)Buff.affect(attacker,ShieldArmor.class).level(attacker.HT/10);break;
			case 6:if(charge>25&&Random.Int(10)==0)defender.damage(2*damage,this);break;
			default:break;}if(charge<FULL_CHARGE)charge++;if(defender.HP<=damage&&Random.Int(10)==0&&Dungeon.level!=null&&Dungeon.level.insideMap(defender.pos)){Heap heap=Dungeon.level.drop(randomLinkDrop(),defender.pos);if(heap.sprite!=null)heap.sprite.drop();}updateQuickslot();}
		return super.proc(attacker,defender,damage);
	}
	public static Item randomLinkDrop(){int index=Random.chances(LINK_DROP_WEIGHTS);return ((Item)Reflection.newInstance(LINK_DROP_CLASSES[index])).random();}
	public static Class<?>[] linkDropClasses(){return LINK_DROP_CLASSES.clone();}
	public static float[] linkDropWeights(){return LINK_DROP_WEIGHTS.clone();}
	public int charge(){return charge;} public int uptime(){return uptime;}
	@Override public String info(){return super.info()+"\n\n"+Messages.get(this,"charge",charge,FULL_CHARGE);}
	@Override public void storeInBundle(Bundle b){super.storeInBundle(b);b.put(CHARGE,charge);b.put(UPTIME,uptime);}
	@Override public void restoreFromBundle(Bundle b){super.restoreFromBundle(b);charge=Math.max(0,Math.min(FULL_CHARGE,b.getInt(CHARGE)));uptime=Math.max(0,b.getInt(UPTIME));}
}
