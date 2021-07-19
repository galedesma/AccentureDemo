package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(GameRepository gameRepo, PlayerRepository playerRepo, GamePlayerRepository gpRepo, ShipRepository shipRepo, SalvoRepository salvoRepo, ScoreRepository scoreRepo){
		return (args) ->{
			Date today = new Date();

			Game game1 = new Game(today);
			Game game2 = new Game(Date.from(today.toInstant().plusSeconds(3600)));
			Game game3 = new Game(Date.from(today.toInstant().plusSeconds(3600*2)));
			Game game4 = new Game(Date.from(today.toInstant().plusSeconds(3600*3)));
			Game game5 = new Game(Date.from(today.toInstant().plusSeconds(3600*4)));
			Game game6 = new Game(Date.from(today.toInstant().plusSeconds(3600*5)));

			Player jbauer = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player cobrian = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player talmeida = new Player("t.almeida@ctu.gov", passwordEncoder().encode("kb"));
			Player dpalmer = new Player("d.palmer@whitehouse.gov", passwordEncoder().encode("mole"));

			Ship ship1a = new Ship("Destroyer", Arrays.asList("H2","H3","H4"));
			Ship ship1b = new Ship("Submarine", Arrays.asList("E1","F1","G1"));
			Ship ship1c = new Ship("Patrol Boat", Arrays.asList("B4","B5"));
			Ship ship2a = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship ship2b = new Ship("Patrol Boat", Arrays.asList("F1", "F2"));
			Ship ship3a = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship ship3b = new Ship("Patrol Boat", Arrays.asList("C6","C7"));
			Ship ship4a = new Ship("Submarine", Arrays.asList("A2","A3","A4"));
			Ship ship4b = new Ship("Patrol Boat", Arrays.asList("G6","H6"));
			Ship ship5 = new Ship("Submarine", Arrays.asList("C1","C2","C3"));
			Ship ship6 = new Ship("Submarine", Arrays.asList("D1", "E1", "F1"));
			Ship ship7 = new Ship("Battleship", Arrays.asList("I2", "I3", "I4", "I5"));
			Ship ship8 = new Ship("Carrier", Arrays.asList("G5", "G6", "G7", "G8","G9"));

			Salvo salvo1a = new Salvo(Arrays.asList("B5", "C5", "F1"),1);
			Salvo salvo1b = new Salvo(Arrays.asList("F2", "D5"),2);
			Salvo salvo2a = new Salvo(Arrays.asList("B4", "B5", "B6"),1);
			Salvo salvo2b = new Salvo(Arrays.asList("E1", "H3", "A2"),2);
			Salvo salvo3a = new Salvo(Arrays.asList("A2", "A4", "G6"),1);
			Salvo salvo3b = new Salvo(Arrays.asList("A3", "H6"),2);
			Salvo salvo4a = new Salvo(Arrays.asList("B5", "D5", "C7"),1);
			Salvo salvo4b = new Salvo(Arrays.asList("C5","C6"),2);
			Salvo salvo5 = new Salvo(Arrays.asList("G6", "H6"),1);
			Salvo salvo6 = new Salvo(Arrays.asList("A2", "A3", "D8"),1);
			Salvo salvo7 = new Salvo(Arrays.asList("A3", "A4", "F7"),1);
			Salvo salvo8 = new Salvo(Arrays.asList("A2", "G6", "H6"),1);
			Salvo salvo9 = new Salvo(Arrays.asList("A1", "A2", "A3"),1);
			Salvo salvo10 = new Salvo(Arrays.asList("G6", "G7","G8"), 2);

			Score score1 = new Score(1.0, game1, jbauer);
			Score score2 = new Score(0.0, game1, cobrian);
			Score score3 = new Score(1.0, game2, jbauer);
			Score score4 = new Score(0.0, game2, cobrian);
			Score score5 = new Score(0.5, game3, cobrian);
			Score score6 = new Score(0.5, game3, talmeida);
			Score score7 = new Score(0.0, game4, jbauer);
			Score score8 = new Score(1.0, game4, cobrian);
			Score score9 = new Score(0.0, game5, talmeida);
			Score score10 = new Score(1.0, game5, jbauer);
			Score score11 = new Score();

			GamePlayer gp1 = new GamePlayer();
			gp1.setGame(game1);
			gp1.setPlayer(jbauer);
			gp1.addShip(ship1a);
			gp1.addShip(ship1b);
			gp1.addShip(ship1c);

			GamePlayer gp2 = new GamePlayer();
			gp2.setGame(game1);
			gp2.setPlayer(cobrian);
			gp2.addShip(ship2a);
			gp2.addShip(ship2b);

			GamePlayer gp3 = new GamePlayer();
			gp3.setGame(game2);
			gp3.setPlayer(jbauer);
			gp3.addShip(ship3a);
			gp3.addShip(ship3b);

			GamePlayer gp4 = new GamePlayer();
			gp4.setGame(game2);
			gp4.setPlayer(cobrian);
			gp4.addShip(ship4a);
			gp4.addShip(ship4b);

			GamePlayer gp5 = new GamePlayer();
			gp5.setGame(game3);
			gp5.setPlayer(cobrian);

			GamePlayer gp6 = new GamePlayer();
			gp6.setGame(game3);
			gp6.setPlayer(talmeida);

			GamePlayer gp7 = new GamePlayer();
			gp7.setGame(game4);
			gp7.setPlayer(jbauer);
			gp7.addShip(ship5);
			gp7.addShip(ship6);

			GamePlayer gp8 = new GamePlayer();
			gp8.setGame(game4);
			gp8.setPlayer(cobrian);

			GamePlayer gp9 = new GamePlayer();
			gp9.setGame(game5);
			gp9.setPlayer(talmeida);

			GamePlayer gp10 = new GamePlayer();
			gp10.setGame(game5);
			gp10.setPlayer(jbauer);
			gp10.addShip(ship7);
			gp10.addShip(ship8);

			GamePlayer gp11 = new GamePlayer();
			gp11.setGame(game6);
			gp11.setPlayer(dpalmer);

			gp1.addSalvo(salvo1a);
			gp1.addSalvo(salvo1b);
			gp2.addSalvo(salvo2a);
			gp2.addSalvo(salvo2b);
			gp3.addSalvo(salvo3a);
			gp3.addSalvo(salvo3b);
			gp4.addSalvo(salvo4a);
			gp4.addSalvo(salvo4b);
			gp5.addSalvo(salvo5);
			gp6.addSalvo(salvo6);
			gp7.addSalvo(salvo7);
			gp8.addSalvo(salvo8);
			gp9.addSalvo(salvo9);
			gp1.addSalvo(salvo10);

			gameRepo.save(game1);
			gameRepo.save(game2);
			gameRepo.save(game3);
			gameRepo.save(game4);
			gameRepo.save(game5);
			gameRepo.save(game6);
			playerRepo.save(jbauer);
			playerRepo.save(cobrian);
			playerRepo.save(talmeida);
			playerRepo.save(dpalmer);

			gpRepo.save(gp1);
			gpRepo.save(gp2);
			gpRepo.save(gp3);
			gpRepo.save(gp4);
			gpRepo.save(gp5);
			gpRepo.save(gp6);
			gpRepo.save(gp7);
			gpRepo.save(gp8);
			gpRepo.save(gp9);
			gpRepo.save(gp10);
			gpRepo.save(gp11);

			shipRepo.save(ship1a);
			shipRepo.save(ship1b);
			shipRepo.save(ship1c);
			shipRepo.save(ship2a);
			shipRepo.save(ship2b);
			shipRepo.save(ship3a);
			shipRepo.save(ship4a);
			shipRepo.save(ship4b);
			shipRepo.save(ship5);
			shipRepo.save(ship6);
			shipRepo.save(ship7);
			shipRepo.save(ship8);

			salvoRepo.save(salvo1a);
			salvoRepo.save(salvo1b);
			salvoRepo.save(salvo2a);
			salvoRepo.save(salvo2b);
			salvoRepo.save(salvo3a);
			salvoRepo.save(salvo3b);
			salvoRepo.save(salvo4a);
			salvoRepo.save(salvo4b);
			salvoRepo.save(salvo5);
			salvoRepo.save(salvo6);
			salvoRepo.save(salvo7);
			salvoRepo.save(salvo8);
			salvoRepo.save(salvo9);
			salvoRepo.save(salvo10);

			scoreRepo.save(score1);
			scoreRepo.save(score2);
			scoreRepo.save(score3);
			scoreRepo.save(score4);
			scoreRepo.save(score5);
			scoreRepo.save(score6);
			scoreRepo.save(score7);
			scoreRepo.save(score8);
			scoreRepo.save(score9);
			scoreRepo.save(score10);
			scoreRepo.save(score11);
		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username -> {
			Player player = playerRepository.findByUserName(username);
			if(player != null){
				return new User(player.getUserName(), player.getPassword(), AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Usuario no encontrado: " + username);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception{

		http.authorizeRequests()
				.antMatchers("/web/game.html", "/api/game_view/**", "/h2-console/**", "/rest/**").hasAuthority("USER")
				.antMatchers("/**").permitAll();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		http.headers().frameOptions().disable();

		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
