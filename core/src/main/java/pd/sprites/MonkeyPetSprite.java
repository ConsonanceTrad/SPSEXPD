/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import pd.Dungeon;
import pd.items.food.fruit.Blackberry;
import watabou.noosa.TextureFilm;
public class MonkeyPetSprite extends MobSprite {
	public MonkeyPetSprite(){texture(Assets.Sprites.SPS_KLIKS);TextureFilm f=new TextureFilm(texture,16,16);idle=new Animation(2,true);idle.frames(f,17,18,19,20);run=new Animation(8,true);run.frames(f,21,22,23,24);attack=new Animation(15,false);attack.frames(f,25,26,27,28);zap=attack.clone();die=new Animation(8,false);die.frames(f,29,30,30,30);play(idle);}
	@Override public void attack(int cell){if(Dungeon.level!=null&&!Dungeon.level.adjacent(cell,ch.pos)){((MissileSprite)parent.recycle(MissileSprite.class)).reset(ch.pos,cell,new Blackberry(),ch::onAttackComplete);play(zap);turnTo(ch.pos,cell);}else super.attack(cell);}
	@Override public int blood(){return 0xFFcdcdb7;}
}
