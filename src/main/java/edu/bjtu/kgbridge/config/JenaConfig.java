package edu.bjtu.kgbridge.config;

import edu.bjtu.kgbridge.builtins.*;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: JenaConfig
 * Package: edu.bjtu.kgbridge.config
 * Description: 注册原语的配置类
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 13:21
 */
@Configuration
public class JenaConfig {

    @Bean
    public void registerBuiltins() {
        BuiltinRegistry.theRegistry.register(new GreaterOrEqual());
        BuiltinRegistry.theRegistry.register(new LessOrEqual());
        BuiltinRegistry.theRegistry.register(new LessThan());
        BuiltinRegistry.theRegistry.register(new GreaterThan());
        BuiltinRegistry.theRegistry.register(new Equal());
        BuiltinRegistry.theRegistry.register(new NotEqual());
        BuiltinRegistry.theRegistry.register(new CompareMul());
    }
}