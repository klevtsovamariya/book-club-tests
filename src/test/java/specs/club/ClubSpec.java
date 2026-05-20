package specs.club;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class ClubSpec {

    public static RequestSpecification clubRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulCreateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/successful_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("owner", notNullValue())
            .build();

    public static ResponseSpecification successfulClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/successful_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("owner", notNullValue())
            .build();

    public static ResponseSpecification successfulClubListResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/successful_club_list_response_schema.json"))
            .expectBody("results", notNullValue())
            .build();

    public static ResponseSpecification successfulClubReviewListResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/successful_club_review_list_response_schema.json"))
            .expectBody("results", notNullValue())
            .build();

    public static ResponseSpecification successfulCreateClubReviewResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/successful_club_review_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("user", notNullValue())
            .build();

    public static ResponseSpecification successfulClubReviewResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/successful_club_review_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("user", notNullValue())
            .build();

    public static ResponseSpecification successfulDeleteClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .expectBody(emptyString())
            .build();

    public static ResponseSpecification successfulClubMembershipResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .expectBody(emptyString())
            .build();

    public static ResponseSpecification notFoundClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath("schemas/common/detail_error_response_schema.json"))
            .build();

    public static ResponseSpecification unauthorizedClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/common/detail_error_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification forbiddenClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(403)
            .expectBody(matchesJsonSchemaInClasspath("schemas/common/detail_error_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification invalidClubReviewResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/invalid_club_review_response_schema.json"))
            .build();
}
