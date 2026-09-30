/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import pd.actors.Char;
import watabou.noosa.TextureFilm;
public class ThiefKingSprite extends MobSprite {
	public ThiefKingSprite(){texture(Assets.Sprites.SPS_THIEF_KING);TextureFilm f=new TextureFilm(texture,16,16);idle=new Animation(2,true);idle.frames(f,0,0,0,0);run=new Animation(15,false);run.frames(f,1,2,3,4,5);attack=new Animation(15,false);attack.frames(f,6,7,8);zap=new Animation(15,false);zap.frames(f,9,10);die=new Animation(8,false);die.frames(f,11,12,13,14);play(idle);}
	@Override public void link(Char ch){super.link(ch);add(State.LEVITATING);}
	@Override public void die(){super.die();remove(State.LEVITATING);}
}
