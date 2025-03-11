package com.example.userservice.com.support;

import com.example.userservice.com.config.QuerydslConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test"})
@Import(QuerydslConfig.class)
public abstract class TestSupport {

}
