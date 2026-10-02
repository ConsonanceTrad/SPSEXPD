package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.messages.InlineText;
public class FireDamage2Trap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(FireDamage2Trap.class)
			.t("name", "火伤大陷阱")
			.t("desc", "会释放大范围火属性伤害的陷阱。");
	}


 public FireDamage2Trap(){ super(ORANGE, CROSSHAIR, FireEffectDamage.class, 2, 20); } }
