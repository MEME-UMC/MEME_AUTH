package umc.meme.auth.domain.artist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.domain.artist.entity.enums.Category;
import umc.meme.auth.domain.artist.entity.enums.MakeupLocation;
import umc.meme.auth.domain.artist.entity.enums.Region;
import umc.meme.auth.domain.artist.entity.enums.WorkExperience;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.dto.UserRequest;
import umc.meme.auth.global.enums.DayOfWeek;
import umc.meme.auth.global.enums.Times;

import java.util.List;
import java.util.Map;

@SuperBuilder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "Artist")
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

    public void update(UserRequest.ArtistExtraDto joinDto) {
        if (joinDto.getProfileImg() != null)
            this.profileImg = joinDto.getProfileImg();
        if (joinDto.getNickname() != null)
            this.nickname = joinDto.getNickname();

        // 여기서 유효성 검증을 어떻게 하지
        this.introduction = joinDto.getIntroduction();
        this.workExperience = joinDto.getWorkExperience();
        this.region = joinDto.getRegion();
        this.specialization = joinDto.getSpecialization();
        this.makeupLocation = joinDto.getMakeupLocation();
        this.shopLocation = joinDto.getShopLocation();
        this.availableDayOfWeekAndTime = joinDto.getAvailableDayOfWeekAndTime();
    }
}
