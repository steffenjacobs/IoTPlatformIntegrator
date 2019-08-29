package me.steffenjacobs.extern.babelnetconnector;

/** @author Steffen Jacobs */
public enum BabelLanguage {
	ENGLISH("EN", "English"), //
	AFRIKAANS("AF", "Afrikaans"), ALBANIAN("SQ", "Albanian"), ARABIC("AR", "Arabic"), ARMENIAN("HY", "Armenian"), AZERBAIJANI("AZ", "Azerbaijani"), //
	BASQUE("EU", "Basque"), BENGALI("BN", "Bengali"), BULGARIAN("BG", "Bulgarian"), CATALAN("CA", "Catalan"), CHINESE("ZH", "Chinese"), //
	CROATIAN("HR", "Croatian"), CZECH("CS", "Czech"), DANISH("DA", "Danish"), DUTCH("NL", "Dutch"), ESPERANTO("EO", "Esperanto"), //
	ESTONIAN("ET", "Estonian"), FINNISH("FI", "Finnish"), FRENCH("FR", "French"), GALICIAN("GL", "Galician"), GEORGIAN("KA", "Georgian"), //
	GERMAN("DE", "German"), GREEK("EL", "Greek"), HEBREW("HE", "Hebrew"), HINDI("HI", "Hindi"), HUNGARIAN("HU", "Hungarian"), //
	ICELANDIC("IS", "Icelandic"), INDONESIAN("ID", "Indonesian"), IRISH("GA", "Irish"), ITALIAN("IT", "Italian"), JAPANESE("JA", "Japanese"), //
	KAZAKH("KK", "Kazakh"), KOREAN("KO", "Korean"), LATIN("LA", "Latin"), LATVIAN("LV", "Latvian"), LITHUANIAN("LT", "Lithuanian"), //
	MALAY("MS", "Malay"), MALTESE("MT", "Maltese"), NORWEGIAN_BOKMÅL("NO", "Norwegian_Bokmål"), PERSIAN("FA", "Persian"), POLISH("PL", "Polish"), //
	PORTUGUESE("PT", "Portuguese"), ROMANIAN("RO", "Romanian"), RUSSIAN("RU", "Russian"), SERBIAN("SR", "Serbian"), SIMPLE_ENGLISH("SIMPLE", "Simple_English"), //
	SLOVAK("SK", "Slovak"), SLOVENIAN("SL", "Slovenian"), SPANISH("ES", "Spanish"), SWAHILI("SW", "Swahili"), SWEDISH("SV", "Swedish"), //
	TAGALOG("TL", "Tagalog"), TAMIL("TA", "Tamil"), THAI("TH", "Thai"), TIBETAN("BO", "Tibetan"), TURKISH("TR", "Turkish"), //
	UKRANIAN("UK", "Ukranian"), URDU("UR", "Urdu"), VIETNAMESE("VI", "Vietnamese"), WELSH("CY", "Welsh"), WARAY_WARAY("WAR", "Waray_Waray"), //
	CEBUANO("CEB", "Cebuano"), MINANGKABAU("MIN", "Minangkabau"), UZBEK("UZ", "Uzbek"), VOLAPÜK("VO", "Volapük"), NORWEGIAN_NYNORSK("NN", "Norwegian_Nynorsk"), //
	OCCITAN("OC", "Occitan"), MACEDONIAN("MK", "Macedonian"), BELARUSIAN("BE", "Belarusian"), NEWAR__NEPAL_BHASA("NEW", "Newar__Nepal_Bhasa"), TATAR("TT", "Tatar"), //
	PIEDMONTESE("PMS", "Piedmontese"), TELUGU("TE", "Telugu"), BELARUSIAN_TARAŠKIEVICA("BE_X_OLD", "Belarusian_Taraškievica"), HAITIAN("HT", "Haitian"), BOSNIAN("BS", "Bosnian"), //
	BRETON("BR", "Breton"), JAVANESE("JV", "Javanese"), MALAGASY("MG", "Malagasy"), CHECHEN("CE", "Chechen"), LUXEMBOURGISH("LB", "Luxembourgish"), //
	MARATHI("MR", "Marathi"), MALAYALAM("ML", "Malayalam"), WESTERN_PANJABI("PNB", "Western_Panjabi"), BASHKIR("BA", "Bashkir"), BURMESE("MY", "Burmese"), //
	CANTONESE("ZH_YUE", "Cantonese"), LOMBARD("LMO", "Lombard"), YORUBA("YO", "Yoruba"), WEST_FRISIAN("FY", "West_Frisian"), ARAGONESE("AN", "Aragonese"), //
	CHUVASH("CV", "Chuvash"), TAJIK("TG", "Tajik"), KIRGHIZ("KY", "Kirghiz"), NEPALI("NE", "Nepali"), IDO("IO", "Ido"), //
	GUJARATI("GU", "Gujarati"), BISHNUPRIYA_MANIPURI("BPY", "Bishnupriya_Manipuri"), SCOTS("SCO", "Scots"), SICILIAN("SCN", "Sicilian"), LOW_SAXON("NDS", "Low_Saxon"), //
	KURDISH("KU", "Kurdish"), ASTURIAN("AST", "Asturian"), QUECHUA("QU", "Quechua"), SUNDANESE("SU", "Sundanese"), ALEMANNIC("ALS", "Alemannic"), //
	SCOTTISH_GAELIC("GD", "Scottish_Gaelic"), KANNADA("KN", "Kannada"), AMHARIC("AM", "Amharic"), INTERLINGUA("IA", "Interlingua"), NEAPOLITAN("NAP", "Neapolitan"), //
	SORANI("CKB", "Sorani"), BUGINESE("BUG", "Buginese"), SAMOGITIAN("BAT_SMG", "Samogitian"), WALLOON("WA", "Walloon"), BANYUMASAN("MAP_BMS", "Banyumasan"), //
	MONGOLIAN("MN", "Mongolian"), EGYPTIAN_ARABIC("ARZ", "Egyptian_Arabic"), MAZANDARANI("MZN", "Mazandarani"), SINHALESE("SI", "Sinhalese"), PUNJABI("PA", "Punjabi"), //
	MIN_NAN("ZH_MIN_NAN", "Min_Nan"), YIDDISH("YI", "Yiddish"), SAKHA("SAH", "Sakha"), VENETIAN("VEC", "Venetian"), FAROESE("FO", "Faroese"), //
	SANSKRIT("SA", "Sanskrit"), BAVARIAN("BAR", "Bavarian"), NAHUATL("NAH", "Nahuatl"), OSSETIAN("OS", "Ossetian"), TARANTINO("ROA_TARA", "Tarantino"), //
	KAPAMPANGAN("PAM", "Kapampangan"), ORIYA("OR", "Oriya"), UPPER_SORBIAN("HSB", "Upper_Sorbian"), NORTHERN_SAMI("SE", "Northern_Sami"), LIMBURGISH("LI", "Limburgish"), //
	HILL_MARI("MRJ", "Hill_Mari"), MAORI("MI", "Maori"), ILOKANO("ILO", "Ilokano"), CORSICAN("CO", "Corsican"), FIJI_HINDI("HIF", "Fiji_Hindi"), //
	CENTRAL_BICOLANO("BCL", "Central_Bicolano"), GAN("GAN", "Gan"), NORTH_FRISIAN("FRR", "North_Frisian"), RUSYN("RUE", "Rusyn"), GILAKI("GLK", "Gilaki"), //
	MEADOW_MARI("MHR", "Meadow_Mari"), DUTCH_LOW_SAXON("NDS_NL", "Dutch_Low_Saxon"), VÕRO("FIU_VRO", "Võro"), PASHTO("PS", "Pashto"), TURKMEN("TK", "Turkmen"), //
	PANGASINAN("PAG", "Pangasinan"), WEST_FLEMISH("VLS", "West_Flemish"), MANX("GV", "Manx"), MINGRELIAN("XMF", "Mingrelian"), ZAZAKI("DIQ", "Zazaki"), //
	KHMER("KM", "Khmer"), KOMI("KV", "Komi"), ZEELANDIC("ZEA", "Zeelandic"), KASHUBIAN("CSB", "Kashubian"), CRIMEAN_TATAR("CRH", "Crimean_Tatar"), //
	HAKKA("HAK", "Hakka"), VEPSIAN("VEP", "Vepsian"), AYMARA("AY", "Aymara"), DIVEHI("DV", "Divehi"), SOMALI("SO", "Somali"), //
	SARDINIAN("SC", "Sardinian"), CLASSICAL_CHINESE("ZH_CLASSICAL", "Classical_Chinese"), NORMAN("NRM", "Norman"), ROMANSH("RM", "Romansh"), UDMURT("UDM", "Udmurt"), //
	KOMI_PERMYAK("KOI", "Komi_Permyak"), CORNISH("KW", "Cornish"), UYGHUR("UG", "Uyghur"), SATERLAND_FRISIAN("STQ", "Saterland_Frisian"), LADINO("LAD", "Ladino"), //
	WU("WUU", "Wu"), LIGURIAN("LIJ", "Ligurian"), FRIULIAN("FUR", "Friulian"), EMILIAN_ROMAGNOL("EML", "Emilian_Romagnol"), ASSAMESE("AS", "Assamese"), //
	BIHARI("BH", "Bihari"), ZAMBOANGA_CHAVACANO("CBK_ZAM", "Zamboanga_Chavacano"), GUARANI("GN", "Guarani"), PALI("PI", "Pali"), GAGAUZ("GAG", "Gagauz"), //
	PICARD("PCD", "Picard"), RIPUARIAN("KSH", "Ripuarian"), NOVIAL("NOV", "Novial"), SILESIAN("SZL", "Silesian"), ANGLO_SAXON("ANG", "Anglo_Saxon"), //
	NAVAJO("NV", "Navajo"), INTERLINGUE("IE", "Interlingue"), ACEHNESE("ACE", "Acehnese"), EXTREMADURAN("EXT", "Extremaduran"), FRANCO_PROVENÇALARPITAN("FRP", "Franco_ProvençalArpitan"), //
	MIRANDESE("MWL", "Mirandese"), LINGALA("LN", "Lingala"), SHONA("SN", "Shona"), LOWER_SORBIAN("DSB", "Lower_Sorbian"), LEZGIAN("LEZ", "Lezgian"), //
	PALATINATE_GERMAN("PFL", "Palatinate_German"), KARACHAY_BALKAR("KRC", "Karachay_Balkar"), HAWAIIAN("HAW", "Hawaiian"), PENNSYLVANIA_GERMAN("PDC", "Pennsylvania_German"), KABYLE("KAB", "Kabyle"), //
	KALMYK("XAL", "Kalmyk"), KINYARWANDA("RW", "Kinyarwanda"), ERZYA("MYV", "Erzya"), TONGAN("TO", "Tongan"), ARAMAIC("ARC", "Aramaic"), //
	GREENLANDIC("KL", "Greenlandic"), BANJAR("BJN", "Banjar"), KABARDIAN_CIRCASSIAN("KBD", "Kabardian_Circassian"), LAO("LO", "Lao"), HAUSA("HA", "Hausa"), //
	PAPIAMENTU("PAP", "Papiamentu"), TOK_PISIN("TPI", "Tok_Pisin"), AVAR("AV", "Avar"), LAK("LBE", "Lak"), MOKSHA("MDF", "Moksha"), //
	LOJBAN("JBO", "Lojban"), WOLOF("WO", "Wolof"), NAURUAN("NA", "Nauruan"), BURYAT_RUSSIA("BXR", "Buryat_Russia"), TAHITIAN("TY", "Tahitian"), //
	SRANAN("SRN", "Sranan"), IGBO("IG", "Igbo"), NORTHERN_SOTHO("NSO", "Northern_Sotho"), KONGO("KG", "Kongo"), TETUM("TET", "Tetum"), //
	KARAKALPAK("KAA", "Karakalpak"), ABKHAZIAN("AB", "Abkhazian"), LATGALIAN("LTG", "Latgalian"), ZULU("ZU", "Zulu"), ZHUANG("ZA", "Zhuang"), //
	TUVAN("TYV", "Tuvan"), MIN_DONG("CDO", "Min_Dong"), CHEYENNE("CHY", "Cheyenne"), ROMANI("RMY", "Romani"), OLD_CHURCH_SLAVONIC("CU", "Old_Church_Slavonic"), //
	TSWANA("TN", "Tswana"), CHEROKEE("CHR", "Cherokee"), AROMANIAN("ROA_RUP", "Aromanian"), TWI("TW", "Twi"), GOTHIC("GOT", "Gothic"), //
	BISLAMA("BI", "Bislama"), NORFOLK("PIH", "Norfolk"), SAMOAN("SM", "Samoan"), KIRUNDI("RN", "Kirundi"), BAMBARA("BM", "Bambara"), //
	MOLDOVAN("MO", "Moldovan"), SWATI("SS", "Swati"), INUKTITUT("IU", "Inuktitut"), SINDHI("SD", "Sindhi"), PONTIC("PNT", "Pontic"), //
	KIKUYU("KI", "Kikuyu"), OROMO("OM", "Oromo"), XHOSA("XH", "Xhosa"), TSONGA("TS", "Tsonga"), EWE("EE", "Ewe"), //
	AKAN("AK", "Akan"), FIJIAN("FJ", "Fijian"), TIGRINYA("TI", "Tigrinya"), KASHMIRI("KS", "Kashmiri"), LUGANDA("LG", "Luganda"), //
	SANGO("SG", "Sango"), CHICHEWA("NY", "Chichewa"), FULA("FF", "Fula"), VENDA("VE", "Venda"), CREE("CR", "Cree"), //
	SESOTHO("ST", "Sesotho"), DZONGKHA("DZ", "Dzongkha"), TUMBUKA("TUM", "Tumbuka"), INUPIAK("IK", "Inupiak"), CHAMORRO("CH", "Chamorro"), //
	SERBO_CROATIAN("SH", "Serbo_Croatian"), SOUTH_AZERBAIJANI("AZB", "South_Azerbaijani"), MAITHILI("MAI", "Maithili"), NORTHERN_LURI("LRC", "Northern_Luri"), GOAN_KONKANI("GOM", "Goan_Konkani"), //
	LIVVINKARJALA("OLO", "Livvinkarjala"), PATOIS("JAM", "Patois"), TULU("TCY", "Tulu"), ADYGHE("ADY", "Adyghe"), INTERNATIONAL("MUL", "International");
	
	private final String key;
	private final String displayName;

	private BabelLanguage(String key, String displayName) {
		this.key = key;
		this.displayName = displayName;
	}

	public String getKey() {
		return key;
	}

	public String getDisplayName() {
		return displayName;
	}
}
