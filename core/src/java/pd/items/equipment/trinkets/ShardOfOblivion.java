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

package pd.items.equipment.trinkets;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Identification;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.Image;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ShardOfOblivion extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShardOfOblivion.class)
			.t("name", "遗忘碎片")
			.t("desc", "经过炼金釜的烹煮，这一小块邪能碎片已经化为了...一片虚空？光线似乎在碎片边缘发生了弯曲，而只要你不握住它，它就会悬停在原地。碎片似乎通过魔法从你的无知中获得力量，所以对此最好不要想太多。")
			.t("typical_stats_desc", "这件饰物通常会使你每装备或使用一件未鉴定装备就提升20%%敌人掉落战利品的概率，效益上限为_%d_件未鉴定装备。碎片还会阻止你自动鉴定装备，但能用于手动鉴定已就绪的物品。")
			.t("stats_desc", "在当前等级下，这件饰物会使你每装备或使用一件未鉴定装备就提升20%%敌人掉落战利品的概率，效益上限为_%d_件未鉴定装备。碎片还会阻止你自动鉴定装备，但能用于手动鉴定已就绪的物品。")
			.t("ac_identify", "鉴定")
			.t("identify_prompt", "鉴定一件物品")
			.t("identify_ready", "一件物品的鉴定已就绪：%s。可使用遗忘碎片将其鉴定。")
			.t("identify_ready_worn", "你所装备的物品的鉴定已就绪。可使用遗忘碎片将其鉴定。")
			.t("identify_not_yet", "这个物品的鉴定尚未就绪。")
			.t("identify", "你鉴定了这个物品！")
			.t("wandusetracker.name", "已使用未鉴定法杖")
			.t("wandusetracker.desc", "你近期已使用一根未鉴定法杖，短期内遗忘碎片将其视为一件已使用的未鉴定装备。\n\n剩余回合数：%s")
			.t("thrownusetracker.name", "已使用未鉴定投武")
			.t("thrownusetracker.desc", "你近期已使用一件未鉴定投掷武器，短期内遗忘碎片将其视为一件已使用的未鉴定装备。\n\n剩余回合数：%s");
	}


	{
		image = EquipmentNonEquipDict.OBLIVION_SHARD_0;
	}

	public static final String AC_IDENTIFY = "IDENTIFY";

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc", buffedLvl()+1);
		} else {
			return Messages.get(this, "stats_desc", 1);
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_IDENTIFY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_IDENTIFY)){
			curUser = hero;
			curItem = this;
			GameScene.selectItem(identifySelector);
		} else {
			super.execute(hero, action);
		}
	}

	public static WndBag.ItemSelector identifySelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(ShardOfOblivion.class, "identify_prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return !item.isIdentified() && item.isUpgradable();
		}

		@Override
		public void onSelect(Item item) {
			if (item == null){
				return;
			}

			boolean ready = false;
			if (item instanceof Weapon){
				ready = ((Weapon) item).readyToIdentify();
				if (item.isEquipped(curUser) && curUser.pointsInTalent(Talent.ADVENTURERS_INTUITION) == 2){
					ready = true;
				}
			} else if (item instanceof Armor){
				ready = ((Armor) item).readyToIdentify();
				if (item.isEquipped(curUser) && curUser.pointsInTalent(Talent.VETERANS_INTUITION) == 2){
					ready = true;
				}
			} else if (item instanceof Ring){
				ready = ((Ring) item).readyToIdentify();
				if (item.isEquipped(curUser) && curUser.pointsInTalent(Talent.THIEFS_INTUITION) == 2){
					ready = true;
				}
			} else if (item instanceof Wand){
				ready = ((Wand) item).readyToIdentify();
			}

			if (ready){
				item.identify();
				Badges.validateItemLevelAquired(item);
				curUser.sprite.operate(curUser.pos);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
				GLog.p(Messages.get(ShardOfOblivion.class, "identify"));
			} else {
				GLog.w(Messages.get(ShardOfOblivion.class, "identify_not_yet"));
			}
		}
	};

	public static boolean passiveIDDisabled(){
		return trinketLevel(ShardOfOblivion.class) >= 0;
	}

	public static class WandUseTracker extends FlavourBuff{

		{
			type = buffType.POSITIVE;
		}

		public static float DURATION = 50f;

		@Override
		public int icon() {
			return BuffIndicator.WAND;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0, 0.6f, 1);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

	public static class ThrownUseTracker extends FlavourBuff{

		{
			type = buffType.POSITIVE;
		}

		public static float DURATION = 50f;

		@Override
		public int icon() {
			return BuffIndicator.THROWN_WEP;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0, 0.6f, 1);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

	public static float lootChanceMultiplier(){
		return lootChanceMultiplier(trinketLevel(ShardOfOblivion.class));
	}

	public static float lootChanceMultiplier(int level){
		if (level < 0) return 1f;

		int wornUnIDed = 0;
		if (Dungeon.hero.belongings.weapon() != null && !Dungeon.hero.belongings.weapon().isIdentified()){
			wornUnIDed++;
		}
		if (Dungeon.hero.belongings.secondWep() != null && !Dungeon.hero.belongings.secondWep().isIdentified()){
			wornUnIDed++;
		}
		if (Dungeon.hero.belongings.armor() != null && !Dungeon.hero.belongings.armor().isIdentified()){
			wornUnIDed++;
		}
		if (Dungeon.hero.belongings.ring() != null && !Dungeon.hero.belongings.ring().isIdentified()){
			wornUnIDed++;
		}
		if (Dungeon.hero.belongings.misc() != null && !Dungeon.hero.belongings.misc().isIdentified()){
			wornUnIDed++;
		}
		if (Dungeon.hero.buff(WandUseTracker.class) != null){
			wornUnIDed++;
		}
		if (Dungeon.hero.buff(ThrownUseTracker.class) != null){
			wornUnIDed++;
		}

		wornUnIDed = Math.min(wornUnIDed, level+1);
		return 1f + .2f*wornUnIDed;

	}
}
