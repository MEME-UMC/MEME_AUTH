package umc.meme.auth.domain.artist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.enums.Category;
import umc.meme.auth.global.enums.MakeupLocation;
import umc.meme.auth.global.enums.Region;
import umc.meme.auth.global.enums.WorkExperience;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.global.enums.DayOfWeek;
import umc.meme.auth.global.enums.Times;

import java.util.List;
import java.util.Map;

@SuperBuilder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Artist extends User {

    @Column(nullable = true, length = 500)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private WorkExperience workExperience;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private List<Region> region;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private List<Category> specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MakeupLocation makeupLocation;

    @Column(nullable = true)
    private String shopLocation; // 샵의 위치

    @ElementCollection
    @CollectionTable(name = "available_time_mapping",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")})
    @MapKeyColumn(name = "day_of_week")
    @MapKeyEnumerated(EnumType.STRING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Map<DayOfWeek, Times> availableDayOfWeekAndTime;

    public void update(AuthRequest.ArtistExtraDto joinDto) {
        if (joinDto.getProfile_img() != null)
            this.profileImg = joinDto.getProfile_img();
        if (joinDto.getNickname() != null)
            this.nickname = joinDto.getNickname();
        if (joinDto.getGender() != null)
            this.gender = joinDto.getGender();
        if (joinDto.getIntroduction() != null)
            this.introduction = joinDto.getIntroduction();
        if (joinDto.getWork_experience() != null)
            this.workExperience = joinDto.getWork_experience();
        if (joinDto.getRegion() != null)
            this.region = joinDto.getRegion();
        if (joinDto.getSpecialization() != null)
            this.specialization = joinDto.getSpecialization();
        if (joinDto.getMakeup_location() != null)
            this.makeupLocation = joinDto.getMakeup_location();
        if (joinDto.getShop_location() != null)
            this.shopLocation = joinDto.getShop_location();
        if (joinDto.getAvailableDayOfWeekAndTime() != null)
            this.availableDayOfWeekAndTime = joinDto.getAvailableDayOfWeekAndTime();
    }
}
