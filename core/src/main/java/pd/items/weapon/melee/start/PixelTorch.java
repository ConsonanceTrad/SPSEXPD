/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.start;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Light;
import pd.actors.buffs.Shocked;
import pd.actors.hero.Hero;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PixelTorch extends NormalMeleeWeapon {
	public static final String AC_TLIGHT = "TLIGHT";
	public PixelTorch(){super(2,1f,1f,1,3,15,ItemSpriteSheet.LEGACY_PIXEL_TORCH); unique=true; reinforced=true; defaultAction=AC_TLIGHT;}
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
