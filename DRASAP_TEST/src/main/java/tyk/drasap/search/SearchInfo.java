package tyk.drasap.search;

import java.util.HashMap;

/**
 * õÉÖµÄÌîñðÛ·éNXB
 */
public class SearchInfo {
	static HashMap<String, String> attrMap = new HashMap<String, String>();
	static {
		attrMap.put("CREATE_DATE", "ì¬ú");
		attrMap.put("CREATE_USER", "ì¬Ò");
		attrMap.put("MACHINE_JP", "u¼Ì(a)");
		attrMap.put("MACHINE_EN", "u¼Ì(p)");
		attrMap.put("USED_FOR", "pr");
		attrMap.put("MATERIAL", "Þ¿");
		attrMap.put("TREATMENT", "ME\Ê");
		attrMap.put("PROCUREMENT", "²Bæª");
		attrMap.put("SUPPLYER_JP", "[J[¼(a)");
		attrMap.put("SUPPLYER_EN", "[J[¼(p)");
		attrMap.put("SUPPLYER_TYPE", "[J[`®");
		attrMap.put("ATTACH01", "Yt}Ô1");
		attrMap.put("ATTACH02", "Yt}Ô2");
		attrMap.put("ATTACH03", "Yt}Ô3");
		attrMap.put("ATTACH04", "Yt}Ô4");
		attrMap.put("ATTACH05", "Yt}Ô5");
		attrMap.put("ATTACH06", "Yt}Ô6");
		attrMap.put("ATTACH07", "Yt}Ô7");
		attrMap.put("ATTACH08", "Yt}Ô8");
		attrMap.put("ATTACH09", "Yt}Ô9");
		attrMap.put("ATTACH10", "Yt}Ô10");
		attrMap.put("MACHINE_NO", "uNO");
		attrMap.put("MACHINE_NAME", "@í¼Ì");
		attrMap.put("MACHINE_SPEC1", "udl1");
		attrMap.put("MACHINE_SPEC2", "udl2");
		attrMap.put("MACHINE_SPEC3", "udl3");
		attrMap.put("MACHINE_SPEC4", "udl4");
		attrMap.put("MACHINE_SPEC5", "udl5");
		attrMap.put("DRWG_TYPE", "}ÊíÞ");
		attrMap.put("DRWG_SIZE", "}ÊTCY");
		attrMap.put("ISSUE", "ñoæª");
		attrMap.put("SUPPLY", "ÁÕæª");
		attrMap.put("CAD_TYPE", "CADíÊ");
		attrMap.put("ENGINEER", "ÝvÒ¼");
		attrMap.put("PROHIBIT", "gpÖ~æª");
		attrMap.put("PROHIBIT_DATE", "gpÖ~ú");
		attrMap.put("PROHIBIT_EMPNO", "gpÖ~ÒEÔ");
		attrMap.put("PROHIBIT_NAME", "gpÖ~Ò¼O");
		attrMap.put("PAGES", "y[W");
		attrMap.put("ACL_ID", "ANZXx");
		attrMap.put("ACL_UPDATE", "±¸¾½ÚÍÞÙÏXú");
		attrMap.put("ACL_EMPNO", "±¸¾½ÚÍÞÙÏXÒEÔ");
		attrMap.put("ACL_NAME", "±¸¾½ÚÍÞÙÏXÒ¼");
		attrMap.put("ATTACH_MAX", "Yt}");
		attrMap.put("LATEST_FLAG", "ÅV}Ôæª");
		attrMap.put("REPLACE_FLAG", "·ÖtO");
		attrMap.put("CREATE_DIV", "ì¬R[h");
		attrMap.put("MEDIA_ID", "fBAID");
		attrMap.put("TWIN_DRWG_NO", "1¨2iÔ}Ô");
	}

	/**
	 * @return
	 */
	public static HashMap<String, String> getAttrMap() {
		return attrMap;
	}

}
