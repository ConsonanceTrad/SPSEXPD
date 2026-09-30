/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import com.watabou.noosa.TextureFilm;
public class SnakePetSprite extends MobSprite { public SnakePetSprite(){texture(Assets.Sprites.SPS_SNAKE_PET);TextureFilm f=new TextureFilm(texture,16,16);idle=new Animation(2,true);idle.frames(f,0,0,0,1);run=new Animation(10,true);run.frames(f,5,4,3,4,5);attack=new Animation(14,false);attack.frames(f,0,2,0);zap=attack.clone();die=new Animation(10,false);die.frames(f,2,6,7);play(idle);} }
