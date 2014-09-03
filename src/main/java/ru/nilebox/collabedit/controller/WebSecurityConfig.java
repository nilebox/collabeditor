package ru.nilebox.collabedit.controller;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

/**
 *
 * @author nile
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .headers().addHeaderWriter(
        new XFrameOptionsHeaderWriter(
            XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));
 
  }
}
