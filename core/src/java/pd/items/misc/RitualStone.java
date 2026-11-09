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

package pd.items.misc;

import java.util.ArrayList;
import java.util.LinkedList;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Item;
import pd.levels.Level;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.rituals.Ritual;
import pd.rituals.RitualKnowledge;
import pd.rituals.RitualMaterial;
import pd.rituals.Rituals;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 布阵石（批次C）——一次性投掷道具，压缩仪式的布置速度。
 *
 * <p>两条用法（设计裁决：布阵石只负责材料布局，仪式仍需法器对落点激发）：</p>
 * <ul>
 * <li><b>收录</b>（以自身为中心）：扫描 5×5 内的仪式材料，命中某个仪式且该仪式的
 *     讲解页已获得（{@link RitualKnowledge}）时，把配方数量的材料从地面收进石里并记录该仪式；
 *     未获得讲解页的仪式不允许收录。</li>
 * <li><b>投掷</b>（落点为中心）：把石内材料按记录的仪式配方物化到落点（凑齐即完成布置），
 *     布置完成消耗一枚布阵石；无记录/讲解页已失效时按普通物品落地，不浪费。</li>
 * </ul>
 */
public class RitualStone extends Item {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(RitualStone.class)
			.t("name", "布阵石")
			.t("ac_record", "收录仪式")
			.t("record_no_match", "脚边五格内没有凑成仪式的材料。")
			.t("record_unknown", "你不认识这个仪式——先找到它的讲解书页再收录。")
			.t("record_fail", "地上的材料不够凑成一个完整的仪式。")
			.t("record_done", "布阵石刻下了「%s」的材料阵。")
			.t("lay_nothing", "布阵石里还没有收录任何仪式。")
			.t("lay_unknown", "石上的符文黯淡了——收录的仪式已经无法辨认。")
			.t("lay_missing", "石内的材料不足，「%s」未能布成，剩余的材料留在了落点。")
			.t("lay_done", "「%s」已在落点布成，用法器激发即可生效！")
			.t("desc", "一块刻着符文阵的灰石。仪式师用它省去一捧一捧摆材料的功夫：\n\n「收录仪式」会把脚边五格内已学会仪式的材料收进石里；之后把它掷向目标处，材料就会自动在落点布成阵。布置完成后还需用法器对落点激发，仪式方才生效。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsDict.RITUAL_STONE;
		stackable = true;
		defaultAction = AC_THROW;
	}

	private static final String AC_RECORD = "RECORD";
	private static final String RECORD_RITUAL = "record_ritual";
	private static final String STORED = "stored";

	/** 已收录的仪式 id（null = 未收录） */
	private String recordedRitual = null;
	/** 石内存着的材料 */
	private LinkedList<Item> stored = new LinkedList<>();

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_RECORD );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		//SPSEXPD: 收录——以自身为中心扫描 5×5（项目约定：物品动作走 spendAndNext 防回合卡死）
		if (action.equals( AC_RECORD ) && hero != null) {
			if (record( hero, hero.pos )) {
				hero.spendAndNext( 1f );
			}
		} else {
			super.execute( hero, action );
		}
	}

	/** SPSEXPD: 收录；返回 true 表示收录成功（消耗 1 回合） */
	public boolean record( Hero hero, int center ) {
		Level level = Dungeon.level;
		if (level == null || hero == null) return false;

		Ritual ritual = Rituals.matchAt( level, center );
		if (ritual == null) {
			GLog.w( Messages.get( RitualStone.class, "record_no_match" ) );
			return false;
		}
		if (!RitualKnowledge.known( ritual )) {
			//SPSEXPD: 设计裁决——未阅读讲解页的仪式不能收录（仪式本身仍可激发）
			GLog.w( Messages.get( RitualStone.class, "record_unknown", ritual.name() ) );
			return false;
		}
		if (!Rituals.consumeAt( level, center, ritual )) {
			GLog.w( Messages.get( RitualStone.class, "record_fail" ) );
			return false;
		}

		//材料以等量新实例存入石中（同类物品在石内合堆）
		for (RitualMaterial m : ritual.materials()) {
			Item material = Rituals.materialize( m );
			if (material == null) continue;
			material.quantity( m.quantity );
			stored.add( material );
		}
		recordedRitual = ritual.id();

		if (level.heroFOV != null && level.heroFOV[center]) {
			CellEmitter.center( center ).burst( Speck.factory( Speck.STAR ), 8 );
			Sample.INSTANCE.play( Assets.Sounds.EVOKE );
		}
		GLog.i( Messages.get( RitualStone.class, "record_done", ritual.name() ) );
		return true;
	}

	@Override
	protected void onThrow( int cell ) {
		//SPSEXPD: 投掷布置——落点为中心物化材料并完成布置；布置失败按普通物品落地（不浪费）
		if (Dungeon.level != null && lay( cell )) {
			return; //布置成功，投出的这枚即被消耗（投掷路径已从背包 detach）
		}
		super.onThrow( cell );
	}

	/** SPSEXPD: 在落点布置已收录的仪式；成功返回 true */
	public boolean lay( int cell ) {
		Level level = Dungeon.level;
		if (level == null) return false;
		if (recordedRitual == null || stored.isEmpty()) {
			if (level.heroFOV != null && level.heroFOV[cell]) {
				GLog.w( Messages.get( RitualStone.class, "lay_nothing" ) );
			}
			return false;
		}
		Ritual ritual = Rituals.byId( recordedRitual );
		if (ritual == null || !RitualKnowledge.known( ritual )) {
			GLog.w( Messages.get( RitualStone.class, "lay_unknown" ) );
			return false;
		}

		//按配方从石内取材料，物化到落点（数量优先合成整堆）
		for (RitualMaterial m : ritual.materials()) {
			int need = m.quantity;
			while (need > 0) {
				Item found = null;
				for (Item item : stored) {
					if (item != null && m.itemClass.isInstance( item ) && item.quantity() > 0) {
						found = item;
						break;
					}
				}
				if (found == null) {
					//材料不够：把已物化的部分留在落点，但布置视为失败（不消耗布阵石）
					GLog.w( Messages.get( RitualStone.class, "lay_missing", ritual.name() ) );
					return false;
				}
				int take = Math.min( need, found.quantity() );
				need -= take;
				Item drop = Rituals.materialize( m );
				if (drop == null) return false;
				drop.quantity( take );
				level.drop( drop, cell ).sprite.drop();
				if (take >= found.quantity()) {
					stored.remove( found );
				} else {
					found.quantity( found.quantity() - take );
				}
			}
		}

		recordedRitual = null; //一次性：布置完成清空记录（材料已搬空）

		if (level.heroFOV != null && level.heroFOV[cell]) {
			CellEmitter.center( cell ).burst( Speck.factory( Speck.STAR ), 10 );
			Sample.INSTANCE.play( Assets.Sounds.EVOKE );
			GLog.i( Messages.get( RitualStone.class, "lay_done", ritual.name() ) );
		}
		return true;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( RECORD_RITUAL, recordedRitual == null ? "" : recordedRitual );
		bundle.put( STORED, stored );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		String id = bundle.getString( RECORD_RITUAL );
		recordedRitual = id.isEmpty() ? null : id;
		stored = new LinkedList<>();
		if (bundle.contains( STORED )) {
			for (Object o : bundle.getCollection( STORED )) {
				if (o instanceof Item) stored.add( (Item) o );
			}
		}
	}

	/** 描述里追加收录状态（玩家需知道石里记着什么） */
	@Override
	public String desc() {
		String base = Messages.get(this, "desc");
		if (recordedRitual != null) {
			Ritual ritual = Rituals.byId( recordedRitual );
			if (ritual != null) {
				return base + "\n\n_当前收录：" + ritual.name() + "_";
			}
		}
		return base;
	}
}
