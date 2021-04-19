/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.config;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import vip.xiaonuo.core.consts.CommonConstant;

import java.util.List;

/**
 * swagger配置
 *
 * @author xuyuxiang
 * 加入分组功能(默认注释掉)
 * <p>
 * https://doc.xiaominfo.com/knife4j/changelog/2017-12-18-swagger-bootstrap-ui-1.7-issue.html
 * </p>
 * @author ldw4033#163.com
 * @date 2021/4/9 10:42
 **/
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    private List<Parameter> getParameters() {
        Parameter parameter = new ParameterBuilder()
                .name("Authorization")
                .description("token令牌")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();

        List<Parameter> parameters = CollectionUtil.newArrayList();
        parameters.add(parameter);
        return parameters;
    }

    @Bean
    public Docket defaultApi() {
        List<Parameter> parameters = getParameters();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(defaultApiInfo())
                .groupName("默认接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage(CommonConstant.DEFAULT_PACKAGE_NAME))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }

    private ApiInfo defaultApiInfo() {
        return new ApiInfoBuilder()
                .title("Snowy Doc")
                .description("Snowy Doc文档")
                .termsOfServiceUrl("https://www.xiaonuo.vip")
                .contact(new Contact("xuyuxiang, yubaoshan, dongxiayu", "https://www.xiaonuo.vip", ""))
                .version("1.0")
                .build();
    }

    /**
     * 想分组请放开注释
     */

    // @Bean
    // public Docket groupRestApi() {
    //     List<Parameter> parameters = getParameters();
    //     return new Docket(DocumentationType.SWAGGER_2)
    //             .apiInfo(groupApiInfo())
    //             .groupName("自定义")
    //             .select()
    //             //TODO 这里改为自己的包名
    //             .apis(RequestHandlerSelectors.basePackage("com.example.XXX"))
    //             .paths(PathSelectors.any())
    //             .build()
    //             .globalOperationParameters(parameters);
    // }
    //
    // private ApiInfo groupApiInfo() {
    //     return new ApiInfoBuilder()
    //             .title("自定义")
    //             .description("自定义API")
    //             .termsOfServiceUrl("http://www.example.com/")
    //             .version("1.0")
    //             .build();
    // }

}
