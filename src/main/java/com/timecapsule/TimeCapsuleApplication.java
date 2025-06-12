package com.timecapsule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 时光信笺应用启动类
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Slf4j
@SpringBootApplication(exclude = {
		RedisAutoConfiguration.class,
		RedisRepositoriesAutoConfiguration.class
})
public class TimeCapsuleApplication {

	public static void main(String[] args) throws UnknownHostException {
		ConfigurableApplicationContext application = SpringApplication.run(TimeCapsuleApplication.class, args);

		Environment env = application.getEnvironment();
		String ip = InetAddress.getLocalHost().getHostAddress();
		String port = env.getProperty("server.port");
		String path = env.getProperty("server.servlet.context-path", "");

		// 确保路径格式正确，避免双斜杠问题
		String basePath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		String swaggerPath = basePath + "/doc.html";
		String druidPath = basePath + "/druid";

		log.info("\n----------------------------------------------------------\n\t" +
				"时光信笺应用启动成功! Access URLs:\n\t" +
				"Local: \t\thttp://localhost:" + port + path + "\n\t" +
				"External: \thttp://" + ip + ":" + port + path + "\n\t" +
				"Swagger: \thttp://" + ip + ":" + port + swaggerPath + "\n\t" +
				"Druid: \t\thttp://" + ip + ":" + port + druidPath + "\n" +
				"----------------------------------------------------------");
	}
}