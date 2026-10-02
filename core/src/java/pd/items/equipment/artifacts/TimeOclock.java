/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import java.util.ArrayList;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.specific.keys.Key;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class TimeOclock extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TimeOclock.class)
			.t("name", "时间怀表")
			.t("ac_activate", "激活")
			.t("ac_restart", "耗竭-重置")
			.t("in_use", "你的怀表正在使用中。")
			.t("no_charge", "怀表的充能不足。")
			.t("cursed", "受诅咒的怀表无法使用。")
			.t("onstasis", "你周遭的世界似乎就在这一瞬间变化了。")
			.t("onfreeze", "你周围的一切突然缓慢下来。")
			.t("prompt", "你想怎样使用怀表的魔法？\n\n当你被静止，周围的时间会正常流动，而你则会冻结并变得无敌。\n\n当时间被减缓，你的行动会被加快。")
			.t("stasis", "使我彻底静止")
			.t("freeze", "延缓周围时间")
			.t("desc", "这只小型的华贵怀表看起来却并不怎么起眼，但你仍觉得它精雕细刻的框架内蕴含着某种强大的力量。在看着秒针转动的同时，你能感受到一种魔法正在拉扯着你，使用这种魔法肯定能给你一些控制时间的方法。")
			.t("desc_hint", "怀表指针有些僵硬，如果你能找到一些发条……")
			.t("desc_cursed", "被诅咒的怀表把它自己锁在了你的身边，你可以感觉它试图操纵你的时间流动。")
			.t("clock.name", "魔法发条")
			.t("clock.levelup", "你给你的怀表上了发条。")
			.t("clock.maxlevel", "你的怀表已经拧不动了！")
			.t("clock.no_hourglass", "你没有需要这个发条的时间怀表。")
			.t("clock.desc", "这一发条应该能够在你的怀表上完美使用。");
	}

	public static final String AC_ACTIVATE="ACTIVATE",AC_RESTART="RESTART";
	private static final String SANDBAGS="sandbags",STASIS="stasis",LEGACY_BUFF="buff";
	private int sandBags;
	{image=SpecificPlaceHolderDict.SOMETHING_0;levelCap=5;chargeCap=5;charge=5;defaultAction=AC_ACTIVATE;}
	@Override public ArrayList<String> actions(Hero h){ArrayList<String>a=super.actions(h);if(isEquipped(h)&&charge>0&&!cursed)a.add(AC_ACTIVATE);if(!isEquipped(h)&&level()>4&&!cursed)a.add(AC_RESTART);return a;}
	@Override public void execute(final Hero h,String action){
		if(AC_ACTIVATE.equals(action)){if(!isEquipped(h))GLog.i(Messages.get(Artifact.class,"need_to_equip"));else if(activeBuff!=null)GLog.i(Messages.get(this,"in_use"));else if(charge<=1)GLog.i(Messages.get(this,"no_charge"));else if(cursed)GLog.i(Messages.get(this,"cursed"));else GameScene.show(new WndOptions(Messages.titleCase(name()),Messages.get(this,"prompt"),Messages.get(this,"stasis"),Messages.get(this,"freeze")){@Override protected void onSelect(int i){if(i==0)useStasis(h);else if(i==1)useFreeze(h);}});
		}else if(AC_RESTART.equals(action))restartFloor(h);else super.execute(h,action);
	}
	public boolean useStasis(Hero h){if(h==null||charge<=1||activeBuff!=null)return false;showActivation(h,"onstasis");TimeStasis stasis=new TimeStasis();if(!stasis.attachTo(h))return false;activeBuff=stasis;return true;}
	public boolean useFreeze(Hero h){if(h==null||Dungeon.level==null||charge<=1||activeBuff!=null)return false;showActivation(h,"onfreeze");charge--;Buff.affect(h,HasteBuff.class,15f);if(h.sprite!=null)h.sprite.emitter().start(Speck.factory(Speck.UP),0.4f,4);for(Mob m:Dungeon.level.mobs().toArray(new Mob[0])){Buff.prolong(m,Paralysis.class,3f);Buff.prolong(m,Slow.class,15f);Buff.affect(m,ArmorBreak.class,15f).level(30);if(m.sprite!=null)m.sprite.centerEmitter().start(Speck.factory(Speck.NOTE),0.3f,5);}h.spendAndNext(1f);updateQuickslot();return true;}
	private void showActivation(Hero h,String message){GLog.i(Messages.get(this,message));if(h.sprite!=null){GameScene.flash(0xFFFFFF);Sample.INSTANCE.play(Assets.Sounds.TELEPORT);}}
	public boolean restartFloor(Hero h){if(h==null||level()<5||isEquipped(h))return false;level(0);for(Item item:h.belongings.backpack.items.toArray(new Item[0]))if(item instanceof Key&&((Key)item).depth==Dungeon.depth)item.detachAll(h.belongings.backpack);InterlevelScene.returnDepth=Dungeon.depth;InterlevelScene.mode=InterlevelScene.Mode.RESET;Game.switchScene(InterlevelScene.class);return true;}
	@Override public void activate(Char ch){super.activate(ch);if(activeBuff!=null&&activeBuff.target==null)activeBuff.attachTo(ch);}
	@Override public boolean doUnequip(Hero h,boolean collect,boolean single){if(!super.doUnequip(h,collect,single))return false;if(activeBuff!=null){activeBuff.detach();activeBuff=null;}return true;}
	@Override protected ArtifactBuff passiveBuff(){return new OclockRecharge();}
	@Override public Item upgrade(){chargeCap++;sandBags=Math.max(sandBags,level()+1);return super.upgrade();}
	public int charge(){return charge;} public int chargeCap(){return chargeCap;}
	public int sandBags(){return sandBags;}
	@Override public String desc(){String desc=super.desc();if(isEquipped(Dungeon.hero)){if(!cursed&&level()<levelCap)desc+="\n\n"+Messages.get(this,"desc_hint");else if(cursed)desc+="\n\n"+Messages.get(this,"desc_cursed");}return desc;}
	public class OclockRecharge extends ArtifactBuff{@Override public boolean act(){if(charge<chargeCap&&!cursed){partialCharge+=1f/(60f-(chargeCap-charge)*2f);if(partialCharge>=1){partialCharge--;charge++;}}else if(cursed&&Random.Int(10)==0)((Hero)target).spend(TICK);updateQuickslot();spend(TICK);return true;}}
	public class TimeStasis extends ArtifactBuff{
		@Override public boolean attachTo(Char t){if(!super.attachTo(t))return false;spend(4f);((Hero)t).spendAndNext(4f);Hunger h=t.buff(Hunger.class);if(h!=null&&!h.isStarving())h.satisfy(4f);charge--;t.invisible++;updateQuickslot();if(Dungeon.level!=null)Dungeon.observe();return true;}
		@Override public boolean act(){detach();return true;}@Override public void detach(){if(target!=null&&target.invisible>0)target.invisible--;super.detach();activeBuff=null;if(Dungeon.level!=null)Dungeon.observe();}
	}
	@Override public void storeInBundle(Bundle b){super.storeInBundle(b);b.put(SANDBAGS,sandBags);b.put(STASIS,activeBuff!=null);if(activeBuff!=null)b.put(LEGACY_BUFF,activeBuff);}
	@Override public void restoreFromBundle(Bundle b){super.restoreFromBundle(b);chargeCap=5+level();sandBags=b.getInt(SANDBAGS);if(b.contains(LEGACY_BUFF)){activeBuff=new TimeStasis();activeBuff.restoreFromBundle(b.getBundle(LEGACY_BUFF));}else if(b.getBoolean(STASIS))activeBuff=new TimeStasis();}
	public static class Clock extends Item{{image=GroundFunctionalFallingDict.SANDBAG_0;}@Override public boolean doPickUp(Hero h,int pos){TimeOclock o=h.belongings.getItem(TimeOclock.class);if(o!=null&&!o.cursed&&o.level()<o.levelCap){o.upgrade();Sample.INSTANCE.play(Assets.Sounds.DEWDROP);GameScene.pickUp(this,pos);h.spendAndNext(pickupDelay());return true;}GLog.w(Messages.get(this,"no_hourglass"));return false;}@Override public int value(){return 30;}@Override public boolean isUpgradable(){return false;}@Override public boolean isIdentified(){return true;}}
}
