package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.ShockEffectDamage;
import pd.messages.InlineText;
public class ShockDamage2Trap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ShockDamage2Trap.class)
			.t("name", "雷伤大陷阱")
			.t("desc", "会释放大范围雷属性伤害的陷阱。");
	}


 public ShockDamage2Trap(){ super(YELLOW, CROSSHAIR, ShockEffectDamage.class, 2, 20); } }
