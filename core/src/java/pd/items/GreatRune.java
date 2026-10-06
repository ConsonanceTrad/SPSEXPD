/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package pd.items;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.Armor;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;

import java.util.ArrayList;
import pd.messages.InlineText;

public class GreatRune extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GreatRune.class)
			.t("name", "附魔符文")
			.t("ac_inscribe", "附魔")
			.t("prompt", "选择一件要附魔的装备")
			.t("weapon", "为这件武器选择一个附魔。")
			.t("armor", "为这件护甲选择一个刻印。")
			.t("seal", "为这枚破损纹章选择一个刻印。")
			.t("cancel", "放弃附魔")
			.t("item", "你完成了附魔。")
			.t("desc", "为武器或护甲随机提供三种附魔供你选择。它可以用磨刀石和奥术刻笔锻造而成。");
	}




	public static final String AC_INSCRIBE = "INSCRIBE";

	{
		image = ConsumScrollAmuletAmuletDict.STONE_ENCHANT_0;
		stackable = true;
		defaultAction = AC_INSCRIBE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_INSCRIBE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_INSCRIBE.equals(action)) {
			curUser = hero;
			GameScene.selectItem(SELECTOR);
		}
	}

	private final WndBag.ItemSelector SELECTOR = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(GreatRune.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			//SPSEXPD: 拆卸下来的破损纹章同样可以刻印
			return item instanceof Weapon || item instanceof Armor || item instanceof BrokenSeal;
		}

		@Override
		public void onSelect(Item item) {
			if (item instanceof Weapon) chooseWeapon((Weapon)item);
			else if (item instanceof Armor) chooseArmor((Armor)item);
			else if (item instanceof BrokenSeal) chooseSeal((BrokenSeal)item);
		}
	};

	private void chooseWeapon(final Weapon weapon) {
		final Weapon.Enchantment[] choices = {
				Weapon.Enchantment.random(), Weapon.Enchantment.random(), Weapon.Enchantment.random()
		};
		GameScene.show(new WndOptions(Messages.titleCase(name()),
				Messages.get(this, "weapon"),
				choices[0].name(), choices[1].name(), choices[2].name(), Messages.get(this, "cancel")) {
			@Override
			protected void onSelect(int index) {
				if (index < 3) {
					weapon.enchant(choices[index]);
					complete(weapon);
				}
			}
		});
	}

	private void chooseArmor(final Armor armor) {
		final Armor.Glyph[] choices = {Armor.Glyph.random(), Armor.Glyph.random(), Armor.Glyph.random()};
		GameScene.show(new WndOptions(Messages.titleCase(name()),
				Messages.get(this, "armor"),
				choices[0].name(), choices[1].name(), choices[2].name(), Messages.get(this, "cancel")) {
			@Override
			protected void onSelect(int index) {
				if (index < 3) {
					armor.inscribe(choices[index]);
					complete(armor);
				}
			}
		});
	}

	private void chooseSeal(final BrokenSeal seal) {
		//SPSEXPD: 纹章与护甲一样 3 选 1 刻印
		Class<? extends Armor.Glyph> existing = seal.getGlyph() != null ? seal.getGlyph().getClass() : null;
		final Armor.Glyph[] choices = {
				Armor.Glyph.random( existing ), Armor.Glyph.random( existing ), Armor.Glyph.random( existing )
		};
		GameScene.show(new WndOptions(Messages.titleCase(name()),
				Messages.get(this, "seal"),
				choices[0].name(), choices[1].name(), choices[2].name(), Messages.get(this, "cancel")) {
			@Override
			protected void onSelect(int index) {
				if (index < 3) {
					seal.inscribe(choices[index]);
					complete(seal);
				}
			}
		});
	}

	private void complete(Item item) {
		if (detach(Dungeon.hero.belongings.backpack) == null) return;
		ScrollOfRemoveCurse.uncurse(Dungeon.hero, item);
		item.identify();
		Dungeon.hero.spend(2f);
		GLog.p(Messages.get(this, "item"));
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 30 * quantity;
	}
}
