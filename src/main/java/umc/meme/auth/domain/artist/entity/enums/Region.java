package umc.meme.auth.domain.artist.entity.enums;

public enum Region {
    JONGNO("jongno"), JUNG("jung"), YONGSAN("yongsan"), SEONGDONG("seongdong"),
    GWANGJIN("gwangjin"), DONGDAEMUN("dongdaemun"),
    JUNGNANG("jungnang"), SEONGBUK("seongbuk"), GANGBUK("gangbuk"),
    DOBONG("dobong"), NOWON("nowon"), EUNPYEONG("eunpyeong"),
    SEODAEMUN("seodaemun"), MAPO("mapo"), YANGCHEON("yangcheon"),
    GANGSEO("gangseo"), GURO("guro"), GEUMCHEON("geumcheon"),
    YEONGDEUNGPO("yeongdeunpo"), DONGJAK("dongjak"), GWANAK("gwanak"),
    SEOCHO("seocho"), GANGNAM("gangnam"), SONGPA("songpa"), GANGDONG("gangdong");

    private String value;

    Region(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
