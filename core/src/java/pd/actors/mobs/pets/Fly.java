/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Pushing;
import pd.items.Item;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.potions.PotionOfMending;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.FlySprite;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Fly extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Fly.class)
			.t("name", "飞蝇")
			.t("desc", "这只苍蝇嗡嗡作响，雄心勃勃，梦想有一天变成一只美丽的蝴蝶。受到攻击时还会招来同伴。")
			.t("$flytwo.name", "离群飞蝇")
			.t("$flytwo.desc", "由飞蝇吸引来的家伙。");
	}



	{ spriteClass=FlySprite.class;cooldown=50;properties.add(Property.BEAST);updateStats(true); }
	@Override protected Kind kind(){return Kind.FLY;}
	@Override public boolean lovefood(Item item){return item instanceof PetFood||item instanceof MeatFood;}
	@Override public Item SupercreateLoot(){return new PotionOfMending();}
	@Override public void updateStats(boolean refill){int old=HT;HT=150+petLevel()*2;defenseSkill=petLevel()*3/2;if(refill)HP=HT;else if(HT>old)HP=Math.min(HT,HP+HT-old);}
	@Override public int damageRoll(){return Random.NormalIntRange(5+petLevel()/2,5+petLevel()*3/2);}
	@Override public int drRoll(){return Random.IntRange(petLevel()*2,Math.max(petLevel()*2,petLevel()*5));}
	@Override public int attackSkill(Char target){return petLevel()+5;}
	@Override public int attackProc(Char enemy,int damage){if(enemy!=null&&Random.Int(10)==0)Buff.affect(enemy,Slow.class,5f);return super.attackProc(enemy,damage);}
	@Override public int defenseProc(Char enemy,int damage){if(enemy!=null&&cooldown<=0){int cell=vacantCardinal(enemy.pos);if(cell>=0){summon(cell,enemy.pos);cooldown=Math.max(10,30-petLevel());}}if(cooldown>0)cooldown--;return super.defenseProc(enemy,damage);}
	int vacantCardinal(int center){if(Dungeon.level==null)return -1;ArrayList<Integer> cells=new ArrayList<>();for(int offset:PathFinder.NEIGHBOURS4){int cell=center+offset;if(cell>=0&&cell<Dungeon.level.length()&&Dungeon.level.passable[cell]&&Actor.findChar(cell)==null)cells.add(cell);}return cells.isEmpty()?-1:Random.element(cells);}
	protected FlyTwo createMinion(){FlyTwo minion=new FlyTwo();minion.updateStats();return minion;}
	private void summon(int cell,int origin){FlyTwo minion=createMinion();minion.pos=cell;GameScene.add(minion);Actor.add(new Pushing(minion,origin,cell));if(Game.instance!=null)Sample.INSTANCE.play(Assets.Sounds.BEE);}

	public static class FlyTwo extends DirectableAlly {
		{spriteClass=FlySprite.class;viewDistance=6;flying=true;state=WANDERING;}
		void updateStats(){int level=Dungeon.hero==null?0:Dungeon.hero.petLevel;defenseSkill=15+level;HT=HP=Dungeon.hero!=null&&Dungeon.hero.subClass==HeroSubClass.LEADER?Math.max(1,level*5):1;}
		@Override public int attackSkill(Char target){return defenseSkill*2;}
		@Override public int damageRoll(){int level=Dungeon.hero==null?0:Dungeon.hero.petLevel;return Random.NormalIntRange(5+level/2,5+level*3/2);}
	}
}
