package vms.api.Main;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.jmx.ParentAwareNamingStrategy;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "vms.api" })
public class TomcatApplication extends SpringBootServletInitializer {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TomcatApplication.class);
	}

	/*
	 * @Bean public ServletContextInitializer contextInitializer() { return new
	 * ServletContextInitializer() {
	 * 
	 * @Override public void onStartup(ServletContext servletContext) throws
	 * ServletException { servletContext.setInitParameter( "dummy.type" ,
	 * "on-context-parameters" ); } }; }
	 * 
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TomcatApplication.class, args);
	}

	/*@Bean
	public DataSource dataSource() throws SQLException {
		HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create().type(HikariDataSource.class).build();
		
		dataSource.setPoolName("dataSource_" + UUID.randomUUID().toString());
		return dataSource ;
	}*/
		
	@Bean
	@ConditionalOnMissingBean(value = ObjectNamingStrategy.class, search = SearchStrategy.CURRENT)
	public ParentAwareNamingStrategy objectNamingStrategy() {
		ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(new AnnotationJmxAttributeSource());
		namingStrategy.setDefaultDomain("domain_" + UUID.randomUUID().toString());
		return namingStrategy;
	}
	

	/*
	 * @Bean protected ServletContextListener listener() {
	 * 
	 * return new ServletContextListener() {
	 * 
	 * @Override public void contextInitialized(ServletContextEvent sce) {
	 * log.info("Initialising Context..."); }
	 * 
	 * @Override public final void contextDestroyed(ServletContextEvent sce) {
	 * 
	 * log.info("Destroying Context...");
	 * 
	 * try { log.info("Calling MySQL AbandonedConnectionCleanupThread shutdown");
	 * com.mysql.jdbc.AbandonedConnectionCleanupThread.shutdown();
	 * 
	 * } catch (InterruptedException e) {
	 * log.error("Error calling MySQL AbandonedConnectionCleanupThread shutdown {}",
	 * e); }
	 * 
	 * ClassLoader cl = Thread.currentThread().getContextClassLoader();
	 * 
	 * Enumeration<Driver> drivers = DriverManager.getDrivers(); while
	 * (drivers.hasMoreElements()) { Driver driver = drivers.nextElement();
	 * 
	 * if (driver.getClass().getClassLoader() == cl) {
	 * 
	 * try { log.info("Deregistering JDBC driver {}", driver);
	 * DriverManager.deregisterDriver(driver);
	 * 
	 * } catch (SQLException ex) { log.error("Error deregistering JDBC driver {}",
	 * driver, ex); }
	 * 
	 * } else { log.
	 * trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader"
	 * , driver); } } } };
	 */
}