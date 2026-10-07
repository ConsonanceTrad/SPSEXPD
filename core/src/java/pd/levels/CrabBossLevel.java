/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;
import java.util.ArrayList;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.CrabKing;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Shell;
import pd.actors.mobs.SpsHermitCrab;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.scenes.GameScene;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
public class CrabBossLevel extends Level {
	public static final int WIDTH=48, HEIGHT=48, TOP=2, HALL_WIDTH=13, HALL_HEIGHT=15, CHAMBER_HEIGHT=3;
	public static final int LEFT=(WIDTH-HALL_WIDTH)/2, CENTER=LEFT+HALL_WIDTH/2;
	public static final int SHELL_CELL=(TOP+1)*WIDTH+CENTER;
	private int arenaDoor; private boolean enteredArena;
	{ color1=0x4b6636; color2=0xf2f2f2; }
	@Override public String tilesTex(){return Assets.Environment.TILES_PRISON;}
	@Override public String waterTex(){return Assets.Environment.WATER_PRISON;}
	@Override protected boolean build(){ setSize(WIDTH,HEIGHT); Painter.fill(this,LEFT,TOP,HALL_WIDTH,HALL_HEIGHT,Terrain.EMPTY); Painter.fill(this,CENTER,TOP,1,HALL_HEIGHT,Terrain.EMPTY); for(int y=TOP+1;y<TOP+HALL_HEIGHT;y+=2){map[y*WIDTH+CENTER-2]=Terrain.STATUE_SP;map[y*WIDTH+CENTER+2]=Terrain.STATUE_SP;} map[(TOP-1)*WIDTH+CENTER]=Terrain.WALL; arenaDoor=(TOP+HALL_HEIGHT)*WIDTH+CENTER; map[arenaDoor]=Terrain.DOOR; Painter.fill(this,LEFT,TOP+HALL_HEIGHT+1,HALL_WIDTH,CHAMBER_HEIGHT,Terrain.EMPTY); Painter.fill(this,LEFT,TOP+HALL_HEIGHT+1,1,CHAMBER_HEIGHT,Terrain.WATER); Painter.fill(this,LEFT+HALL_WIDTH-1,TOP+HALL_HEIGHT+1,1,CHAMBER_HEIGHT,Terrain.WATER); int entrance=(TOP+HALL_HEIGHT+2+Random.Int(CHAMBER_HEIGHT-1))*WIDTH+LEFT+Random.Int(HALL_WIDTH-2); map[entrance]=Terrain.PEDESTAL; transitions.add(new LevelTransition(this,entrance,LevelTransition.Type.BRANCH_ENTRANCE,Dungeon.depth,0,LevelTransition.Type.REGULAR_ENTRANCE)); for(int cell=0;cell<length();cell++){if(map[cell]==Terrain.EMPTY&&Random.Int(10)==0)map[cell]=Terrain.EMPTY_DECO;else if(map[cell]==Terrain.WALL&&Random.Int(8)==0)map[cell]=Terrain.WALL_DECO;} return true; }
	@Override protected void createMobs(){}
	@Override protected void createItems(){}
	@Override public Mob createMob(){return null;}
	@Override public Actor addRespawner(){return null;}
	@Override public int randomRespawnCell(Char ch){return -1;}
	@Override public void pressCell(int cell){super.pressCell(cell);enterArena(cell);}
	//SPSEXPD: 玩家走进竞技场时才会触发首领战——pressCell 在行走路径上不会被回调
	@Override public void occupyCell(Char ch){super.occupyCell(ch);if(ch==Dungeon.hero)enterArena(ch.pos);}
	private void enterArena(int cell){if(!enteredArena&&Dungeon.hero!=null&&Dungeon.hero.pos==cell&&outsideEntranceRoom(cell)){enteredArena=true;pd.Statistics.qualifiedForBossChallengeBadge=true;spawnBosses();Dungeon.observe();}}
	private void spawnBosses(){ArrayList<Integer> candidates=new ArrayList<>();for(int cell=0;cell<length();cell++)if(passable[cell]&&outsideEntranceRoom(cell)&&Actor.findChar(cell)==null&&cell!=SHELL_CELL&&cell!=SHELL_CELL-1&&cell!=SHELL_CELL+1&&cell!=SHELL_CELL+WIDTH&&cell!=SHELL_CELL-WIDTH)candidates.add(cell);if(candidates.isEmpty())return;CrabKing king=new CrabKing();king.pos=Random.element(candidates);king.state=king.HUNTING;Shell shell=new Shell();shell.pos=SHELL_CELL;GameScene.add(king);GameScene.add(shell);int[] cells={SHELL_CELL+1,SHELL_CELL-1,SHELL_CELL+WIDTH,SHELL_CELL-WIDTH};for(int cell:cells){SpsHermitCrab crab=new SpsHermitCrab();crab.pos=cell;crab.state=crab.HUNTING;GameScene.add(crab);}}
	boolean outsideEntranceRoom(int cell){return cell/WIDTH<arenaDoor/WIDTH;}
	int arenaDoorForTesting(){return arenaDoor;}
	private boolean completed(){if(Dungeon.hero==null)return false;AdventureJournal j=Dungeon.hero.belongings.getItem(AdventureJournal.class);return j!=null&&j.isCompleted(12);}
	private static final String DOOR="door",ENTERED="entered";
	@Override public void storeInBundle(Bundle b){super.storeInBundle(b);b.put(DOOR,arenaDoor);b.put(ENTERED,enteredArena);}
	@Override public void restoreFromBundle(Bundle b){super.restoreFromBundle(b);arenaDoor=b.getInt(DOOR);enteredArena=b.getBoolean(ENTERED);if(enteredArena&&!completed()){boolean found=false;for(Mob mob:mobs())if(mob instanceof CrabKing){found=true;break;}if(!found)enteredArena=false;}}
}
