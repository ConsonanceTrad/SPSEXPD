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

package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dread;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.hero.spells.Stasis;
import pd.actors.mobs.npcs.NPC;
import pd.effects.FloatingText;
import pd.effects.MagicMissile;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.journal.Bestiary;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.WardSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.geom.PointF;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class WandOfWarding extends Wand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfWarding.class)
			.t("name", "哨卫法杖")
			.t("staff_name", "哨卫魔杖")
			.t("no_more_wards", "你的法杖无法维持更多的哨卫。")
			.t("bad_location", "你不能在那里设置一个哨卫。")
			.t("desc", "这根短小的金属法杖尖端上有一颗亮紫色宝石悬空浮动。")
			.t("stats_desc", "这根法杖并不能直接攻击敌人，但可以召唤出哨卫元素与哨卫结晶。你可以在视野中的任何地方召唤哨卫，即使是隔着一堵墙。这根法杖最多能提供_%d点能量_以维持哨卫。")
			.t("upgrade_stat_name_1", "哨卫伤害")
			.t("upgrade_stat_name_2", "哨卫能量")
			.t("bmage_desc", "当_战斗法师_以哨卫魔杖近战攻击目标时，有概率为所有哨卫元素和哨卫结晶恢复一定生命值。")
			.t("eleblast_desc", "哨卫魔杖的元素风暴会治疗范围内的所有哨卫元素和结晶。")
			.t("$ward.desc_generic_ward", "这个哨卫元素会自动攻击进入视野的目标。\n\n使用哨卫法杖向该哨卫施法能使其升级。\n\n哨卫能发动攻击的次数有限，次数耗尽后它们便会消散。")
			.t("$ward.desc_generic_sentry", "这个哨卫结晶攻击力与哨卫元素相同，但以生命值取代了有限的发动次数。它的样子有点像哨卫法杖顶端的宝石。\n\n使用哨卫法杖向该哨卫施法能使其升级并治疗它。\n\n这个哨卫每次发动攻击时会消耗一定的生命值，但你可以用哨卫法杖治疗它。")
			.t("$ward.name_1", "小哨卫元素")
			.t("$ward.desc_1", "这个最基本的哨卫元素会自动攻击进入视野的目标，造成_%1$d~%2$d点伤害。_\n\n使用哨卫法杖向该哨卫施法能使其升级。\n\n这个哨卫在仅发动1次攻击之后就会消散。\n\n你的哨卫法杖在使用_%3$d点能量_维持这个哨卫。")
			.t("$ward.name_2", "哨卫元素")
			.t("$ward.desc_2", "这个升级过的哨卫元素结构更精致，而且在攻击数次后才会消散。它每次发动攻击能造成_%1$d~%2$d点伤害_。\n\n使用哨卫法杖向该哨卫施法能使其升级。\n\n这个哨卫在发动3次攻击之后才会消散。\n\n你的哨卫法杖在使用_%3$d点能量_维持这个哨卫。")
			.t("$ward.name_3", "大哨卫元素")
			.t("$ward.desc_3", "这个满级的哨卫元素能发动更多次攻击，而且攻击速度更快。它每次发动攻击能造成_%1$d~%2$d点伤害_。\n\n使用哨卫法杖向该哨卫施法能使其进化。\n\n这个哨卫在发动5次攻击之后才会消散。\n\n你的哨卫法杖在使用_%3$d点能量_维持这个哨卫。")
			.t("$ward.name_4", "小哨卫结晶")
			.t("$ward.desc_4", "这个小小的哨卫结晶攻击力与大哨卫元素相同，但以生命值取代了有限的发动次数。它的样子有点像哨卫法杖顶端的宝石。它每次发动攻击能造成_%1$d~%2$d点伤害_。\n\n使用哨卫法杖向该哨卫施法能使其升级并治疗它。\n\n这个哨卫每次发动攻击时会消耗一定的生命值，但你可以用哨卫法杖治疗它。\n\n你的哨卫法杖在使用_%3$d点能量_维持这个哨卫。")
			.t("$ward.name_5", "哨卫结晶")
			.t("$ward.desc_5", "这个升级过的哨卫结晶比小哨卫结晶体积更大，形体也更结实。它每次发动攻击能造成_%1$d~%2$d点伤害_。\n\n使用哨卫法杖向该哨卫施法能使其升级并治疗它。\n\n这个哨卫每次发动攻击时会消耗一定的生命值，但你可以用哨卫法杖治疗它。\n\n你的哨卫法杖在使用_%3$d点能量_维持这个哨卫。")
			.t("$ward.name_6", "大哨卫结晶")
			.t("$ward.desc_6", "这个满级的哨卫结晶比之前的坚固了不少。它每次发动攻击能造成_%1$d~%2$d点伤害_。\n\n使用哨卫法杖向该哨卫施法能治疗它。\n\n这个哨卫每次发动攻击时会消耗一定的生命值，但你可以用哨卫法杖治疗它。\n\n你的哨卫法杖在使用_%3$d点能量_维持这个哨卫。")
			.t("$ward.discover_hint", "你可通过某根法杖遇到该单位。")
			.t("$ward.dismiss_title", "要驱散这个哨卫吗？")
			.t("$ward.dismiss_body", "不想让法杖继续维持这个哨卫的话，你可以选择驱散它立即将其破坏移除。\n\n要驱散这个哨卫吗？")
			.t("$ward.dismiss_confirm", "是")
			.t("$ward.dismiss_cancel", "否");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_WARDING_0;
		usesTargeting = false; //player usually targets wards or spaces, not enemies
	}

	@Override
	public int collisionProperties(int target) {
		if (cursed)                                 return super.collisionProperties(target);
		else if (!Dungeon.level.heroFOV[target])    return Ballistica.PROJECTILE;
		else                                        return Ballistica.STOP_TARGET;
	}

	@Override
	public void execute(Hero hero, String action) {
		//cursed warding does use targeting as it's just doing regular cursed zaps
		usesTargeting = cursed && cursedKnown;
		super.execute(hero, action);
	}

	private boolean wardAvailable = true;
	
	@Override
	public boolean tryToZap(Hero owner, int target) {
		
		int currentWardEnergy = 0;
		for (Char ch : Actor.chars()){
			if (ch instanceof Ward){
				currentWardEnergy += ((Ward) ch).tier;
			}
		}

		if (Stasis.getStasisAlly() instanceof Ward){
			currentWardEnergy += ((Ward) Stasis.getStasisAlly()).tier;
		}
		
		int maxWardEnergy = 0;
		for (Buff buff : curUser.buffs()){
			if (buff instanceof Wand.Charger){
				if (((Charger) buff).wand() instanceof WandOfWarding){
					maxWardEnergy += 2 + ((Charger) buff).wand().level();
				}
			}
		}
		
		wardAvailable = (currentWardEnergy < maxWardEnergy);
		
		Char ch = Actor.findChar(target);
		if (ch instanceof Ward){
			if (!wardAvailable && ((Ward) ch).tier <= 3){
				GLog.w( Messages.get(this, "no_more_wards"));
				return false;
			}
		} else {
			if ((currentWardEnergy + 1) > maxWardEnergy){
				GLog.w( Messages.get(this, "no_more_wards"));
				return false;
			}
		}
		
		return super.tryToZap(owner, target);
	}
	
	@Override
	public void onZap(Ballistica bolt) {

		int target = bolt.collisionPos;
		Char ch = Actor.findChar(target);
		if (ch != null && !(ch instanceof Ward)){
			if (bolt.dist > 1) target = bolt.path.get(bolt.dist-1);

			ch = Actor.findChar(target);
			if (ch != null && !(ch instanceof Ward)){
				GLog.w( Messages.get(this, "bad_location"));
				Dungeon.level.pressCell(bolt.collisionPos);
				return;
			}
		}

		if (ch != null){
			if (ch instanceof Ward){
				if (wardAvailable) {
					((Ward) ch).upgrade( buffedLvl() );
				} else {
					((Ward) ch).wandHeal( buffedLvl() );
				}
				ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((Ward) ch).tier);
			} else {
				GLog.w( Messages.get(this, "bad_location"));
				Dungeon.level.pressCell(target);
			}
			
		} else if (!Dungeon.level.passable[target]){
			GLog.w( Messages.get(this, "bad_location"));
			Dungeon.level.pressCell(target);

		} else {
			Ward ward = new Ward();
			ward.pos = target;
			ward.wandLevel = buffedLvl();
			GameScene.add(ward, 1f);
			Dungeon.level.occupyCell(ward);
			ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
			Dungeon.level.pressCell(target);

		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile m = MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.WARD,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		
		if (bolt.dist > 10){
			m.setSpeed(bolt.dist*20);
		}
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		int level = Math.max( 0, staff.buffedLvl() );

		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		float procChance = (level+1f)/(level+5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			for (Char ch : Actor.chars()){
				if (ch instanceof Ward){
					((Ward) ch).wandHeal(staff.buffedLvl(), powerMulti);
					ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((Ward) ch).tier);
				}
			}
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0x8822FF );
		particle.am = 0.3f;
		particle.setLifespan(3f);
		particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
		particle.setSize( 1f, 2f);
		particle.radiateXY(2.5f);
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", level()+2);
		else
			return Messages.get(this, "stats_desc", 2);
	}

	@Override
	public String upgradeStat1(int level) {
		return 2+level + "-" + (8+4*level);
	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(level+2);
	}

	public static class Ward extends NPC {

		public int tier = 1;
		private int wandLevel = 1;

		public int totalZaps = 0;

		{
			spriteClass = WardSprite.class;

			alignment = Alignment.ALLY;

			properties.add(Property.IMMOVABLE);
			properties.add(Property.INORGANIC);

			viewDistance = 4;
			state = WANDERING;
		}

		@Override
		public String name() {
			return Messages.get(this, "name_" + tier );
		}

		public void upgrade(int wandLevel ){
			if (this.wandLevel < wandLevel){
				this.wandLevel = wandLevel;
			}

			switch (tier){
				case 1: case 2: default:
					break; //do nothing
				case 3:
					HT = 35;
					HP = 15 + (5-totalZaps)*4;
					break;
				case 4:
					HT = 54;
					HP += 19;
					break;
				case 5:
					HT = 84;
					HP += 30;
					break;
				case 6:
					wandHeal(wandLevel);
					break;
			}

			if (Actor.chars().contains(this) && tier >= 3){
				Bestiary.setSeen(WardSentry.class);
			}

			if (tier < 6){
				tier++;
				viewDistance++;
				if (sprite != null){
					((WardSprite)sprite).updateTier(tier);
					sprite.place(pos);
				}
				GameScene.updateFog(pos, viewDistance+1);
			}

		}

		//this class is used so that wards and sentries can have two entries in the Bestiary
		public static class WardSentry extends Ward{};

		public void wandHeal( int wandLevel ){
			wandHeal( wandLevel, 1f );
		}

		public void wandHeal( int wandLevel, float healFactor ){
			if (this.wandLevel < wandLevel){
				this.wandLevel = wandLevel;
			}

			int heal;
			switch(tier){
				default:
					return;
				case 2:
					heal = Math.round(1 * healFactor);
					break;
				case 3:
					heal = Math.round(Random.IntRange(1, 2) * healFactor);
					break;
				case 4:
					heal = Math.round(9 * healFactor); //9/5 1.8
					break;
				case 5:
					heal = Math.round(12 * healFactor); //12/6, 2
					break;
				case 6:
					heal = Math.round(16 * healFactor); //16/7, 2.28
					break;
			}

			if (tier <= 3){
				totalZaps = (Math.max(0, totalZaps-heal));
			} else {
				HP = Math.min(HT, HP + heal);
			}
			if (sprite != null) sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);

		}

		@Override
		public int defenseSkill(Char enemy) {
			if (tier > 3){
				defenseSkill = 4 + Dungeon.scalingDepth();
			}
			return super.defenseSkill(enemy);
		}

		@Override
		public int drRoll() {
			int dr = super.drRoll();
			if (tier > 3){
				return dr + Math.round(Random.NormalIntRange(0, 3 + Dungeon.scalingDepth()/2) / (7f - tier));
			} else {
				return dr;
			}
		}

		@Override
		protected boolean canAttack( Char enemy ) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}

		@Override
		protected boolean doAttack(Char enemy) {
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}

			return !visible;
		}

		private void zap() {
			spend( 1f );

			//always hits
			int dmg = Hero.heroDamageIntRange( 2 + wandLevel, 8 + 4*wandLevel );
			Char enemy = this.enemy;
			enemy.damage( dmg, this );
			if (enemy.isAlive()){
				Wand.wandProc(enemy, wandLevel, 1);
			}

			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromFriendlyMagic();
				GLog.n(Messages.capitalize(Messages.get( this, "kill", name() )));
				Dungeon.fail( WandOfWarding.class );
			}

			totalZaps++;
			switch(tier){
				case 1: case 2: case 3: default:
					if (totalZaps >= (2*tier-1)){
						die(this);
					}
					break;
				case 4:
					damage(5, this);
					break;
				case 5:
					damage(6, this);
					break;
				case 6:
					damage(7, this);
					break;
			}
		}

		public void onZapComplete() {
			zap();
			next();
		}

		@Override
		protected boolean getCloser(int target) {
			return false;
		}

		@Override
		protected boolean getFurther(int target) {
			return false;
		}

		@Override
		public CharSprite sprite() {
			WardSprite sprite = (WardSprite) super.sprite();
			sprite.linkVisuals(this);
			return sprite;
		}

		@Override
		public void updateSpriteState() {
			super.updateSpriteState();
			((WardSprite)sprite).updateTier(tier);
			sprite.place(pos);
		}
		
		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog(pos, viewDistance+1);
		}
		
		@Override
		public boolean canInteract(Char c) {
			return true;
		}

		@Override
		public boolean interact( Char c ) {
			if (c != Dungeon.hero){
				return true;
			}
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndOptions( sprite(),
							Messages.get(Ward.this, "dismiss_title"),
							Messages.get(Ward.this, "dismiss_body"),
							Messages.get(Ward.this, "dismiss_confirm"),
							Messages.get(Ward.this, "dismiss_cancel") ){
						@Override
						protected void onSelect(int index) {
							if (index == 0){
								die(null);
							}
						}
					});
				}
			});
			return true;
		}

		@Override
		public String description() {
			if (!Actor.chars().contains(this)){
				//for viewing in the journal
				if (tier < 4){
					return Messages.get(this, "desc_generic_ward");
				} else {
					return Messages.get(this, "desc_generic_sentry");
				}
			} else {
				return Messages.get(this, "desc_" + tier, 2 + wandLevel, 8 + 4 * wandLevel, tier);
			}
		}
		
		{
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Dread.class );
			immunities.add( Vertigo.class );
			immunities.add( AllyBuff.class );
		}

		private static final String TIER = "tier";
		private static final String WAND_LEVEL = "wand_level";
		private static final String TOTAL_ZAPS = "total_zaps";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TIER, tier);
			bundle.put(WAND_LEVEL, wandLevel);
			bundle.put(TOTAL_ZAPS, totalZaps);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			tier = bundle.getInt(TIER);
			viewDistance = 3 + tier;
			wandLevel = bundle.getInt(WAND_LEVEL);
			totalZaps = bundle.getInt(TOTAL_ZAPS);
		}
	}
}
