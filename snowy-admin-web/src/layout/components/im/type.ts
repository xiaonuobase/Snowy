export interface User {
	id: string;
	name: string;
	avatar: string;
}

export interface ImMessageUserVo {
	userId: string;
	content: string;
	type: string; // b端用户 1 c端用户 2
	createTime: string;
	unreadCount: string; // 未读信息条数
}

export interface Message {
	id: string;
	content: string;
	fromUserId: string;
	toUserId: string;
	name: string;
	avatar: string;
	createTime: string;
}

