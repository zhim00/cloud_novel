package com.rin.novel.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mail 配置属性
 *
 * @author zhim00
 */
@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(String nickname, String username) {

}
