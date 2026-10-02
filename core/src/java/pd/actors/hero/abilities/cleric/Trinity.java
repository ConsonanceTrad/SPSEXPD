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

package pd.actors.hero.abilities.cleric;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MagicImmune;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.spells.BodyForm;
import pd.actors.hero.spells.ClericSpell;
import pd.actors.hero.spells.MindForm;
import pd.actors.hero.spells.SpiritForm;
import pd.effects.Enchanting;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.ClassArmor;
import pd.items.equipment.armor.ClothArmor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.artifacts.ChaliceOfBlood;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.equipment.artifacts.EtherealChains;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.artifacts.SkeletonKey;
import pd.items.equipment.artifacts.TalismanOfForesight;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.equipment.artifacts.UnstableSpellbook;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.wands.WandOfFireblast;
import pd.items.equipment.wands.WandOfRegrowth;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.enchantments.Crystal;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.melee.WornShortsword;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.ui.HeroIcon;
import pd.ui.ItemButton;
import pd.ui.QuickSlotButton;
import pd.ui.RedButton;
import pd.ui.Window;
import pd.utils.GLog;
import pd.windows.WndTitledMessage;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Trinity extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Trinity.class)
			.t("name", "三位一体")
			.t("no_imbue", "三位一体当前尚未获得任何位格的效果集，使用位格法术以使其获得效果集！")
			.t("no_duplicate", "三位一体无法复制你已经装备的装备效果！")
			.t("ench_glyph_use", "三位一体会消耗_%2$s充能_以获得该附魔或刻印的效果_%1$d回合_。")
			.t("rare_ench_glyph_use", "三位一体会消耗_%2$s充能_以获得该_强力_附魔或刻印的效果_%1$d回合_。")
			.t("wand_use", "三位一体会消耗_%2$s充能_以使用该法杖的_%1$d级_效果。")
			.t("wand_multi_use", "三位一体会消耗_%2$s充能_以使用该_复充能_法杖的_%1$d级_效果。")
			.t("thrown_use", "三位一体会消耗_%2$s充能_以使用该投武的_%1$d级_效果。")
			.t("ring_use", "三位一体会消耗_%2$s充能_以获得20回合_%1$d等级_该戒指的效果。")
			.t("alchemiststoolkit_use", "三位一体会消耗_%2$s充能_以使用该神器的远端炼金效果。")
			.t("chaliceofblood_use", "三位一体会消耗_%2$s充能_以获得该神器的_%1$d级_被动生命回复效果20回合。")
			.t("driedrose_use", "三位一体会消耗_%2$s充能_以使用该神器召唤_%1$d级_幽灵生命值的友好腐化怨灵的效果。")
			.t("etherealchains_use", "三位一体会消耗_%2$s充能_以使用该神器_%1$d格_范围的锁链施放效果。")
			.t("hornofplenty_use", "三位一体会消耗_%2$s充能_以使用该神器的小吃一口效果。")
			.t("masterthievesarmband_use", "三位一体会消耗_%2$s充能_以使用该神器的_%1$d级_敌人窃取效果。")
			.t("sandalsofnature_use", "三位一体会消耗_%2$s充能_以使用该神器的随机有害种子扎根效果。")
			.t("skeletonkey_use", "三位一体会消耗_%2$s充能_以使用该神器的插入效果。")
			.t("talismanofforesight_use", "三位一体会消耗_%2$s充能_以使用该神器的_%1$d级_探查效果。")
			.t("timekeepershourglass_use", "三位一体会消耗_%2$s充能_以获得该神器持续_%1$d_回合的时间冻结效果。")
			.t("unstablespellbook_use", "三位一体会消耗_%2$s充能_以使用该神器_%1$d/10_无额外消耗使用秘卷概率的随机卷轴效果。")
			.t("cost", "该护甲技能充能消耗不定，但通常为_%d_。")
			.t("short_desc", "牧师获得_三位一体_的技能，可模拟其已鉴定的装备并通过使用全新法术进行装备分配。")
			.t("desc", "牧师获得一套_三位一体_的护甲技能，通过使用三种全新法术选择并使用各式各样的物品效果。每种位格法术都专用于模拟牧师本局已鉴定的不同种类装备效果：体之位格(武器与护甲)、智之位格(法杖与投武)、魂之位格(戒指与神器)。\n\n每种位格法术同时只能模拟一种效果，而牧师使用三位一体时可以选择所使用的位格法术的种类。三位一体无法复制你已装备的装备效果。")
			.t("wndusetrinity.text", "选择三位一体所使用的位格法术。不同位格的效果可同时生效。")
			.t("wndusetrinity.body", "_体之位格：%s_")
			.t("wndusetrinity.mind", "_智之位格：%s_")
			.t("wndusetrinity.spirit", "_魂之位格：%s_")
			.t("wnditemtypeselect.text", "选择三位一体所模拟的物品效果。附加信息将在确认之前显示。")
			.t("wnditemconfirm.body", "选择体之位格效果")
			.t("wnditemconfirm.mind", "选择智之位格效果")
			.t("wnditemconfirm.spirit", "选择魂之位格效果");
	}


	{
		baseChargeUse = 25;
	}

	private Bundlable bodyForm = null;
	private Bundlable mindForm = null;
	private Bundlable spiritForm = null;

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (bodyForm == null && mindForm == null && spiritForm == null){
			GLog.w(Messages.get(this, "no_imbue"));
		} else {
			GameScene.show(new WndUseTrinity(armor));
		}

	}

	@Override
	public int targetedPos(Char user, int dst) {
		if (mindForm != null){
			return ((Item)mindForm).targetingPos((Hero)user, dst);
		}
		return super.targetedPos(user, dst);
	}

	public class WndUseTrinity extends WndTitledMessage {

		public WndUseTrinity(ClassArmor armor) {
			super(new HeroIcon(Trinity.this),
					Messages.titleCase(Trinity.this.name()),
					Messages.get(WndUseTrinity.class, "text"));

			int top = height;

			if (bodyForm != null){
				RedButton btnBody = null;
				if (bodyForm instanceof Weapon.Enchantment){

					btnBody = new RedButton(Messages.get(WndUseTrinity.class, "body",
							Messages.titleCase(((Weapon.Enchantment)bodyForm).name()))
							+ " " + trinityItemUseText(bodyForm.getClass()), 6){
						@Override
						protected void onClick() {
							if (Dungeon.hero.belongings.weapon() != null &&
									((Weapon)Dungeon.hero.belongings.weapon()).enchantment != null &&
									((Weapon)Dungeon.hero.belongings.weapon()).enchantment.getClass().equals(bodyForm.getClass())){
								GLog.w(Messages.get(Trinity.class, "no_duplicate"));
								hide();
							} else {
								Buff.prolong(Dungeon.hero, BodyForm.BodyFormBuff.class, BodyForm.duration()).setEffect(bodyForm);

								//Crystal is set to 30-60% durability (~10-20 melee weapon uses) based on talent tier
								if (bodyForm instanceof Crystal){
									((Crystal) bodyForm).setDurability(BodyForm.duration()*1.5f);
								}

								Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
								Weapon w = new WornShortsword();
								if (Dungeon.hero.belongings.weapon() != null) {
									w.image = Dungeon.hero.belongings.weapon().image;
								}
								w.enchant((Weapon.Enchantment) bodyForm);
								Enchanting.show(Dungeon.hero, w);
								Dungeon.hero.sprite.operate(Dungeon.hero.pos);
								Dungeon.hero.spendAndNext(1f);
								armor.charge -= trinityChargeUsePerEffect(bodyForm.getClass());
								armor.updateQuickslot();
								Invisibility.dispel();
								hide();
							}
						}
					};
					if (Dungeon.hero.belongings.weapon() != null) {
						btnBody.icon(new ItemSprite(Dungeon.hero.belongings.weapon().image, ((Weapon.Enchantment) bodyForm).glowing()));
					} else {
						btnBody.icon(new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.WORN_SHORTSWORD_0, ((Weapon.Enchantment) bodyForm).glowing()));
					}
				} else if (bodyForm instanceof Armor.Glyph){
					btnBody = new RedButton(Messages.get(WndUseTrinity.class, "body",
							Messages.titleCase(((Armor.Glyph)bodyForm).name()))
							+ " " + trinityItemUseText(bodyForm.getClass()), 6){
						@Override
						protected void onClick() {
							if (Dungeon.hero.belongings.armor() != null &&
									Dungeon.hero.belongings.armor().glyph != null &&
									(Dungeon.hero.belongings.armor()).glyph.getClass().equals(bodyForm.getClass())){
								GLog.w(Messages.get(Trinity.class, "no_duplicate"));
								hide();
							} else {
								Buff.prolong(Dungeon.hero, BodyForm.BodyFormBuff.class, BodyForm.duration()).setEffect(bodyForm);
								Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
								Armor a = new ClothArmor();
								if (Dungeon.hero.belongings.armor() != null) {
									a.image = Dungeon.hero.belongings.armor().image;
								}
								a.inscribe((Armor.Glyph) bodyForm);
								Enchanting.show(Dungeon.hero, a);
								Dungeon.hero.sprite.operate(Dungeon.hero.pos);
								Dungeon.hero.spendAndNext(1f);
								armor.charge -= trinityChargeUsePerEffect(bodyForm.getClass());
								armor.updateQuickslot();
								Invisibility.dispel();
								hide();
							}
						}
					};
					if (Dungeon.hero.belongings.armor() != null) {
						btnBody.icon(new ItemSprite(Dungeon.hero.belongings.armor().image, ((Armor.Glyph) bodyForm).glowing()));
					} else {
						btnBody.icon(new ItemSprite(EquipmentEquipArmorBasicArmorDict.ARMOR_CLOTH_0, ((Armor.Glyph) bodyForm).glowing()));
					}
				}
				btnBody.multiline = true;
				btnBody.setSize(width, 100); //for text layout
				btnBody.setRect(0, top + 2, width, btnBody.reqHeight());
				add(btnBody);
				top = (int)btnBody.bottom();

				btnBody.enable(Dungeon.hero.buff(MagicImmune.class) == null && armor.charge >= trinityChargeUsePerEffect(bodyForm.getClass()));
			}

			if (mindForm != null){
				RedButton btnMind = new RedButton(Messages.get(WndUseTrinity.class, "mind",
						Messages.titleCase(((Item)mindForm).name()))
						+ " " + trinityItemUseText(mindForm.getClass()), 6){
					@Override
					protected void onClick() {
						hide();
						MindForm.targetSelector mindEffect = new MindForm.targetSelector();
						mindEffect.setEffect(mindForm);
						GameScene.selectCell(mindEffect);
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						Enchanting.show(Dungeon.hero, (Item)mindForm);
						Dungeon.hero.sprite.operate(Dungeon.hero.pos);

						if (((Item) mindForm).usesTargeting && Dungeon.quickslot.contains(armor)){
							QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
						}
					}
				};
				btnMind.icon(new ItemSprite((Item)mindForm));
				btnMind.multiline = true;
				btnMind.setSize(width, 100); //for text layout
				btnMind.setRect(0, top + 2, width, btnMind.reqHeight());
				add(btnMind);
				top = (int)btnMind.bottom();

				btnMind.enable(armor.charge >= trinityChargeUsePerEffect(mindForm.getClass()));
				if (mindForm instanceof Wand && Dungeon.hero.buff(MagicImmune.class) != null){
					btnMind.enable(false);
				}
			}

			if (spiritForm != null){
				RedButton btnSpirit = new RedButton(Messages.get(WndUseTrinity.class, "spirit",
						Messages.titleCase(((Item)spiritForm).name()))
						+ " " + trinityItemUseText(spiritForm.getClass()), 6){
					@Override
					protected void onClick() {
						if ((Dungeon.hero.belongings.ring() != null && Dungeon.hero.belongings.ring().getClass().equals(spiritForm.getClass()))
								|| (Dungeon.hero.belongings.misc() != null && Dungeon.hero.belongings.misc().getClass().equals(spiritForm.getClass()))
								|| (Dungeon.hero.belongings.artifact() != null && Dungeon.hero.belongings.artifact().getClass().equals(spiritForm.getClass()))){
							GLog.w(Messages.get(Trinity.class, "no_duplicate"));
							hide();
							return;
						}
						Invisibility.dispel();
						//Rings and the Chalice specifically get their passive effects for 20 turns
						if (spiritForm instanceof Ring || spiritForm instanceof ChaliceOfBlood) {
							Buff.prolong(Dungeon.hero, SpiritForm.SpiritFormBuff.class, SpiritForm.SpiritFormBuff.DURATION).setEffect(spiritForm);
							Dungeon.hero.spendAndNext(1f);
						} else {
							SpiritForm.applyActiveArtifactEffect(armor, (Artifact) spiritForm);
							//turn spending is handled within the application of the artifact effect
						}
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						Enchanting.show(Dungeon.hero, (Item) spiritForm);
						Dungeon.hero.sprite.operate(Dungeon.hero.pos);
						armor.charge -= trinityChargeUsePerEffect(spiritForm.getClass());
						armor.updateQuickslot();
						hide();
					}
				};
				if (spiritForm instanceof Artifact){
					((Artifact) spiritForm).resetForTrinity(SpiritForm.artifactLevel());
				}

				btnSpirit.icon(new ItemSprite((Item)spiritForm));
				btnSpirit.multiline = true;
				btnSpirit.setSize(width, 100); //for text layout
				btnSpirit.setRect(0, top + 2, width, btnSpirit.reqHeight());
				add(btnSpirit);
				top = (int)btnSpirit.bottom();

				btnSpirit.enable(Dungeon.hero.buff(MagicImmune.class) == null && armor.charge >= trinityChargeUsePerEffect(spiritForm.getClass()));
			}

			resize(width, top);

		}

	}

	private static final String BODY = "body_form";
	private static final String MIND = "mind_form";
	private static final String SPIRIT = "spirit_form";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (bodyForm != null)   bundle.put(BODY, bodyForm);
		if (mindForm != null)   bundle.put(MIND, mindForm);
		if (spiritForm != null) bundle.put(SPIRIT, spiritForm);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(BODY))  bodyForm = bundle.get(BODY);
		if (bundle.contains(MIND))  mindForm = bundle.get(MIND);
		if (bundle.contains(SPIRIT))spiritForm = bundle.get(SPIRIT);
	}

	@Override
	public int icon() {
		return HeroIcon.TRINITY;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.BODY_FORM, Talent.MIND_FORM, Talent.SPIRIT_FORM, Talent.HEROIC_ENERGY};
	}

	public static class WndItemtypeSelect extends WndTitledMessage {

		//probably want a callback here?
		public WndItemtypeSelect(HolyTome tome, ClericSpell spell) {
			super(new HeroIcon(spell), Messages.titleCase(spell.name()), Messages.get(WndItemtypeSelect.class, "text"));

			//start by filtering and sorting
			ArrayList<Class<?>> discoveredClasses = new ArrayList<>();
			if (spell == BodyForm.INSTANCE) {
				for (Class<?> cls : Catalog.ENCHANTMENTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.GLYPHS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
			} else if (spell == MindForm.INSTANCE){
				for (Class<?> cls : Catalog.WANDS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.THROWN_WEAPONS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.TIPPED_DARTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
			} else if (spell == SpiritForm.INSTANCE){
				for (Class<?> cls : Catalog.RINGS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.ARTIFACTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
					//no tome specifically
					discoveredClasses.remove(HolyTome.class);
				}
			}

			ArrayList<Item> options = new ArrayList<>();
			for (Class<?> cls : discoveredClasses){
				if (Weapon.Enchantment.class.isAssignableFrom(cls)){
					MeleeWeapon w = new WornShortsword(){
						@Override
						public String name() {
							//for button tooltips
							return enchantment.name();
						}
					};
					if (Dungeon.hero.belongings.weapon() != null){
						w.image = Dungeon.hero.belongings.weapon().image;
					}
					w.enchant((Weapon.Enchantment) Reflection.newInstance(cls));
					w.cursedKnown = true;
					options.add(w);
				} else if (Armor.Glyph.class.isAssignableFrom(cls)) {
					Armor a = new ClothArmor(){
						@Override
						public String name() {
							//for button tooltips
							return glyph.name();
						}
					};
					if (Dungeon.hero.belongings.armor() != null){
						a.image = Dungeon.hero.belongings.armor().image;
					}
					a.inscribe((Armor.Glyph) Reflection.newInstance(cls));
					a.cursedKnown = true;
					options.add(a);
				} else {
					options.add((Item) Reflection.newInstance(cls));
				}
			}

			int top = height + 2;
			int left = 0;

			for (Item item : options){
				ItemButton btn = new ItemButton(){
					@Override
					protected void onClick() {
						GameScene.show(new WndItemConfirm(WndItemtypeSelect.this, item, tome, spell));
					}
				};
				btn.item(item);
				btn.slot().textVisible(false);
				btn.setRect(left, top, 19, 19);
				add(btn);

				left += 20;
				if (left >= width - 19){
					top += 20;
					left = 0;
				}
			}

			if (left > 0){
				top += 20;
				left = 0;
			}

			resize(width, top);

		}

	}

	public static class WndItemConfirm extends WndTitledMessage {

		public WndItemConfirm(Window parentWnd, Item item, HolyTome tome, ClericSpell spell){
			super(new ItemSprite(item),  Messages.titleCase(getName(item)), getText(item));

			String text;
			if (spell == BodyForm.INSTANCE){
				text = Messages.get(this, "body");
			} else if (spell == MindForm.INSTANCE){
				text = Messages.get(this, "mind");
			} else {
				text = Messages.get(this, "spirit");
			}

			RedButton btnConfirm = new RedButton(text){
				@Override
				protected void onClick() {
					parentWnd.hide();
					WndItemConfirm.this.hide();

					if (item instanceof MeleeWeapon) {
						((Trinity)Dungeon.hero.armorAbility).bodyForm = ((MeleeWeapon) item).enchantment;
					} else if (item instanceof Armor) {
						((Trinity)Dungeon.hero.armorAbility).bodyForm = ((Armor) item).glyph;
					} else if (item instanceof Wand || item instanceof MissileWeapon){
						((Trinity)Dungeon.hero.armorAbility).mindForm = item;
					} else {
						((Trinity)Dungeon.hero.armorAbility).spiritForm = item;
					}
					spell.onSpellCast(tome, Dungeon.hero);

					Dungeon.hero.sprite.operate(Dungeon.hero.pos);
					Enchanting.show(Dungeon.hero, item);
					Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				}
			};
			btnConfirm.setRect(0, height+2, width, 16);
			add(btnConfirm);

			resize(width, (int)btnConfirm.bottom());

		}

		private static String getName(Item item){
			if (item instanceof MeleeWeapon){
				return ((MeleeWeapon) item).enchantment.name();
			} else if (item instanceof Armor){
				return (((Armor) item).glyph.name());
			}
			return item.name();
		}

		private static String getText(Item item){
			if (item instanceof MeleeWeapon){
				return ((MeleeWeapon) item).enchantment.desc() + "\n\n" + trinityItemUseText(((MeleeWeapon) item).enchantment.getClass());
			} else if (item instanceof Armor){
				return ((Armor) item).glyph.desc() + "\n\n" + trinityItemUseText(((Armor) item).glyph.getClass());
			} else {
				return item.desc() + "\n\n" + trinityItemUseText(item.getClass());
			}
		}

	}

	public static String trinityItemUseText(Class<?> cls ){
		float chargeUse = trinityChargeUsePerEffect(cls);
		if (Weapon.Enchantment.class.isAssignableFrom(cls) || Armor.Glyph.class.isAssignableFrom(cls)) {
			for (Class ench : Weapon.Enchantment.rare) {
				if (ench.equals(cls)) {
					return Messages.get(Trinity.class, "rare_ench_glyph_use", BodyForm.duration(), Messages.decimalFormat("#.##", chargeUse));
				}
			}
			for (Class glyph : Armor.Glyph.rare){
				if (glyph.equals(cls)){
					return Messages.get(Trinity.class, "rare_ench_glyph_use", BodyForm.duration(), Messages.decimalFormat("#.##", chargeUse));
				}
			}
			return Messages.get(Trinity.class, "ench_glyph_use", BodyForm.duration(), Messages.decimalFormat("#.##", chargeUse));
		}
		if (MissileWeapon.class.isAssignableFrom(cls)){
			return Messages.get(Trinity.class, "thrown_use", MindForm.itemLevel(), Messages.decimalFormat("#.##", chargeUse));
		}
		if (Wand.class.isAssignableFrom(cls)){
			if (cls.equals(WandOfFireblast.class) || cls.equals(WandOfRegrowth.class)){
				return Messages.get(Trinity.class, "wand_multi_use", MindForm.itemLevel(), Messages.decimalFormat("#.##", chargeUse));
			}
			return Messages.get(Trinity.class, "wand_use", MindForm.itemLevel(), Messages.decimalFormat("#.##", chargeUse));
		}
		if (Ring.class.isAssignableFrom(cls)){
			return Messages.get(Trinity.class, "ring_use", SpiritForm.ringLevel(), Messages.decimalFormat("#.##", chargeUse));
		}
		if (Artifact.class.isAssignableFrom(cls)){
			//消息键 = <神器类简单名>_use：神器改名需同步 4 份 messages 里的该键
			return Messages.get(Trinity.class, cls.getSimpleName() + "_use", SpiritForm.artifactLevel(), Messages.decimalFormat("#.##", chargeUse));
		}
		return "error!";

	}

	public static float trinityChargeUsePerEffect(Class<?> cls){
		float chargeUse = Dungeon.hero.armorAbility.chargeUse(Dungeon.hero);
		if (Weapon.Enchantment.class.isAssignableFrom(cls) || Armor.Glyph.class.isAssignableFrom(cls)) {
			for (Class ench : Weapon.Enchantment.rare) {
				if (ench.equals(cls)) {
					return 2*chargeUse; //50 charge
				}
			}
			for (Class glyph : Armor.Glyph.rare){
				if (glyph.equals(cls)){
					return 2*chargeUse; //50 charge
				}
			}
		}
		if (cls.equals(WandOfFireblast.class) || cls.equals(WandOfRegrowth.class)){
			return 2*chargeUse;
		}
		if (Artifact.class.isAssignableFrom(cls)){
			if (cls.equals(DriedRose.class) || cls.equals(UnstableSpellbook.class) || cls.equals(SkeletonKey.class)){
				return 2*chargeUse; //50 charge
			}
			if (cls.equals(EtherealChains.class) || cls.equals(TalismanOfForesight.class) || cls.equals(TimekeepersHourglass.class)){
				return 1.4f*chargeUse; //35 charge
			}
		}
		//all other effects are standard charge use, 25 at base
		return chargeUse;
	}

}
