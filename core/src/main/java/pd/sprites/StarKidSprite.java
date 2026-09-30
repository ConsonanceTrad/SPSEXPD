/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import com.watabou.noosa.TextureFilm;
public class StarKidSprite extends MobSprite { public StarKidSprite(){texture(Assets.Sprites.SPS_STAR_KID);TextureFilm f=new TextureFilm(texture,12,16);idle=new Animation(2,true);idle.frames(f,0,0,0,1,0,0,1,1);run=new Animation(15,true);run.frames(f,2,3,4,5,6,7);attack=new Animation(12,false);attack.frames(f,8,9,10);zap=attack.clone();die=new Animation(8,false);die.frames(f,11,12,13,14);play(idle);}@Override public int blood(){return 0xFFcccccc;} }
