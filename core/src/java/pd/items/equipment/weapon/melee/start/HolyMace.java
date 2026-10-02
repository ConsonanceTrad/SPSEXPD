/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import java.util.ArrayList;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.GreatRune;
import pd.items.Item;
import pd.items.Torch;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;
public class HolyMace extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HolyMace.class)
			.t("name", "圣洁钉锤")
			.t("ac_add", "灌注")
			.t("ac_light", "强光")
			.t("ac_trial", "审判")
			.t("prompt", "选择目标")
			.t("prompt2", "选择火把或强力符石")
			.t("desc", "一把受祝福的钉锤，可以吸收火把和强力符石来提升能力。");
	}

	public static final String AC_ADD="ADD",AC_LIGHT="LIGHT",AC_TRIAL="TRIAL";private static final String CHARGE="charge",UP1="uptime1",UP2="uptime2";private int charge,uptime1=1,uptime2=1;
	public HolyMace(){super(3,1.2f,1f,2,8,20,EquipmentEquipWeaponUniqueWeaponDict.HOLY_HAMMER);unique=true;reinforced=true;cursed=true;defaultAction=AC_ADD;usesTargeting=true;}
	@Override protected void applyLegacyUpgrade(Stats stats){stats.min++;stats.max+=3;}@Override public Item uncurse(){return this;}
	@Override public ArrayList<String> actions(Hero h){ArrayList<String>a=super.actions(h);a.add(AC_ADD);if(charge>4)a.add(AC_LIGHT);if(charge>9)a.add(AC_TRIAL);return a;}
	@Override public void execute(Hero h,String a){if(AC_ADD.equals(a)){curUser=h;GameScene.selectItem(selector);}else if(AC_LIGHT.equals(a))light(h);else if(AC_TRIAL.equals(a)){curUser=h;GameScene.selectCell(shooter);}else super.execute(h,a);}
	public boolean imbue(Item i){if(i instanceof Torch){uptime1++;return true;}if(i instanceof GreatRune){uptime2++;return true;}return false;}
	public boolean light(Hero h){if(charge<5||Dungeon.level==null)return false;Buff.affect(h,Light.class,10f+uptime1);for(Mob m:Dungeon.level.mobs().toArray(new Mob[0]))if(h.fieldOfView!=null&&h.fieldOfView[m.pos])Buff.affect(m,Terror.class,10f+uptime1).object=h.id();charge-=5;h.spendAndNext(1f);return true;}
	public boolean trial(Hero h,int target){if(h==null||Dungeon.level==null||charge<10||!Dungeon.level.insideMap(target))return false;Ballistica shot=new Ballistica(h.pos,target,Ballistica.MAGIC_BOLT);Char d=Actor.findChar(shot.collisionPos);if(d==null||d==h)return false;charge-=10;d.damage(Math.max(d.HT/20+uptime2,1),this);if(d.HP<d.HT/3&&!Char.hasProp(d,Char.Property.BOSS)&&!Char.hasProp(d,Char.Property.MINIBOSS))d.die(this);h.spendAndNext(1f);return true;}
	@Override public int proc(Char a,Char d,int damage){charge=Math.min(10,charge+1);if(Char.hasProp(d,Char.Property.DEMONIC)||Char.hasProp(d,Char.Property.UNKNOW)||Char.hasProp(d,Char.Property.UNDEAD)||Char.hasProp(d,Char.Property.DRAGON))d.damage(damage/2,this);return super.proc(a,d,damage);}
	private final WndBag.ItemSelector selector=new WndBag.ItemSelector(){public String textPrompt(){return Messages.get(HolyMace.class,"prompt2");}public boolean itemSelectable(Item i){return i instanceof Torch||i instanceof GreatRune;}public void onSelect(Item i){if(i!=null&&imbue(i)){i.detach(curUser.belongings.backpack);curUser.spendAndNext(2f);}}};
	private final CellSelector.Listener shooter=new CellSelector.Listener(){public void onSelect(Integer t){if(t!=null)trial(curUser,t);}public String prompt(){return Messages.get(HolyMace.class,"prompt");}};
	public int charge(){return charge;}public int lightLevel(){return uptime1;}public int trialLevel(){return uptime2;}
	@Override public void storeInBundle(Bundle b){super.storeInBundle(b);b.put(CHARGE,charge);b.put(UP1,uptime1);b.put(UP2,uptime2);}@Override public void restoreFromBundle(Bundle b){super.restoreFromBundle(b);charge=Math.max(0,Math.min(10,b.getInt(CHARGE)));uptime1=Math.max(1,b.getInt(UP1));uptime2=Math.max(1,b.getInt(UP2));}
}
