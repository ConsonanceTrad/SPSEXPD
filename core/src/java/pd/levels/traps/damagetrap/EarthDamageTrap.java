package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.messages.InlineText;
public class EarthDamageTrap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(EarthDamageTrap.class)
			.t("name", "地伤陷阱")
			.t("desc", "会释放地属性伤害的陷阱。");
	}
 public EarthDamageTrap(){ super(GREEN, LARGE_DOT, EarthEffectDamage.class, 1, 10); } }
