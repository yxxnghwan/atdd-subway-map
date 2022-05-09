package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.mockDao.MockStationDao;
import wooteco.subway.repository.entity.StationEntity;

public class Fixture {

    private static final MockStationDao stationDao = new MockStationDao();

    public static ExtractableResponse<Response> createLineRequest(final Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createStationRequest(final Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static Long saveStation(final String name) {
        final Station station = Station.createWithoutId(name);
        final StationEntity saved = stationDao.save(new StationEntity(station));
        return saved.getId();
    }
}
