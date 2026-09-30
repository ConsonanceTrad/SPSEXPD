/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import watabou.noosa.TextureFilm;
public class SpiderPetSprite extends MobSprite { public SpiderPetSprite(){texture(Assets.Sprites.SPS_SPIDER_PET);TextureFilm f=new TextureFilm(texture,16,16);idle=new Animation(10,true);idle.frames(f,0,0,0,0,0,1,0,1);run=new Animation(15,true);run.frames(f,0,2,0,3);attack=new Animation(12,false);attack.frames(f,0,4,5,0);zap=attack.clone();die=new Animation(12,false);die.frames(f,6,7,8,9);play(idle);}@Override public int blood(){return 0xFFBFE5B8;} }
