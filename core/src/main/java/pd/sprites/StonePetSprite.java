/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import render.noosa.TextureFilm;
public class StonePetSprite extends MobSprite { public StonePetSprite(){texture(Assets.Sprites.SPS_KLIKS);TextureFilm f=new TextureFilm(texture,16,16);idle=new Animation(2,true);idle.frames(f,0,1,2,3);run=new Animation(4,true);run.frames(f,4,5,6,7);attack=new Animation(15,false);attack.frames(f,8,9,10,11);zap=attack.clone();die=new Animation(6,false);die.frames(f,12,13,14,15);play(idle);}@Override public int blood(){return 0xFFcdcdb7;} }
