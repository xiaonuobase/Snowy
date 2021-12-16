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
package vip.xiaonuo.sys.modular.log.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 系统访问日志表
 *
 * @author xuyuxiang
 * @date 2020/3/11 11:56
 */
@Data
@TableName("sys_vis_log")
public class SysVisLog {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 名称
     */
    @Excel(name = "名称", width = 20)
    private String name;

    /**
     * 是否执行成功（Y-是，N-否）
     */
    @Excel(name = "是否执行成功", replace = {"是_Y", "否_N"}, width = 20)
    private String success;

    /**
     * 具体消息
     */
    @Excel(name = "具体消息", width = 20)
    private String message;

    /**
     * ip
     */
    @Excel(name = "ip", width = 20)
    private String ip;

    /**
     * 地址
     */
    @Excel(name = "地址", width = 20)
    private String location;

    /**
     * 浏览器
     */
    @Excel(name = "浏览器", width = 20)
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统", width = 20)
    private String os;

    /**
     * 访问类型（字典 1登入 2登出）
     */
    @Excel(name = "访问类型", replace = {"登入_1", "登出_2"}, width = 20)
    private Integer visType;

    /**
     * 访问时间
     */
    @Excel(name = "操作时间", databaseFormat = "yyyy-MM-dd HH:mm:ss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visTime;

    /**
     * 访问人
     */
    private String account;

    /**
     * 签名数据（ID除外）
     */
    private String signValue;

    /**
     * 重写tostring方法
     */
    @Override
    public String toString(){
        String toStr = name + success + message + ip + location + browser
                + os + visType + visTime + account;
        return toStr.replaceAll(" +","");
    }
}
