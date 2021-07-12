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
package vip.xiaonuo.sys.modular.file.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.param.BaseParam;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 文件信息表
 * </p>
 *
 * @author yubaoshan
 * @date 2020/6/7 22:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysFileInfoParam extends BaseParam {

    /**
     * 主键id
     */
    @NotNull(message = "id不能为空，请检查id参数", groups = {delete.class, detail.class})
    private Long id;

    /**
     * 文件存储位置（1:阿里云，2:腾讯云，3:minio，4:本地）
     */
    private Integer fileLocation;

    /**
     * 文件仓库
     */
    private String fileBucket;

    /**
     * 文件名称（上传时候的文件名）
     */
    private String fileOriginName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小kb
     */
    private Long fileSizeKb;

    /**
     * 存储到bucket的名称（文件唯一标识id）
     */
    @NotEmpty(message = "存储到bucket的名称不能为空，请检查fileObjectName参数", groups = {trace.class})
    private String fileObjectName;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文档创建时是否创建相同文件内容的模板文件
     */
    private Boolean sample = false;

    /**
     * 模式：编辑edit 查看view
     */
    private String mode;

    /**
     * 类型：桌面desktop 手机mobile
     */
    private String type;

}
