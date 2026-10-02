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

package pd.items.equipment.artifacts;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Notice;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mimic;
import pd.effects.CheckedCell;
import pd.effects.particles.ElmoParticle;
import pd.items.Heap;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.journal.Catalog;
import pd.levels.CellFlags;
import pd.levels.SpsSokobanLevel;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.mechanics.ConeAOE;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class TalismanOfForesight extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TalismanOfForesight.class)
			.t("name", "先见护符")
			.t("ac_scry", "探查")
			.t("ac_notice", "耗竭-预知")
			.t("no_charge", "你的护符尚未被完全充能。")
			.t("scry", "护符将关于本层的知识填满了你的脑海。")
			.t("low_charge", "护符至少要充能5%才能探查。")
			.t("prompt", "选择要探查的位置")
			.t("levelup", "你的护符变得更强大了！")
			.t("full_charge", "你的护符充满了能量！")
			.t("desc", "一块奇怪的有着光滑雕刻的石头。你觉得它在关注着你周围的一切，留意任何不寻常的东西。")
			.t("desc_worn", "当你拿着护符时你感觉你的感知力提高了。")
			.t("desc_cursed", "被诅咒的护符目不转睛地瞪着你，使你无法集中精力。")
			.t("$foresight.name", "先见")
			.t("$foresight.levelup", "你的护符变得更强大了！你似乎预见到了某些危险。")
			.t("$foresight.full_charge", "你的护符充能满了！")
			.t("$foresight.uneasy", "你感到很不安。")
			.t("$foresight.desc", "你感到非常焦虑，仿佛周遭有未被发现的危险。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;

		exp = 0;
		levelCap = 10;

		charge = 0;
		partialCharge = 0;
		chargeCap = 100;

		defaultAction = AC_SCRY;
	}

	public static final String AC_SCRY = "SCRY";
	public static final String AC_NOTICE = "NOTICE";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped(hero) && charge == chargeCap && !cursed
				&& !(Dungeon.level instanceof SpsSokobanLevel)) {
			actions.add(AC_SCRY);
		}
		if (isEquipped(hero) && level() > 2 && !cursed) actions.add(AC_NOTICE);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);

		if (AC_SCRY.equals(action)) {
			if (!isEquipped(hero))  GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge != chargeCap) GLog.i(Messages.get(this, "no_charge"));
			else if (Dungeon.level instanceof SpsSokobanLevel) return;
			else useScry(hero);
		} else if (AC_NOTICE.equals(action)) {
			if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			else if (level() > 2 && !cursed) useNotice(hero);
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Foresight();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		// SPS-PD 0.9.8 charges only through time and discovered secrets.
	}

	protected void useScry(Hero hero) {
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.busy();
		}
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BEACON);
		charge = 0;
		if (Dungeon.level != null) {
			for (int i = 0; i < Dungeon.level.length(); i++) {
				int terrain = Dungeon.level.map[i];
				if ((Terrain.flags[terrain] & Terrain.SECRET) != 0) {
					GameScene.updateMap(i);
					if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[i]) {
						GameScene.discoverTile(i, terrain);
					}
				}
			}
		}
		GLog.p(Messages.get(this, "scry"));
		Buff.affect(hero, Awareness.class, Awareness.DURATION);
		Dungeon.observe();
		updateQuickslot();
	}

	protected void useNotice(Hero hero) {
		int duration = level() * 10;
		level(level() - 2);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		Buff.affect(hero, Notice.class, duration);
		hero.spend(Actor.TICK);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		updateQuickslot();
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if ( isEquipped( Dungeon.hero ) ){
			if (!cursed) {
				desc += "\n\n" + Messages.get(this, "desc_worn");

			} else {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
		}

		return desc;
	}

	private float maxDist(){
		return Math.min(5 + 2*level(), (charge-3)/1.08f);
	}

	public CellSelector.Listener scry = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer target) {
			if (target != null && target != curUser.pos){

				//enforces at least 2 tiles of distance
				if (Dungeon.level.adjacent(target, curUser.pos)){
					target += (target - curUser.pos);
				}

				float dist = Dungeon.level.trueDistance(curUser.pos, target);

				if (dist >= 3 && dist > maxDist()){
					Ballistica trajectory = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET);
					int i = 0;
					while (i < trajectory.path.size()
							&& Dungeon.level.trueDistance(curUser.pos, trajectory.path.get(i)) <= maxDist()){
						target = trajectory.path.get(i);
						i++;
					}
					dist = Dungeon.level.trueDistance(curUser.pos, target);
				}

				//starts at 200 degrees, loses 8% per tile of distance
				float angle = Math.round(200*(float)Math.pow(0.92, dist));
				ConeAOE cone = new ConeAOE(new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET), angle);

				int earnedExp = 0;
				boolean noticed = false;
				for (int cell : cone.cells){
					GameScene.checkedCell( cell, curUser.pos );
					if (Dungeon.level.discoverable[cell] && !(Dungeon.level.mapped[cell] || Dungeon.level.visited[cell])){
						Dungeon.level.mapped[cell] = true;
						earnedExp++;
					}

					if (Dungeon.level.secret[cell]) {
						int oldValue = Dungeon.level.map[cell];
						GameScene.discoverTile(cell, oldValue);
						CellFlags.discover( Dungeon.level,  cell );
						ScrollOfMagicMapping.discover(cell);
						noticed = true;

						if (oldValue == Terrain.SECRET_TRAP){
							earnedExp += 10;
						} else if (oldValue == Terrain.SECRET_DOOR){
							earnedExp += 100;
						}
					}

					Char ch = Actor.findChar(cell);
					if (ch != null
							&& (ch.alignment != Char.Alignment.NEUTRAL || ch instanceof Mimic)
							&& ch.alignment != curUser.alignment){
						Buff.append(curUser, CharAwareness.class, 5 + 2*level()).charID = ch.id();

						artifactProc(ch, visiblyUpgraded(), (int)(3 + dist*1.08f));

						if (!curUser.fieldOfView[ch.pos]){
							earnedExp += 10;
						}
					}

					Heap h = Dungeon.level.heaps.get(cell);
					if (h != null){
						Buff.append(curUser, HeapAwareness.class, 5 + 2*level()).pos = h.pos;

						if (!h.seen){
							earnedExp += 10;
						}
					}

				}

				exp += earnedExp;
				if (exp >= 100 + 50*level() && level() < levelCap) {
					exp -= 100 + 50*level();
					upgrade();
					Catalog.countUse(TalismanOfForesight.class);
					GLog.p( Messages.get(TalismanOfForesight.class, "levelup") );
				}
				updateQuickslot();

				//5 charge at 2 tiles, up to 30 charge at 25 tiles
				charge -= 3 + dist*1.08f;
				partialCharge -= (dist*1.08f)%1f;
				if (partialCharge < 0 && charge > 0){
					partialCharge ++;
					charge --;
				}
				while (charge < 0){
					charge++;
					partialCharge--;
				}
				Invisibility.dispel(curUser);
				Talent.onArtifactUsed(Dungeon.hero);
				updateQuickslot();
				Dungeon.observe();
				Dungeon.hero.checkVisibleMobs();
				GameScene.updateFog();

				curUser.sprite.zap(target);
				curUser.spendAndNext(Actor.TICK);
				Sample.INSTANCE.play(Assets.Sounds.SCAN);
				if (noticed) Sample.INSTANCE.play(Assets.Sounds.SECRET);

			}

		}

		@Override
		public String prompt() {
			return Messages.get(TalismanOfForesight.class, "prompt");
		}
	};

	private static final String WARN = "warn";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(WARN, warn > 0);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		warn = bundle.getBoolean(WARN) ? 3 : 0;
	}
	
	private int warn = 0;
	
	public class Foresight extends ArtifactBuff{

		@Override
		public boolean act() {
			spend( TICK );

			checkAwareness();

			if (charge < chargeCap && !cursed) {
				partialCharge += 0.04f + level() * 0.006f;
				if (partialCharge > 1f && charge < chargeCap) {
					partialCharge--;
					charge++;
					updateQuickslot();
				} else if (charge >= chargeCap) {
					partialCharge = 0;
					GLog.p(Messages.get(this, "full_charge"));
				}
			}

			return true;
		}

		public void checkAwareness(){
			boolean smthFound = false;
			if (Dungeon.level == null || target == null || target.pos < 0
					|| target.pos >= Dungeon.level.length() || Dungeon.level.heroFOV == null) {
				if (warn > 0) warn--;
				return;
			}

			int distance = 3;

			int cx = target.pos % Dungeon.level.width();
			int cy = target.pos / Dungeon.level.width();
			int ax = cx - distance;
			if (ax < 0) {
				ax = 0;
			}
			int bx = cx + distance;
			if (bx >= Dungeon.level.width()) {
				bx = Dungeon.level.width() - 1;
			}
			int ay = cy - distance;
			if (ay < 0) {
				ay = 0;
			}
			int by = cy + distance;
			if (by >= Dungeon.level.height()) {
				by = Dungeon.level.height() - 1;
			}

			for (int y = ay; y <= by; y++) {
				for (int x = ax, p = ax + y * Dungeon.level.width(); x <= bx; x++, p++) {

					if (Dungeon.level.heroFOV[p]
							&& Dungeon.level.secret[p]
							&& Dungeon.level.map[p] != Terrain.SECRET_DOOR) smthFound = true;
				}
			}

			if (smthFound && !cursed) {
				if (warn == 0) {
					GLog.w( Messages.get(this, "uneasy") );
					if (target instanceof Hero){
						((Hero)target).interrupt();
					}
				}
				warn = 3;
			} else {
				if (warn > 0) warn--;
			}
			BuffIndicator.refreshHero();
		}

		public void charge() {
			charge = Math.min(charge + 2 + level() / 3, chargeCap);
			exp++;
			if (exp >= 4 && level() < levelCap) {
				upgrade();
				exp -= 4;
				GLog.p(Messages.get(this, "levelup"));
			}
			updateQuickslot();
		}

		@Override
		public boolean isCursed() {
			return cursed;
		}

		@Override
		public int icon() {
			if (warn > 0)
				return BuffIndicator.FORESIGHT;
			else
				return BuffIndicator.NONE;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}

	public static class CharAwareness extends FlavourBuff {

		public int charID;

		private static final String CHAR_ID = "char_id";

		@Override
		public void detach() {
			super.detach();
			Dungeon.observe();
			GameScene.updateFog();
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			charID = bundle.getInt(CHAR_ID);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CHAR_ID, charID);
		}

	}

	public static class HeapAwareness extends FlavourBuff {

		public int pos;
		public int depth = Dungeon.depth;
		public int branch = Dungeon.branch;

		private static final String POS = "pos";
		private static final String DEPTH = "depth";
		private static final String BRANCH = "branch";

		@Override
		public void detach() {
			super.detach();
			Dungeon.observe();
			GameScene.updateFog();
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
			depth = bundle.getInt(DEPTH);
			branch = bundle.getInt(BRANCH);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
			bundle.put(DEPTH, depth);
			bundle.put(BRANCH, branch);
		}
	}

}
