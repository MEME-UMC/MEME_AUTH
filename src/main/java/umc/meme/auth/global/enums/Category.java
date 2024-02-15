package umc.meme.auth.global.enums;

public enum Category {
    DAILY("daily"), INTERVIEW("interview"), ACTOR("actor"), PARTY("party"),
    WEDDING("wedding"), PROSTHETIC("prosthetic"), STUDIO("studio"), ETC("etc");

    private String value;

    Category(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
