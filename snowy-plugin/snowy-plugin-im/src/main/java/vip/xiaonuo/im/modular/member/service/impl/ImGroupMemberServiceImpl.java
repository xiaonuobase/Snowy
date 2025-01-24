/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.im.modular.member.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.im.core.manager.WebSocketSessionManager;
import vip.xiaonuo.im.core.utils.WebSocketUtil;
import vip.xiaonuo.im.modular.member.entity.ImGroupMember;
import vip.xiaonuo.im.modular.member.mapper.ImGroupMemberMapper;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberAddParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberEditParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberIdParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberPageParam;
import vip.xiaonuo.im.modular.member.service.ImGroupMemberService;

import java.util.Date;
import java.util.List;

/**
 * IM-群组成员Service接口实现类
 *
 * @author liuchunming
 * @date 2024/05/27 16:48
 **/
@Service
public class ImGroupMemberServiceImpl extends ServiceImpl<ImGroupMemberMapper, ImGroupMember> implements ImGroupMemberService {

    @Override
    public Page<ImGroupMember> page(ImGroupMemberPageParam imGroupMemberPageParam) {
        QueryWrapper<ImGroupMember> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isAllNotEmpty(imGroupMemberPageParam.getSortField(), imGroupMemberPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(imGroupMemberPageParam.getSortOrder());
            queryWrapper.orderBy(true, imGroupMemberPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(imGroupMemberPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ImGroupMember::getId);
        }
        queryWrapper.lambda().eq(StringUtils.isNotBlank(imGroupMemberPageParam.getGroupId()), ImGroupMember::getGroupId, imGroupMemberPageParam.getGroupId());
        queryWrapper.lambda().eq(StringUtils.isNotBlank(imGroupMemberPageParam.getUserId()), ImGroupMember::getUserId, imGroupMemberPageParam.getUserId());
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ImGroupMemberAddParam imGroupMemberAddParam) {
        ImGroupMember imGroupMember = BeanUtil.toBean(imGroupMemberAddParam, ImGroupMember.class);
        this.save(imGroupMember);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ImGroupMemberEditParam imGroupMemberEditParam) {
        ImGroupMember imGroupMember = this.queryEntity(imGroupMemberEditParam.getId());
        BeanUtil.copyProperties(imGroupMemberEditParam, imGroupMember);
        this.updateById(imGroupMember);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ImGroupMemberIdParam> imGroupMemberIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(imGroupMemberIdParamList, ImGroupMemberIdParam::getId));
    }

    @Override
    public ImGroupMember detail(ImGroupMemberIdParam imGroupMemberIdParam) {
        return this.queryEntity(imGroupMemberIdParam.getId());
    }

    @Override
    public ImGroupMember queryEntity(String id) {
        ImGroupMember imGroupMember = this.getById(id);
        if (ObjectUtil.isEmpty(imGroupMember)) {
            throw new CommonException("IM-群组成员不存在，id值为：{}", id);
        }
        return imGroupMember;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void silence(ImGroupMemberEditParam imGroupMemberEditParam) {
        ImGroupMember imGroupMember = this.queryEntity(imGroupMemberEditParam.getId());
        imGroupMember.setSilenceTime(imGroupMemberEditParam.getSilenceTime());
        // 推送到websocket中
        boolean b = WebSocketSessionManager.SESSIONS.containsKey(imGroupMemberEditParam.getUserId());
        if (b) {
            WebSocketSession webSocketSession = WebSocketSessionManager.SESSIONS.get(imGroupMemberEditParam.getUserId());
            // 手动拼接json数据节省性能
            JSONObject obj = JSONUtil.createObj();
            obj.set("messageType", "1");
            obj.set("groupId", imGroupMember.getGroupId());
            obj.set("silenceTime", imGroupMemberEditParam.getSilenceTime());
            synchronized (webSocketSession) {
                WebSocketUtil.sendMessage(webSocketSession, obj.toString());
            }
        }
        this.updateById(imGroupMember);

    }

    @Override
    public List<ImGroupMember> getSilenceGroup() {
        LambdaQueryWrapper<ImGroupMember> imGroupMemberLambdaQueryWrapper = Wrappers.lambdaQuery();
        imGroupMemberLambdaQueryWrapper.select(ImGroupMember::getGroupId, ImGroupMember::getSilenceTime, ImGroupMember::getUserId, ImGroupMember::getId);
        imGroupMemberLambdaQueryWrapper.eq(ImGroupMember::getUserId, StpUtil.getLoginId());
        imGroupMemberLambdaQueryWrapper.gt(ImGroupMember::getSilenceTime, new Date());
        return this.list(imGroupMemberLambdaQueryWrapper);
    }

    @Override
    public void cancelSilence(ImGroupMemberEditParam imGroupMemberEditParam) {
        ImGroupMember imGroupMember = this.queryEntity(imGroupMemberEditParam.getId());
        LambdaUpdateWrapper<ImGroupMember> imGroupMemberLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        imGroupMemberLambdaUpdateWrapper.set(ImGroupMember::getSilenceTime, null);
        imGroupMemberLambdaUpdateWrapper.eq(ImGroupMember::getId, imGroupMemberEditParam.getId());

        // 推送到websocket中
        boolean b = WebSocketSessionManager.SESSIONS.containsKey(imGroupMemberEditParam.getUserId());
        if (b) {
            WebSocketSession webSocketSession = WebSocketSessionManager.SESSIONS.get(imGroupMemberEditParam.getUserId());
            // 手动拼接json数据节省性能
            JSONObject obj = JSONUtil.createObj();
            obj.set("messageType", "2");
            obj.set("groupId", imGroupMember.getGroupId());
            synchronized (webSocketSession) {
                WebSocketUtil.sendMessage(webSocketSession, obj.toString());
            }
        }
        this.update(imGroupMemberLambdaUpdateWrapper);
    }
}
