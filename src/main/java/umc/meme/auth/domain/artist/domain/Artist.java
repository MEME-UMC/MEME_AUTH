package umc.meme.auth.domain.artist.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.domain.artist.domain.enums.*;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.dto.UserRequest;

import java.util.Date;
import java.util.List;

@SuperBuilder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "Artist")
@Entity
public class Artist extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Column(nullable = true, length = 40)
    private String nickname;

    @Column(nullable = true)
    private String profileSrc;

    @Column(nullable = true, length = 500)
    private String introduction = "안녕하세요! 저는 ___입니다!";

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private WorkExperience workExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MakeupLocation makeupLocation;

    @Column(nullable = true)
    private Date inactiveDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private AvailableTime availableTime;
//
//    //    @Enumerated(EnumType.STRING)
////    @Column(nullable = false)
//    private List<Region> region;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private List<Category> specialization;

    public void update(UserRequest.artistExtraDto joinDto){
        this.nickname = joinDto.getNickname();
        this.gender = Gender.valueOf(joinDto.getGender());
        this.profileSrc = joinDto.getProfileSrc();
        this.introduction = joinDto.getIntroduction();
        this.workExperience = WorkExperience.valueOf(joinDto.getWorkExperience());
        this.makeupLocation = MakeupLocation.valueOf(joinDto.getMakeupLocation());
        this.availableTime = AvailableTime.valueOf(joinDto.getAvailableTime());
    }
}
