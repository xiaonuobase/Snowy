export interface User {
	id: string;
	name: string;
	avatar: string;
	current?: -1;
	total?:-1;
	useType?:1; // 用户类型； 1-用户 2-群组
}

export interface ImGroupVo {
	id:string;
	name:string;
	avatar:string;
	createTime:string;
	userList:User[];
}

export interface ImMessageUserVo {
	userId: string;
	content: string;
	type: string; // b端用户 1 c端用户 2
	createTime: string;
	unreadCount: number; // 未读信息条数
	isRecall: string; // 是否撤回
}

export interface Message {
	id: string;
	content: string;
	fromUserId: string;
	toUserId: string;
	name: string;
	avatar: string;
	createTime: string;
	isRead:string;
	isRecall:string;
	type: string; //消息类型：1-文本，2-图片，3-视频，4-文件
	chatType: string; //聊天类型：1-单聊，2-群聊 
	toUserType: string;
	fromUserType: string;
}


export interface ImMessageBo {
	fromUserId: string;
	toUserId: string;
	content: string;
	type: string; //消息类型：1-文本，2-图片，3-视频，4-文件
	chatType: string; //聊天类型：1-单聊，2-群聊 
	toUserType: string;
	fromUserType: string;
}