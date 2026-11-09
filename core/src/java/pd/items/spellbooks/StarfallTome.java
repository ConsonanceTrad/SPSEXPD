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

package pd.items.spellbooks;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.messages.InlineText;
import pd.Dungeon;

/**
 * SPSEXPD: 原典《星落原典》（批次E）——耗法力以自身为中心，5×5 内所有敌方目标被星辉击落
 * （纯粹伤害，法强加成）。持有超过 1 本原典时施法过载（见 Spellbook）。
 */
public class StarfallTome extends Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(StarfallTome.class)
			.t("name", "星落原典")
			.t("desc", "书页间嵌着几粒不肯熄灭的星光。合上书时，它们落回原处。\n\n施法（消耗 12 法力）：以自身为中心，五格内的所有敌方目标被星辉击中，受到一段_纯粹伤害_（无视护甲与抗性，受_法强_加成）。\n\n_原典_：效果强大，但同时持有超过一本原典会让每次施法过载（效果打折、额外多耗法力）。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override
	public boolean isTome() { return true; }

	@Override
	public int manaCost() { return 12; }

	@Override
	public void onCast( Hero hero ) {
		//SPSEXPD: 自我中心 5×5 纯粹伤害——不设 blob、直接结算，headless 依赖 Dungeon.level null 守卫
		if (Dungeon.level == null) return;
		int dmg = power( hero, 8 );
		for (int off : pd.rituals.Rituals.radiusOffsets( Dungeon.level )) {
			int cell = hero.pos + off;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char ch = Actor.findChar( cell );
			if (ch != null && ch != hero && ch.alignment != Char.Alignment.ALLY) {
				ch.damage( dmg, this );
				if (ch.sprite != null) {
					ch.sprite.centerEmitter().burst( pd.effects.Speck.factory( pd.effects.Speck.STAR ), 8 );
					ch.sprite.flash();
				}
			}
		}
	}
}
