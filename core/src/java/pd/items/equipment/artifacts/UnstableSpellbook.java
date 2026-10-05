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

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import com.badlogic.gdx.Gdx;
import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Regeneration;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import pd.items.DewVial;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.misc.SkillOfAtk;
import pd.items.misc.SkillOfDef;
import pd.items.misc.SkillOfMig;
import pd.items.equipment.rings.RingOfEnergy;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfLullaby;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.scrolls.ScrollOfTerror;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class UnstableSpellbook extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UnstableSpellbook.class)
			.t("name", "无序魔典")
			.t("ac_read", "阅读")
			.t("ac_add", "加入")
			.t("ac_song", "耗竭-圣歌")
			.t("blinded", "你不能在失明的时候阅读书籍。")
			.t("no_charge", "你的咒语书耗尽了能量。")
			.t("cursed", "被诅咒的魔典锁死了书页，你无法阅读它。")
			.t("update", "魔法书变得更加强大。")
			.t("dew_empty", "高能露珠不足。")
			.t("prompt", "选择一个卷轴")
			.t("infuse_scroll", "你将卷轴的能量注入了书中。")
			.t("unable_scroll", "你无法将这个卷轴添加到书中。")
			.t("unknown_scroll", "你仍然不清楚这个卷轴的种类。")
			.t("desc", "这本魔典就其年岁而言被保养得异常好。当你翻动页面时它不断地噼啪作响并发出嘶嘶声，涌动着不稳定的能量。一旦诵读这本书，没人能笃定你会释放出什么咒语。")
			.t("desc_cursed", "被诅咒的魔典将自己绑在了你身上，并在抑制你使用大部分卷轴的能力。")
			.t("desc_index", "下次升级该物品所需要的露珠为%s。")
			.t("desc_empowered", "被你放进书中的卷轴闪耀着魔力的微光。你能够利用魔典进一步激发卷轴能力，展现出秘卷效果，但这样也会消耗你额外的充能。")
			.t("read_empowered", "被你放入魔典的卷轴都充满了魔法能量。你可以自主选择导引出该卷轴的正常或秘卷能力。\n\n如果选择释放秘卷能力，则会消耗2点魔典充能。");
	}




	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;

		levelCap = 10;

		charge = level()/2+2;
		partialCharge = 0;
		chargeCap = level()/2+2;

		defaultAction = AC_READ;
	}

	public static final String AC_READ = "READ";
	public static final String AC_ADD = "ADD";
	public static final String AC_SONG = "SONG";

	private final ArrayList<Class> scrolls = new ArrayList<>();

	public UnstableSpellbook() {
		super();

		setupScrolls();
	}

	private void setupScrolls(){
		scrolls.clear();

		Class<?>[] scrollClasses = Generator.Category.SCROLL.classes;
		float[] probs = Generator.Category.SCROLL.defaultProbsTotal.clone(); //array of primitives, clone gives deep copy.
		int i = Random.chances(probs);

		while (i != -1){
			scrolls.add(scrollClasses[i]);
			probs[i] = 0;

			i = Random.chances(probs);
		}
		scrolls.remove(ScrollOfTransmutation.class);
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && charge > 0 && !cursed) {
			actions.add(AC_READ);
		}
		if (isEquipped( hero ) && level() < levelCap && !cursed) {
			actions.add(AC_ADD);
		}
		if (!isEquipped(hero) && level() > 3 && !cursed) actions.add(AC_SONG);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		if (action.equals( AC_READ )) {

			if (hero.buff( Blindness.class ) != null) GLog.w( Messages.get(this, "blinded") );
			else if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge <= 0)                     GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                          GLog.i( Messages.get(this, "cursed") );
			else {
				doReadEffect(hero);
			}

		} else if (action.equals( AC_ADD )) {
			upgradeWithDew(hero);
		} else if (action.equals(AC_SONG)) {
			sing(hero);
		} else {
			super.execute(hero, action);
		}
	}

	public void doReadEffect(Hero hero){
		if (hero == null || charge <= 0 || cursed) return;
		charge--;

		Scroll scroll = null;
		for (int attempts = 0; attempts < 100 && scroll == null; attempts++) {
			Scroll candidate = randomScroll();
			if (candidate == null
				//reduce the frequency of these scrolls by half
				|| ((candidate instanceof ScrollOfIdentify ||
				candidate instanceof ScrollOfRemoveCurse ||
				candidate instanceof ScrollOfMagicMapping) && Random.Int(2) == 0)
				|| (candidate instanceof pd.items.consum.scrolls.ScrollOfTeleportation
				&& Dungeon.bossLevel())) continue;
			scroll = candidate;
		}
		if (scroll == null) scroll = new ScrollOfIdentify();

		scroll.ownedByBook = true;
		scroll.anonymize();
		scroll.talentChance = 0;  //spellbook does not trigger on-scroll talents
		curItem = scroll;
		curUser = hero;
		if (useRegularScrollEffect()) scroll.doRead();
		else scroll.empoweredRead();

		updateQuickslot();
	}

	protected Scroll randomScroll() {
		return (Scroll) Generator.random(Generator.Category.SCROLL);
	}

	protected boolean useRegularScrollEffect() {
		return Random.Int(15) < level();
	}

	protected boolean upgradeWithDew(Hero hero) {
		if (hero == null || !isEquipped(hero) || cursed || level() >= levelCap) return false;
		DewVial vial = hero.belongings.getItem(DewVial.class);
		int cost = (level() + 1) * 100;
		//SPSEXPD: 露珠瓶已单池化，升级直接消耗露珠池
		if (vial == null || vial.checkVol() <= cost) {
			GLog.w(Messages.get(UnstableSpellbook.class, "dew_empty"));
			return false;
		}
		vial.upbook(cost);
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		}
		hero.busy();
		hero.spend(2f);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
		upgrade();
		GLog.w(Messages.get(this, "update"));
		return true;
	}

	protected boolean sing(Hero hero) {
		if (hero == null || isEquipped(hero) || cursed || level() <= 3 || Dungeon.level == null) return false;
		int songLevel = level();
		Buff.affect(hero, AttackUp.class, songLevel * 10f).level(25);
		Buff.affect(hero, DefenceUp.class, songLevel * 10f).level(25);
		Buff.affect(hero, Arcane.class, songLevel * 2f);
		Buff.affect(hero, TargetShoot.class, songLevel * 10f);
		if (songLevel >= 8) {
			hero.improveAttackSkill(1);
			hero.improveDefenseSkill(1);
			GLog.w(Messages.get(SkillOfAtk.class, "skillup"));
			GLog.w(Messages.get(SkillOfDef.class, "skillup"));
		}
		if (songLevel >= 10) {
			hero.improveMagicSkill(1);
			Buff.affect(hero, Invisibility.class, songLevel * 10f);
			Buff.affect(hero, HasteBuff.class, songLevel * 3f);
			GLog.w(Messages.get(SkillOfMig.class, "skillup"));
		}
		hero.spendAndNext(1f);
		detach(hero.belongings.backpack);
		Dungeon.level.drop(new UnstableSpellbook(), hero.pos);
		updateQuickslot();
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		return true;
	}

	private void checkForArtifactProc(Hero user, Scroll scroll){
		//if the base scroll (exotics all match) is an AOE effect, then also trigger illuminate
		if (scroll instanceof ScrollOfLullaby
				|| scroll instanceof ScrollOfRemoveCurse || scroll instanceof ScrollOfTerror) {
			for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
				if (Dungeon.level.heroFOV[mob.pos]) {
					artifactProc(mob, visiblyUpgraded(), 1);
				}
			}
		//except rage, which affects everything even if it isn't visible
		} else if (scroll instanceof ScrollOfRage){
			for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
				artifactProc(mob, visiblyUpgraded(), 1);
			}
		}
	}

	//forces the reading of a regular scroll if the player tried to exploit by quitting the game when the menu was up
	public static class ExploitHandler extends Buff {
		{ actPriority = VFX_PRIO; }

		public Scroll scroll;

		@Override
		public boolean act() {
			curUser = Dungeon.hero;
			curItem = scroll;
			scroll.anonymize();
			scroll.talentChance = 0;
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					scroll.doRead();
					Item.updateQuickslot();
				}
			});
			detach();
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( "scroll", scroll );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			scroll = (Scroll)bundle.get("scroll");
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new bookRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		//SPS-PD 0.9.8 only charges the book through its passive buff.
	}

	@Override
	public Item upgrade() {
		chargeCap = (level()+1)/2+3;
		return super.upgrade();
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		setupScrolls();
		while (!scrolls.isEmpty() && scrolls.size() > (levelCap-1-level())) {
			scrolls.remove(0);
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (cursed && isEquipped(Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_cursed");
		if (level() < levelCap) desc += "\n\n" + Messages.get(this, "desc_index", (level()+1)*100);

		return desc;
	}

	private static final String SCROLLS =   "scrolls";
	private static final String LEGACY_PARTIAL_CHARGE = "partialCharge";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( SCROLLS, scrolls.toArray(new Class[scrolls.size()]) );
		bundle.put(LEGACY_PARTIAL_CHARGE, partialCharge);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		chargeCap = level() == 0 ? 2 : (level()/2)+3;
		charge = Math.max(0, Math.min(bundle.getInt("charge"), chargeCap));
		if (bundle.contains(LEGACY_PARTIAL_CHARGE)) partialCharge = bundle.getFloat(LEGACY_PARTIAL_CHARGE);
		if (bundle.contains(SCROLLS) && bundle.getClassArray(SCROLLS) != null) {
			scrolls.clear();
			for (Class<?> scroll : bundle.getClassArray(SCROLLS)) {
				if (scroll != null) scrolls.add(scroll);
			}
		}
	}

	public class bookRecharge extends ArtifactBuff{
		@Override
		public boolean act() {
			if (charge < chargeCap && !cursed) {
				partialCharge += 1 / (150f - (chargeCap - charge)*15f);
				if (partialCharge >= 1) {
					partialCharge--;
					charge++;
					if (charge == chargeCap) partialCharge = 0;
				}
			}

			updateQuickslot();

			spend( TICK );

			return true;
		}
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(UnstableSpellbook.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return ScrollHolder.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Scroll && item.isIdentified() && scrolls.contains(item.getClass());
		}

		@Override
		public void onSelect(Item item) {
			if (item != null && item instanceof Scroll && item.isIdentified()){
				Hero hero = Dungeon.hero;
				for (int i = 0; ( i <= 1 && i < scrolls.size() ); i++){
					if (scrolls.get(i).equals(item.getClass())){
						hero.sprite.operate( hero.pos );
						hero.busy();
						hero.spend( 2f );
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
						hero.sprite.emitter().burst( ElmoParticle.FACTORY, 12 );

						scrolls.remove(i);
						item.detach(hero.belongings.backpack);

						upgrade();
						Catalog.countUse(UnstableSpellbook.class);
						GLog.i( Messages.get(UnstableSpellbook.class, "infuse_scroll") );
						return;
					}
				}
				GLog.w( Messages.get(UnstableSpellbook.class, "unable_scroll") );
			} else if (item instanceof Scroll && !item.isIdentified()) {
				GLog.w( Messages.get(UnstableSpellbook.class, "unknown_scroll") );
			}
		}
	};
}
