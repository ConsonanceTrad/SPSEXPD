package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.messages.InlineText;
public class EarthDamage2Trap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(EarthDamage2Trap.class)
			.t("name", "地伤大陷阱")
			.t("desc", "会释放大范围地属性伤害的陷阱。");
	}
 public EarthDamage2Trap(){ super(GREEN, CROSSHAIR, EarthEffectDamage.class, 2, 20); } }
