/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;
import java.util.ArrayList;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.items.equipment.weapon.missiles.buildblock.*;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;
public class DiamondPickaxe extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DiamondPickaxe.class)
			.t("name", "史蒂夫的钻石镐")
			.t("ac_mine", "挖掘")
			.t("no_thing", "这里没有东西可以挖掘。")
			.t("break", "你太饿了，无法挖掘。")
			.t("desc", "一把具有时运、耐久、效率、锋利、精准和杀手能力的钻石镐。");
	}

	public static final String AC_MINE="MINE"; public DiamondPickaxe(){super(3,2f,.5f,2,2,8,SpecificPlaceHolderDict.SOMETHING_0);unique=true;reinforced=true;defaultAction=AC_MINE;}
	@Override protected void applyLegacyUpgrade(Stats stats){stats.min++;stats.max++;}
	@Override public ArrayList<String> actions(Hero h){ArrayList<String>a=super.actions(h);a.add(AC_MINE);return a;}
	@Override public void execute(Hero h,String a){if(AC_MINE.equals(a)){if(!mine(h))GLog.i(Messages.get(this,isHungry(h)?"break":"no_thing"));}else super.execute(h,a);}
	private static boolean isHungry(Hero h){Hunger hunger=h==null?null:h.buff(Hunger.class);return hunger!=null&&hunger.isStarving();}
	public boolean mine(Hero h){if(h==null||Dungeon.level==null||isHungry(h))return false;for(int o:PathFinder.NEIGHBOURS8){int p=h.pos+o;if(!Dungeon.level.insideMap(p))continue;int t=Dungeon.level.map[p];Item drop=null;if(t==Terrain.WALL)drop=new WallBlock();else if(t==Terrain.DOOR)drop=new DoorBlock();else if(t==Terrain.BOOKSHELF)drop=new BookBlock();else if(t==Terrain.GLASS_WALL)drop=null;else if(t==Terrain.BARRICADE)drop=new WoodenBlock();else if(t==Terrain.STATUE)drop=new StoneBlock();else continue;
		if(Random.Int(3)==0)for(Mob m:Dungeon.level.mobs().toArray(new Mob[0]))m.beckon(h.pos);Level.set(p,Terrain.EMBERS,Dungeon.level);GameScene.updateMap(p);if(Random.Int(60)==1){if(t==Terrain.WALL)drop=new Gold(50);else if(t==Terrain.DOOR)drop=Generator.random(Generator.Category.SEED);else if(t==Terrain.BOOKSHELF)drop=Generator.random(Generator.Category.SCROLL);else if(t==Terrain.BARRICADE)drop=Generator.random(Generator.Category.MEDICINE);else if(t==Terrain.STATUE)drop=Generator.random();}if(drop!=null){Heap heap=Dungeon.level.drop(drop,h.pos);if(heap.sprite!=null)heap.sprite.drop();}Hunger hunger=h.buff(Hunger.class);if(hunger!=null&&!hunger.isStarving()){hunger.satisfy(-10);BuffIndicator.refreshHero();}h.spendAndNext(6f);return true;}return false;}
	@Override public int proc(Char a,Char d,int damage){if(a instanceof Hero)((Hero)a).spp++;if(Random.Int(10)<1&&d.isAlive())d.damage(damage,a);if(d instanceof Mob&&d.HP<=damage&&((Mob)d).firstItem){((Mob)d).firstItem=false;if(Random.Int(6)==0&&Dungeon.level!=null)Dungeon.level.drop(Generator.random(),d.pos).sprite.drop();}return super.proc(a,d,damage);}
}
