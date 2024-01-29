package umc.meme.auth.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.meme.auth.global.common.status.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "result"})
public class BaseResponseDto<T> {
  private final boolean success;
  private final String code;
  private final String message;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T result;

  // 성공한 경우 응답 생성
  public static <T> BaseResponseDto<T> onSuccess(T data){
    return new BaseResponseDto<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), data);
  }
  public static <T> BaseResponseDto<T> of(String message, String code, T data){
    return new BaseResponseDto<>(true, code, message, data);
  }

  // 실패한 경우 응답 생성
  public static <T> BaseResponseDto<T> onFailure(String message, String code, T data) {
    return new BaseResponseDto<>(false, code, message, data);
  }
}
