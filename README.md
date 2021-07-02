CONTRACT TEST DEMO
- 

Consumer ve Provider arasındaki anlaşmayı test eder.

Kullanılan teknolojiler
-

- Maven
- Pact.io
- JUnit

Nasıl kullanılır
- 

- Pact tanımlaması yapıldıktan sonra proje ***mvn clean install*** komutu ile proje build edilir.
- Build sırasında oluşturulan pact dökümanı ***mvn pact:publish*** komutu ile pom'da belirtilmiş broker'a upload edilir.
- Broker'a upload edilen pact dökümanı ***mvn pact:verify*** komutu ile gerçek servise karşı test edilir.



**Proje dockerize edildiyse;**

- Provider pipeline'ına eklenen stage'de ***docker run --entrypoint mvn ${REGISTRY_URL}/contract-test-demo:latest pact:verify*** komutu koşulur.
PactBroker'da bu provider için yüklenmiş olan tüm pact dökümanları test edilir.
