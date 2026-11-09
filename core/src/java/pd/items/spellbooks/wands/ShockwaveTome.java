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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.spellbooks.wands;

import java.util.ArrayList;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import pd.mechanics.ConeAOE;
import pd.scenes.PixelScene;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 原典《震波原典》（法术书化批次3d）——震波法杖 5 级曲线：
 * 锥形冲击波（半径 6），14~18 伤害（受法强）+ 残废 5 回合。
 */
public class ShockwaveTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(ShockwaveTome.class)
			.t("name", "震波原典")
			.t("cast_prompt", "选择冲击波推出的方向")
			.t("desc", "把这本书摔在桌上，整张桌子都会嗡一声。\n\n施法（消耗 5 法力）：沿点选方向推出锥形冲击波（半径 6），命中的敌人受_14~18点伤害_（受_法强_加成）并_残废_5回合。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public boolean targetRequired() { return true; }
	/** 施法等级（照震波法杖 5 级口径） */
	public int castLevel() { return 5; }

	@Override
	public void castAt( Hero hero, int cell ) {
		if (hero.sprite == null) return;
		Ballistica aim = new Ballistica( hero.pos, cell, Ballistica.WONT_STOP );
		int dist = Math.min( aim.dist, 4 + castLevel() / 2 );
		ConeAOE cone = new ConeAOE( aim, Math.max( 1, dist ), 50,
				Ballistica.STOP_SOLID | Ballistica.STOP_TARGET );

		for (Ballistica ray : cone.outerRays) {
			((MagicMissile) hero.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FORCE_CONE, hero.sprite, ray.path.get( ray.dist ), null );
		}

		hero.sprite.zap( cell );
		Sample.INSTANCE.play( pd.Assets.Sounds.BLAST, 1f, 0.5f );
		PixelScene.shake( 2, 0.5f );

		int base = render.utils.math.Random.NormalIntRange( 4 + castLevel(), 8 + 2 * castLevel() );
		for (int c : cone.cells) {
			Actor ch = Actor.findChar( c );
			if (ch == null || !(ch instanceof Char) || !((Char) ch).isAlive()) continue;
			if (((Char) ch).alignment == hero.alignment) continue;
			((Char) ch).damage( power( hero, base ), this );
			if (((Char) ch).isAlive()) {
				Buff.affect( (Char) ch, Cripple.class, 5f );
			}
		}
	}
}
