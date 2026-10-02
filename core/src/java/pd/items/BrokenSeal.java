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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Combo;
import pd.actors.buffs.HoldFast;
import pd.actors.buffs.Regeneration;
import pd.actors.buffs.ShieldBuff;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import pd.windows.WndUseItem;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.math.GameMath;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import pd.messages.InlineText;

public class BrokenSeal extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BrokenSeal.class)
			.t("name", "破损纹章")
			.t("ac_affix", "贴附")
			.t("prompt", "选择一件护甲。")
			.t("unknown_armor", "你需要先鉴定那件护甲有无诅咒。")
			.t("cursed_armor", "纹章不能贴附于被诅咒的盔甲。")
			.t("affix", "你将纹章佩挂在了护甲上！")
			.t("desc", "一枚蜡制纹章，作为勇气的象征而贴附在护甲之上。纹章上刻有磨损的防御符咒，并从中碎裂为两半。\n\n这是一件来自家乡的纪念物，在纹章的支持下战士会变得不屈不挠。佩戴着纹章，战士会在将要受伤至生命值半数以下时立即获得护盾。\n\n纹章可以被_贴附在护甲上_并能在护甲间转移。它能够携带一次升级，前提是升级时纹章需已贴附在护甲上。")
			.t("inscribed", "纹章刻有_%s_。")
			.t("choose_title", "选择一个刻印")
			.t("choose_desc", "这件护甲与破损纹章均刻有刻印。请选择一个想保留的刻印。\n\n护甲刻印：%1$s\n破损纹章刻印：%2$s\n\n注意，如果选择保留护甲的刻印，纹章将无法转移该刻印。")
			.t("discover_hint", "某位英雄初始携带该物品。")
			.t("warriorshield.name", "战士护盾")
			.t("warriorshield.desc_active", "战士的破损纹章正使他变得不屈不挠，使其获得在生命值之上的护盾。在护盾首次触发后其可被再次使用之前护盾需要进行冷却。\n\n这种护盾并不会随时间衰减，但如果附近持续几回合没有敌人则护盾会结束。当其结束时，任何未使用的护盾都会降低护盾冷却，最多降低50%%。\n\n剩余护盾：%1$d\n\n当前冷却：%2$d")
			.t("warriorshield.desc_cooldown", "战士近期已经从他的破碎纹章获得了护盾，而他必须等待直至他可从其护盾效果中再次获益。\n\n剩余回合数：%d")
			.t("warriorshield.desc_negative_cooldown", "战士护盾的冷却时间当前为负值，意味着当他的护盾生效时，冷却时间会比通常的150回合更低。护盾冷却可被减至最低-150回合，意味着护盾可在生效后立刻冷却完毕，以便再次激活护盾。\n\n当前冷却时间：%d");
	}


	public static final String AC_AFFIX = "AFFIX";

	//only to be used from the quickslot, for tutorial purposes mostly.
	public static final String AC_INFO = "INFO_WINDOW";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;

		cursedKnown = levelKnown = true;
		unique = true;
		bones = false;

		defaultAction = AC_INFO;
	}

	private Armor.Glyph glyph;

	public boolean canTransferGlyph(){
		if (glyph == null){
			return false;
		}
		if (Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE) == 2){
			return true;
		} else if (Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE) == 1
			&& (Arrays.asList(Armor.Glyph.common).contains(glyph.getClass())
				|| Arrays.asList(Armor.Glyph.uncommon).contains(glyph.getClass()))){
			return true;
		} else {
			return false;
		}
	}

	public Armor.Glyph getGlyph(){
		return glyph;
	}

	public void setGlyph( Armor.Glyph glyph ){
		this.glyph = glyph;
	}

	public int maxShield( int armTier, int armLvl ){
		// 5-15, based on equip tier and iron will
		return 3 + 2*armTier + Dungeon.hero.pointsInTalent(Talent.IRON_WILL);
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null ? glyph.glowing() : null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions =  super.actions(hero);
		actions.add(AC_AFFIX);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_AFFIX)){
			curItem = this;
			GameScene.selectItem(armorSelector);
		} else if (action.equals(AC_INFO)) {
			GameScene.show(new WndUseItem(null, this));
		}
	}

	//outgoing is either the seal itself as an item, or an armor the seal is affixed to
	public void affixToArmor(Armor armor, Item outgoing){
		if (armor != null) {
			if (!armor.cursedKnown){
				GLog.w(Messages.get(BrokenSeal.class, "unknown_armor"));

			} else if (armor.cursed && (getGlyph() == null || !getGlyph().curse())){
				GLog.w(Messages.get(BrokenSeal.class, "cursed_armor"));

			} else if (armor.glyph != null && getGlyph() != null &&
					(canTransferGlyph() || outgoing instanceof BrokenSeal) //if glyph is on the seal in isolation, always allow xfer
					&& armor.glyph.getClass() != getGlyph().getClass()) {

				GameScene.show(new WndOptions(new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
						Messages.get(BrokenSeal.class, "choose_title"),
						Messages.get(BrokenSeal.class, "choose_desc", armor.glyph.name(), getGlyph().name()),
						armor.glyph.name(),
						getGlyph().name()){
					@Override
					protected void onSelect(int index) {
						if (index == -1) return;

						if (outgoing == BrokenSeal.this) {
							detach(Dungeon.hero.belongings.backpack);
						} else if (outgoing instanceof Armor){
							((Armor) outgoing).detachSeal();
						}

						if (index == 0) setGlyph(null);
						//if index is 1, then the glyph transfer happens in affixSeal

						GLog.p(Messages.get(BrokenSeal.class, "affix"));
						Dungeon.hero.sprite.operate(Dungeon.hero.pos);
						Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
						armor.affixSeal(BrokenSeal.this);
					}

					@Override
					public void hide() {
						super.hide();
						Dungeon.hero.next();
					}
				});

			} else {
				if (outgoing == this) {
					detach(Dungeon.hero.belongings.backpack);
				} else if (outgoing instanceof Armor){
					((Armor) outgoing).detachSeal();
				}

				GLog.p(Messages.get(BrokenSeal.class, "affix"));
				Dungeon.hero.sprite.operate(Dungeon.hero.pos);
				Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
				armor.affixSeal(this);
				Dungeon.hero.next();
			}
		}
	}

	@Override
	public String name() {
		return glyph != null ? glyph.name( super.name() ) : super.name();
	}

	@Override
	public String info() {
		String info = super.info();
		if (glyph != null){
			info += "\n\n" + Messages.get(this, "inscribed", glyph.name());
			info += " " + glyph.desc();
		}
		return info;
	}

	@Override
	//scroll of upgrade can be used directly once, same as upgrading armor the seal is affixed to then removing it.
	public boolean isUpgradable() {
		return level() == 0;
	}

	protected static WndBag.ItemSelector armorSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return  Messages.get(BrokenSeal.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Armor;
		}

		@Override
		public void onSelect( Item item ) {
			if (item instanceof Armor) {
				BrokenSeal seal = (BrokenSeal) curItem;
				seal.affixToArmor((Armor)item, seal);
			}
		}
	};

	private static final String GLYPH = "glyph";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GLYPH, glyph);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		glyph = (Armor.Glyph)bundle.get(GLYPH);
	}

	public static class WarriorShield extends ShieldBuff {

		{
			type = buffType.POSITIVE;

			detachesAtZero = false;
			shieldUsePriority = 2;
		}

		private Armor armor;

		private int cooldown = 0;
		private float turnsSinceEnemies = 0;
		private int initialShield = 0;

		private static int COOLDOWN_START = 150;

		@Override
		public int icon() {
			if (coolingDown() || shielding() > 0 || cooldown < 0){
				return BuffIndicator.SEAL_SHIELD;
			} else {
				return BuffIndicator.NONE;
			}
		}

		@Override
		public void tintIcon(Image icon) {
			icon.resetColor();
			if (coolingDown() && shielding() == 0){
				icon.brightness(0.3f);
			} else if (cooldown < 0) {
				icon.invert();
			}
		}

		@Override
		public float iconFadePercent() {
			if (shielding() > 0){
				return GameMath.gate(0, 1f - shielding()/(float)initialShield, 1);
			} else if (coolingDown()){
				return GameMath.gate(0, cooldown / (float)COOLDOWN_START, 1);
			} else if (cooldown < 0) {
				return GameMath.gate(0, (COOLDOWN_START+cooldown) / (float)COOLDOWN_START, 1);
			} else {
				return 0;
			}
		}

		@Override
		public String iconTextDisplay() {
			if (shielding() > 0){
				return Integer.toString(shielding());
			} else if (coolingDown() || cooldown < 0){
				return Integer.toString(cooldown);
			} else {
				return "";
			}
		}

		@Override
		public String desc() {
			if (shielding() > 0) {
				return Messages.get(this, "desc_active", shielding(), cooldown);
			} else if (cooldown < 0) {
				return Messages.get(this, "desc_negative_cooldown", cooldown);
			} else {
				return Messages.get(this, "desc_cooldown", cooldown);
			}
		}

		@Override
		public synchronized boolean act() {
			if (cooldown > 0 && Regeneration.regenOn()){
				cooldown--;
			}

			if (shielding() > 0){
				if (Dungeon.hero.visibleEnemies() == 0 && Dungeon.hero.buff(Combo.class) == null){
					turnsSinceEnemies += HoldFast.buffDecayFactor(target);
					if (turnsSinceEnemies >= 5){
						if (cooldown > 0) {
							float percentLeft = shielding() / (float)initialShield;
							//max of 50% cooldown refund
							cooldown = Math.max(0, (int)(cooldown - COOLDOWN_START * (percentLeft / 2f)));
						}
						decShield(shielding());
					}
				} else {
					turnsSinceEnemies = 0;
				}
			}
			
			if (shielding() <= 0 && maxShield() <= 0 && cooldown == 0){
				detach();
			}
			
			spend(TICK);
			return true;
		}

		public synchronized void activate() {
			incShield(maxShield());
			cooldown = Math.max(0, cooldown+COOLDOWN_START);
			turnsSinceEnemies = 0;
			initialShield = maxShield();
		}

		public boolean coolingDown(){
			return cooldown > 0;
		}

		public void reduceCooldown(float percentage){
			cooldown -= Math.round(COOLDOWN_START*percentage);
			cooldown = Math.max(cooldown, -COOLDOWN_START);
		}

		public synchronized void setArmor(Armor arm){
			armor = arm;
		}

		public synchronized int maxShield() {
			//metamorphed iron will logic
			if (((Hero)target).heroClass != HeroClass.WARRIOR && ((Hero) target).hasTalent(Talent.IRON_WILL)){
				return ((Hero) target).pointsInTalent(Talent.IRON_WILL);
			}

			if (armor != null && armor.isEquipped((Hero)target) && armor.checkSeal() != null) {
				return armor.checkSeal().maxShield(armor.tier, armor.level());
			} else {
				return 0;
			}
		}

		public static final String COOLDOWN = "cooldown";
		public static final String TURNS_SINCE_ENEMIES = "turns_since_enemies";
		public static final String INITIAL_SHIELD = "initial_shield";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(COOLDOWN, cooldown);
			bundle.put(TURNS_SINCE_ENEMIES, turnsSinceEnemies);
			bundle.put(INITIAL_SHIELD, initialShield);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			if (bundle.contains(COOLDOWN)) {
				cooldown = bundle.getInt(COOLDOWN);
				turnsSinceEnemies = bundle.getFloat(TURNS_SINCE_ENEMIES);
				initialShield = bundle.getInt(INITIAL_SHIELD);

			//if we have shield from pre-3.1, have it last a bit
			} else if (shielding() > 0) {
				turnsSinceEnemies = -100;
				initialShield = shielding();
			}
		}
	}
}
