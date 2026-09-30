/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import watabou.noosa.TextureFilm;
public class FlyPetSprite extends MobSprite { public FlyPetSprite(){texture(Assets.Sprites.SPS_FLY_PET);TextureFilm f=new TextureFilm(texture,16,16);idle=new Animation(15,true);idle.frames(f,16,17,18,19,20,21);run=new Animation(15,true);run.frames(f,16,17,18,19,20,21);attack=new Animation(20,false);attack.frames(f,22,23,24,25);zap=attack.clone();die=new Animation(15,false);die.frames(f,26,27,28,29,30);play(idle);}@Override public int blood(){return 0xFF8BA077;} }
