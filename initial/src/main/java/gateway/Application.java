package gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(UriConfiguration.class)
public class Application {

	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
// Creating a simple route

//	@Bean
//	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route(p -> p
//						.path("/get")
//						.filters(f -> f.addRequestHeader("Hello", "World"))
//						.uri("http://httpbin.org:80"))
//				.build();
//	}

//Using hystrix

//	@Bean
//	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route(p -> p
//						.path("/get")
//						.filters(f -> f.addRequestHeader("Hello", "World"))
//						.uri("http://httpbin.org:80"))
//				.route(p -> p
//						.host("*.hystrix.com")
//						.filters(f -> f.hystrix(config -> config.setName("mycmd")))
//						.uri("http://httpbin.org:80")).
//						build();
//	}

//In case of timeout

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/get")
						.filters(f -> f.addRequestHeader("Hello", "World"))
						.uri("http://httpbin.org:80"))
				.route(p -> p
						.host("*.hystrix.com")
						.filters(f -> f
								.hystrix(config -> config
								.setName("mycmd")
								.setFallbackUri("forward:/fallback")))
						.uri("http://httpbin.org:80"))
				.build();
	}

//Now when the Hystrix wrapped route times out it will call /fallback in the Gateway app. Lets add the /fallback endpoint to our application./
	@RequestMapping("/fallback")
	public Mono<String> fallback() {
		return Mono.just("fallback");
	}


}


//Writing tests - we should write some tests to make sure our Gateway is doing what we expect it should.
//In most cases we want to limit out dependencies on outside resources, especially in unit tests, so we should not depend on HTTPBin
// One solution to this problem is to make the URI in our routes configurable, so we can easily change the URI if we need to.

@ConfigurationProperties
class UriConfiguration {

	private String httpbin = "http://httpbin.org:80";

	public String getHttpbin() {
		return httpbin;
	}

	public void setHttpbin(String httpbin) {
		this.httpbin = httpbin;
	}
}

