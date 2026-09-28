/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;
public class RibbonRatSprite extends MobSprite { public RibbonRatSprite(){texture(Assets.Sprites.SPS_RIBBON_RAT);TextureFilm f=new TextureFilm(texture,16,15);idle=new Animation(2,true);idle.frames(f,48,48,48,49);run=new Animation(10,true);run.frames(f,54,55,56,57,58);attack=new Animation(15,false);attack.frames(f,50,51,52,53);zap=attack.clone();die=new Animation(10,false);die.frames(f,59,60,61,62);play(idle);} }
