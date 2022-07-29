package xyz.raysmen.lp.core;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * CodeGenerator
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core
 * @date 2022/05/17 18:07
 * @description MyBatis-plus 代码生成器
 */
public class CodeGenerator {
    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://localhost:3306/lp_service_core?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
            "root", "root")
            .dbQuery(new MySqlQuery())
            .typeConvert(new MySqlTypeConvert())
            .keyWordsHandler(new MySqlKeyWordsHandler()).build();

    /**
     * 策略配置
     */
    private StrategyConfig.Builder strategyConfig() {
        return new StrategyConfig.Builder();
    }

    /**
     * 全局配置
     */
    private GlobalConfig.Builder globalConfig() {
        return new GlobalConfig.Builder();
    }

    /**
     * 包配置
     */
    private PackageConfig.Builder packageConfig() {
        return new PackageConfig.Builder();
    }

    /**
     * 模板配置
     */
    private TemplateConfig.Builder templateConfig() {
        return new TemplateConfig.Builder();
    }

    /**
     * 注入配置
     */
    private InjectionConfig.Builder injectionConfig() {
        // 测试自定义输出文件之前注入操作，该操作再执行生成代码前 debug 查看
        /*return new InjectionConfig.Builder().beforeOutputFile((tableInfo, objectMap) -> {
            System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
        });*/
        return new InjectionConfig.Builder();
    }

    @Test
    public void generateCode() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        String projectPath = System.getProperty("user.dir");
        generator.global(globalConfig()
                .author("Rays")
                .outputDir(projectPath + "/src/main/java")
                .dateType(DateType.TIME_PACK).build());
        generator.packageInfo(packageConfig()
                .parent("xyz.raysmen.lp.core")
                // 此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
                .entity("pojo.entity")
                .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper"))
                .build());
        generator.strategy(strategyConfig()
                // 实体生成策略
                .entityBuilder()
                .enableLombok()
                .logicDeleteColumnName("is_deleted")
                .logicDeletePropertyName("deleted")
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .enableRemoveIsPrefix()
                .idType(IdType.AUTO)
                // 映射器生成策略
                .mapperBuilder()
                .formatMapperFileName("%sMapper")
                .formatXmlFileName("%sMapper")
                // 控制器生成策略
                .controllerBuilder()
                .formatFileName("%sController")
                .enableRestStyle()
                // 服务生成策略
                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")
                .build());
        generator.execute();
    }

    @Test
    public void test() {
        // 执行该测试时所在的模块根目录地址（本地计算机）
        System.out.println(System.getProperty("user.dir"));
    }
}
