import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class PactTests {

  /*
  Endpoints
   */
  private static final String USER_ENDPOINT = "/v2/user/user1";
  private static final String PET_ENDPOINT = "/v2/pet";

  /**
   * Ayağa kalkacak mock servisin bilgileri verilir. host & port boş bırakılırsa default localhost'da random port ile ayağa kaldırıyor. Debug
   * sırasında mockProvider.getUrl() ile görülebilir.
   */
  @Rule
  public PactProviderRule mockProvider
      = new PactProviderRule("petstore", "localhost", 8080, this);

  /**
   * Contract tanımlanır. Contract'a yeni endpointler ekleneceği takdirde toPact() metoduyla finalize etmek yerine rule tanımlamaya devam edilir.
   */
  @Pact(consumer = "demo-consumer")
  public RequestResponsePact createPact(PactDslWithProvider builder) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return builder
        .given("Find pet by id")
        .uponReceiving("FindPetByID")
        .path(PET_ENDPOINT)
        .method("POST")
        .body(expectedRequestPayload())
        .headers("accept", "text/plain, application/json, application/*+json, */*")
        .willRespondWith()
        .status(200)
        .headers(headers)
        .body(expectedResponseForPetEndpoint())

        .given("get user by user name")
        .uponReceiving("GetUserByUserName")
        .path(USER_ENDPOINT)
        .method("GET")
        .headers("accept", "text/plain, application/json, application/*+json, */*")
        .willRespondWith()
        .status(200)
        .headers(headers)
        .body(expectedResponseForUserEndpoint()).toPact();
  }

  /**
   * Tanımlanan rule'a göre mock servisi ayağa kaldırıp istek atarak, pact dökümanını oluşturur. Her endpoint için mock servise request atılması
   * gerekiyor.
   */
  @Test
  @PactVerification()
  public void getAndCreateName() {
    new RestTemplate().getForEntity(mockProvider.getUrl() + USER_ENDPOINT, String.class);

    new RestTemplate()
        .exchange(
            mockProvider.getUrl() + PET_ENDPOINT,
            HttpMethod.POST,
            new HttpEntity<>(expectedRequestPayload(), getDefaultHeaders()),
            String.class
        );
  }

  private HttpHeaders getDefaultHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    return httpHeaders;
  }

  private String expectedRequestPayload() {
    return "{\n"
        + "  \"id\": 6978,\n"
        + "  \"category\": {\n"
        + "    \"id\": 6978,\n"
        + "    \"name\": \"string\"\n"
        + "  },\n"
        + "  \"name\": \"string\",\n"
        + "  \"photoUrls\": [\n"
        + "    \"string\"\n"
        + "  ],\n"
        + "  \"tags\": [\n"
        + "    {\n"
        + "      \"id\": 6978,\n"
        + "      \"name\": \"string\"\n"
        + "    }\n"
        + "  ],\n"
        + "  \"status\": \"available\"\n"
        + "}";
  }

  private PactDslJsonBody expectedResponseForPetEndpoint() {
    PactDslJsonBody expectedBody = new PactDslJsonBody();
    expectedBody
        .integerType("id")
        .stringType("name")
        .stringType("status")
          .object("category")
            .integerType("id")
            .stringType("name")
          .closeObject()
        .eachLike("tags")
          .integerType("id")
          .stringType("name")
          .closeObject()
        .closeArray()
        .array("photoUrls")
          .stringType()
        .closeArray();

    return expectedBody;
  }

  private PactDslJsonBody expectedResponseForUserEndpoint() {
    PactDslJsonBody expectedBody = new PactDslJsonBody();
    expectedBody
        .integerType("id")
        .stringType("username", "firstName", "lastName", "email", "password", "phone")
        .integerType("userStatus");

    return expectedBody;
  }
}
