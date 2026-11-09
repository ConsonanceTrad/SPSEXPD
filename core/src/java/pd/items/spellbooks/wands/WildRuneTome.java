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

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.WildRuneMark;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.CellEmitter;
import pd.effects.particles.SparkParticle;
import pd.items.equipment.wands.WandOfWildRune;
import pd.utils.GLog;
import pd.messages.Messages;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 原典《狂野符文原典》（法术书化批次3d）——狂野符文法杖的书化：
 * 烙下引爆倍率 40%（20%+4%×5 级）的狂野符文（15 回合），照原杖口径重写。
 */
public class WildRuneTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(WildRuneTome.class)
			.t("name", "狂野符文原典")
			.t("cast_prompt", "选择要烙印的目标")
			.t("desc", "石板般的封皮上，刻痕还在自己移动。\n\n施法（消耗 5 法力）：给目标烙下_狂野符文_——它之后被任意法杖命中时引爆，爆炸量为那一击伤害的_40%_，并向周围逐格衰减扩散（15回合）。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public boolean targetRequired() { return true; }
	/** 施法等级（照狂野符文 5 级口径） */
	public int castLevel() { return 5; }

	@Override
	public void castAt( Hero hero, int cell ) {
		Char target = Actor.findChar( cell );
		if (target == null || target.alignment == hero.alignment) {
			GLog.w( Messages.get( WildRuneTome.class, "no_target" ) );
			return;
		}

		Sample.INSTANCE.play( pd.Assets.Sounds.ZAP );
		CellEmitter.get( target.pos ).burst( SparkParticle.FACTORY, 6 );

		float ratio = WandOfWildRune.ratioForLevel( castLevel() );
		Buff.affect( target, WildRuneMark.class ).setup( ratio, 15f );
		GLog.i( Messages.get( WildRuneTome.class, "marked" ) );
	}
}
