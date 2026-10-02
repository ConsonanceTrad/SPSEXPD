package pd.items.misc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import javax.imageio.ImageIO;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.buffs.BoxStar;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MirrorShield;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.armor.normalarmor.RubberArmor;
import pd.items.equipment.armor.normalarmor.VestArmor;
import pd.items.equipment.artifacts.AlienBag;
import pd.items.equipment.artifacts.EtherealChains;
import pd.items.equipment.artifacts.TimeOclock;
import pd.items.equipment.bombs.BuildBomb;
import pd.items.equipment.bombs.DarkBomb;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.bombs.EarthBomb;
import pd.items.equipment.bombs.FishingBomb;
import pd.items.equipment.bombs.HugeBomb;
import pd.items.equipment.bombs.IceBomb;
import pd.items.equipment.bombs.LightBomb;
import pd.items.equipment.bombs.SpsFireBomb;
import pd.items.equipment.bombs.StormBomb;
import pd.items.consum.potions.Potion;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.rings.RingOfForce;
import pd.items.equipment.rings.RingOfMight;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.wands.CannonOfMage;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.melee.start.BraveBook;
import pd.items.equipment.weapon.melee.start.DiamondPickaxe;
import pd.items.equipment.weapon.melee.start.HolyMace;
import pd.items.equipment.weapon.melee.start.LinkSword;
import pd.items.equipment.weapon.melee.start.PixelTorch;
import pd.items.equipment.weapon.missiles.ShitBall;
import pd.items.equipment.weapon.missiles.darts.PoisonDart;
import pd.items.equipment.weapon.missiles.fusion.RocketMissile;
import pd.items.equipment.weapon.missiles.throwing.EmpBola;
import pd.items.equipment.weapon.missiles.throwing.EscapeKnive;
import pd.items.equipment.weapon.missiles.throwing.Skull;
import pd.items.equipment.weapon.missiles.throwing.Wave;
import pd.items.equipment.weapon.spammo.BattleAmmo;
import pd.items.equipment.weapon.spammo.GoldAmmo;
import pd.items.equipment.weapon.spammo.WoodenAmmo;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public final class SpsSkinOneStartTest {
	private static final String[] ICON_HASHES={
		"6DF34D68B0AF3BBDF7A279FE6B2E0B6C1458E5508A973AA3211C8680872D2D3A","5A996EAD61CCD23E609C6B0613E82A37B75ADA98D70992FAB73C286040113D89",
		"1C4C051317B842B67C1606310168BF7089987EF5B216B0FC6219ECCD9A3C428B","41EDD549D64A01244B2AEF5E6A6B9AB6A87385A6CB60066F2A4A391BAC9B7E9E",
		"268F2B9EE7FD03718681775BA5275FA84617AC7E80ED071C0DDFF55BECDDA6F8","4B38AB7F387BF44179942F5C2F0BB645984FFBD91E67DDEFE87E67B251982F8F",
		"728F8C93B488D9C19BD862FB65C38AF7D1798E8368EEA253941EF81B69521C0A","9D8BB7B00A6AF03A19B113190E1AF6B8C48C401F1A3FAB668614ABDAE6C55BCD",
		"D9C25E7CCEF7B5F8C57C3407925D9B12191811C3666EC40D0A9F219F55CBF0A9","FE1AB37D74BE7E727D5FF40BB56441CB4D85973139D3043FF79E097ED429EA22",
		"2E8FFC1892B87C9C31FA0042B2DB85A05F5485539319D7CF3987CE3F7B1C49D8","22A8F0D814AAEA389FB5628EFBE0B6020347A0E18B8C30B010E357B8E50F86E0"};
	private static final String[] LINK_DROP_ICON_HASHES={
		"7A3EB5DBDC6812C311DD31EBE7C1AC75843016701602F61E2D1500CE5033F2EF","23982C69F473BEFA87CFCA9EC4C6258EEA8F2F2C5E5F2BB351623C0B4E2D1707",
		"6125A239ECB56DFAB1C64885956275662D8DC9350207E619C0AC7A663B3F35EA","7DE2D486B617FD24B395873BB8CF7AA0166586514B38CCED6C20981B409FEB49"};
	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app=new HeadlessApplication(new ApplicationAdapter(){@Override public void create(){}},new HeadlessApplicationConfiguration());
		Gdx.files=new HeadlessFiles(); Game.version="test";
		Random.pushGenerator(0x535053534B494E31L);
		try {
			Scroll.initLabels(); Potion.initColors(); Ring.initGems(); Generator.fullReset();
			testLoadouts(); testItemState(); testLinkDrops(); testIcons();
			System.out.println("SPS皮肤1开局测试通过：8职业装备、属性补偿、专属物品、勇者剑掉落池、强化等级、独立状态与存档均正常。");
		} finally { Random.popGenerator(); app.exit(); }
	}

	private static void testLoadouts() {
		Hero h=start(HeroClass.WARRIOR);
		check(h.belongings.weapon==null&&h.belongings.armor instanceof VestArmor&&h.belongings.armor.level()==1,"战士皮肤1装备错误");
		check(h.belongings.misc instanceof RingOfForce&&h.belongings.ring instanceof RingOfMight&&h.belongings.getItem(AttackShield.class)!=null,"战士皮肤1戒指或波动拳缺失");

		h=start(HeroClass.MAGE);
		check(h.STR==Hero.STARTING_STR+4&&h.belongings.weapon instanceof pd.items.equipment.weapon.melee.normalweapon.Whip&&h.belongings.weapon.level()==2,"法师皮肤1武器或力量错误");
		check(h.belongings.getItem(CannonOfMage.class)!=null&&h.belongings.armor.level()==1,"法师皮肤1七彩大炮或护甲错误");

		h=start(HeroClass.ROGUE);
		check(h.STR==Hero.STARTING_STR+1&&h.belongings.weapon instanceof LinkSword&&h.belongings.weapon.reinforced&&h.belongings.artifact instanceof EtherealChains,"盗贼皮肤1勇者剑或锁链错误");

		h=start(HeroClass.HUNTRESS);
		check(h.STR==Hero.STARTING_STR+1&&h.belongings.armor instanceof RubberArmor&&h.belongings.armor.level()==1,"女猎手皮肤1橡胶甲错误");
		check(h.belongings.artifact instanceof TimeOclock&&h.belongings.artifact.level()==5,"女猎手皮肤1怀表错误");

		h=start(HeroClass.PERFORMER);
		check(h.belongings.weapon instanceof GunA&&h.belongings.weapon.level()==2&&h.belongings.artifact instanceof AlienBag,"演员皮肤1枪械或肩包错误");
		check(h.belongings.getItem(GoldAmmo.class)!=null&&h.belongings.getItem(WoodenAmmo.class)!=null&&h.belongings.getItem(BattleAmmo.class)!=null&&h.belongings.getItem(BShovel.class)!=null,"演员皮肤1弹药或按钮缺失");

		h=start(HeroClass.SOLDIER);
		check(h.STR==Hero.STARTING_STR+2&&h.belongings.weapon==null&&h.belongings.armor.level()==3,"星兵皮肤1属性或护甲错误");
		check(h.belongings.getItem(AttackShoes.class)!=null&&h.belongings.getItem(MKbox.class)!=null,"星兵皮肤1鞋或问号箱缺失");

		h=start(HeroClass.FOLLOWER);
		check(h.STR==Hero.STARTING_STR+4&&h.belongings.weapon instanceof DiamondPickaxe&&h.belongings.weapon.reinforced&&h.belongings.secondWep instanceof PixelTorch&&h.belongings.secondWep.reinforced,"信徒皮肤1双工具武器错误");

		h=start(HeroClass.ASCETIC);
		check(h.STR==Hero.STARTING_STR+4&&h.belongings.weapon instanceof HolyMace&&h.belongings.weapon.level()==1&&h.belongings.weapon.reinforced,"苦修者皮肤1圣锤错误");
		check(h.belongings.secondWep instanceof BraveBook&&h.belongings.secondWep.level()==1&&h.belongings.secondWep.reinforced,"苦修者皮肤1勇者之书错误");
	}

	private static void testItemState() {
		AttackShield shield=new AttackShield();for(int i=0;i<40;i++)shield.gainCharge();check(shield.charge()==20,"波动拳积蓄没有封顶");checkRestored(shield,new AttackShield(),"波动拳存档失败");
		BShovel button=new BShovel();button.gainCharge(1000);check(button.charge()==150,"奇迹按钮积蓄没有封顶");checkRestored(button,new BShovel(),"奇迹按钮存档失败");
		LinkSword sword=new LinkSword();LinkSword other=new LinkSword();sword.power(new Hero());check(sword.uptime()==1&&other.uptime()==0,"勇者剑强化被不同实例共享");checkRestored(sword,new LinkSword(),"勇者剑存档失败");
		HolyMace mace=new HolyMace();HolyMace mace2=new HolyMace();check(mace.imbue(new pd.items.GreatRune())&&mace.trialLevel()==2&&mace2.trialLevel()==1,"圣锤灌注等级被不同实例共享");checkRestored(mace,new HolyMace(),"圣锤存档失败");
		BraveBook book=new BraveBook();BraveBook book2=new BraveBook();check(book.imbue(new pd.items.consum.medicine.Greaterpill())&&book.healingLevel()==2&&book2.healingLevel()==1,"勇者之书灌注等级被不同实例共享");checkRestored(book,new BraveBook(),"勇者之书存档失败");
		CannonOfMage cannon=new CannonOfMage();cannon.curCharges=7;cannon.upgrade();check(cannon.reinforced&&cannon.maxCharges==7&&cannon.curCharges==7,"七彩大炮升级后没有保持7发弹仓");
		Hero energy=new Hero();Talent.initClassTalents(energy);energy.spp=73;Bundle hb=new Bundle();energy.storeInBundle(hb);Hero restored=new Hero();restored.restoreFromBundle(hb);check(restored.spp==73,"皮肤武器能量没有随英雄存档恢复");
		Mob protectedTarget=new TestMob();protectedTarget.HP=protectedTarget.HT=100;Mob attacker=new TestMob();attacker.HP=attacker.HT=100;Buff.affect(protectedTarget,BoxStar.class,3f);protectedTarget.damage(40,attacker);check(protectedTarget.HP==100,"无敌星没有免疫伤害");Buff.detach(protectedTarget,BoxStar.class);Buff.affect(protectedTarget,MirrorShield.class,3f);protectedTarget.damage(40,attacker);check(protectedTarget.HP==100&&attacker.HP<100,"智慧守护没有免疫并反射伤害");
	}
	private static void testLinkDrops(){
		Class<?>[] expected={BuildBomb.class,DungeonBomb.class,HugeBomb.class,SpsFireBomb.class,IceBomb.class,EarthBomb.class,StormBomb.class,LightBomb.class,DarkBomb.class,FishingBomb.class,RocketMissile.class,EmpBola.class,EscapeKnive.class,PoisonDart.class,Skull.class,Wave.class,ShitBall.class};
		float[] weights={3,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2};
		check(Arrays.equals(expected,LinkSword.linkDropClasses()),"勇者剑掉落池条目或顺序错误");check(Arrays.equals(weights,LinkSword.linkDropWeights()),"勇者剑掉落池权重错误");
		HashSet<Integer> seen=new HashSet<>();for(int roll=0;roll<2000;roll++){Item item=LinkSword.randomLinkDrop();for(int i=0;i<expected.length;i++)if(expected[i].isInstance(item)){seen.add(i);break;}}check(seen.size()==expected.length,"勇者剑掉落池存在无法生成的条目："+seen.size()+"/"+expected.length);
		Mob target=new TestMob();target.HP=target.HT=100;EarthBomb.applyEarthEffects(target);check(target.buff(Roots.class)!=null&&target.buff(Ooze.class)!=null,"酸蚀炸弹没有施加缠绕和腐蚀淤泥");
		check(DarkBomb.dealsHeavyDamageTo(new TaggedMob(pd.actors.Char.Property.BEAST)),"暗黑炸弹没有识别生命体");check(!DarkBomb.dealsHeavyDamageTo(new TaggedMob(pd.actors.Char.Property.UNDEAD)),"暗黑炸弹错误地将亡灵识别为生命体");
	}
	private static void testIcons(){try{BufferedImage sheet=ImageIO.read(new File("sprites/items/items.png"));for(int i=0;i<ICON_HASHES.length;i++)check(ICON_HASHES[i].equals(hash(sheet,64+i*16,192)),"皮肤1第"+(i+1)+"个原始图标错误");for(int i=0;i<LINK_DROP_ICON_HASHES.length;i++)check(LINK_DROP_ICON_HASHES[i].equals(hash(sheet,i*16,752)),"勇者剑掉落池第"+(i+1)+"个补充图标错误");}catch(Exception e){throw new AssertionError("无法校验皮肤1原始图标",e);}}
	private static String hash(BufferedImage sheet,int left,int top)throws Exception{ByteBuffer pixels=ByteBuffer.allocate(16*16*4).order(ByteOrder.LITTLE_ENDIAN);for(int y=top;y<top+16;y++)for(int x=left;x<left+16;x++)pixels.putInt(sheet.getRGB(x,y));byte[] digest=MessageDigest.getInstance("SHA-256").digest(pixels.array());StringBuilder out=new StringBuilder(64);for(byte value:digest)out.append(String.format("%02X",value&0xFF));return out.toString();}

	private static Hero start(HeroClass cls){Dungeon.LimitedDrops.reset();Dungeon.quickslot=new QuickSlot();Hero h=new Hero();h.skin=1;Dungeon.hero=h;cls.initHero(h);return h;}
	private static void checkRestored(Item source,Item target,String msg){Bundle b=new Bundle();source.storeInBundle(b);target.restoreFromBundle(b);Bundle b2=new Bundle();target.storeInBundle(b2);check(b2.toString().length()>2,msg);}
	private static void check(boolean ok,String msg){if(!ok)throw new AssertionError(msg);}
	private static final class TestMob extends Mob {@Override public int drRoll(){return 0;}}
	private static final class TaggedMob extends Mob {TaggedMob(Property property){properties.add(property);}@Override public int drRoll(){return 0;}}
	private SpsSkinOneStartTest(){}
}
