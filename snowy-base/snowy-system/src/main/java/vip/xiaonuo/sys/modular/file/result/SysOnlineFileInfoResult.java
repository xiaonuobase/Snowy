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
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy-layui
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy-layui
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.modular.file.result;

import cn.hutool.core.io.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.sys.modular.file.util.OnlineDocumentUtil;

import java.io.File;
import java.util.HashMap;

/**
 * 在线文件信息结果集
 *
 * @author yubaoshan
 * @date 2020/6/7 22:15
 */
@Data
public class SysOnlineFileInfoResult {

    public String fileId;
    public String[] history;
    public String type = "desktop";
    public String mode = "edit";
    public String documentType;
    public Document document;
    public EditorConfig editorConfig;
    public String token;

    public static class Document {
        public String title;
        public String url;
        public String fileType;
        public String key;
        public Permissions permissions;
    }

    public static class Permissions {
        public Boolean comment;
        public Boolean download;
        public Boolean edit;
        public Boolean fillForms;
        public Boolean modifyFilter;
        public Boolean modifyContentControl;
        public Boolean review;

        public Permissions(String mode, String type, Boolean canEdit) {
            comment = !mode.equals("view") && !mode.equals("fillForms") && !mode.equals("embedded") && !mode.equals("blockcontent");
            download = true;
            edit = canEdit && (mode.equals("edit") || mode.equals("filter") || mode.equals("blockcontent"));
            fillForms = !mode.equals("view") && !mode.equals("comment") && !mode.equals("embedded") && !mode.equals("blockcontent");
            modifyFilter = !mode.equals("filter");
            modifyContentControl = !mode.equals("blockcontent");
            review = mode.equals("edit") || mode.equals("review");
        }
    }

    public static class EditorConfig {
        public HashMap<String, Object> actionLink = null;
        public String mode = "edit";
        public String callbackUrl;
        public String lang = "en";
        public Integer forcesavetype = 1;
        public User user;
        public Customization customization;
        public Embedded embedded;

        public EditorConfig(String actionData) {
            if (actionData != null) {
                Gson gson = new Gson();
                actionLink = gson.fromJson(actionData, new TypeToken<HashMap<String, Object>>() { }.getType());
            }
            user = new User();
            customization = new Customization();
        }

        public void InitDesktop(String url) {
            embedded = new Embedded();
            embedded.saveUrl = url;
            embedded.embedUrl = url;
            embedded.shareUrl = url;
            embedded.toolbarDocked = "top";
        }

        public static class User {
            public String id = "-1";
            public String name = CommonConstant.UNKNOWN;
        }

        public static class Customization {
            public GoBack goback;
            public Boolean forcesave = true;
            public Customization()
            {
                goback = new GoBack();
            }

            public class GoBack {
                public String url;
            }
        }

        public static class Embedded {
            public String saveUrl;
            public String embedUrl;
            public String shareUrl;
            public String toolbarDocked;
        }
    }

    public SysOnlineFileInfoResult(String fileId, String fileOriginName, String userId, String userName) {
        if (fileOriginName == null) fileOriginName = "";
        fileOriginName = fileOriginName.trim();
        documentType = OnlineDocumentUtil.getFileType(fileOriginName).toLowerCase();
        this.fileId = fileId;
        document = new Document();
        document.title = fileOriginName;
        document.url = OnlineDocumentUtil.getFileUri(fileId, fileOriginName);
        document.fileType = FileUtil.getSuffix(fileOriginName);
        document.key = GenerateRevisionId(fileOriginName + SymbolConstant.PERIOD + new File(OnlineDocumentUtil.getStoragePath(fileId + SymbolConstant.PERIOD + FileUtil.getSuffix(fileOriginName))).lastModified());

        editorConfig = new EditorConfig(null);
        editorConfig.callbackUrl = OnlineDocumentUtil.getCallback(fileId, document.fileType);
        editorConfig.lang = "zh";

        if (userId != null) editorConfig.user.id = userId;
        if (userName != null) editorConfig.user.name = userName;

        editorConfig.customization.goback.url = "";

        changeType(mode, type);
    }

    public void changeType(String _mode, String _type) {
        if (_mode != null) mode = _mode;
        if (_type != null) type = _type;

        Boolean canEdit = OnlineDocumentUtil.getEditedSuffix().contains(SymbolConstant.PERIOD + FileUtil.getSuffix(document.title));

        editorConfig.mode = canEdit && !mode.equals("view") ? "edit" : "view";

        document.permissions = new Permissions(mode, type, canEdit);

        if (type.equals("embedded")) InitDesktop();
    }

    public static String GenerateRevisionId(String expectedKey) {
        if (expectedKey.length() > 20)
            expectedKey = Integer.toString(expectedKey.hashCode());

        String key = expectedKey.replace("[^0-9-.a-zA-Z_=]", "_");

        return key.substring(0, Math.min(key.length(), 20));
    }

    public void InitDesktop() {
        editorConfig.InitDesktop(document.url);
    }


}
