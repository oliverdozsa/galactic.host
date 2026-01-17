package host.galactic.stellar;

import host.galactic.stellar.rest.requests.commission.CommissionSignEnvelopeRequest;
import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import host.galactic.stellar.rest.responses.commission.CommissionSignEnvelopeResponse;
import host.galactic.testutils.AuthForTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;

import java.net.URL;

import static io.restassured.RestAssured.given;

public class StellarBaseTestRestCommission {
    public URL url;

    private AuthForTest auth = new AuthForTest();

    public StellarBaseTestRestCommission(URL url) {
        this.url = url;
    }

    public CommissionGetPublicKeyResponse getPublicKey() {
        return given()
                .get(url + "/publickey")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body()
                .as(CommissionGetPublicKeyResponse.class);
    }

    public CommissionSignEnvelopeResponse signEnvelope(String base64Envelope, String user, Long votingId) {
        var asBob = auth.loginAs(user);
        var request = new CommissionSignEnvelopeRequest(base64Envelope);

        return given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(CommissionSignEnvelopeResponse.class);
    }
}
