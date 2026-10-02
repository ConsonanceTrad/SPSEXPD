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

import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.abilities.Ratmogrify;
import pd.items.KingsCrown;
import pd.items.specific.reward.SewerReward;
import pd.items.equipment.weapon.melee.Spork;
import pd.journal.Notes;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.RatKingSprite;
import pd.utils.Holiday;
import pd.windows.WndInfoArmorAbility;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.utils.data.Callback;
import pd.messages.InlineText;

public class RatKing extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RatKing.class)
			.t("name", "鼠王")
			.t("not_sleeping", "我可不是在睡觉！")
			.t("what_is_it", "你这是想干什么？我可没时间管这些破事。我的王国可不会自己运转下去！")
			.t("confused", "这...我这是在哪？我的王国需要我！")
			.t("crown_clothes", "把衣服穿上！会见皇室的礼仪都不懂吗！")
			.t("crown_desc", "哇，那个皇冠是要给本王的吗！？它看上去比我的皇冠更闪更亮，那我就好心好意地接受了！\n\n当然，我堂堂鼠王也不会白拿的。作为交换，本王能赐你一项配得上“英雄”之称的强大能力！怎么样？要不要？")
			.t("crown_yes", "当然了！")
			.t("crown_info", "我愿闻其详。")
			.t("crown_no", "还是算了...")
			.t("crown_thankyou", "嘿嘿嘿嘿，多谢了！不要让本王失望了，去宣扬我的威名吧！")
			.t("crown_fine", "行吧！反正我也不怎么想要那个闪闪发光的皇冠...")
			.t("crown_after", "新衣服穿着合身吗？诚信交易，概不退换！")
			.t("desc_crown", "这只老鼠比普通的啮齿小鼠大一点。它戴着矮人国王的皇冠。")
			.t("desc_birthday", "这只老鼠比普通的啮齿小鼠大一点。它戴着一顶小小的绿色派对帽，而不是它常戴的皇冠。祝鼠王生日快乐！")
			.t("desc_winter", "这只老鼠比普通的啮齿小鼠大一点。它戴着一顶小小的节日帽，而不是常戴的皇冠。假日快乐！")
			.t("desc", "这只老鼠比普通的啮齿小鼠大一点。它戴着一顶小小的皇冠。")
			.t("discover_hint", "你可在某个地牢区域中的终点遇到该单位。");
	}




	{
		spriteClass = RatKingSprite.class;
		
		state = SLEEPING;
		properties.add(Property.BEAST);
		properties.add(Property.BOSS);
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public float speed() {
		return 2f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
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

	@Override
	public SewerReward SupercreateLoot() {
		return new SewerReward();
	}

	//***This functionality is for when rat king may be summoned by a distortion trap

	@Override
	protected void onAdd() {
		super.onAdd();
		if (firstAdded && Dungeon.depth != 5){
			yell(Messages.get(this, "confused"));
		}
	}

	@Override
	public Notes.Landmark landmark() {
		return Dungeon.depth == 5 ? Notes.Landmark.RAT_KING : null;
	}

	@Override
	protected boolean act() {
		if (Dungeon.depth < 5){
			if (pos == Dungeon.level.exit()){
				destroy();
				sprite.killAndErase();
			} else {
				target = Dungeon.level.exit();
			}
		} else if (Dungeon.depth > 5){
			if (pos == Dungeon.level.entrance()){
				destroy();
				sprite.killAndErase();
			} else {
				target = Dungeon.level.entrance();
			}
		}
		return super.act();
	}

	//***

	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return super.interact(c);
		}

		KingsCrown crown = Dungeon.hero.belongings.getItem(KingsCrown.class);
		Spork spork = Dungeon.hero.belongings.getItem(Spork.class);
		if (state == SLEEPING) {
			notice();
			yell( Messages.get(this, "not_sleeping") );
			state = WANDERING;
		} else if (Statistics.deepestFloor > 10 && spork == null && !Dungeon.LimitedDrops.SPS_SPORK.dropped()) {
			Dungeon.sporkAvailable = true;
			yell(Messages.get(this, "thanks"));
		} else if (spork != null) {
			yell(Messages.get(this, "havefun"));
		} else if (crown != null){
			if (Dungeon.hero.belongings.armor() == null){
				yell( Messages.get(RatKing.class, "crown_clothes") );
			} else {
				Badges.validateRatmogrify();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndOptions(
								sprite(),
								Messages.titleCase(name()),
								Messages.get(RatKing.class, "crown_desc"),
								Messages.get(RatKing.class, "crown_yes"),
								Messages.get(RatKing.class, "crown_info"),
								Messages.get(RatKing.class, "crown_no")
						){
							@Override
							protected void onSelect(int index) {
								if (index == 0){
									crown.upgradeArmor(Dungeon.hero, Dungeon.hero.belongings.armor(), new Ratmogrify());
									Statistics.qualifiedForRandomVictoryBadge = false;
									((RatKingSprite)sprite).resetAnims();
									yell(Messages.get(RatKing.class, "crown_thankyou"));
								} else if (index == 1) {
									GameScene.show(new WndInfoArmorAbility(Dungeon.hero.heroClass, new Ratmogrify()));
								} else {
									yell(Messages.get(RatKing.class, "crown_fine"));
								}
							}
						});
					}
				});
			}
		} else if (Dungeon.hero.armorAbility instanceof Ratmogrify) {
			yell( Messages.get(RatKing.class, "crown_after") );
		} else {
			yell( Messages.get(this, "what_is_it") );
		}
		return true;
	}
	
	@Override
	public String description() {
		if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
			return Messages.get(this, "desc_crown");
		} else if (Holiday.getCurrentHoliday() == Holiday.APRIL_FOOLS){
			return Messages.get(this, "desc_birthday");
		} else if (Holiday.getCurrentHoliday() == Holiday.WINTER_HOLIDAYS){
			return Messages.get(this, "desc_winter");
		} else {
			return super.description();
		}
	}
}
