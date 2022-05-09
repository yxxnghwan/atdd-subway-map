package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixture.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.response.LineResponseDto;
import wooteco.subway.dto.response.StationResponseDto;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", "선릉역");
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", "잠실역");
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", station1.getId().toString());
        params.put("downStationId", station2.getId().toString());
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = createLineRequest(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", "선릉역");
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", "잠실역");
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", station1.getId().toString());
        params.put("downStationId", station2.getId().toString());
        params.put("distance", "10");
        createLineRequest(params);

        // when
        ExtractableResponse<Response> response = createLineRequest(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        /// given
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", "선릉역");
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", "잠실역");
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> lineParams1 = new HashMap<>();
        lineParams1.put("name", "2호선");
        lineParams1.put("color", "bg-green-600");
        lineParams1.put("upStationId", station1.getId().toString());
        lineParams1.put("downStationId", station2.getId().toString());
        lineParams1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = createLineRequest(lineParams1);

        Map<String, String> lineParams2 = new HashMap<>();
        lineParams2.put("name", "신분당선");
        lineParams2.put("color", "bg-red-600");
        lineParams2.put("upStationId", station1.getId().toString());
        lineParams2.put("downStationId", station2.getId().toString());
        lineParams2.put("distance", "10");
        ExtractableResponse<Response> createResponse2 = createLineRequest(lineParams2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponseDto.class).stream()
                .map(LineResponseDto::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void showLine() {
        /// given
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", "선릉역");
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", "잠실역");
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", station1.getId().toString());
        params.put("downStationId", station2.getId().toString());
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = createLineRequest(params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        LineResponseDto responseBody = response.jsonPath().getObject(".", LineResponseDto.class);

        // then
        assertAll(
                () -> assertThat(responseBody.getName()).isEqualTo("2호선"),
                () -> assertThat(responseBody.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", "선릉역");
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", "잠실역");
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", station1.getId().toString());
        params.put("downStationId", station2.getId().toString());
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = createLineRequest(params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Map<String, String> modifyParams = new HashMap<>();
        modifyParams.put("name", "신분당선");
        modifyParams.put("color", "bg-red-600");
        ExtractableResponse<Response> modifyResponse = RestAssured.given().log().all()
                .body(modifyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void removeLine() {
        // given
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", "선릉역");
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", "잠실역");
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", station1.getId().toString());
        params.put("downStationId", station2.getId().toString());
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = createLineRequest(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
