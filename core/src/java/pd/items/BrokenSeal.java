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
import pd.actors.buffs.Hunger;
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
import pd.atlas.items.EquipmentNonEquipDict;

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
			.t("desc", "一枚蜡制纹章，作为勇气的象征而贴附在护甲之上。纹章上刻有磨损的防御符咒，并从中碎裂为两半。\n\n这是一件来自家乡的纪念物，在纹章的支持下战士会变得不屈不挠：护盾会随时间累积（上限为护甲等级 + 阶位×2），但在极度饥饿时停止累积。\n\n纹章可以被_贴附在护甲上_并能在护甲间转移。它能够携带最多 _2 级_升级（升级时纹章需已贴附在护甲上），并能携带_任意刻印_。\n\n纹章_取下后_同样是一件可被打造的物品：它可以被_升级卷轴_继续升级（仍以 2 级为限），也可以被_附魔道具_刻上新的刻印。")
			.t("inscribed", "纹章刻有_%s_。")
			.t("choose_title", "选择一个刻印")
			.t("choose_desc", "这件护甲与破损纹章均刻有刻印。请选择一个想保留的刻印。\n\n护甲刻印：%1$s\n破损纹章刻印：%2$s\n\n注意，如果选择保留护甲的刻印，纹章将无法转移该刻印。")
			.t("discover_hint", "某位英雄初始携带该物品。")
			.t("$warriorshield.name", "战士护盾")
			.t("$warriorshield.desc_active", "战士的破损纹章正使他变得不屈不挠，使其获得在生命值之上的护盾。在护盾首次触发后其可被再次使用之前护盾需要进行冷却。\n\n这种护盾并不会随时间衰减，但如果附近持续几回合没有敌人则护盾会结束。当其结束时，任何未使用的护盾都会降低护盾冷却，最多降低50%%。\n\n剩余护盾：%1$d\n\n当前冷却：%2$d")
			.t("$warriorshield.desc_cooldown", "战士近期已经从他的破碎纹章获得了护盾，而他必须等待直至他可从其护盾效果中再次获益。\n\n剩余回合数：%d")
			.t("$warriorshield.desc_negative_cooldown", "战士护盾的冷却时间当前为负值，意味着当他的护盾生效时，冷却时间会比通常的150回合更低。护盾冷却可被减至最低-150回合，意味着护盾可在生效后立刻冷却完毕，以便再次激活护盾。\n\n当前冷却时间：%d");
	}




	public static final String AC_AFFIX = "AFFIX";

	//only to be used from the quickslot, for tutorial purposes mostly.
	public static final String AC_INFO = "INFO_WINDOW";

	/** SPSEXPD: 纹章可携带的升级上限（破损纹章 2 级；碎片在子类覆写为 1 级）。 */
	public int maxCarriedLevel() {
		return 2;
	}

	/** SPSEXPD: 能否携带某刻印 —— 破损纹章可携带任意刻印；碎片只允许普通刻印（非诅咒、非强大，子类覆写）。 */
	public boolean canCarryGlyph( Armor.Glyph g ) {
		return g != null;
	}

	{
		image = EquipmentNonEquipDict.SEAL_SHARD;

		cursedKnown = levelKnown = true;
		unique = true;
		bones = false;

		defaultAction = AC_INFO;
	}

	private Armor.Glyph glyph;

	public boolean canTransferGlyph(){
		//SPSXPD: 原为破碎天赋「刻印转移」解锁，现直接赋予物品 —— 任意刻印均可转移
		return glyph != null;
	}

	public Armor.Glyph getGlyph(){
		return glyph;
	}

	public void setGlyph( Armor.Glyph glyph ){
		this.glyph = glyph;
	}

	/** SPSEXPD: 供附魔道具（注魔秘卷/附魔符石）给拆卸下来的纹章刻上刻印。
	 *  会拒绝本纹章无法携带的刻印（碎片只允许普通刻印），避免出现「消耗了道具却没生效」。 */
	public void inscribe( Armor.Glyph glyph ){
		if (glyph != null && !canCarryGlyph( glyph )) return;
		setGlyph( glyph );
	}

	/** SPSEXPD: 随机一个本纹章可携带的刻印（池中全是禁用品时返回 null）。 */
	public Armor.Glyph randomCarriableGlyph() {
		Class<? extends Armor.Glyph> exclude = glyph != null ? glyph.getClass() : null;
		for (int i = 0; i < 64; i++) {
			Armor.Glyph g = Armor.Glyph.random( exclude );
			if (canCarryGlyph( g )) return g;
		}
		return null;
	}

	/** SPSEXPD: 随机刻上可携带的刻印；返回是否真的刻上了（供调用方决定要不要消耗道具）。 */
	public boolean inscribeRandom() {
		Armor.Glyph g = randomCarriableGlyph();
		if (g == null) return false;
		inscribe( g );
		return true;
	}

	public int maxShield( int armTier, int armLvl ){
		//SPSEXPD: 护盾上限 = 护甲等级 + 阶位 × 2（护盾随时间累积，见 WarriorShield.act）
		//碎片不提供护盾（子类覆写为 0）
		return armLvl + armTier * 2;
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

			//SPSEXPD: 取消「诅咒护甲需配诅咒刻印」的限制——纹章可贴附到任意护甲，携带任意刻印
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
				//SPSXPD: 开局初始化（Dungeon.init -> initWarrior）时 hero.sprite 尚未创建，不能直接调用
				if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.operate(Dungeon.hero.pos);
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
	//SPSEXPD: 拆卸下来的纹章可被升级卷轴/铁匠继续升级，直到携带上限；
	//贴在护甲上时仍由护甲升级同步记录（见 Armor.upgrade）
	public boolean isUpgradable() {
		return level() < maxCarriedLevel();
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
			//SPSEXPD: 护盾改为随时间累积（上限 = 护甲等级 + 阶位×2）；极度饥饿时不累积。
			//同时取消原「触发后冷却」与「附近无敌人 5 回合则清空」的规则：护盾被消耗后会自动补回来。
			Hunger hunger = target.buff(Hunger.class);
			boolean starving = hunger != null && hunger.isStarving();

			int cap = maxShield();
			if (cap > 0 && !starving && shielding() < cap) {
				incShield(1);
			}

			if (shielding() <= 0 && cap <= 0){
				detach();
			}

			spend(TICK);
			return true;
		}

		public synchronized void activate() {
			//SPSEXPD: 累积机制下不再需要冷却，直接把护盾补到当前上限
			incShield(maxShield());
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
			//SPSXPD: 原「钢铁意志」职业特判已并入 maxShield()，此处不再区分职业
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
