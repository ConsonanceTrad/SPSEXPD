/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items;

import pd.Assets;
import pd.actors.blobs.Blob;
import pd.actors.blobs.TorchLight;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HighLight;
import pd.actors.hero.Hero;
import pd.effects.particles.FlameParticle;
import pd.journal.Catalog;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

import java.util.ArrayList;

public class Torch extends Item {

	public static final String AC_LIGHT	= "LIGHT";
	public static final String AC_SET = "SET";
	
	public static final float TIME_TO_LIGHT = 1;
	
	{
		image = ItemSpriteSheet.TORCH;
		
		stackable = true;
		
		defaultAction = AC_LIGHT;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_LIGHT );
		actions.add( AC_SET );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		if (action.equals(AC_LIGHT) || action.equals(AC_SET)) {
			
			hero.spend( TIME_TO_LIGHT );
			hero.busy();
			
			if (hero.sprite != null) hero.sprite.operate(hero.pos);
			
			detach( hero.belongings.backpack );
			Catalog.countUse(getClass());
			if (action.equals(AC_LIGHT)) Buff.affect(hero, HighLight.class, 300f);
			else GameScene.add(Blob.seed(hero.pos, 1, TorchLight.class));
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			
			if (hero.sprite != null) {
				Emitter emitter = hero.sprite.centerEmitter();
				emitter.start( FlameParticle.FACTORY, 0.2f, 3 );
			}
		} else super.execute(hero, action);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 8 * quantity;
	}

}
