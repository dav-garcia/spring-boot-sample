/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.web.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer.UserDetailsBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SpringBootApplication
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SampleWebUiApplication extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().and()
            .formLogin().loginPage("/login").failureUrl("/login?error").permitAll().and()
            .logout().permitAll().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetailsBuilder user = auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN", "USER");
        for (int i = 1; i <= 500; i++) {
            user.and().withUser("user" + i).password("user" + i).roles("USER");
        }
	}

	@Bean
	public MessageRepository messageRepository() {
		return new InMemoryMessageRepository();
	}

	@Bean
	public Converter<String, Message> messageConverter() {
		return new Converter<String, Message>() {
			@Override
			public Message convert(String id) {
				return messageRepository().findMessage(Long.valueOf(id));
			}
		};
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleWebUiApplication.class, args);
	}

}
