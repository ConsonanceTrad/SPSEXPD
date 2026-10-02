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

package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.effects.Speck;
import pd.effects.particles.ShaftParticle;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.TormentedSpiritSprite;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class TormentedSpirit extends Wraith {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TormentedSpirit.class)
			.t("name", "咒缚灵")
			.t("desc", "咒缚灵是本性善良却受诅咒折磨的魂灵。只要诅咒仍在，它们就会像更强的怨灵一样攻击你！\n\n或许可以在与之相邻时使用祛邪卷轴净化魂灵的诅咒。如果诅咒被净化，它们一定会报答你的...")
			.t("thank_you", "谢谢你...");
	}


	{
		spriteClass = TormentedSpiritSprite.class;
	}

	//50% more damage scaling than regular wraiths
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + Math.round(1.5f*level)/2, 2 + Math.round(1.5f*level) );
	}

	//50% more accuracy (and by extension evasion) scaling than regular wraiths
	@Override
	public int attackSkill( Char target ) {
		return 10 + Math.round(1.5f*level);
	}

	public void cleanse(){
		Sample.INSTANCE.play( Assets.Sounds.GHOST );
		yell(Messages.get(this, "thank_you"));

		//50/50 between weapon or armor, always uncursed & enchanted, 50% chance to be +1 if level 0
		Item prize;
		if (Random.Int(2) == 0){
			prize = Generator.randomWeapon(true);
			((Weapon)prize).enchant();
		} else {
			prize = Generator.randomArmor();
			((Armor) prize).inscribe();
		}
		prize.cursed = false;
		prize.cursedKnown = true;

		if (prize.level() == 0 && Random.Int(2) == 0){
			prize.upgrade();
		}

		Dungeon.level.drop(prize, pos).sprite.drop();

		destroy();
		sprite.die();
		sprite.tint(1, 1, 1, 1);
		sprite.emitter().start( ShaftParticle.FACTORY, 0.3f, 4 );
		sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );

	}

}
