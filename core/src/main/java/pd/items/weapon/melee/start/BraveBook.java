/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.start;
import java.util.ArrayList;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.medicine.Greaterpill;
import pd.items.potions.PotionOfStrength;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndBag;
import render.utils.serialize.Bundle;
public class BraveBook extends NormalMeleeWeapon {
	public static final String AC_ADD="ADD",AC_IMPROVE="IMPROVE",AC_HEAL="HEAL";private static final String CHARGE="charge",UP1="uptime1",UP2="uptime2";private int charge,uptime1=1,uptime2=1;
	public BraveBook(){super(2,1.2f,.5f,1,4,14,ItemSpriteSheet.LEGACY_BRAVE_BOOK);unique=true;reinforced=true;cursed=true;defaultAction=AC_ADD;}
	@Override protected void applyLegacyUpgrade(Stats stats){stats.min++;stats.max++;}@Override public Item uncurse(){return this;}
	@Override public ArrayList<String> actions(Hero h){ArrayList<String>a=super.actions(h);a.add(AC_ADD);if(charge>4)a.add(AC_IMPROVE);if(charge>9)a.add(AC_HEAL);return a;}
	@Override public void execute(Hero h,String a){if(AC_ADD.equals(a)){curUser=h;GameScene.selectItem(selector);}else if(AC_IMPROVE.equals(a))improve(h);else if(AC_HEAL.equals(a))heal(h);else super.execute(h,a);}
	public boolean imbue(Item i){if(i instanceof PotionOfStrength){uptime1++;return true;}if(i instanceof Greaterpill){uptime2++;return true;}return false;}
	public boolean improve(Hero h){if(charge<5)return false;Buff.affect(h,Muscle.class,10f+uptime1);Buff.affect(h,SuperArcane.class,6f).level(5+uptime1);charge-=5;h.spendAndNext(1f);return true;}
	public boolean heal(Hero h){if(charge<10)return false;Buff.prolong(h,HTimprove.class,100f);h.updateHT(true);Buff.affect(h,BerryRegeneration.class).level(10+uptime2);Buff.affect(h,ShieldArmor.class).level(10+uptime2);charge=0;h.spendAndNext(1f);return true;}
	@Override public int proc(Char a,Char d,int damage){charge=Math.min(10,charge+1);if(d.buff(ShieldArmor.class)!=null||d.buff(MagicArmor.class)!=null||d.buff(EnergyArmor.class)!=null)d.damage(damage,a);if(d.buff(Silent.class)!=null)d.damage((int)(damage*.5f),a);else Buff.affect(d,Silent.class,5f);return super.proc(a,d,damage);}
	private final WndBag.ItemSelector selector=new WndBag.ItemSelector(){public String textPrompt(){return Messages.get(BraveBook.class,"prompt");}public boolean itemSelectable(Item i){return i instanceof PotionOfStrength||i instanceof Greaterpill;}public void onSelect(Item i){if(i!=null&&imbue(i)){i.detach(curUser.belongings.backpack);curUser.spendAndNext(2f);}}};
	public int charge(){return charge;}public int strengthLevel(){return uptime1;}public int healingLevel(){return uptime2;}
	@Override public void storeInBundle(Bundle b){super.storeInBundle(b);b.put(CHARGE,charge);b.put(UP1,uptime1);b.put(UP2,uptime2);}@Override public void restoreFromBundle(Bundle b){super.restoreFromBundle(b);charge=Math.max(0,Math.min(10,b.getInt(CHARGE)));uptime1=Math.max(1,b.getInt(UP1));uptime2=Math.max(1,b.getInt(UP2));}
}
