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

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.hero.Hero;
import pd.actors.hero.Mana;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.CellEmitter;
import pd.effects.particles.FlameParticle;
import pd.levels.Level;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

/**
 * SPSEXPD: 原典《焰柱原典》（批次E）——耗法力点选目标格，3×3 焰柱轰击（法强加成）。
 */
public class EmberpillarTome extends Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(EmberpillarTome.class)
			.t("name", "焰柱原典")
			.t("cast_prompt", "选择焰柱轰落的位置")
			.t("desc", "封皮像烧红的铁，翻开时指尖能听见火焰的呼吸。\n\n施法（消耗 5 法力）：点选一处位置，3×3 范围内燃起焰柱，对其中所有敌方目标造成火系伤害并留下烈焰。伤害受_法强_加成。\n\n_原典_：效果强大。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override
	public boolean isTome() { return true; }

	@Override
	public int manaCost() { return 5; }

	@Override
	public void execute( Hero hero, String action ) {
		//SPSEXPD: 选目标格施法（走 tryCast 扣法力，选点成功后结算）
		if (action.equals( AC_CAST ) && hero != null && Dungeon.level != null) {
			curUser = hero;
			curBook = this;
			GameScene.selectCell( caster );
		} else {
			super.execute( hero, action );
		}
	}

	@Override
	public void onCast( Hero hero ) {
		//无选点路径的兜底：以自身为中心轰击（自我点选 AC_CAST 直接给效果）
		slam( hero, hero.pos );
	}

	/** SPSEXPD: 焰柱结算——3×3 火焰 + 伤害（法强加成），墙格跳过 */
	public void slam( Hero hero, int center ) {
		Level level = Dungeon.level;
		if (level == null) return;
		int dmg = power( hero, 12 );
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				int cell = center + dy * level.width() + dx;
				if (!level.insideMap(cell) || level.solid[cell]) continue;
				GameScene.add( Blob.seed( cell, 2, Fire.class ) );
				Char ch = Actor.findChar( cell );
				if (ch != null && ch.alignment != Char.Alignment.ALLY) {
					ch.damage( dmg, this );
					if (ch.sprite != null) {
						ch.sprite.centerEmitter().burst( FlameParticle.FACTORY, 6 );
						ch.sprite.flash();
					}
				} else if (cell == center && level.heroFOV != null && level.heroFOV[cell]) {
					CellEmitter.center( cell ).burst( FlameParticle.FACTORY, 8 );
				}
			}
		}
	}

	//SPSEXPD: 选点器（与法器 channeler 同构）——先扣法力再轰击
	private static Hero curUser;
	private static EmberpillarTome curBook;

	private static final CellSelector.Listener caster = new CellSelector.Listener() {

		@Override
		public void onSelect( Integer target ) {
			if (target == null || curUser == null || curBook == null) return;
			int cost = curBook.manaCost();
			if (!Mana.has( curUser, cost )) {
				GLog.w( Messages.get( Spellbook.class, "no_mana" ) );
				return;
			}
			Mana.spend( curUser, cost );
			curBook.slam( curUser, target );
			curUser.spendAndNext( 1f );
		}

		@Override
		public String prompt() {
			return Messages.get( EmberpillarTome.class, "cast_prompt" );
		}
	};
}
