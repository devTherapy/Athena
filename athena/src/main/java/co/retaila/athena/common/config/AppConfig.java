package co.retaila.athena.common.config;

import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.constants.SecurityConstants;
import co.retaila.athena.common.repositories.ClientRepository;
import co.retaila.athena.common.repositories.UserRepository;
import co.retaila.lib.security.athena.services.AthenaSecurityService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

		return mapper;
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public AthenaSecurityService athenaSecurityService(UserRepository userRepository, ClientRepository clientRepository) {
		return new AthenaSecurityService() {
			@Override
			public boolean userExistsByUsername(String username) {
				return userRepository.existsByUsernameAndDomainName(username, DomainConstants.ATHENA);
			}

			@Override
			public boolean clientExistsByIdentifier(String clientId) {
				return clientRepository.existsByIdentifier(clientId);
			}

			@Override
			public String[] getExcludeFromAuthUrls() {
				return SecurityConstants.EXCLUDE_FROM_AUTH_URLS;
			}
		};
	}

}
