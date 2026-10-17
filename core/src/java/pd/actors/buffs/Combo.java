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

package pd.actors.buffs;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.DwarfKing;
import pd.items.BrokenSeal;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.ui.Window;
import render.noosa.Game;
import render.utils.data.Callback;

import java.util.function.Supplier;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.ui.ActionIndicator;
import pd.ui.AttackIndicator;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import pd.windows.WndCombo;
import render.noosa.BitmapText;
import render.noosa.Image;
import render.noosa.Visual;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Combo extends Buff implements ActionIndicator.Action {

	//SPSXPD: window construction measures text, which must happen on the render thread.
	//Called from the actor thread it throws "Text measured from the actor thread!", so
	//route every popup through here instead of calling the scene directly.
	private static void showWindow(final Supplier<Window> factory){
		Game.runOnRenderThread(new Callback(){
			@Override public void call(){ GameScene.show(factory.get()); }
		});
	}
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Combo.class)
			.t("name", "连击")
			.t("action_name", "连击")
			.t("combo", "%d连击！")
			.t("bad_target", "你的目标必须是攻击距离以内的敌人。")
			.t("prompt", "选择一个攻击目标")
			.t("clash_prompt", "选择要冲闪到的地方")
			.t("shift_prompt", "选择要挪移的目标")
			.t("desc", "角斗士，或贴着完整/镀金纹章的人，在成功击中对手时会积累战斗气势。每次攻击命中都会增加1次连击，但长时间不进行攻击，连击数就会归零。只要仍在积攒连击，其破碎纹章提供的护盾就不会衰减。\n积攒连击可以激活必定命中的连击战技！连击数到达 2 或 4 时可使用对应的战技，具体能用哪些要看_当前护甲的阶位_：\n护甲 1-2 阶：冲闪（2 连击）/ 暴雨（4 连击）\n护甲 3-4 阶：挪移（2 连击）/ 反击（4 连击）\n护甲 5-6 阶：粉碎（2 连击）/ 撞击（4 连击）\n部分连击战技会重置连击数，部分则不会，但每种连击战技在每次连击中只能使用一次。\n\n当前连击：%1$d\n\n连击归零剩余回合：%2$s")
			.t("$combomove.clash.name", "冲闪")
			.t("$combomove.clash.desc", "向指定方向快速移动，最远 _%d 格_（随连击数提高）。使用后本次连击不能再使用冲闪，但不会重置连击数。")
			.t("$combomove.fury.name", "暴雨")
			.t("$combomove.fury.desc", "你每有1点连击数便对一个敌人攻击一次，每次攻击造成60%伤害，并可触发武器附魔。使用后重置连击数。")
			.t("$combomove.fury.empower_desc", "_跳跃最多%d格以接近目标_，并根据你当前的连击数对敌人发动同等次数的攻击，每次攻击造成60%%的伤害并可触发附魔效果。使用后重置连击数。")
			.t("$combomove.shift.name", "挪移")
			.t("$combomove.shift.desc", "挑起_身旁_的一名敌人，强行与它_交换位置_。使用后重置连击数。")
			.t("$combomove.parry.name", "反击")
			.t("$combomove.parry.desc", "使用后，招架下一回合内受到的首次攻击，并立即进行反击。若未触发招架，则重置连击数。")
			.t("$combomove.parry.empower_desc", "使用后，招架下一回合内受到的_所有攻击_，并立即进行反击。若未触发招架，则重置连击数。")
			.t("$combomove.crush.name", "粉碎")
			.t("$combomove.crush.desc", "对目标造成%d%%(连击数x25%%)的伤害，并对7x7范围内的其它敌人造成一半伤害。使用后重置连击数。")
			.t("$combomove.crush.empower_desc", "_跳跃最多%1$d格以接近目标_，对主要目标造成%2$d%%（连击数x25%%）的伤害，并对7x7范围内的其它敌人造成一半伤害。使用后重置连击数。")
			.t("$combomove.slam.name", "撞击")
			.t("$combomove.slam.desc", "造成等同于自身防御%d%%(连击数x20%%)的额外伤害。使用后重置连击数。")
			.t("$combomove.slam.empower_desc", "_跳跃最多%1$d格以接近目标_，并对其造成等同于自身防御%2$d%%(连击数x20%%)的额外伤害。使用后重置连击数。");
	}




	{
		type = buffType.POSITIVE;
	}
	
	private int count = 0;
	private float comboTime = 0f;
	private float initialComboTime = 5f;

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}
	
	@Override
	public void tintIcon(Image icon) {
		ComboMove move = getHighestMove();
		if (move != null){
			icon.hardlight(move.tintColor);
		} else {
			icon.resetColor();
		}
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (initialComboTime - comboTime)/ initialComboTime);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int)comboTime);
	}
	
	/** SPSEXPD: 上次命中时穿着的护甲——连击技按护甲阶位分组，换护甲不能继承连击数。 */
	private pd.items.equipment.armor.Armor comboArmor;
	private static final String COMBO_ARMOR = "combo_armor";

	public void hit( Char enemy ) {

		//SPSEXPD: 连击技是跟着_贴着纹章的那件护甲_走的，所以_换护甲不保留连击数_——
		//否则可以先穿低阶甲攒连击，再换上高阶甲直接甩出高阶战技（武器切换不受影响，允许）。
		if (target instanceof Hero) {
			pd.items.equipment.armor.Armor worn = comboArmorOf((Hero) target);
			if (comboArmor != worn) {
				comboArmor = worn;
				count = 0;
				comboTime = 0f;
			}
		}

		//SPSEXPD: 镀金纹章——上次被打断时保留的一半层数，在下次连击开始时继承
		if (count == 0 && target instanceof Hero) {
			Hero h = (Hero) target;
			if (h.spsCarriedCombo > 0) {
				count = h.spsCarriedCombo;
				h.spsCarriedCombo = 0;
				comboTime = Math.max(comboTime, 5f);
			}
		}

		count++;
		if (comboTime <= 5f) {
			comboTime = Math.max(comboTime, 5f);
			initialComboTime = 5f;
		}

		if (!enemy.isAlive() || (enemy.buff(Corruption.class) != null && enemy.HP == enemy.HT)){
			comboTime = 15f + 15f*((Hero)target).pointsInTalent(Talent.CLEAVE);
			initialComboTime = comboTime;
		}

		if ((getHighestMove() != null)) {

			ActionIndicator.setAction( this );
			Badges.validateMasteryCombo( count );

			GLog.p( Messages.get(this, "combo", count) );
			
		}

		BuffIndicator.refreshHero(); //refresh the buff visually on-hit

	}

	public void addTime( float time ){
		comboTime += time;
	}

	@Override
	public void detach() {
		//SPSEXPD: 镀金纹章——连击被主动打断（如使用战技重置）时也保留一半层数
		if (gilded() && count >= 2 && target instanceof Hero) {
			((Hero)target).spsCarriedCombo = count / 2;
		}
		super.detach();
		ActionIndicator.clearAction(this);
	}

	/** SPSEXPD: 护甲上的纹章是否为镀金纹章（提供连击强化）。 */
	private boolean gilded(){
		return sealOf() != null && sealOf().retainsHalfCombo();
	}

	/** SPSEXPD: 连击被打断的时间倍率（镀金纹章为 2，即打断时间翻倍）。 */
	private float comboBreakFactor(){
		return sealOf() == null ? 1f : Math.max(1f, sealOf().comboBreakFactor());
	}

	private BrokenSeal sealOf(){
		if (!(target instanceof Hero)) return null;
		Hero hero = (Hero) target;
		if (hero.belongings == null || hero.belongings.armor() == null) return null;
		return hero.belongings.armor().checkSeal();
	}

	/** SPSEXPD: 打断时保留一半层数；返回 true 表示已处理（外层不必再 detach）。 */
	private boolean carryHalfCombo(){
		if (!gilded() || count < 2) return false;
		detach();
		return true;
	}

	@Override
	public boolean act() {
		//SPSEXPD: 镀金纹章——连击被打断的时间翻倍（comboTime 衰减速度减半）
		comboTime -= TICK * HoldFast.buffDecayFactor(target) / comboBreakFactor();
		spend(TICK);
		if (comboTime <= 0) {
			//SPSEXPD: 连击时间耗尽就是连击结束——撤掉动作栏按钮并 detach。
			//镀金纹章「保留一半层数」由 detach() 自己负责，不在这里拦（否则 count 还在，
			//下面那段补按钮的代码会把入口一直挂着，超时后按钮永远不消失）。
			if (ActionIndicator.action == this) ActionIndicator.setAction(null);
			detach();
			return true;
		}
		//SPSEXPD: 用了「不消耗连击数」的战技（冲闪/招架/位移/猛砸等）后，动作栏按钮会被清掉，
		//下次命中才恢复。这里每回合补回按钮，让它立刻可见（不必等下一次命中）。
		if (getHighestMove() != null && ActionIndicator.action != this) {
			ActionIndicator.setAction(this);
		}
		return true;
	}

	/**
	 * SPSEXPD: 连击技跟随护甲与护甲上的纹章——脱下/切换护甲、摘下或贴附纹章时立刻清空连击数。
	 * 这里刻意不复用 detach()：镀金纹章「打断后保留一半层数」的规则不适用于换装，直接抹掉。
	 */
	public static void clearOnGearChange( Char ch ) {
		if (ch == null) return;
		Combo combo = ch.buff(Combo.class);
		if (combo == null) return;
		combo.detach();
		if (ch instanceof Hero) ((Hero) ch).spsCarriedCombo = 0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", count, dispTurns(comboTime));
	}

	private static final String COUNT = "count";
	private static final String TIME  = "combotime";
	private static final String INITIAL_TIME  = "initialComboTime";

	//SPSEXPD: 冲闪取代了原来的「冲击」，旧档的 clobber_used 键仍作兜底读取
	private static final String CLASH_USED = "clash_used";
	private static final String LEGACY_CLOBBER_USED = "clobber_used";
	private static final String PARRY_USED   = "parry_used";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNT, count);
		bundle.put(TIME, comboTime);
		bundle.put(INITIAL_TIME, initialComboTime);

		bundle.put(CLASH_USED, clashUsed);
		bundle.put(PARRY_USED, parryUsed);
		//SPSEXPD: 记住连击对应的护甲，读档后换甲同样不继承连击
		if (comboArmor != null) bundle.put(COMBO_ARMOR, comboArmor);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		count = bundle.getInt( COUNT );
		comboTime = bundle.getFloat( TIME );

		initialComboTime = bundle.getFloat( INITIAL_TIME );

		clashUsed = bundle.contains(CLASH_USED)
				? bundle.getBoolean(CLASH_USED)
				: bundle.getBoolean(LEGACY_CLOBBER_USED);
		parryUsed = bundle.getBoolean(PARRY_USED);

		//SPSEXPD: 连击对应的护甲（旧档没有这个键，留 null 即可）
		comboArmor = bundle.contains(COMBO_ARMOR)
				? (pd.items.equipment.armor.Armor) bundle.get(COMBO_ARMOR)
				: null;

		if (getHighestMove() != null) ActionIndicator.setAction(this);
	}

	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.COMBO;
	}

	@Override
	public Visual secondaryVisual() {
		BitmapText txt = new BitmapText(PixelScene.pixelFont);
		txt.text( Integer.toString(count) );
		txt.hardlight(CharSprite.POSITIVE);
		txt.measure();
		return txt;
	}

	@Override
	public int indicatorColor() {
		ComboMove best = getHighestMove();
		if (best == null) {
			return 0xDFDFDF;
		} else {
			//take the tint color and darken slightly to match buff icon
			int r = (int) ((best.tintColor >> 16) * 0.875f);
			int g = (int) (((best.tintColor >> 8) & 0xFF) * 0.875f);
			int b = (int) ((best.tintColor & 0xFF) * 0.875f);
			return (r << 16) + (g << 8) + b;
		}
	}

	@Override
	public void doAction() {
		showWindow(() -> new WndCombo(this));
	}

	public enum ComboMove {
		//SPSEXPD: 连击技按护甲阶位分组，每段两个（解锁连击数为 2 / 4）
		CLASH(2, 0x00FF00, 1, 2),
		FURY (4, 0xFF0000, 1, 2),
		SHIFT(2, 0xFFFF00, 3, 4),
		PARRY(4, 0xFFFF00, 3, 4),
		CRUSH(2, 0xFFCC00, 5, 6),
		SLAM (4, 0xCCFF00, 5, 6);

		public int comboReq, tintColor, tierMin, tierMax;

		ComboMove(int comboReq, int tintColor, int tierMin, int tierMax){
			this.comboReq = comboReq;
			this.tintColor = tintColor;
			this.tierMin = tierMin;
			this.tierMax = tierMax;
		}

		/** SPSEXPD: 当前护甲阶位是否允许使用该战技。 */
		public boolean allowedForTier(int tier){
			return tier >= tierMin && tier <= tierMax;
		}

		public String title(){
			return Messages.get(this, name() + ".name");
		}

		public String desc(int count){
			switch (this){
				case CLASH:
					return Messages.get(this, name() + ".desc", clashRange(count));
				case SHIFT:
					return Messages.get(this, name() + ".desc");
				case SLAM:
					if (count >= 3 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 3){
						return Messages.get(this, name() + ".empower_desc", count/3, count*20);
					} else {
						return Messages.get(this, name() + ".desc", count*20);
					}
				case PARRY:
					if (count >= 9 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 2){
						return Messages.get(this, name() + ".empower_desc");
					} else {
						return Messages.get(this, name() + ".desc");
					}
				case CRUSH:
					if (count >= 3 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 3){
						return Messages.get(this, name() + ".empower_desc", count/3, count*25);
					} else {
						return Messages.get(this,  name() + ".desc", count*25);
					}
				case FURY: default:
					if (count >= 3 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 3){
						return Messages.get(this, name() + ".empower_desc", count/3);
					} else {
						return Messages.get(this,  name() + ".desc");
					}
			}
		}

	}

	private boolean clashUsed = false;
	private boolean parryUsed = false;

	/** SPSEXPD: 连击技跟随_贴附了纹章的那件护甲_的阶位（不是主甲）：主甲 → 副甲依次找，
	 *  都没有提供连击的纹章时退回主甲阶位（角斗士子职业走这条）。无护甲时按 1。 */
	public static int armorTier(){
		Hero hero = Dungeon.hero;
		if (hero == null || hero.belongings == null) return 1;
		pd.items.equipment.armor.Armor comboArmor = comboArmorOf(hero);
		pd.items.equipment.armor.Armor worn = comboArmor != null ? comboArmor : hero.belongings.armor();
		return worn == null ? 1 : worn.tier;
	}

	/** SPSEXPD: 身上哪件护甲提供了连击（贴了完整/镀金纹章）——主甲优先，其次副甲。 */
	public static pd.items.equipment.armor.Armor comboArmorOf( Hero hero ){
		if (hero == null || hero.belongings == null) return null;
		for (pd.items.equipment.armor.Armor armor :
				new pd.items.equipment.armor.Armor[]{ hero.belongings.armor(), hero.belongings.secondArmor }) {
			if (armor == null) continue;
			pd.items.BrokenSeal seal = armor.checkSeal();
			if (seal != null && seal.providesCombo()) return armor;
		}
		return null;
	}

	/** SPSEXPD: 冲闪的最大移动距离——随连击数提高（1 + 连击数/2）。 */
	public static int clashRange(int count){
		return 1 + Math.max(0, count) / 2;
	}

	public ComboMove getHighestMove(){
		int tier = armorTier();
		ComboMove best = null;
		for (ComboMove move : ComboMove.values()){
			if (count >= move.comboReq && move.allowedForTier(tier)){
				best = move;
			}
		}
		return best;
	}

	public int getComboCount(){
		return count;
	}

	public boolean canUseMove(ComboMove move){
		if (!move.allowedForTier(armorTier()))    return false;
		if (move == ComboMove.CLASH && clashUsed)  return false;
		if (move == ComboMove.PARRY && parryUsed)  return false;
		return move.comboReq <= count;
	}

	public void useMove(ComboMove move){
		if (move == ComboMove.PARRY){
			parryUsed = true;
			comboTime = Math.max(comboTime, 5f);
			Invisibility.dispel();
			Buff.affect(target, ParryTracker.class, Actor.TICK);
			((Hero)target).spendAndNext(Actor.TICK);
			Dungeon.hero.busy();
		} else if (move == ComboMove.CLASH){
			//SPSEXPD: 冲闪——选一个落点快速移动（不攻击）
			moveBeingUsed = move;
			GameScene.selectCell(clashListener);
		} else if (move == ComboMove.SHIFT){
			//SPSEXPD: 挪移——选一个身旁的敌人，与它交换位置
			moveBeingUsed = move;
			GameScene.selectCell(shiftListener);
		} else {
			moveBeingUsed = move;
			GameScene.selectCell(listener);
		}
	}

	public static class ParryTracker extends FlavourBuff{
		{ actPriority = HERO_PRIO+1;}

		public boolean parried;

		@Override
		public void detach() {
			if (!parried && target.buff(Combo.class) != null) target.buff(Combo.class).detach();
			super.detach();
		}
	}

	public static class RiposteTracker extends Buff{
		{ actPriority = VFX_PRIO;}

		public Char enemy;

		@Override
		public boolean act() {
			if (target.buff(Combo.class) != null) {
				moveBeingUsed = ComboMove.PARRY;
				target.sprite.attack(enemy.pos, new Callback() {
					@Override
					public void call() {
						target.buff(Combo.class).doAttack(enemy);
						next();
					}
				});
				detach();
				return false;
			} else {
				detach();
				return true;
			}
		}
	}

	private static ComboMove moveBeingUsed;
	private static int furyHitsLeft = 0;

	private void doAttack(final Char enemy) {

		AttackIndicator.target(enemy);

		boolean wasAlly = enemy.alignment == target.alignment;
		Hero hero = (Hero) target;

		float dmgMulti = 1f;
		int dmgBonus = 0;

		//variance in damage dealt
		switch (moveBeingUsed) {
			case SLAM:
				dmgBonus = Math.round(target.drRoll() * count / 5f);
				break;
			case CRUSH:
				dmgMulti = 0.25f * count;
				break;
			case FURY:
				dmgMulti = 0.6f;
				break;
		}

		int oldPos = enemy.pos;
		if (hero.attack(enemy, dmgMulti, dmgBonus, Char.INFINITE_ACCURACY)){
			//special on-hit effects
			switch (moveBeingUsed) {
				case PARRY:
					hit(enemy);
					break;
				case CRUSH:
					WandOfBlastWave.BlastWave.blast(enemy.pos);
					PathFinder.buildDistanceMap(target.pos, BArray.not(Dungeon.level.solid, null), 3);
					for (Char ch : Actor.chars()) {
						if (ch != enemy && ch.alignment == Char.Alignment.ENEMY
								&& PathFinder.distance[ch.pos] < Integer.MAX_VALUE) {
							int aoeHit = Math.round(target.damageRoll() * 0.25f * count);
							aoeHit /= 2;
							aoeHit -= ch.drRoll();
							if (ch.buff(Vulnerable.class) != null) aoeHit *= 1.33f;
							if (ch instanceof DwarfKing){
								//change damage type for DK so that crush AOE doesn't count for DK's challenge badge
								ch.damage(aoeHit, this);
							} else {
								ch.damage(aoeHit, target);
							}
							ch.sprite.bloodBurstA(target.sprite.center(), aoeHit);
							ch.sprite.flash();

							if (!ch.isAlive() && hero.hasTalent(Talent.LETHAL_DEFENSE)) {
								Buff.affect(hero, BrokenSeal.WarriorShield.class).reduceCooldown(hero.pointsInTalent(Talent.LETHAL_DEFENSE)/3f);
							}
						}
					}
					break;
				default:
					//nothing
					break;
			}
		}

		Invisibility.dispel();

		//Post-attack behaviour
		switch(moveBeingUsed){
			case PARRY:
				//do nothing
				break;

			case FURY:
				if (count > 0){
					furyHitsLeft = count;
					count = 0;
					detach();
					hero.spend(hero.attackDelay());
				}
				furyHitsLeft--;
				//fury attacks as many times as you have combo count
				if (furyHitsLeft > 0 && enemy.isAlive() && hero.canAttack(enemy) &&
						hero.paralysed == 0 && (wasAlly || enemy.alignment != target.alignment)){
					target.sprite.attack(enemy.pos, new Callback() {
						@Override
						public void call() {
							doAttack(enemy);
						}
					});
				} else {
					furyHitsLeft = 0;
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					ActionIndicator.clearAction(Combo.this);
					hero.next();
				}
				break;

			default:
				detach();
				ActionIndicator.clearAction(Combo.this);
				hero.spendAndNext(hero.attackDelay());
				break;
		}

		if (!enemy.isAlive() || (!wasAlly && enemy.alignment == target.alignment)) {
			if (hero.hasTalent(Talent.LETHAL_DEFENSE)){
				Buff.affect(hero, BrokenSeal.WarriorShield.class).reduceCooldown(hero.pointsInTalent(Talent.LETHAL_DEFENSE)/3f);
			}
		}

	}

	private CellSelector.Listener listener = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar( cell );
			if (enemy == null
					|| enemy == target
					|| !Dungeon.level.heroFOV[cell]
					|| target.isCharmedBy( enemy )) {
				GLog.w(Messages.get(Combo.class, "bad_target"));

			} else if (!((Hero)target).canAttack(enemy)){
				if (((Hero) target).pointsInTalent(Talent.ENHANCED_COMBO) < 3
					|| Dungeon.level.distance(target.pos, enemy.pos) > 1 + target.buff(Combo.class).count/3){
					GLog.w(Messages.get(Combo.class, "bad_target"));
				} else {
					Ballistica c = new Ballistica(target.pos, enemy.pos, Ballistica.PROJECTILE);
					if (c.collisionPos == enemy.pos){
						final int leapPos = c.path.get(c.dist-1);
						if (!Dungeon.level.passable[leapPos] && !(target.flying && Dungeon.level.avoid[leapPos])){
							GLog.w(Messages.get(Combo.class, "bad_target"));
						} else if (Dungeon.hero.rooted) {
							PixelScene.shake( 1, 1f );
							GLog.w(Messages.get(Combo.class, "bad_target"));
						} else {
							Dungeon.hero.busy();
							target.sprite.jump(target.pos, leapPos, new Callback() {
								@Override
								public void call() {
									target.move(leapPos);
									Dungeon.level.occupyCell(target);
									Dungeon.observe();
									GameScene.updateFog();
									target.sprite.attack(cell, new Callback() {
										@Override
										public void call() {
											doAttack(enemy);
										}
									});
								}
							});
						}
					} else {
						GLog.w(Messages.get(Combo.class, "bad_target"));
					}
				}

			} else {
				Dungeon.hero.busy();
				target.sprite.attack(cell, new Callback() {
					@Override
					public void call() {
						doAttack(enemy);
					}
				});
			}
		}

		@Override
		public String prompt() {
			return Messages.get(Combo.class, "prompt");
		}
	};

	/** SPSEXPD: 冲闪——选一个落点快速移动（最远 1 + 连击数/2 格，不攻击）。 */
	private CellSelector.Listener clashListener = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Hero hero = (Hero) target;
			int maxRange = clashRange(count);
			if (cell == target.pos
					|| !Dungeon.level.insideMap(cell)
					|| Dungeon.level.distance(target.pos, cell) > maxRange
					|| !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null){
				GLog.w(Messages.get(Combo.class, "bad_target"));
				return;
			}
			if (PathFinder.find(target.pos, cell, Dungeon.level.passable) == null){
				GLog.w(Messages.get(Combo.class, "bad_target"));
				return;
			}
			clashUsed = true;
			Dungeon.hero.busy();
			target.sprite.jump(target.pos, cell, new Callback(){
				@Override
				public void call() {
					target.move(cell);
					Dungeon.level.occupyCell(target);
					Dungeon.observe();
					GameScene.updateFog();
					ActionIndicator.clearAction(Combo.this);
					hero.spendAndNext(hero.attackDelay());
				}
			});
		}

		@Override
		public String prompt() {
			return Messages.get(Combo.class, "clash_prompt");
		}
	};

	/** SPSEXPD: 挪移——挑起身旁的一名敌人，强行与它交换位置。 */
	private CellSelector.Listener shiftListener = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Hero hero = (Hero) target;
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == target
					|| !Dungeon.level.adjacent(target.pos, cell)
					|| !Dungeon.level.heroFOV[cell]){
				GLog.w(Messages.get(Combo.class, "bad_target"));
				return;
			}
			int heroPos = target.pos;
			Dungeon.hero.busy();

			//交换双方位置
			target.pos = cell;
			enemy.pos = heroPos;
			Dungeon.level.occupyCell(target);
			Dungeon.level.occupyCell(enemy);
			target.sprite.place(target.pos);
			enemy.sprite.place(enemy.pos);
			Dungeon.observe();
			GameScene.updateFog();
			Invisibility.dispel();

			//挪移是强力控制：使用后重置连击
			count = 0;
			detach();
			hero.spendAndNext(hero.attackDelay());
		}

		@Override
		public String prompt() {
			return Messages.get(Combo.class, "shift_prompt");
		}
	};
}
