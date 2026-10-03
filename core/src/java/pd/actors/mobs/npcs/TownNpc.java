/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.items.Amulet;
import pd.items.ChallengeBook;
import pd.items.CrystalVial;
import pd.items.Flag;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.PotKey;
import pd.items.SaveYourLife;
import pd.items.TestCloak;
import pd.items.equipment.armor.fusion.CatSharkArmor;
import pd.items.specific.challengelists.ChallengePageDrops;
import pd.items.specific.challengelists.IceChallenge;
import pd.items.consum.eggs.CocoCatEgg;
import pd.items.consum.eggs.HaroEgg;
import pd.items.consum.food.FireMeat;
import pd.items.consum.food.FishCracker;
import pd.items.consum.food.Honey;
import pd.items.consum.food.completefood.FishPetFood;
import pd.items.consum.food.meatfood.FunnyFood;
import pd.items.specific.journalpages.NewHome;
import pd.items.consum.medicine.LingPotion;
import pd.items.misc.CursePhone;
import pd.items.misc.FishBone;
import pd.items.misc.FourClover;
import pd.items.misc.GhostGirlRose;
import pd.items.misc.RainShield;
import pd.items.misc.SkillOfAtk;
import pd.items.quest.ChallengeJournal;
import pd.items.quest.GnollClothes;
import pd.items.specific.sellitem.Apk931;
import pd.items.specific.sellitem.ApostleBox;
import pd.items.specific.sellitem.BottleFlower;
import pd.items.specific.sellitem.CrossPhoto;
import pd.items.specific.sellitem.DevUpPlan;
import pd.items.specific.sellitem.DwarfHammer;
import pd.items.specific.sellitem.HummingTool;
import pd.items.specific.sellitem.HunterLens;
import pd.items.specific.sellitem.NouthSouth;
import pd.items.specific.sellitem.Simple360;
import pd.items.specific.sellitem.Tissue;
import pd.items.specific.sellitem.UncleDumbbell;
import pd.items.summon.RustybladeCat;
import pd.items.equipment.wands.WandOf13;
import pd.items.equipment.wands.WandOfBlackMeow;
import pd.items.equipment.wands.WandOfShatteredFireblast;
import pd.items.equipment.weapon.melee.special.AFlySock;
import pd.items.equipment.weapon.melee.special.Goei;
import pd.items.equipment.weapon.melee.special.MeleePan;
import pd.items.equipment.weapon.melee.special.Pumpkin;
import pd.items.equipment.weapon.melee.special.XiXiBox;
import pd.items.equipment.weapon.missiles.buildblock.PlantPotBlock;
import pd.items.equipment.weapon.missiles.fusion.TempestBoomerang;
import pd.items.equipment.weapon.missiles.throwing.BottleFire;
import pd.items.equipment.weapon.missiles.throwing.HoneyArrow;
import pd.items.equipment.weapon.missiles.throwing.LynnDoll;
import pd.items.equipment.weapon.missiles.throwing.MoneyBook;
import pd.levels.GroundItems;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.ui.Window;
import render.noosa.Game;
import render.utils.data.Callback;

import java.util.function.Supplier;
import pd.sprites.TownNpcSprite;
import pd.windows.WndAflyInfo;
import pd.windows.WndDream;
import pd.windows.WndEgoalInfo;
import pd.windows.WndGoblin;
import pd.windows.WndHate;
import pd.windows.WndHotel;
import pd.windows.WndIce13;
import pd.windows.WndIssic;
import pd.windows.WndMix;
import pd.windows.WndONS;
import pd.windows.WndQuest;
import pd.windows.WndSaidBySun;
import pd.windows.WndShower;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** Data-backed implementation of the original named residents of Dolya town. */
public class TownNpc extends NPC {

	//SPSXPD: window construction measures text, which must happen on the render thread.
	//Called from the actor thread it throws "Text measured from the actor thread!", so
	//route every popup through here instead of calling the scene directly.
	private static void showWindow(final Supplier<Window> factory){
		Game.runOnRenderThread(new Callback(){
			@Override public void call(){ GameScene.show(factory.get()); }
		});
	}

	public enum Spec {
		UDAWOS("udawos", 14, 16, 2),
		TYPED_SCROLL("typedscroll", 15, 14, 2),
		G2159687("g2159687", 16, 16, 2),
		CONSIDERED_HAMSTER("consideredhamster", 16, 16, 2),
		BILBOLDEV("bilboldev", 16, 14, 3),
		XIXI_ZERO("xixizero", 16, 16, 1),
		MILLILITRE("millilitre", 16, 16, 2),
		NYRDS("nyrds", 16, 14, 2),
		HBB("hbb", 16, 16, 3),
		SFB("sfb", 16, 16, 2),
		FLY_LING("flyling", 16, 16, 3),
		OMICRONRG9("omicronrg9", 16, 16, 6),
		HONEY_POOOOT("honeypoooot", 16, 16, 2),
		JINKELOID("jinkeloid", 16, 16, 2),
		ATV9("atv9", 16, 16, 3),
		SP931("sp931", 16, 16, 2),
		DREAM_PLAYER("dreamplayer", 16, 16, 2),
		EVAN("evan", 16, 14, 2),
		ICE13("ice13", 16, 16, 6),
		HEXA("hexa", 16, 16, 2),
		COCONUT("coconut", 16, 16, 3),
		LOCASTAN("locastan", 16, 16, 3),
		GOBLIN_PLAYER("goblinplayer", 16, 16, 2),
		DACHHACK("dachhack", 16, 16, 3),
		OLD_NEW_STWIST("oldnewstwist", 12, 15, 3),
		HATE_SOKOBAN("hatesokoban", 16, 16, 2),
		LAJI("laji", 16, 16, 3),
		KOSTIS12345("kostis12345", 16, 16, 2),
		APOSTLE("apostle", 16, 16, 2),
		NUT_PAINTER("nutpainter", 16, 16, 2),
		JUH9870("juh9870", 16, 16, 2),
		SAD_SALTAN("sadsaltan", 12, 15, 3),
		SHOWER("shower", 16, 16, 3),
		RENNPC("rennpc", 16, 16, 6),
		OTILUKE_NPC("otilukenpc", 16, 16, 3),
		WATABOU("watabou", 16, 14, 2),
		WHITE_GHOST("whiteghost", 16, 16, 1, 4),
		UNCLE_S("uncles", 16, 16, 1),
		A_REAL_MAN("arealman", 16, 16, 0),
		LYN("lyn", 16, 16, 2),
		SAID_BY_SUN("saidbysun", 16, 16, 2),
		LERY("lery", 12, 14, 2, 21),
		BLACK_MEOW("blackmeow", 16, 17, 2),
		CAT_SHEEP("catsheep", 16, 16, 2),
		FRUIT_CAT("fruitcat", 16, 16, 2),
		MEMORY_OF_SAND("memoryofsand", 16, 16, 2),
		A_FLY("afly", 16, 16, 1),
		BONE_STAR("bonestar", 16, 16, 1),
		STORM_AND_RAIN("stormandrain", 16, 16, 2),
		RAVENWOLF("ravenwolf", 16, 16, 2),
		LYNN("lynn", 16, 16, 3),
		RAIN_TRAINER("raintrainer", 16, 16, 2),
		RUSTYBLADE("rustyblade", 16, 16, 2),
		TEMPEST102("tempest102", 16, 16, 2),
		ALIVE_FISH("alivefish", 16, 16, 2),
		ASH_WOLF("ashwolf", 16, 16, 4),
		COCONUT2("coconut2", 16, 16, 3),
		HMDZL001("hmdzl001", 16, 16, 3),
		NEW_PLAYER("newplayer", 16, 16, 2),
		THANK_LIST("thanklist", 16, 16, 1),
		MAYOR("tinkerer4", 16, 16, 0),
		GEOLOGIST("tinkerer5", 12, 15, 0);

		public final String id;
		public final String asset;
		public final int frameWidth;
		public final int frameHeight;
		public final int lineCount;
		public final int firstFrame;

		Spec(String id, int frameWidth, int frameHeight, int lineCount) {
			this(id, frameWidth, frameHeight, lineCount, 0);
		}

		Spec(String id, int frameWidth, int frameHeight, int lineCount, int firstFrame) {
			this.id = id;
			this.asset = "sprites/npcs/sps_town_" + assetName(id);
			this.frameWidth = frameWidth;
			this.frameHeight = frameHeight;
			this.lineCount = lineCount;
			this.firstFrame = firstFrame;
		}

		private static String assetName(String id) {
			switch (id) {
				case "consideredhamster": return "mimic.png";
				case "xixizero": return "cat_lix.png";
				case "flyling": return "whiteling.png";
				case "nutpainter": return "painter.png";
				case "tinkerer4": return "noodlemire.png";
				case "tinkerer5": return "xavier251998.png";
				case "rennpc": return "ren.png";
				default: return id + ".png";
			}
		}
	}

	private Spec spec = Spec.UDAWOS;
	private boolean first = true;
	private boolean ashWolfNewHomeGiven;

	public static TownNpc create(Spec spec) {
		switch (spec) {
			case UDAWOS: return new Udawos();
			case TYPED_SCROLL: return new TypedScroll();
			case G2159687: return new G2159687();
			case CONSIDERED_HAMSTER: return new ConsideredHamster();
			case BILBOLDEV: return new Bilboldev();
			case XIXI_ZERO: return new XixiZero();
			case MILLILITRE: return new Millilitre();
			case NYRDS: return new NYRDS();
			case HBB: return new HBB();
			case SFB: return new SFB();
			case FLY_LING: return new FlyLing();
			case OMICRONRG9: return new Omicronrg9();
			case HONEY_POOOOT: return new HoneyPoooot();
			case JINKELOID: return new Jinkeloid();
			case ATV9: return new ATV9();
			case SP931: return new SP931();
			case DREAM_PLAYER: return new DreamPlayer();
			case EVAN: return new Evan();
			case ICE13: return new Ice13();
			case HEXA: return new HeXA();
			case COCONUT: return new Coconut();
			case LOCASTAN: return new Locastan();
			case GOBLIN_PLAYER: return new GoblinPlayer();
			case DACHHACK: return new Dachhack();
			case OLD_NEW_STWIST: return new OldNewStwist();
			case HATE_SOKOBAN: return new HateSokoban();
			case LAJI: return new LaJi();
			case KOSTIS12345: return new Kostis12345();
			case APOSTLE: return new Apostle();
			case NUT_PAINTER: return new NutPainter();
			case JUH9870: return new Juh9870();
			case SAD_SALTAN: return new SadSaltan();
			case SHOWER: return new Shower();
			case RENNPC: return new RENnpc();
			case OTILUKE_NPC: return new OtilukeNPC();
			case WATABOU: return new Watabou();
			case WHITE_GHOST: return new WhiteGhost();
			case UNCLE_S: return new UncleS();
			case A_REAL_MAN: return new ARealMan();
			case LYN: return new Lyn();
			case SAID_BY_SUN: return new SaidbySun();
			case LERY: return new Lery();
			case BLACK_MEOW: return new BlackMeow();
			case CAT_SHEEP: return new CatSheep();
			case FRUIT_CAT: return new FruitCat();
			case MEMORY_OF_SAND: return new MemoryOfSand();
			case A_FLY: return new AFly();
			case BONE_STAR: return new BoneStar();
			case STORM_AND_RAIN: return new StormAndRain();
			case RAVENWOLF: return new Ravenwolf();
			case LYNN: return new Lynn();
			case RAIN_TRAINER: return new RainTrainer();
			case RUSTYBLADE: return new Rustyblade();
			case TEMPEST102: return new Tempest102();
			case ALIVE_FISH: return new AliveFish();
			case ASH_WOLF: return new AshWolf();
			case COCONUT2: return new Coconut2();
			case HMDZL001: return new Hmdzl001();
			case NEW_PLAYER: return new NewPlayer();
			case THANK_LIST: return new ThankList();
			case MAYOR: return new Tinkerer4();
			case GEOLOGIST: return new Tinkerer5();
			default: throw new IllegalArgumentException("Unsupported town NPC: " + spec);
		}
	}

	{
		spriteClass = TownNpcSprite.class;
	}

	public TownNpc configure(Spec spec) {
		this.spec = spec;
		properties.clear();
		state = PASSIVE;
		applyLegacyProperties(spec);
		applyLegacyState(spec);
		return this;
	}

	private void applyLegacyProperties(Spec spec) {
		switch (spec) {
			case A_REAL_MAN: case ALIVE_FISH: case BLACK_MEOW: case CAT_SHEEP:
			case FRUIT_CAT: case HATE_SOKOBAN: case HBB: case SAID_BY_SUN: case XIXI_ZERO:
				properties.add(Property.BEAST); break;
			case BILBOLDEV: case COCONUT: case COCONUT2: case EVAN:
				properties.add(Property.MECH); break;
			case DREAM_PLAYER: case HEXA: case JINKELOID: case LERY: case RAIN_TRAINER:
				properties.add(Property.ELEMENT); break;
			case FLY_LING: case HONEY_POOOOT: case LYNN: case RENNPC: case SHOWER:
				properties.add(Property.ELF); break;
			case STORM_AND_RAIN: case WATABOU:
				properties.add(Property.DEMONIC); break;
			case LAJI: case OLD_NEW_STWIST:
				properties.add(Property.ORC); break;
			case ATV9: case BONE_STAR: case ICE13: case MEMORY_OF_SAND: case MILLILITRE:
			case NUT_PAINTER: case OMICRONRG9: case OTILUKE_NPC: case RAVENWOLF:
			case RUSTYBLADE: case SAD_SALTAN: case TEMPEST102: case MAYOR:
			case GEOLOGIST: case UNCLE_S:
				properties.add(Property.HUMAN); break;
			case CONSIDERED_HAMSTER: case G2159687: case KOSTIS12345: case NEW_PLAYER:
			case NYRDS: case SFB: case SP931: case THANK_LIST:
				properties.add(Property.UNKNOW); break;
			case GOBLIN_PLAYER: case JUH9870: case LOCASTAN:
				properties.add(Property.GOBLIN); break;
			case DACHHACK: case HMDZL001:
				properties.add(Property.PLANT); break;
			case UDAWOS:
				properties.add(Property.DWARF); break;
			default:
				break;
		}
		if (spec == Spec.APOSTLE) {
			properties.add(Property.MECH);
			properties.add(Property.ELEMENT);
		} else if (spec == Spec.A_FLY || spec == Spec.LYN) {
			properties.add(Property.ELF);
			properties.add(Property.DEMONIC);
		} else if (spec == Spec.TYPED_SCROLL) {
			properties.add(Property.DEMONIC);
			properties.add(Property.UNKNOW);
		} else if (spec == Spec.ASH_WOLF) {
			properties.add(Property.ORC);
			properties.add(Property.IMMOVABLE);
		} else if (spec == Spec.WHITE_GHOST) {
			properties.add(Property.UNKNOW);
		}
	}

	private void applyLegacyState(Spec spec) {
		switch (spec) {
			case A_FLY: case APOSTLE: case DREAM_PLAYER: case GOBLIN_PLAYER:
			case HONEY_POOOOT: case ICE13: case KOSTIS12345: case LAJI:
			case MILLILITRE: case NEW_PLAYER: case NUT_PAINTER: case OMICRONRG9:
			case RAVENWOLF: case SAD_SALTAN: case SAID_BY_SUN: case SHOWER:
			case SP931: case THANK_LIST: case XIXI_ZERO:
				state = WANDERING; break;
			case OLD_NEW_STWIST:
				state = SLEEPING; break;
			default:
				state = PASSIVE; break;
		}
	}

	public Spec spec() {
		return spec;
	}

	private String key(String suffix) {
		return "actors.mobs.npcs." + spec.id + "." + suffix;
	}

	@Override
	public String name() {
		return Messages.get(key("name"));
	}

	@Override
	public String description() {
		if (spec == Spec.OLD_NEW_STWIST && Dungeon.gnollMission) {
			return Messages.get(key("desc_gnollmission"));
		}
		return Messages.get(key("desc"));
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int damage, Object source) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public pd.items.Item SupercreateLoot() {
		if (spec == Spec.HBB) return new FishCracker();
		if (spec == Spec.SHOWER) return new FishPetFood();
		if (spec == Spec.HMDZL001) {
			return Badges.checkOtilukeRescued() ? new SaveYourLife() : new DevUpPlan();
		}
		if (spec == Spec.SAID_BY_SUN) return new TestCloak();
		if (spec == Spec.G2159687) return new Simple360();
		if (spec == Spec.A_REAL_MAN) return new NouthSouth();
		if (spec == Spec.JINKELOID) return new BottleFlower();
		if (spec == Spec.MEMORY_OF_SAND) return new Tissue();
		if (spec == Spec.MILLILITRE) return new CrossPhoto();
		if (spec == Spec.GOBLIN_PLAYER) return new HummingTool();
		if (spec == Spec.OTILUKE_NPC) return new DwarfHammer();
		if (spec == Spec.SP931) return new Apk931();
		if (spec == Spec.STORM_AND_RAIN) return new HunterLens();
		if (spec == Spec.UNCLE_S) return new UncleDumbbell();
		if (spec == Spec.BILBOLDEV) return new SkillOfAtk();
		if (spec == Spec.DACHHACK) return new PlantPotBlock();
		if (spec == Spec.DREAM_PLAYER) return new FunnyFood();
		if (spec == Spec.FLY_LING) return new LingPotion();
		if (spec == Spec.LAJI) return new CatSharkArmor();
		if (spec == Spec.NUT_PAINTER) return new FourClover();
		if (spec == Spec.TEMPEST102) return new TempestBoomerang();
		if (spec == Spec.APOSTLE) return new ApostleBox();
		if (spec == Spec.ALIVE_FISH) return new FishBone();
		if (spec == Spec.RAIN_TRAINER) return new RainShield();
		if (spec == Spec.RENNPC) return new CursePhone();
		if (spec == Spec.WHITE_GHOST) return new GhostGirlRose();
		if (spec == Spec.A_FLY) return new AFlySock();
		if (spec == Spec.COCONUT2) return new MeleePan();
		if (spec == Spec.XIXI_ZERO) return new XiXiBox();
		if (spec == Spec.FRUIT_CAT) return new MoneyBook(5);
		if (spec == Spec.LERY) return new BottleFire();
		if (spec == Spec.HONEY_POOOOT) return new HoneyArrow(3);
		if (spec == Spec.LYNN) return new LynnDoll();
		if (spec == Spec.RUSTYBLADE) return new RustybladeCat();
		if (spec == Spec.BLACK_MEOW) return new WandOfBlackMeow();
		if (spec == Spec.BONE_STAR) return new CrystalVial();
		if (spec == Spec.ICE13) return new WandOf13();
		if (spec == Spec.SFB) return new WandOfShatteredFireblast();
		if (spec == Spec.ASH_WOLF) return new HaroEgg();
		if (spec == Spec.COCONUT) return new CocoCatEgg();
		if (spec == Spec.NEW_PLAYER) return new DevUpPlan();
		if (spec == Spec.OLD_NEW_STWIST) return Generator.random(Generator.Category.EASTERWEAPON);
		return super.SupercreateLoot();
	}

	@Override
	public boolean interact(Char ch) {
		if (ch != Dungeon.hero) return super.interact(ch);
		sprite.turnTo(pos, ch.pos);
		if (spec == Spec.MAYOR) {
			showWindow(() -> new WndQuest(this, Messages.get(key(first ? "tell1" : "tell2"))));
			first = false;
		} else if (spec == Spec.GEOLOGIST) {
			if (first) {
				showWindow(() -> new WndQuest(this, Messages.get(key("tell3"))));
				Dungeon.level.drop(new FireMeat(), Dungeon.hero.pos).sprite.drop();
				first = false;
			} else {
				showWindow(() -> new WndQuest(this, Messages.get(key(Random.Int(2) == 0 ? "tell1" : "tell2"))));
			}
		} else if (spec == Spec.RENNPC) {
			if (first) {
				if (Dungeon.hero.belongings.getItem(ChallengeJournal.class) == null) {
					dropAtHero(new ChallengeBook());
				}
				yell(Messages.get(key("yell3")));
				first = false;
			} else if (renRewardReady()) {
				Dungeon.LimitedDrops.SPS_GOEI.drop();
				pd.items.Heap heap = Dungeon.level.drop(new Goei(), Dungeon.hero.pos);
				if (heap.sprite != null) heap.sprite.drop();
				yell(Messages.get(key("yell5")));
			} else {
				int[] lines = {1, 2, 4, 6};
				yell(Messages.get(key("yell" + lines[Random.Int(lines.length)])));
			}
		} else if (spec == Spec.JINKELOID) {
			if (Statistics.gnollArchersKilled >= 100 && Statistics.mossySkeletonsKilled >= 100
					&& Statistics.albinoPiranhasKilled >= 100 && Statistics.goldThievesKilled >= 100) {
				yell(Messages.get(key("yell3")));
				ChallengePageDrops.offer(new IceChallenge(), Dungeon.hero.pos);
			} else {
				yell(Messages.get(key("yell" + Random.IntRange(1, 2))));
			}
		} else if (spec == Spec.OTILUKE_NPC) {
			if (first) {
				Dungeon.level.drop(new Amulet(), Dungeon.hero.pos).sprite.drop();
				first = false;
			}
			yell(Messages.get(key("yell" + Random.IntRange(1, 3))));
		} else if (spec == Spec.HMDZL001) {
			yell(Messages.get(key("yell" + Random.IntRange(1, 3))));
			destroy();
			if (sprite != null) sprite.die();
		} else if (spec == Spec.THANK_LIST) {
			int index = Random.Int(THANKS.length);
			if (index == 46) yell(Messages.get(key("yell1")));
			else if (THANKS[index] != null) yell(THANKS[index]);
		} else if (spec == Spec.A_REAL_MAN) {
			showWindow(() -> new WndMix());
		} else if (spec == Spec.A_FLY) {
			if (Random.Int(2) == 0) yell(Messages.get(key("yell1")));
			else showWindow(() -> new WndAflyInfo());
		} else if (spec == Spec.SAID_BY_SUN) {
			switch (Random.Int(3)) {
				case 0: yell(Messages.get(key("yell1"))); break;
				case 1: yell(Messages.get(key("yell2"))); break;
				default:
					if (Badges.checkOtilukeRescued()) showWindow(() -> new WndSaidBySun());
					break;
			}
		} else if (spec == Spec.DREAM_PLAYER) {
			switch (Random.Int(3)) {
				case 0: yell(Messages.get(key("yell1"))); break;
				case 1: yell(Messages.get(key("yell2"))); break;
				default:
					if (Badges.checkOtilukeRescued()) showWindow(() -> new WndDream());
					break;
			}
		} else if (spec == Spec.XIXI_ZERO) {
			if (Random.Int(2) == 0) yell(Messages.get(key("yell1")));
			else showWindow(() -> new WndEgoalInfo());
		} else if (spec == Spec.HATE_SOKOBAN) {
			switch (Random.Int(3)) {
				case 0: yell(Messages.get(key("yell1"))); break;
				case 1: yell(Messages.get(key("yell2"))); break;
				default:
					if (Badges.checkOtilukeRescued()) showWindow(() -> new WndHate());
					break;
			}
		} else if (spec == Spec.MILLILITRE) {
			switch (Random.Int(3)) {
				case 0: yell(Messages.get(key("yell1"))); break;
				case 1: yell(Messages.get(key("yell2"))); break;
				default:
					if (Badges.checkOtilukeRescued()) showWindow(() -> new WndIssic());
					break;
			}
		} else if (spec == Spec.G2159687) {
			switch (Random.Int(3)) {
				case 0: yell(Messages.get(key("yell1"))); break;
				case 1: yell(Messages.get(key("yell2"))); break;
				default: showWindow(() -> new WndHotel()); break;
			}
		} else if (spec == Spec.HONEY_POOOOT) {
			switch (Random.Int(3)) {
				case 0:
					yell(Messages.get(key("yell1")));
					break;
				case 1:
					yell(Messages.get(key("yell2")));
					break;
				default:
					if (Badges.checkOtilukeRescued() && !Statistics.potKeyClaimed) {
						Dungeon.level.drop(new PotKey(), Dungeon.hero.pos).sprite.drop();
						Statistics.potKeyClaimed = true;
					}
					break;
			}
		} else if (spec == Spec.LYNN) {
			yell(Messages.get(key("yell" + Random.IntRange(1, 2))));
			if (applyLynnPenalty(Dungeon.hero)) {
				yell(Messages.get(key("yell3")));
			}
		} else if (spec == Spec.ICE13) {
			int line = Random.Int(7);
			if (line < 6) {
				yell(Messages.get(key("yell" + (line + 1))));
			} else if (Badges.checkOtilukeRescued()) {
				showWindow(() -> new WndIce13());
			}
		} else if (spec == Spec.OLD_NEW_STWIST) {
			GnollClothes clothes = Dungeon.hero.belongings.getItem(GnollClothes.class);
			if (!Dungeon.gnollMission && first) {
				yell(Messages.get(key("yell1")));
				yell(Messages.get(key("yell2")));
				first = false;
			} else if (!Dungeon.gnollMission && clothes == null) {
				yell(Messages.get(key("yell3")));
			} else if (!Dungeon.gnollMission) {
				yell(Messages.get(key("yell4")));
				showWindow(() -> new WndONS(clothes));
			} else {
				yell(Messages.get(key(first ? "yell6" : "yell5")));
				first = false;
			}
		} else if (spec == Spec.GOBLIN_PLAYER) {
			int result = Random.Int(3);
			if (result < 2) yell(Messages.get(key("yell" + (result + 1))));
			else if (Badges.checkOtilukeRescued()) showWindow(() -> new WndGoblin());
		} else if (spec == Spec.SHOWER) {
			int result = Random.Int(4);
			if (result < 3) yell(Messages.get(key("yell" + (result + 1))));
			else if (Badges.checkOtilukeRescued()) showWindow(() -> new WndShower());
		} else if (spec == Spec.HBB) {
			int result = Random.Int(4);
			if (result < 3) {
				yell(Messages.get(key("yell" + (result + 1))));
			} else {
				Item reward = hbbReward(result, Badges.checkOtilukeRescued());
				if (reward != null) dropAtHero(reward);
			}
		} else if (spec == Spec.ASH_WOLF) {
			Plant.Seed seed = takeFirstAshWolfSeed();
			if (seed != null) {
				yell(Messages.get(key("yell1")));
				if (Dungeon.level != null) GroundItems.plant( Dungeon.level, seed, Dungeon.hero.pos);
			} else {
				yell(Messages.get(key("yell2")));
			}
			if (consumeAshWolfPumpkins(Dungeon.hero) > 0) {
				yell(Messages.get(key("yell3")));
				dropAtHero(new Honey());
			}
			if (takeAshWolfNewHomeOffer()) {
				yell(Messages.get(key("yell4")));
				dropAtHero(new NewHome());
			}
		} else if (spec.lineCount > 0) {
			yell(Messages.get(key("yell" + Random.IntRange(1, spec.lineCount))));
		}
		return true;
	}

	Item hbbReward(int result, boolean otilukeRescued) {
		return spec == Spec.HBB && result == 3 && otilukeRescued ? new Flag() : null;
	}

	private boolean renRewardReady() {
		return spec == Spec.RENNPC && !Dungeon.LimitedDrops.SPS_GOEI.dropped()
				&& Statistics.gnollArchersKilled > 50 && Statistics.mossySkeletonsKilled > 50
				&& Statistics.albinoPiranhasKilled > 50 && Statistics.goldThievesKilled > 50;
	}

	Plant.Seed takeFirstAshWolfSeed() {
		if (spec != Spec.ASH_WOLF || !first) return null;
		first = false;
		return (Plant.Seed)Generator.random(Generator.Category.SEED4);
	}

	int consumeAshWolfPumpkins(pd.actors.hero.Hero hero) {
		if (spec != Spec.ASH_WOLF || hero == null) return 0;
		java.util.ArrayList<Pumpkin> pumpkins = hero.belongings.getAllItems(Pumpkin.class);
		for (Pumpkin pumpkin : pumpkins) {
			if (hero.belongings.weapon == pumpkin) hero.belongings.weapon = null;
			else if (hero.belongings.secondWep == pumpkin) hero.belongings.secondWep = null;
			else pumpkin.detachAll(hero.belongings.backpack);
		}
		Item.updateQuickslot();
		return pumpkins.size();
	}

	boolean takeAshWolfNewHomeOffer() {
		if (spec != Spec.ASH_WOLF || ashWolfNewHomeGiven) return false;
		if (Statistics.deepestFloor <= 24 && !Dungeon.isChallenged(Challenges.TEST_TIME)) return false;
		ashWolfNewHomeGiven = true;
		return true;
	}

	private void dropAtHero(Item item) {
		if (Dungeon.level == null || Dungeon.hero == null) return;
		Heap heap = Dungeon.level.drop(item, Dungeon.hero.pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
	}

	public boolean applyLynnPenalty(pd.actors.hero.Hero hero) {
		if (spec != Spec.LYNN || hero == null) return false;
		LynnDoll doll = hero.belongings.getItem(LynnDoll.class);
		if (doll == null) return false;
		Buff.prolong(hero, HolyStun.class, 20f);
		hero.HP /= 3;
		doll.detachAll(hero.belongings.backpack);
		return true;
	}

	private static final String[] THANKS = {
			"SuperSaiyan99", "Noodlemire", null, "猫佑薄荷", "RavenWolf", "Watabou",
			"Bilboldev", "ConsideredHamster", "Dachhack", "HeXA", "Juh9870", "Locastan",
			"NYRDS", "OldNewStwist", "SadSaltan", "Typedscroll", "Leppan", "Sharku2011",
			"老雷霆", "qi", "ian949", null, "RaiseDead", "指虎教徒", null, "白幽妹", "章华",
			"Hmorow12", "Eccentric_eye", "Ropuszka", "TrashboxBobylev", "Q1a2q1a2",
			"QueenOfTheMeowntain", "Buue2", "lighthouse64", "najecniv20", "Torian99",
			"deeperbroken", "DGN1", "Eldskutf", null, "SkyMuffin", "DragosCat1",
			"BlankDriver", "A神", "和缺月拉钩丶", null
	};

	private static final String SPEC = "town_npc_spec";
	private static final String FIRST = "town_npc_first";
	private static final String ASH_WOLF_NEW_HOME = "ash_wolf_new_home";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPEC, spec.name());
		bundle.put(FIRST, first);
		bundle.put(ASH_WOLF_NEW_HOME, ashWolfNewHomeGiven);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		if (bundle.contains(SPEC)) configure(Spec.valueOf(bundle.getString(SPEC)));
		super.restoreFromBundle(bundle);
		first = !bundle.contains(FIRST) || bundle.getBoolean(FIRST);
		ashWolfNewHomeGiven = bundle.getBoolean(ASH_WOLF_NEW_HOME);
	}
}
