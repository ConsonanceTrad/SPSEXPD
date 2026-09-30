/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Pushing;
import pd.items.Item;
import pd.items.food.completefood.PetFood;
import pd.items.food.fusion.Nut;
import pd.items.scrolls.ScrollOfMirrorImage;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.RibbonRatSprite;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.Random;

import java.util.ArrayList;

public class RibbonRat extends PET {
	{spriteClass=RibbonRatSprite.class;cooldown=50;properties.add(Property.BEAST);updateStats(true);}
	@Override protected Kind kind(){return Kind.RIBBON_RAT;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof Nut;}
	@Override public Item SupercreateLoot(){return new ScrollOfMirrorImage();}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel();if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel(),5+petLevel()*2);}
	@Override public int drRoll(){return Random.IntRange(0,petLevel()*2);}
	@Override public int attackSkill(Char target){return petLevel()+10;}
	@Override public int attackProc(Char enemy,int damage){if(enemy!=null&&cooldown<=0){int cell=vacantCardinal(enemy.pos);if(cell>=0){summon(cell,enemy.pos);cooldown=Math.max(15,40-petLevel());}}if(cooldown>0)cooldown--;return super.attackProc(enemy,damage);}
	int vacantCardinal(int center){if(Dungeon.level==null)return -1;ArrayList<Integer> cells=new ArrayList<>();for(int offset:PathFinder.NEIGHBOURS4){int cell=center+offset;if(cell>=0&&cell<Dungeon.level.length()&&Dungeon.level.passable[cell]&&Actor.findChar(cell)==null)cells.add(cell);}return cells.isEmpty()?-1:Random.element(cells);}
	protected RibbonRatTwo createMinion(){RibbonRatTwo minion=new RibbonRatTwo();minion.updateStats();return minion;}
	private void summon(int cell,int origin){RibbonRatTwo minion=createMinion();minion.pos=cell;GameScene.add(minion);Actor.add(new Pushing(minion,origin,cell));if(Game.instance!=null)Sample.INSTANCE.play(Assets.Sounds.BEE);}

	public static class RibbonRatTwo extends DirectableAlly {
		{spriteClass=RibbonRatSprite.class;viewDistance=6;flying=true;state=WANDERING;}
		void updateStats(){int level=Dungeon.hero==null?0:Dungeon.hero.petLevel;defenseSkill=15+level;HT=HP=Dungeon.hero!=null&&Dungeon.hero.subClass==HeroSubClass.LEADER?Math.max(1,level*5):1;}
		@Override public int attackSkill(Char target){return defenseSkill*2;}
		@Override public int damageRoll(){int level=Dungeon.hero==null?0:Dungeon.hero.petLevel;return Random.NormalIntRange(5+level,5+level*2);}
	}
}
