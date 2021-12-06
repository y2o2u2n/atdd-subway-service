package nextstep.subway.member;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.*;
import static nextstep.subway.member.TestMember.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;

public class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;

    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> findResponse = 회원_정보_조회_요청(createResponse);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);
        // then
        회원_삭제됨(deleteResponse);
    }

    @DisplayName("나의 정보를 관리한다.")
    @Test
    void manageMyInfo() {
        // Background
        회원_생성되어_있음(윤준석);
        TokenResponse token = 로그인_요청됨(윤준석).as(TokenResponse.class);

        // Scenario
        내_정보_조회됨(내_정보_조회_요청(token), 윤준석.getEmail(), 윤준석.getAge());
        내_정보_수정됨(내_정보_수정_요청(token, new MemberRequest(NEW_EMAIL, NEW_PASSWORD, NEW_AGE)));
        내_정보_조회됨(내_정보_조회_요청(token), NEW_EMAIL, NEW_AGE);
        내_정보_삭제됨(내_정보_삭제_요청(token));
    }

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);
        return post("/members", memberRequest);
    }

    public static ExtractableResponse<Response> 회원_생성되어_있음(TestMember member) {
        return 회원_생성을_요청(member.getEmail(), member.getPassword(), member.getAge());
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return get(uri);
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");
        MemberRequest memberRequest = new MemberRequest(email, password, age);
        return put(uri, memberRequest);
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return delete(uri);
    }

    public static void 회원_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 회원_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 회원_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 내_정보_조회_요청(TokenResponse token) {
        return 내_정보_조회_요청(token.getAccessToken());
    }

    public static ExtractableResponse<Response> 내_정보_조회_요청(String accessToken) {
        return get("/members/me", accessToken);
    }

    public static void 내_정보_조회됨(ExtractableResponse<Response> response, String expectedEmail, int expectedAge) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        MemberResponse actualMember = response.as(MemberResponse.class);

        assertAll(
            () -> assertThat(actualMember.getEmail()).isEqualTo(expectedEmail),
            () -> assertThat(actualMember.getAge()).isEqualTo(expectedAge));
    }

    public static void 내_정보_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    public static <T> ExtractableResponse<Response> 내_정보_수정_요청(TokenResponse token, T body) {
        return put("/members/me", token.getAccessToken(), body);
    }

    public static void 내_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 내_정보_삭제_요청(TokenResponse token) {
        return delete("/members/me", token.getAccessToken());
    }

    public static void 내_정보_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
