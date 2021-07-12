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
package vip.xiaonuo.sys.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.file.FileOperator;
import vip.xiaonuo.core.file.modular.local.LocalFileOperator;
import vip.xiaonuo.sys.config.FileConfig;
import vip.xiaonuo.sys.modular.file.result.SysOnlineFileInfoResult;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 在线文档相关工具类
 *
 * @author xuyuxiang
 * @date 2021/3/24 16:12
 */
public class OnlineDocumentUtil {

    private static final String VIEWED_SUFFIX = ".pdf|.djvu|.xps";
    private static final String EDITED_SUFFIX = ".docx|.xlsx|.csv|.pptx|.txt";
    private static final String CONVERT_SUFFIX = ".docm|.dotx|.dotm|.dot|.doc|.odt|.fodt|.ott|.xlsm|.xltx|.xltm|.xlt|.xls|.ods|.fods|.ots|.pptm|.ppt|.ppsx|.ppsm|.pps|.potx|.potm|.pot|.odp|.fodp|.otp|.rtf|.mht|.html|.htm|.epub";

    public static final List<String> DOCUMENT_SUFFIX = Arrays.asList(".doc", ".docx", ".docm", ".dot", ".dotx", ".dotm", ".odt", ".fodt", ".ott", ".rtf", ".txt", ".html", ".htm",
            ".mht", ".pdf", ".djvu", ".fb2", ".epub", ".xps");

    public static final List<String> SPREADSHEET_SUFFIX = Arrays.asList(".xls", ".xlsx", ".xlsm", ".xlt", ".xltx", ".xltm", ".ods", ".fods", ".ots", ".csv");

    public static final List<String> Presentation_SUFFIX = Arrays.asList(".pps", ".ppsx", ".ppsm",".ppt", ".pptx", ".pptm", ".pot", ".potx", ".potm", ".odp", ".fodp", ".otp");


    public static List<String> getFileSuffix() {
        List<String> res = new ArrayList<>();
        res.addAll(getViewedSuffix());
        res.addAll(getEditedSuffix());
        res.addAll(getConvertSuffix());
        return res;
    }

    public static List<String> getViewedSuffix() {
        return Arrays.asList(VIEWED_SUFFIX.split("\\|"));
    }

    public static List<String> getEditedSuffix() {
        return Arrays.asList(EDITED_SUFFIX.split("\\|"));
    }

    public static List<String> getConvertSuffix() {
        return Arrays.asList(CONVERT_SUFFIX.split("\\|"));
    }

    public static String getFileType(String fileOriginName) {
        String suffix = SymbolConstant.PERIOD + FileUtil.getSuffix(fileOriginName);
        if (DOCUMENT_SUFFIX.contains(suffix)) return "Text";
        if (SPREADSHEET_SUFFIX.contains(suffix)) return "Spreadsheet";
        if (Presentation_SUFFIX.contains(suffix)) return "Presentation";
        return "Text";
    }

    public static String getFileUri(String fileId, String fileName) {
        try {
            String filePath = "sysFileInfo/download?id="+ fileId +"&name=" + URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8.toString()).replace("+", "%20");

            return filePath;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String getCallback(String fileId, String fileSuffix) {
        return "sysFileInfo/track?fileObjectName=" + fileId + SymbolConstant.PERIOD + fileSuffix + "&id=" + fileId;
    }

    public static String getStoragePath(String fileIdOrObjectName) {
        String directory = FilesRootPath();
        return directory + File.separator + FileConfig.DEFAULT_BUCKET + File.separator + fileIdOrObjectName;
    }

    public static String FilesRootPath() {
        LocalFileOperator localFileOperator = (LocalFileOperator) SpringUtil.getBean(FileOperator.class);
        Dict localClientDict = (Dict) localFileOperator.getClient();
        String currentSavePath = localClientDict.getStr("currentSavePath");
        File file = new File(currentSavePath);

        if (!file.exists()) {
            file.mkdirs();
        }
        return currentSavePath;
    }

    public static String[] getHistory(SysOnlineFileInfoResult sysOnlineFileInfoResult) {
        String histDir = OnlineDocumentUtil.getHistoryDir(OnlineDocumentUtil.getStoragePath(sysOnlineFileInfoResult.getFileId()));
        if (getFileVersion(histDir) > 0) {
            Integer curVer = getFileVersion(histDir);

            Set<Object> hist = new HashSet<Object>();
            Map<String, Object> histData = new HashMap<String, Object>();

            for (Integer i = 0; i <= curVer; i++) {
                Map<String, Object> obj = new HashMap<String, Object>();
                Map<String, Object> dataObj = new HashMap<String, Object>();
                String verDir = getVersionDir(histDir, i + 1);

                try {
                    String key = null;

                    key = i == curVer ? sysOnlineFileInfoResult.document.key : readFileToEnd(new File(verDir + File.separator + "key.txt"));

                    obj.put("key", key);
                    obj.put("version", i);

                    if (i == 0) {
                        String createdInfo = readFileToEnd(new File(histDir + File.separator + "createdInfo.json"));
                        JSONObject json = (JSONObject) JSONUtil.parse(createdInfo);

                        obj.put("created", json.get("created"));
                        Map<String, Object> user = new HashMap<String, Object>();
                        user.put("id", json.get("id"));
                        user.put("name", json.get("name"));
                        obj.put("user", user);
                    }

                    dataObj.put("key", key);
                    dataObj.put("url", i == curVer ? sysOnlineFileInfoResult.document.url : getStoragePath(verDir + File.separator + "prev" + FileUtil.getSuffix(sysOnlineFileInfoResult.getDocument().title)));
                    dataObj.put("version", i);

                    if (i > 0) {
                        JSONObject changes = (JSONObject) JSONUtil.parse(readFileToEnd(new File(getVersionDir(histDir, i) + File.separator + "changes.json")));
                        JSONObject change = (JSONObject) ((JSONArray) changes.get("changes")).get(0);

                        obj.put("changes", changes.get("changes"));
                        obj.put("serverVersion", changes.get("serverVersion"));
                        obj.put("created", change.get("created"));
                        obj.put("user", change.get("user"));

                        Map<String, Object> prev = (Map<String, Object>) histData.get(Integer.toString(i - 1));
                        Map<String, Object> prevInfo = new HashMap<String, Object>();
                        prevInfo.put("key", prev.get("key"));
                        prevInfo.put("url", prev.get("url"));
                        dataObj.put("previous", prevInfo);
                        dataObj.put("changesUrl", getStoragePath(getVersionDir(histDir, i) + File.separator + "diff.zip"));
                    }

                    hist.add(obj);
                    histData.put(Integer.toString(i), dataObj);

                } catch (Exception ex) {
                }
            }

            Map<String, Object> histObj = new HashMap<String, Object>();
            histObj.put("currentVersion", curVer);
            histObj.put("history", hist);

            Gson gson = new Gson();
            return new String[]{gson.toJson(histObj), gson.toJson(histData)};
        }
        return new String[]{"", ""};
    }

    public static String getHistoryDir(String storagePath) {
        return storagePath += "-hist";
    }

    public static String getVersionDir(String histPath, Integer version) {
        return histPath + File.separator + Integer.toString(version);
    }

    public static Integer getFileVersion(String historyPath) {
        File dir = new File(historyPath);

        if (!dir.exists()) return 0;

        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        return dirs.length;
    }

    private static String readFileToEnd(File file) {
        String output = "";
        try {
            try(FileInputStream is = new FileInputStream(file))
            {
                Scanner scanner = new Scanner(is);
                scanner.useDelimiter("\\A");
                while (scanner.hasNext()) {
                    output += scanner.next();
                }
                scanner.close();
            }
        } catch (Exception e) { }
        return output;
    }

    public static void deleteFileHistory(String fileId) {
        // 判断文件存在不存在
        String histDir = OnlineDocumentUtil.getHistoryDir(OnlineDocumentUtil.getStoragePath(fileId));
        File hisDirFile = FileUtil.file(histDir);
        if (!FileUtil.exist(hisDirFile)) {
            return;
        }

        // 删除文件
        FileUtil.del(hisDirFile);
    }
}
