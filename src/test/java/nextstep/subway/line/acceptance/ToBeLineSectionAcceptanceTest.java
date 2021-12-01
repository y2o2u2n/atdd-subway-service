package nextstep.subway.line.acceptance;

import static nextstep.subway.line.acceptance.LineAcceptanceTest.*;
import static nextstep.subway.line.acceptance.LineSectionAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class ToBeLineSectionAcceptanceTest extends AcceptanceTest {

	/**
	 * Feature: 지하철 구간 관련 기능
	 *
	 *   Background
	 *     Given 지하철역 등록되어 있음 (ex. [강남], [양재], [정자], [광교])
	 *     And 지하철 노선 등록되어 있음 (ex. [강남]----------20----------[광교], [양재], [정자])
	 *
	 *   Scenario: 지하철 구간을 관리
	 *     When 등록된 지하철역이 2개일 때 지하철 구간 삭제 요청
	 *     Then 지하철 구간 삭제 실패됨
	 *     When 동일한 지하철 구간 등록 요청
	 *     Then 지하철 구간 등록 실패됨
	 *     When 등록되지 않은 역들로 지하철 구간 등록 요청
	 *     Then 지하철 구간 등록 실패됨
	 *     When 지하철 구간 등록 요청
	 *     Then 지하철 구간 등록됨
	 *     When 지하철 노선에 등록된 역 목록 조회 요청
	 *     Then 등록한 지하철 구간이 반영된 역 목록이 조회됨 (ex. [강남]-2-[양재]---------18---------[광교])
	 *     When 지하철 구간 등록 요청
	 *     Then 지하철 구간 등록됨
	 *     When 지하철 노선에 등록된 역 목록 조회 요청
	 *     Then 등록한 지하철 구간이 반영된 역 목록이 조회됨 (ex. [강남]-2-[양재]----8----[정자]-----10-----[광교])
	 *     When 지하철 구간 삭제 요청
	 *     Then 지하철 구간 삭제됨
	 *     When 지하철 노선에 등록된 역 목록 조회 요청
	 *     Then 삭제한 지하철 구간이 반영된 역 목록이 조회됨 (ex. [강남]-----10-----[정자]-----10-----[광교])
	 */
	@DisplayName("인수 조건")
	@Test
	void acceptanceCriteria() {
		// Background
		StationResponse 강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
		StationResponse 정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 20)).as(LineResponse.class);

		// Scenario
		지하철_노선에_지하철역_제외_실패됨(지하철_노선에_지하철역_제외_요청(신분당선, 강남역));
		지하철_노선에_지하철역_순서_정렬됨(지하철_노선_조회_요청(신분당선), Arrays.asList(강남역, 광교역));
		지하철_노선에_지하철역_등록_실패됨(지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 광교역, 20));
		지하철_노선에_지하철역_등록_실패됨(지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 8));
		지하철_노선에_지하철역_등록됨(지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 2));
		지하철_노선에_지하철역_순서_정렬됨(지하철_노선_조회_요청(신분당선), Arrays.asList(강남역, 양재역, 광교역));
		지하철_노선에_지하철역_등록됨(지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 8));
		지하철_노선에_지하철역_순서_정렬됨(지하철_노선_조회_요청(신분당선), Arrays.asList(강남역, 양재역, 정자역, 광교역));
		지하철_노선에_지하철역_제외됨(지하철_노선에_지하철역_제외_요청(신분당선, 양재역));
		지하철_노선에_지하철역_순서_정렬됨(지하철_노선_조회_요청(신분당선), Arrays.asList(강남역, 정자역, 광교역));
	}
}