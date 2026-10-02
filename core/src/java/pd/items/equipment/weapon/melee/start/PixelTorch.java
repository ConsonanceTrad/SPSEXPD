/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Light;
import pd.actors.buffs.Shocked;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

public class PixelTorch extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponUniqueWeaponDict.STEVE_TORCH;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PixelTorch.class)
			.t("name", "像素火把")
			.t("ac_tlight", "照明")
			.t("desc", "一支结实的火把，命中敌人会积蓄力量，并可消耗力量制造长时间照明。");
	}



	public static final String AC_TLIGHT = "TLIGHT";
	public PixelTorch(){super(2,1f,1f,1,3,15,SpecificPlaceHolderDict.SOMETHING_0); unique=true; reinforced=true; defaultAction=AC_TLIGHT;}
	@Override public ArrayList<String> actions(Hero hero){ArrayList<String>a=super.actions(hero);a.add(AC_TLIGHT);return a;}
	@Override public void execute(Hero hero,String action){
		if(AC_TLIGHT.equals(action)){Buff.prolong(hero,Light.class,hero.spp>50?50f:1f);if(hero.spp>50)hero.spp-=50;hero.spendAndNext(1f);}
		else super.execute(hero,action);
	}
	@Override public int proc(Char attacker,Char defender,int damage){
		if(Random.Int(100)<20)Buff.affect(defender,Burning.class).reignite(defender,4f);
		else if(Random.Int(80)<20)Buff.affect(defender,Shocked.class).set(4f);
		if(attacker instanceof Hero)((Hero)attacker).spp++;
		return super.proc(attacker,defender,damage);
	}
}
