package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.messages.InlineText;
public class IceDamageTrap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(IceDamageTrap.class)
			.t("name", "冰伤陷阱")
			.t("desc", "会释放冰属性伤害的陷阱。");
	}
 public IceDamageTrap(){ super(TEAL, LARGE_DOT, IceEffectDamage.class, 1, 10); } }
