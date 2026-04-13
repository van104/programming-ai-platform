/*
package com.lrm.aiplatform;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/ai_platform_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai",
                        "van",
                        "lrm0405")
                .globalConfig(builder -> builder
                        .author("lrm")
                        .outputDir(Paths.get("D:\\webapp\\programming-ai-platform\\ai-platform-backend\\src\\main\\java").toString()) // 输出目录
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("com.lrm.aiplatform")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .addInclude("user,code_record") // 设置需要生成的表名
                        .addTablePrefix("code_")
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation() // 启用字段注解
                        .enableFileOverride()
                        .mapperBuilder()
                        .enableFileOverride()
                        .controllerBuilder()
                        .enableRestStyle() // 启用 REST 风格
                        .enableFileOverride()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
*/
