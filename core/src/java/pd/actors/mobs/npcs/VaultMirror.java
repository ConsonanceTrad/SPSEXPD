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

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.BrokenSeal;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.melee.Greatsword;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.melee.Spellblade;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.VaultMirrorSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import pd.windows.WndTitledMessage;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class VaultMirror extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultMirror.class)
			.t("name", "奇怪的黑镜")
			.t("def_verb", "格挡")
			.t("desc", "这面巨大的黑镜刻有各式各样闪闪发光的矮人符文，它轻微地的鸣响着，却不显出任何倒影。")
			.t("approach", "一面巨大的镜子伫立在你的面前，其厚重的石质边框上刻着闪闪发光的矮人符文。远看时镜面一片漆黑，但你俯身靠过去时符文的光芒变得更加明亮，熟悉的景象也从镜面上浮现。")
			.t("scene_warrior", "你看见了你那红色的_蜡质纹章_，其依旧对半裂开，却已遭弃置、半掩雪中。这幅景象与你的记忆相去无几，但在细微之处仍有差异。你绝不会容许自己丢掉那枚纹章，只因留下它也是你对自己的惩罚的一部分。你总是长长审视着它，这也使你马上注意到镜中纹章上裂痕的角度与你的纹章略有不同。")
			.t("scene_mage", "你看见了你的_魔杖_静静的待在一间书房的桌子上。这幅景象与你的记忆相去无几，但在细微之处仍有差异。这正是你在南方魔法学院的公共办公室；但要是说存放那根魔杖的话，放在这里也未免有些太显眼了。办公室看起来也比往常多了不少烟火气，本来你是从来不在那个地方干“正事”的。")
			.t("scene_rogue", "你看见了_暗影斗篷_，静静地挂在皇家军械库里的架子上。这幅景象与你的记忆相去无几，但在细微之处仍有差异。军械库里一片寂静，轮值的守卫以他和善的双目沉稳地扫视着整个房间。他仍如往常那样平和地静伫此处，未曾伤残也无被害之忧；你那熟悉的罪恶感同这安稳的景象一并浮现，又一次刺痛了你。")
			.t("scene_huntress", "你看见了你的_灵能弓_被置于密林中一块盘状巨石之上。这幅景象与你的记忆相去无几，但在细微之处仍有差异。巨石周围的林间空地静谧非常、空无一物，远远不像你当初被授予这把灵弓时身边竞相祝贺的热闹欢腾。实际上，你根本就感知不到周围自然环境中的任何事物。")
			.t("scene_duelist", "你看见了一个高大的身影，身披皇家近卫的甲胄伫立在你面前：那正是你过世的父亲。这幅景象与你的记忆相去无几，但在细微之处仍有差异。父亲眼中的慈爱与你儿时曾记丝毫未差，但他的脸上却多出了他的生命未曾得享的漫长岁月带来的种种沧桑。他向你递出了他的_附魔巨剑_，等待你将其接过。")
			.t("scene_cleric", "你看见了明烛照映的圣堂中有一个被打开的圣物匣，其中装着的正是_神圣法典_。这幅景象与你的记忆相去无几，但在细微之处仍有差异。你从未将圣物匣从圣器室取出过，只是单独带走了这本圣典。圣典中辐射而出的神圣能量也不同寻常，仿佛圣典所导引出的能量来自于某个不同以往的根源。")
			.t("scene_spellsword", "你看见自己的_魔剑_横放在一本摊开的法术书上，四周是一座空无一人的训练场。这幅景象与你的记忆相去无几，但在细微之处仍有差异。剑身上的符文与书中的咒文以完全一致的节奏闪烁，仿佛钢铁与魔法从来就不是两门分离的技艺。")
			.t("scene_final", "你注意到黑镜的玻璃镜面已经无影无踪，再也没有任何障碍可以阻止你伸手取走镜中之物。")
			.t("take", "取走物品")
			.t("scene_take", "你将手探入，取走了其中的物品，但镜中的景象一时却并未变化，仿佛你手中的事物同时也存在于镜中一般。\n\n镜中倒影渐渐消逝，你手中的物品开始随镜框上符文闪烁的节奏一齐轻轻震颤。\n\n你不确定刚刚究竟发生了什么，但你所取出的物品的存在似乎依附于这面黑镜，因此你大概无法将它从宝库中带出。")
			.t("scene_nothing", "你再次触碰了黑镜，然而无事发生。镜面光滑坚硬，模糊映出的只有你指尖的倒影。");
	}


	{
		spriteClass = VaultMirrorSprite.class;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.OBJECT);
	}

	@Override
	protected void throwItems() {
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null) {
			Dungeon.level.drop( heap.pickUp(), pos+Dungeon.level.width() ).sprite.drop( pos );
		}
	}

	public Item reward = null;

	public void createReward(HeroClass cls){
		//we create a new generator here as some heroes call RNG here and some don't
		Random.pushGenerator(Random.Long());
			switch (cls) {
				case WARRIOR:
					reward = new BrokenSeal().upgrade().identify(false);
					((BrokenSeal)reward).setGlyph(Armor.Glyph.random());
					break;
				case MAGE:
					reward = new MagesStaff().upgrade(3).identify(false);
					((MagesStaff)reward).enchant();
					break;
				case ROGUE:
					reward = new CloakOfShadows().upgrade(8).identify(false);
					((CloakOfShadows) reward).directCharge(8);
					break;
				case HUNTRESS:
					reward = new SpiritBow().identify(false);
					((SpiritBow)reward).enchant();
					break;
				case DUELIST:
					reward = new MirrorSword().upgrade(3).identify(false);
					((MeleeWeapon)reward).enchant();
					break;
				case CLERIC:
					reward = new HolyTome().upgrade(8).identify(false);
					((HolyTome) reward).directCharge(8);
					break;
				case SPELLSWORD:
					reward = new Spellblade().upgrade(3).identify(false);
					((MeleeWeapon)reward).enchant();
					break;
			}
		Random.popGenerator();
	}

	@Override
	public boolean interact(Char c) {
		if (c instanceof Hero) {
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (reward != null) {

						String sceneText = Messages.get(VaultMirror.class, "approach") + "\n\n";
						switch (((Hero) c).heroClass){
							case WARRIOR:
								sceneText += Messages.get(VaultMirror.class, "scene_warrior");
								break;
							case MAGE:
								sceneText += Messages.get(VaultMirror.class, "scene_mage");
								break;
							case ROGUE:
								sceneText += Messages.get(VaultMirror.class, "scene_rogue");
								break;
							case HUNTRESS:
								sceneText += Messages.get(VaultMirror.class, "scene_huntress");
								break;
							case DUELIST:
								sceneText += Messages.get(VaultMirror.class, "scene_duelist");
								break;
							case CLERIC:
								sceneText += Messages.get(VaultMirror.class, "scene_cleric");
								break;
							case SPELLSWORD:
								sceneText += Messages.get(VaultMirror.class, "scene_spellsword");
								break;
						}
						sceneText += "\n\n" + Messages.get(VaultMirror.class, "scene_final");

						GameScene.show(new WndOptions(sprite(),
								Messages.titleCase(name()),
								sceneText,
								Messages.get(VaultMirror.class, "take")) {
							@Override
							protected void onSelect(int index) {
								super.onSelect(index);
								if (index == 0) {
									GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), Messages.get(VaultMirror.class, "scene_take")));
									if (reward.doPickUp((Hero) c)) {
										GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
									} else {
										Dungeon.level.drop(reward, c.pos).sprite.drop();
									}
									Imp.Quest.mirrorUsed = true;
									reward = null;
								}
							}
						});
					} else {
						GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), Messages.get(VaultMirror.class, "scene_nothing")));
					}
				}
			});
		}
		return false;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	private static final String REWARD = "reward";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (reward != null) {
			bundle.put(REWARD, reward);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(REWARD)){
			reward = (Item) bundle.get(REWARD);
		}
	}

	public static class MirrorSword extends Greatsword {

		{
			//cannot be taken out of the vault
			unique = true;
		}

	}

}
