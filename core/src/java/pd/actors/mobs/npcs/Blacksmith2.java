package pd.actors.mobs.npcs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.AdamantArmor;
import pd.items.AdamantRing;
import pd.items.AdamantWand;
import pd.items.AdamantWeapon;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.quest.DarkGold;
import pd.items.equipment.rings.Ring;
import pd.items.specific.sellitem.BrokenHammer;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.ranges.RangeWeapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ElectricwelderSprite;
import pd.utils.GLog;
import pd.windows.WndBlacksmith2;
import pd.windows.WndQuest;
import render.noosa.Game;
import render.utils.data.Callback;
import pd.messages.InlineText;

/** SPS troll welder, who combines equipment with matching adamant components. */
public class Blacksmith2 extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Blacksmith2.class)
			.t("name", "巨魔焊工")
			.t("desc", "这个巨魔焊工又高又瘦，皮肤的色泽和纹理都像石头。他正拿着一把与体形极不相称的电焊枪焊接着什么。")
			.t("himself", "和你看到的一样，我是那个顽固铁匠的弟弟。我认为科学技术也能帮巨魔谋生。带来对应的_精金组件_和_50枚暗金_，我就能给你一个惊喜。")
			.t("adamantite", "这种精金组件能让物品承受任意次数的强化。给我_50枚暗金_，我就能把它们焊接起来。")
			.t("same_item", "同一件东西可没法焊接！")
			.t("un_ided", "未知的东西我可没法处理！")
			.t("cursed", "诅咒的东西我可没法处理！")
			.t("already_reforge", "这件物品已经焊接过组件了！")
			.t("degraded", "负等级的东西我可没法处理！")
			.t("cant_reforge", "没法升级的东西我可没法处理！")
			.t("cant_work", "组件和物品不匹配，我可没法处理！")
			.t("looks_better", "你的%s现在可以承受任意次数的强化了。");
	}


	{
		spriteClass = ElectricwelderSprite.class;
		properties.add(Property.TROLL);
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Item SupercreateLoot() {
		return new BrokenHammer();
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo(pos, c.pos);
		if (c != Dungeon.hero) return true;

		DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
		String message;
		if (!hasAdamant()) {
			message = Messages.get(this, "himself");
		} else if (gold == null || gold.quantity() < 50) {
			message = Messages.get(this, "adamantite");
		} else {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndBlacksmith2(Blacksmith2.this, Dungeon.hero));
				}
			});
			return true;
		}

		final String text = message;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(new WndQuest(Blacksmith2.this, text));
			}
		});
		return true;
	}

	public static String verify(Item equipment, Item adamant) {
		if (equipment == adamant) return Messages.get(Blacksmith2.class, "same_item");
		if (!equipment.isIdentified()) return Messages.get(Blacksmith2.class, "un_ided");
		if (equipment.cursed) return Messages.get(Blacksmith2.class, "cursed");
		if (equipment.isReinforced()) return Messages.get(Blacksmith2.class, "already_reforge");
		if (equipment.level() < 0) return Messages.get(Blacksmith2.class, "degraded");
		if (!equipment.isUpgradable()) return Messages.get(Blacksmith2.class, "cant_reforge");

		if (equipment instanceof Armor && adamant instanceof AdamantArmor) return null;
		if ((equipment instanceof MeleeWeapon || equipment instanceof GunWeapon
				|| equipment instanceof RangeWeapon) && adamant instanceof AdamantWeapon) return null;
		if (equipment instanceof Wand && adamant instanceof AdamantWand) return null;
		if (equipment instanceof Ring && adamant instanceof AdamantRing) return null;
		return Messages.get(Blacksmith2.class, "cant_work");
	}

	public static boolean upgrade(Item equipment, Item adamant) {
		if (Dungeon.hero == null || verify(equipment, adamant) != null) return false;
		DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
		if (gold == null || gold.quantity() < 50) return false;

		equipment.reinforce();
		adamant.detach(Dungeon.hero.belongings.backpack);
		if (gold.quantity() == 50) {
			gold.detachAll(Dungeon.hero.belongings.backpack);
		} else {
			gold.quantity(gold.quantity() - 50);
			Item.updateQuickslot();
		}
		GLog.p(Messages.get(Blacksmith2.class, "looks_better", equipment.name()));
		Dungeon.hero.spendAndNext(2f);
		Badges.validateItemLevelAquired(equipment);
		return true;
	}

	public static boolean hasAdamant() {
		if (Dungeon.hero == null) return false;
		return Dungeon.hero.belongings.getItem(AdamantArmor.class) != null
				|| Dungeon.hero.belongings.getItem(AdamantWeapon.class) != null
				|| Dungeon.hero.belongings.getItem(AdamantRing.class) != null
				|| Dungeon.hero.belongings.getItem(AdamantWand.class) != null;
	}

	@Override public int defenseSkill(Char enemy) { return INFINITE_EVASION; }
	@Override public void damage(int dmg, Object src) { }
	@Override public boolean add(Buff buff) { return false; }
	@Override public boolean reset() { return true; }
}
