/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Frost;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Hex;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.windows.WndPetInfo;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Common runtime for the original SPS egg pets. */
public abstract class LegacyPet extends DirectableAlly {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LegacyPet.class)
			.t("levelup", "仪器确定你的宠物等级已提升。");
	}




	public enum Kind {
		BLUE_DRAGON(501), GREEN_DRAGON(502), LIGHT_DRAGON(503), RED_DRAGON(504),
		SHADOW_DRAGON(505), VIOLET_DRAGON(506), SCORPION(507), LERY_FIRE(508),
		GOLD_DRAGON(509), BUG_DRAGON(510), BLUE_GIRL(601), ABI(405), YEAR(666), BUNNY(401),
		HARO(403), PIG(303), COCO_CAT(402), VELOCIROOSTER(404),
		BUTTERFLY(304), CHOCOBO(202), DATURA(301), DOG(201), DWARF_BOY(206),
		FOX_HELPER(305), FROG(306), GENTLE_CRAB(102), KODORA(101), LIT_DEMON(105),
		MONKEY(302), SNAKE(104), SPIDER(204), STAR_KID(106), STONE(205),
		FLY(203), RIBBON_RAT(103);

		public final int legacyType;
		Kind(int legacyType) { this.legacyType = legacyType; }
	}

	protected int cooldown = 50;

	{
		EXP = 0;
		maxLvl = -1;
		flying = true;
		state = HUNTING;
		properties.add(Property.MINIBOSS);
	}

	protected abstract Kind kind();

	public int legacyType() { return kind().legacyType; }

	public boolean lovefood(Item item) {
		if (kind() == Kind.BUG_DRAGON) return false;
		if (item instanceof pd.items.consum.food.completefood.PetFood) return true;
		switch (kind()) {
			case BLUE_DRAGON:
				return item instanceof pd.items.consum.potions.PotionOfFrost
						|| item instanceof pd.plants.Icecap.Seed;
			case GREEN_DRAGON:
				return item instanceof pd.items.consum.scrolls.ScrollOfRecharging
						|| item instanceof pd.items.consum.potions.PotionOfLevitation;
			case LIGHT_DRAGON:
				return item instanceof pd.items.consum.scrolls.ScrollOfRemoveCurse
						|| item instanceof pd.items.consum.potions.PotionOfMindVision;
			case RED_DRAGON:
				return item instanceof pd.items.consum.potions.PotionOfLiquidFlame
						|| item instanceof pd.items.consum.scrolls.ScrollOfRage;
			case SHADOW_DRAGON:
				return item instanceof pd.items.consum.scrolls.ScrollOfTerror
						|| item instanceof pd.items.consum.potions.PotionOfInvisibility;
			case VIOLET_DRAGON:
				return item instanceof pd.items.consum.potions.PotionOfToxicGas
						|| item instanceof pd.items.consum.scrolls.ScrollOfRegrowth;
			case GOLD_DRAGON: case HARO:
				return item instanceof pd.items.consum.food.completefood.CompleteFood;
			case BLUE_GIRL: case SCORPION:
				return item instanceof pd.items.consum.food.meatfood.MeatFood
						|| kind() == Kind.SCORPION && item instanceof pd.items.consum.food.fruit.Fruit;
			case LERY_FIRE:
				return item instanceof pd.items.consum.scrolls.Scroll
						|| item instanceof pd.items.consum.potions.Potion;
			case YEAR:
				return item instanceof pd.items.equipment.weapon.missiles.MoneyPack;
			case BUNNY:
				return item instanceof pd.items.consum.food.vegetable.Vegetable
						|| item instanceof pd.items.consum.food.fruit.Fruit;
			case COCO_CAT:
				return item instanceof pd.items.consum.food.fusion.Nut;
			case VELOCIROOSTER:
				return item instanceof pd.plants.Plant.Seed
						|| item instanceof pd.items.consum.food.vegetable.Vegetable;
			case PIG: case ABI:
				return item instanceof pd.plants.Plant.Seed
						|| item instanceof pd.items.consum.food.vegetable.Vegetable
						|| item instanceof pd.items.consum.food.fruit.Fruit;
			default:
				return false;
		}
	}

	/** The original manually collected pet reward. Ordinary pets override this directly. */
	public Item SupercreateLoot() {
		switch (kind()) {
			case BLUE_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfFreeze(),
						new pd.items.equipment.wands.fusion.WandOfFlow());
			case GREEN_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfLightning(),
						new pd.items.equipment.wands.WandOfTCloud());
			case LIGHT_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfLight(),
						new pd.items.equipment.wands.WandOfCharm());
			case RED_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfFirebolt(),
						new pd.items.equipment.wands.WandOfMeteorite());
			case SHADOW_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfFlock(),
						new pd.items.equipment.wands.fusion.WandOfBlood());
			case VIOLET_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfSwamp(),
						new pd.items.equipment.wands.WandOfAcid());
			case GOLD_DRAGON:
				return Random.oneOf(new pd.items.equipment.wands.WandOfMagicMissile(),
						new pd.items.equipment.wands.WandOfDisintegration());
			case BUG_DRAGON:
				return Random.oneOf(new pd.items.equipment.weapon.missiles.throwing.ErrorAmmo(1),
						new pd.items.equipment.armor.normalarmor.ErrorArmor(),
						new pd.items.equipment.weapon.melee.special.ErrorW(),
						new pd.items.equipment.wands.WandOfError());
			case BLUE_GIRL:
				return new pd.items.equipment.armor.normalarmor.StoneArmor();
			case LERY_FIRE:
				return new pd.items.equipment.weapon.missiles.throwing.BottleFire();
			case SCORPION:
				return new pd.items.consum.potions.PotionOfToxicGas();
			case YEAR:
				return new pd.items.equipment.weapon.missiles.MoneyPack(5);
			case BUNNY:
				return new pd.items.consum.eggs.EasterEgg();
			case COCO_CAT:
				return new pd.items.equipment.bombs.DungeonBomb();
			case VELOCIROOSTER:
				return new pd.items.equipment.weapon.melee.special.SJRBMusic();
			case HARO:
				return new pd.items.consum.scrolls.ScrollOfRecharging();
			case PIG:
				return new pd.items.consum.food.completefood.Honeymeat();
			case ABI:
				return new pd.items.equipment.wands.WandOfLight();
			default:
				return null;
		}
	}

	public int rewardCooldown() { return cooldown; }
	public void restoreRuntimeState(int hp, int savedCooldown) {
		updateStats(false);
		HP = Math.max(1, Math.min(HT, hp));
		cooldown = Math.max(0, savedCooldown);
	}
	public boolean rewardReady() { return cooldown < 4; }

	public Item claimReward() {
		if (!rewardReady()) return null;
		Item reward = SupercreateLoot();
		if (reward != null) cooldown = rewardResetCooldown();
		return reward;
	}

	protected int rewardResetCooldown() {
		if (kind() == Kind.FOX_HELPER) return 135;
		if (kind() == Kind.ABI) return 60;
		if (kind() == Kind.BUNNY || kind() == Kind.COCO_CAT || kind() == Kind.VELOCIROOSTER
				|| kind() == Kind.HARO || kind() == Kind.YEAR) return 10;
		return 30;
	}

	/** @return healed HP, or -1 when this pet refuses the item. */
	public int feed(Item item, Hero hero) {
		if (item == null || hero == null || !lovefood(item)) return -1;
		int healed = (int)((HT - HP) * 0.8f);
		HP = Math.min(HT, HP + healed);
		cooldown /= 2;
		item.detach(hero.belongings.backpack);
		Buff.affect(this, HasteBuff.class, 10f);
		if (sprite != null) sprite.showStatus(CharSprite.POSITIVE, "+" + healed);
		hero.spendAndNext(1f);
		return healed;
	}

	public boolean staying() { return defendingPos == pos && !movingToDefendPos; }
	public void stayHere() {
		defendingPos = pos;
		movingToDefendPos = false;
		aggro(null);
		state = WANDERING;
	}

	public boolean swapPlaces(Hero hero) {
		if (hero == null || Dungeon.level == null || !Dungeon.level.adjacent(pos, hero.pos)) return false;
		int heroPos = hero.pos;
		int petPos = pos;
		if (sprite != null) sprite.move(petPos, heroPos);
		if (hero.sprite != null) hero.sprite.move(heroPos, petPos);
		if (sprite != null) move(heroPos, false);
		else {
			pos = heroPos;
			Dungeon.level.occupyCell(this);
		}
		hero.move(petPos, false);
		float heroSpeed = hero.sprite == null ? 1f : hero.speed();
		hero.spendAndNext(1f / heroSpeed);
		return true;
	}

	@Override
	public boolean interact(Char c) {
		if (c != Dungeon.hero) return super.interact(c);
		if (sprite != null) sprite.turnTo(pos, c.pos);
		GameScene.show(new WndPetInfo(this));
		return true;
	}

	protected int petLevel() {
		return Dungeon.hero == null ? 0 : Dungeon.hero.petLevel;
	}

	public void updateStats(boolean refill) {
		int oldHT = HT;
		int multiplier = kind() == Kind.GOLD_DRAGON ? 8 : kind() == Kind.BUG_DRAGON ? 10 : 5;
		HT = 150 + petLevel() * multiplier;
		defenseSkill = (kind() == Kind.BUG_DRAGON ? 20 : kind() == Kind.GOLD_DRAGON ? 5 : 0) + petLevel();
		if (refill) HP = HT;
		else if (HT > oldHT) HP = Math.min(HT, HP + HT - oldHT);
	}

	@Override
	protected boolean act() {
		updateStats(false);
		if (kind() == Kind.LERY_FIRE && cooldown > 0) cooldown--;
		if (HP < HT) HP = Math.min(HT, HP + petLevel());
		return super.act();
	}

	@Override
	public int damageRoll() {
		int level = petLevel();
		int base = kind() == Kind.BUG_DRAGON ? 15 : kind() == Kind.GOLD_DRAGON ? 10 : 6;
		int scale = kind() == Kind.BUG_DRAGON ? 5 : 4;
		return Random.NormalIntRange(base + level, base + level * scale);
	}

	@Override
	public int attackSkill(Char target) {
		if (kind() == Kind.BUG_DRAGON) return levelRandom(petLevel()) + petLevel();
		return petLevel() + 10;
	}

	private int levelRandom(int level) {
		return level <= 0 ? 0 : Random.Int(level);
	}

	@Override
	public int drRoll() {
		return Random.IntRange(5 + petLevel(), 10 + petLevel() * 3);
	}

	@Override
	public float attackDelay() {
		return kind() == Kind.BLUE_GIRL ? 0.5f : super.attackDelay();
	}

	private boolean rangedDragon() {
		return kind() == Kind.BLUE_DRAGON || kind() == Kind.GREEN_DRAGON
				|| kind() == Kind.LIGHT_DRAGON || kind() == Kind.RED_DRAGON
				|| kind() == Kind.SHADOW_DRAGON || kind() == Kind.VIOLET_DRAGON
				|| kind() == Kind.GOLD_DRAGON;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (rangedDragon()) return Dungeon.level.distance(pos, enemy.pos) <= 2;
		if (kind() == Kind.LERY_FIRE && cooldown <= 0) {
			return new pd.mechanics.Ballistica(
					pos, enemy.pos, pd.mechanics.Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
		return super.canAttack(enemy);
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (kind() == Kind.LERY_FIRE && !Dungeon.level.adjacent(pos, enemy.pos)) {
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap(enemy.pos, () -> { rangedLeryAttack(); next(); });
				return false;
			}
			rangedLeryAttack();
			return true;
		}
		return super.doAttack(enemy);
	}

	private void rangedLeryAttack() {
		spend(TICK);
		cooldown = Math.max(25, 45 - petLevel());
		if (enemy == null || !enemy.isAlive() || !hit(this, enemy, true)) return;
		int roll = Random.Int(5);
		int damage = damageRoll() * (roll == 3 ? 3 : 2);
		enemy.damage(damage, this);
		if (!enemy.isAlive()) return;
		if (roll == 0) Buff.affect(enemy, Frost.class, 10f);
		else if (roll == 1) Buff.affect(enemy, Poison.class).set(petLevel() + 1);
		else if (roll == 2 && petLevel() > 0 && Random.Int(Math.max(1, damage)) < petLevel()) {
			Buff.affect(enemy, Burning.class).reignite(enemy, 4f);
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (kind() == Kind.BUG_DRAGON) {
			enemy.damage(cooldown < 3 ? enemy.HT : damageRoll(), this);
			cooldown = Random.Int(100);
			return 0;
		}
		if (kind() == Kind.BLUE_GIRL) {
			if (cooldown > 0) cooldown--;
			if (cooldown == 0 && enemy.isAlive()) {
				Buff.affect(enemy, Paralysis.class, 2f);
				damage *= 2;
				cooldown = Math.max(20, 40 - petLevel());
			}
			return damage;
		}
		if (kind() == Kind.SCORPION) {
			if (cooldown > 0) cooldown--;
			if (Random.Int(10) == 0 && enemy.isAlive()) Buff.affect(enemy, Ooze.class).set(10f);
			if (cooldown == 0) {
				if (enemy.isAlive()) Buff.affect(enemy, Ooze.class).set(20f);
				HP = Math.min(HT, HP + damage);
				if (Dungeon.hero != null) Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + petLevel());
				damage *= 2;
				cooldown = Math.max(15, 35 - petLevel());
			}
			return damage;
		}
		if (kind() == Kind.LERY_FIRE) return damage;
		if (kind() == Kind.HARO || kind() == Kind.PIG || kind() == Kind.COCO_CAT
				|| kind() == Kind.VELOCIROOSTER || kind() == Kind.BUNNY
				|| kind() == Kind.ABI || kind() == Kind.YEAR || kind() == Kind.BUTTERFLY
				|| kind() == Kind.CHOCOBO || kind() == Kind.DATURA || kind() == Kind.DOG
				|| kind() == Kind.DWARF_BOY || kind() == Kind.FOX_HELPER || kind() == Kind.FROG
				|| kind() == Kind.GENTLE_CRAB || kind() == Kind.KODORA || kind() == Kind.LIT_DEMON
				|| kind() == Kind.MONKEY || kind() == Kind.SNAKE || kind() == Kind.SPIDER
				|| kind() == Kind.STAR_KID || kind() == Kind.STONE || kind() == Kind.FLY
				|| kind() == Kind.RIBBON_RAT) return damage;

		enemy.damage(damageRoll() / 2, elementalSource());
		damage /= 2;
		if (kind() == Kind.GOLD_DRAGON) {
			enemy.damage(damageRoll() / 2, new ElementalDamage(Random.Int(6)));
		}
		if (cooldown > 0) cooldown--;
		if (cooldown == 0 && enemy.isAlive()) {
			applyChargedEffect(enemy);
			cooldown = Math.max(9, 30 - petLevel());
		}
		return damage;
	}

	private Object elementalSource() {
		return new ElementalDamage(kind().ordinal());
	}

	private void applyChargedEffect(Char enemy) {
		switch (kind()) {
			case BLUE_DRAGON: Buff.affect(enemy, Frost.class, 10f); break;
			case GREEN_DRAGON: Buff.affect(enemy, Paralysis.class, 3f); break;
			case LIGHT_DRAGON: Buff.affect(enemy, Paralysis.class, 3f); break;
			case RED_DRAGON: Buff.affect(enemy, Burning.class).reignite(enemy, 10f); break;
			case SHADOW_DRAGON: Buff.affect(enemy, Hex.class, 10f); break;
			case VIOLET_DRAGON: Buff.affect(enemy, Ooze.class).set(petLevel()); break;
			case GOLD_DRAGON: enemy.damage(Math.max(1, enemy.HP / 3), this); break;
		}
	}

	@Override
	public void damage(int damage, Object source) {
		if (source instanceof Hero) return;
		damage = Math.min(damage, Math.max(1, HT / 6));
		super.damage(damage, source);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		if (Dungeon.hero != null) Messages.get(this, "pet_died");
	}

	public static LegacyPet active() {
		if (Dungeon.level == null) return null;
		for (Mob mob : Dungeon.level.mobs()) if (mob instanceof LegacyPet && mob.isAlive()) return (LegacyPet) mob;
		return null;
	}

	public static class ElementalDamage {
		public final int element;
		public ElementalDamage(int element) { this.element = element; }
	}

	private static final String COOLDOWN = "cooldown";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(COOLDOWN, cooldown); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); cooldown = bundle.getInt(COOLDOWN); updateStats(false); }
}
